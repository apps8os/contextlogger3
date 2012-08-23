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
import java.nio.charset.Charset;

import org.json.JSONException;
import org.sizzlelab.contextlogger.android.utils.JsonUtils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import edu.mit.media.funf.IOUtils;
import edu.mit.media.funf.Utils;
import edu.mit.media.funf.configured.ConfiguredPipeline;
import edu.mit.media.funf.configured.FunfConfig;
import edu.mit.media.funf.probe.Probe;
import edu.mit.media.funf.storage.BundleSerializer;

public class MainPipeline extends ConfiguredPipeline {
	public static final String LOGGER_CLASS = "MainPipeline";
	public static final String MAIN_CONFIG = "main_config";
	public static final String COUNT_KEY = "SCAN_COUNT";

	@Override
	public void onCreate() {
		super.onCreate();
		setEncryptionPassword("justdoit".toCharArray());
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		super.onHandleIntent(intent);
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
		return getSystemPrefs(context).getBoolean(ENABLED_KEY, true);
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
}
