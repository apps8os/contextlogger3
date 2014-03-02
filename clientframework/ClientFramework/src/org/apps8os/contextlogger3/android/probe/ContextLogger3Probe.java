/**
 * Copyright (c) 2014 Aalto University and the authors
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
 * Chao Wei (chao.wei@aalto.fi)
 */

package org.apps8os.contextlogger3.android.probe;

import java.util.Set;

import org.apps8os.contextlogger3.android.R;
import org.apps8os.contextlogger3.android.utils.IOUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import edu.mit.media.funf.probe.Probe.Base;

/**
 * 
 */
abstract class ContextLogger3Probe extends Base {
	
	/**
	 * Return a short name of the class
	 * 
	 * @return
	 */
	abstract String getClassName();
	
	/**
	 * Return the intent action name
	 * 
	 * @return
	 */
	abstract String getIntentAction();
	
	
	private ContextLogger3DataReceiver mContextLogger3DataReceiver = null;

	@Override
	public void destroy() {
		super.destroy();
		Log.i(getClassName(), "destroy");
	}

	@Override
	protected void onDisable() {
		super.onDisable();
		LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mContextLogger3DataReceiver);
		Log.i(getClassName(), "onDisable");
	}

	@Override
	protected void onEnable() {
		super.onEnable();
		if(mContextLogger3DataReceiver == null) {
			mContextLogger3DataReceiver = new ContextLogger3DataReceiver(this);
		}
		LocalBroadcastManager.getInstance(getContext()).registerReceiver(mContextLogger3DataReceiver, 
																new IntentFilter(getIntentAction()));
		Log.i(getClassName(), "onEnable");
	}

	@Override
	protected void onStart() {
		super.onStart();
		Log.i(getClassName(), "onStart");
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.i(getClassName(), "onStop");
	}
	
	/**
	 * Return the configuration content
	 * 
	 * @return
	 */
	protected final JSONArray getConfigurationContent() {
		JSONArray ja = null;
		try {
			String filename = getContext().getString(R.string.context_logger3_google_config_json_file_name);
			String json = IOUtil.getJSONString(getContext().getAssets().open(filename));			
			if(!TextUtils.isEmpty(json)) {
				try {
					JSONObject jobject = new JSONObject(json);
					if(jobject != null) {
						JSONArray jarray = jobject.getJSONArray(filename);
						if(jarray != null) {
							for(int i = 0; i < jarray.length(); i++) {
								JSONObject o = jarray.getJSONObject(i);
								if(o.has(getClassName())) {
									ja = o.getJSONArray(getClassName());
									break;
								}
							}
						}
					}
				} catch (Exception e) {
					Log.e(getClassName(), "Read config details failed", e);
				}
			}
		} catch (Exception e) {
			Log.e(getClassName(), "Read config file failed", e);
		}
		return ja;
	}
	
	static class ContextLogger3DataReceiver extends BroadcastReceiver {

		private ContextLogger3Probe mProbe = null;

		public ContextLogger3DataReceiver(ContextLogger3Probe probe) {
			mProbe = probe;
		}

		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			if((mProbe != null) && !TextUtils.isEmpty(action)) {
				try {
					if (action.equals(mProbe.getIntentAction())) {
						Log.i(mProbe.getClassName(), "Probe data is coming...");
						Bundle bundle = intent.getExtras();
						JSONObject json = null;
						if(bundle != null) {
							json = new JSONObject();
							Set<String> keys = bundle.keySet();
							for(String k : keys) {
								try {
									json.put(k, bundle.getString(k));									
								} catch (Exception e) {
									Log.e(mProbe.getClassName(), "Probe data handing error in loop: ", e);
								}
							}
						}
						if(json != null) {
							JsonParser parser = new JsonParser(); 
							JsonObject data = (JsonObject) parser.parse(json.toString());
							mProbe.sendData(data);							
							Log.i(mProbe.getClassName(), "Probe data: " + json.toString());
						}
					}
				} catch (Exception e) {
					Log.e(mProbe.getClassName(), "Probe data handing error: ", e);
				}				
			}
		}
	}

}
