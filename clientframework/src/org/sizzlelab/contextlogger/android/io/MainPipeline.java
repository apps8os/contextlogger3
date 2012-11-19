/**
 * Copyright (c) 2012 Aalto University and the authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining 
 * a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation 
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, 
 * and/or sell copies of the Software, and to permit persons to whom the 
 * Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included 
 * in all copies or substantial portions of the Software.
 *  
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS 
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING 
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER 
 * DEALINGS IN THE SOFTWARE.
 *  
 * Authors:
 * Chaudhary Nalin (nalin.chaudhary@aalto.fi)
 */
package org.sizzlelab.contextlogger.android.io;

import static edu.mit.media.funf.AsyncSharedPrefs.async;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sizzlelab.contextlogger.android.triggershandling.TriggerManager;
import org.sizzlelab.contextlogger.android.triggershandling.TriggerParcel;
import org.sizzlelab.contextlogger.android.triggershandling.TriggersConfig;
import org.sizzlelab.contextlogger.android.utils.JsonUtils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import edu.mit.media.funf.IOUtils;
import edu.mit.media.funf.Utils;
import edu.mit.media.funf.configured.ConfiguredPipeline;
import edu.mit.media.funf.configured.FunfConfig;
import edu.mit.media.funf.probe.Probe;
import edu.mit.media.funf.probe.ProbeExceptions.UnstorableTypeException;
import edu.mit.media.funf.storage.BundleSerializer;

public class MainPipeline extends ConfiguredPipeline {
	public static final String LOGGER_CLASS = "MainPipeline";
	public static final String MAIN_CONFIG = "main_config";
	public static final String COUNT_KEY = "SCAN_COUNT";
	private static final String TRIGGERS_CONFIG = "triggers_config";
	private static final String PREFIX = "edu.mit.media.funf.";
	public static final String ACTION_UPDATE_TRIGGERS_CONFIG = PREFIX + "updateTriggersConfig";
	public static final String LAST_TRIGGERS_CONFIG_UPDATE = "LAST_TRIGGERS_CONFIG_UPDATE";
	private static final String TRIGGERS_INTENT_ACTION = "org.sizzlelab.contextlogger.android.triggersIntentAction";
	private static final String APPLY_TRIGGERS_INTENT_ACTION = "org.sizzlelab.contextlogger.android.applyTriggersIntentAction";
	private TriggerIntentReceiver mTriggerIntReceiver = null;
	
	@Override
	public void onCreate() {
		super.onCreate();
		setEncryptionPassword("justdoit".toCharArray());
		getTriggersConfig().getPrefs().registerOnSharedPreferenceChangeListener(this);
		
		mTriggerIntReceiver = new TriggerIntentReceiver();
		IntentFilter if1 = new IntentFilter(APPLY_TRIGGERS_INTENT_ACTION);
		registerReceiver(mTriggerIntReceiver, if1);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		System.out.println("MainPipeline: onHandleIntent: intent: " + intent.getAction());
		if (ACTION_UPDATE_TRIGGERS_CONFIG.equals(intent.getAction()))
		{
			if ( TriggerManager.isEnabled())
			{
				updateTriggersConfig();
			}
		}
		else
		{
			super.onHandleIntent(intent);			
		}
	}

	@Override
	public BundleSerializer getBundleSerializer() {
		return new BundleToJson();
	}

	public static class BundleToJson implements BundleSerializer {
		public String serialize(Bundle bundle) {
			return JsonUtils.getGson().toJson(Utils.getValues(bundle));
		}
	}

	@Override
	public void onDataReceived(Bundle data) {
		super.onDataReceived(data);
		incrementTotalCount();
	}

	@Override
	public void onStatusReceived(Probe.Status status) {
		super.onStatusReceived(status);
	}

	@Override
	public void onDetailsReceived(Probe.Details details) {
		super.onDetailsReceived(details);
	}

	public static boolean isEnabled(Context context) {
		return getSystemPrefs(context).getBoolean(ENABLED_KEY, false);
	}

	@Override
	public SharedPreferences getSystemPrefs() {
		return getSystemPrefs(this);
	}

	public static SharedPreferences getSystemPrefs(Context context) {
		return async(context.getSharedPreferences(MainPipeline.class.getName()
				+ "_system", MODE_PRIVATE));
	}

	@Override
	public FunfConfig getConfig() {
		return getMainConfig(this);
	}

	public static FunfConfig getMainConfig(Context context) {
		FunfConfig config = getConfig(context, MAIN_CONFIG);
		if (config.getName() == null) {
			String jsonString = getStringFromAsset(context,
					"default_config.json");
			if (jsonString == null) {
				Log.e(LOGGER_CLASS, "Error loading default config.  Using blank config.");
				jsonString = "{}";
			}
			try {
				config.edit().setAll(jsonString).commit();
			} catch (JSONException e) {
				Log.e(LOGGER_CLASS, "Error parsing default config", e);
			}
		}
		return config;
	}

	public static String getStringFromAsset(Context context, String filename) {
		InputStream is = null;
		try {
			is = context.getAssets().open(filename);
			return IOUtils.inputStreamToString(is, Charset.defaultCharset()
					.name());
		} catch (IOException e) {
			Log.e(LOGGER_CLASS, "Unable to read asset to string", e);
			return null;
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					Log.e(LOGGER_CLASS, "Unable to close asset input stream", e);
				}
			}
		}
	}

	public static long getScanCount(Context context) {
		return getSystemPrefs(context).getLong(COUNT_KEY, 0L);
	}

	private void incrementTotalCount() {
		boolean success = false;
		while (!success) {
			SharedPreferences.Editor editor = getSystemPrefs().edit();
			editor.putLong(COUNT_KEY, getScanCount(this) + 1L);
			success = editor.commit();
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		getTriggersConfig().getPrefs().unregisterOnSharedPreferenceChangeListener(this);
	}
	
	
	public TriggersConfig getTriggersConfig()
	{
		return getTriggersConfig(this);
	}
	
	public static TriggersConfig getTriggersConfig(Context context) {
		TriggersConfig tConfig = getTriggersConfig(context, TRIGGERS_CONFIG);
		
		if (tConfig.getName() == null) {
			String jsonString = getStringFromAsset(context, "triggers_config.json");
			if (jsonString == null) {
				jsonString = "{}";
			}
			try {
				tConfig.edit().setAll(jsonString).commit();
			} catch (JSONException e) {
				Log.e(LOGGER_CLASS, "Error parsing default config", e);
			}
		}
		return tConfig;
	}
	
	private static TriggersConfig getTriggersConfig(Context context, String name) {
		SharedPreferences prefs = context.getSharedPreferences(name, android.content.Context.MODE_PRIVATE);
		return TriggersConfig.getInstance(async(prefs));
	}
	
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) 
	{
		if (sharedPreferences.equals(getTriggersConfig().getPrefs()) && TriggersConfig.TRIGGERS_CONFIG_UPDATE_PERIOD_KEY.equals(key))
		{
			cancelTriggersAlarm(ACTION_UPDATE_TRIGGERS_CONFIG);
			scheduleTriggersAlarms();
		}
		else
		{
			super.onSharedPreferenceChanged(sharedPreferences, key);			
		}
	}
	
	private void scheduleTriggersAlarms() {
		TriggersConfig config = getTriggersConfig();
		if (config.getTriggersConfigUpdatePeriod() > 0)
		{
			scheduleTriggersAlarm(ACTION_UPDATE_TRIGGERS_CONFIG, config.getTriggersConfigUpdatePeriod());
		}
	}
	
	private void scheduleTriggersAlarm(String action, long delayInSeconds) {
		Intent i = new Intent(this, getClass());
		i.setAction(action);
		boolean noAlarmExists = (PendingIntent.getService(this, 0, i, PendingIntent.FLAG_NO_CREATE) == null);
		if (noAlarmExists) {
			PendingIntent pi = PendingIntent.getService(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
			AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
			long delayInMilliseconds = Utils.secondsToMillis(delayInSeconds);
			long startTimeInMilliseconds = System.currentTimeMillis() + delayInMilliseconds;
			Log.i(LOGGER_CLASS, "Scheduling alarm for '" + action + "' at " + Utils.millisToSeconds(startTimeInMilliseconds) + " and every " + delayInSeconds  + " seconds");
			alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, startTimeInMilliseconds, delayInMilliseconds, pi);
		}
	}
	
	private void cancelTriggersAlarm(String action) {
		Intent i = new Intent(this, getClass());
		i.setAction(action);
		PendingIntent pi = PendingIntent.getService(this, 0, i, PendingIntent.FLAG_NO_CREATE);
		if (pi != null) {
			AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
			alarmManager.cancel(pi);
			pi.cancel();
		}
	}
	
	public void updateTriggersConfig() {
		String tConfigUpdateUrl = getTriggersConfig().getTriggersConfigUpdateUrl();
		if (tConfigUpdateUrl == null) {
			Log.i(LOGGER_CLASS, "No triggers update url configured.");
		} else {
			try {
				updateTriggersConfig(new URL(tConfigUpdateUrl));
			} catch (MalformedURLException e) {
				Log.e(LOGGER_CLASS, "Invalid triggers update URL specified.", e);
			}
		}
	}
	
	public void updateTriggersConfig(URL url) {
		String jsonString = IOUtils.httpGet(url.toExternalForm(), null);
		updateTriggersConfig(jsonString);
	}
	
	public void updateTriggersConfig(String jsonString) {
		if (jsonString == null) {
			Log.e(LOGGER_CLASS, "A null triggers configuration cannot be specified.");
			return;
		}
		try {
			Log.i(LOGGER_CLASS, "Updating triggers config.");
			TriggersConfig tempConfig = getTemporaryTriggersConfig();
			boolean successfullyWroteConfig = tempConfig.edit().setAll(jsonString).commit();
			if (successfullyWroteConfig) {
				getSystemPrefs().edit().putLong(LAST_TRIGGERS_CONFIG_UPDATE, System.currentTimeMillis()).commit();
			}
			if (successfullyWroteConfig	&& !tempConfig.equals(getTriggersConfig())) {
				getTriggersConfig().edit().setAll(tempConfig).commit();
				if (TriggerManager.isEnabled())
				{
					applyTriggers(jsonString);
				}
			}
		} catch (JSONException e) {
			Log.e(LOGGER_CLASS, "Unable to update configuration.", e);
		}
	}
	
	private void applyTriggers(String tconfig) {
		if (tconfig != null)
		{
			try {
				JSONObject jsonObject = new JSONObject(tconfig);
				JSONArray triggers = jsonObject.getJSONArray("triggers");
				if (triggers != null & triggers.length() > 0)
				{
					ArrayList<TriggerParcel> list = new ArrayList<TriggerParcel>();
					for (int i = 0; i < triggers.length(); i++)
					{
						JSONObject trigger = triggers.getJSONObject(i);
						String action = trigger.getString("action");
						String config = trigger.getString("config");
						
						if (action != null && !action.isEmpty() && config != null && !config.isEmpty())
						{
							TriggerParcel tp = new TriggerParcel();
							tp.setAction(action);
							tp.setConfig(config);
							
							list.add(tp);
						}
					}
					
					if (list.size() > 0)
					{
						Intent i = new Intent();
						i.setAction(TRIGGERS_INTENT_ACTION);
						i.putParcelableArrayListExtra("triggers_info", list);
						this.sendBroadcast(i);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	private TriggersConfig getTemporaryTriggersConfig() {
		SharedPreferences prefs = getSharedPreferences("triggers_tempconfig", MODE_PRIVATE);
		return TriggersConfig.getInstance(async(prefs));
	}
	
	public void sendProbeRequest(String probeName, ArrayList<Bundle> dataRequest) {
		Intent request = new Intent(Probe.ACTION_REQUEST);
		request.setClassName(this, probeName);
		request.putExtra(Probe.CALLBACK_KEY, getCallback());
		request.putExtra(Probe.REQUESTS_KEY, dataRequest);
		startService(request);
	}
	
	private class TriggerIntentReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String intentAction = intent.getAction();
			if (APPLY_TRIGGERS_INTENT_ACTION.equals(intentAction))
			{
				String type = intent.getExtras().getString("TriggerType");
				if (type != null) {
					if (type.equals("1")) {
						getCallback().cancel();
					}
					else if (type.equals("2")) {
						try {
							Bundle[] requests = new Bundle[] {};
							String p = intent.getExtras()
									.getString("ProbeName");
							String v = intent.getExtras().getString(
									"ProbeConfig");
							JSONArray value = new JSONArray(v);
							requests = getBundleArray(value);

							ArrayList<Bundle> dataRequest = new ArrayList<Bundle>(
									Arrays.asList(requests));
							sendProbeRequest(p, dataRequest);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
	
	private static Bundle[] getBundleArray(JSONArray jsonArray) throws JSONException {
		Bundle[] bundles = new Bundle[jsonArray.length()];
		for (int i = 0; i < jsonArray.length(); i++) {
			bundles[i] = getBundle(jsonArray.getJSONObject(i));
		}
		return bundles;
	}
	
	@SuppressWarnings("unchecked")
	private static Bundle getBundle(JSONObject jsonObject) throws JSONException {
		Bundle bundle = new Bundle();
		Iterator<String> paramNames = jsonObject.keys();
		while (paramNames.hasNext()) {
			String paramName = paramNames.next();
			try  {
			Utils.putInBundle(bundle, paramName, jsonObject.get(paramName));
			} catch (UnstorableTypeException e) {
				throw new JSONException(e.getLocalizedMessage());
			}
		}
		return bundle;
	}
}
