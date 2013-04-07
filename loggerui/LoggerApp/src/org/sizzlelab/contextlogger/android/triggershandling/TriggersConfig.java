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
package org.sizzlelab.contextlogger.android.triggershandling;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import edu.mit.media.funf.Utils;

public class TriggersConfig implements OnSharedPreferenceChangeListener 
{
	private final SharedPreferences mSharedPrefs;
	private static final Map<SharedPreferences, TriggersConfig> mTCInstances = new HashMap<SharedPreferences, TriggersConfig>();
	private Map<String, String> mActionTriggerMap = null;
	public static final long DEFAULT_VERSION = 0;
	public static final long DEFAULT_TRIGGERS_CONFIG_UPDATE_PERIOD = 1 * 60 * 60;
	public static final String NAME_KEY = "name";
	public static final String VERSION_KEY = "version";
	public static final String TRIGGERS_KEY = "triggers";
	public static final String ACTION_NAME_KEY = "Action-";
	public static final String TRIGGERS_CONFIG_UPDATE_URL_KEY = "triggersConfigUpdateUrl";
	public static final String TRIGGERS_CONFIG_UPDATE_PERIOD_KEY = "triggersConfigUpdatePeriod";
	
	private TriggersConfig(SharedPreferences prefs) {
		assert prefs != null;
		this.mSharedPrefs = prefs;
		prefs.registerOnSharedPreferenceChangeListener(this);
		mActionTriggerMap = new HashMap<String, String>();
	}
	
	public static TriggersConfig getInstance(SharedPreferences prefs) {
		TriggersConfig tConfig = mTCInstances.get(prefs);
		if (tConfig == null) {
			synchronized (mTCInstances) {
				tConfig = mTCInstances.get(prefs);
				if (tConfig == null) {
					tConfig = new TriggersConfig(prefs);
					mTCInstances.put(prefs, tConfig);
				}
			}
		}
		return tConfig;
	}
	
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if (sharedPreferences == mSharedPrefs && isActionKey(key)) {
			synchronized (mActionTriggerMap) {
				mActionTriggerMap.remove(keyToActionName(key));
			}
		}
	}
	
	public static boolean isActionKey(String key) {
		return key != null && key.startsWith(ACTION_NAME_KEY);
	}
	
	public static String actionNameToKey(String actionName) {
		return ACTION_NAME_KEY + actionName;
	}
	
	public static String keyToActionName(String key) {
		return key.substring(ACTION_NAME_KEY.length());
	}
	
	public String getName() {
		return mSharedPrefs.getString(NAME_KEY, null);
	}

	public long getVersion() {
		return mSharedPrefs.getLong(VERSION_KEY, DEFAULT_VERSION);
	}

	public String getTriggersConfigUpdateUrl() {
		return mSharedPrefs.getString(TRIGGERS_CONFIG_UPDATE_URL_KEY, null);
	}

	public long getTriggersConfigUpdatePeriod() {
		return mSharedPrefs.getLong(TRIGGERS_CONFIG_UPDATE_PERIOD_KEY, DEFAULT_TRIGGERS_CONFIG_UPDATE_PERIOD);
	}
	
	public  Map<String, String> getTriggers() {
		Set<String> actionNames = mSharedPrefs.getAll().keySet();
		synchronized (mActionTriggerMap) {
			for (String key : actionNames) {
				if (isActionKey(key)) {
					String actionName = keyToActionName(key);
					if (!mActionTriggerMap.containsKey(actionName)) {
						getTrigger(actionName);
					}
				}
			}
			return deepCopy(mActionTriggerMap);
		}
	}
	
	public String getTrigger(String actionName) {
		synchronized (mActionTriggerMap) {
			if (mActionTriggerMap.containsKey(actionName)) {
				return mActionTriggerMap.get(actionName);
			}
	
			String jsonString = mSharedPrefs.getString(actionNameToKey(actionName), null);
			if (jsonString == null) {
				return null;
			}

			mActionTriggerMap.put(actionName, jsonString);
			return jsonString;
		}
	}
	
	private Map<String, String> deepCopy(Map<String, String> orig) 
	{
		Map<String, String> copy = new HashMap<String, String>();
		for (Map.Entry<String, String> entry : orig.entrySet()) {
			copy.put(entry.getKey(), entry.getValue());
		}
		return copy;
	}
	
	public SharedPreferences getPrefs() {
		return mSharedPrefs;
	}
	
	public Editor edit() {
		return new Editor();
	}
	
	public class Editor {
		private static final String CONFIG_KEY = "config";
		private static final String ACTION_KEY = "action";
		private SharedPreferences.Editor mEditor = getPrefs().edit();
		private Set<String> mChangedActions = new HashSet<String>();
		private boolean mClear = false;
		
		public Editor setName(String name) {
			mEditor.putString(NAME_KEY, name);
			return this;
		}
		
		public Editor setVersion(int version) {
			mEditor.putInt(VERSION_KEY, version);
			return this;
		}

		public Editor setTriggersConfigUpdateUrl(String triggersConfigUpdateUrl) {
			mEditor.putString(TRIGGERS_CONFIG_UPDATE_URL_KEY, triggersConfigUpdateUrl);
			return this;
		}

		public Editor setTriggersConfigUpdatePeriod(long triggersConfigUpdatePeriod) {
			mEditor.putLong(TRIGGERS_CONFIG_UPDATE_PERIOD_KEY, triggersConfigUpdatePeriod);
			return this;
		}

		public Editor setActionTrigger(String actionName, String triggerJSON) throws JSONException {
			if (!areEqual(new JSONObject(actionName), new JSONObject(triggerJSON))) {
				if (triggerJSON == null || triggerJSON.length() == 0) {
					mEditor.remove(actionNameToKey(actionName));
				} else {
					mEditor.putString(actionNameToKey(actionName), triggerJSON);
				}
				mChangedActions.add(actionName);
			}
			return this;
		}
		
		public boolean areEqual(JSONObject o1, JSONObject o2) {
			if (o1 == null) {
				return o2 == null;
			} else if (o2 == null) {
				return false;
			} else if (o1 == o2) {
				return true;
			}
			if (o1.length() != o2.length()) {
				return false;
			}
			
			// TODO add check for ensuring same contents
			for (int i=0; i<o1.length(); i++) {
			}
			return true;
		}
		
		private void setString(JSONObject jsonObject, String key) {
			String value = jsonObject.optString(key, null);
			if (value == null) {
				mEditor.remove(key);
			} else {
				mEditor.putString(key, value);
			}
		}
		
		private void setPositiveLong(JSONObject jsonObject, String key) {
			long value = jsonObject.optLong(key, 0L);
			if (value <= 0) {
				mEditor.remove(key);
			} else {
				mEditor.putLong(key, value);
			}
		}
		
		public Editor setAll(String jsonString) throws JSONException {
			JSONObject jsonObject = new JSONObject(jsonString);
			mEditor.clear();
			mClear = true;
			setString(jsonObject, NAME_KEY);
			setPositiveLong(jsonObject, VERSION_KEY);
			setString(jsonObject, TRIGGERS_CONFIG_UPDATE_URL_KEY);
			setPositiveLong(jsonObject, TRIGGERS_CONFIG_UPDATE_PERIOD_KEY);
			JSONArray triggers = jsonObject.getJSONArray(TRIGGERS_KEY);
			if (triggers != null & triggers.length() > 0)
			{
				for (int i = 0; i < triggers.length(); i++)
				{
					JSONObject trigger = triggers.getJSONObject(i);
					mEditor.putString(actionNameToKey(trigger.getString(ACTION_KEY)), trigger.getString(CONFIG_KEY));
				}
			}
			return this;
		}
		
		public Editor setAll(TriggersConfig otherConfig) {
			mEditor.clear();
			mClear = true;
			for (Map.Entry<String, ?> entry : otherConfig.getPrefs().getAll().entrySet()) {
				if (entry.getValue() != null) {
					Utils.putInPrefs(mEditor, entry.getKey(), entry.getValue());
				}
			}
			return this;
		}
		
		public Editor clear() {
			mEditor.clear();
			mClear = true;
			return this;
		}
		
		public boolean commit() {
			if (mClear || !mChangedActions.isEmpty()) {
				synchronized (mActionTriggerMap) {
					if (mClear) {
						mActionTriggerMap.clear();
					} else {
						for (String changedActionName : mChangedActions) {
							mActionTriggerMap.remove(changedActionName);
						}
					}
					return mEditor.commit();
				}
			} else {
				return mEditor.commit();
			}
		}
	}
	
	@Override
	public boolean equals(Object o) {
		return o != null 
			&& o instanceof TriggersConfig 
			&& (mSharedPrefs == ((TriggersConfig)o).mSharedPrefs
				|| mSharedPrefs.getAll().equals(((TriggersConfig)o).mSharedPrefs.getAll()));
	}
	
	@Override
	public int hashCode() {
		return mSharedPrefs.hashCode();
	}
}