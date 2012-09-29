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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class TriggerManager extends BroadcastReceiver 
{
	private Context mContext = null;
	private static boolean isEnabled = false;
	private static TriggerManager mInstance = null;
	private Map<String, String> mTMap = new HashMap<String, String>();
	private static final String TRIGGERS_INTENT_ACTION = "org.sizzlelab.contextlogger.android.triggersIntentAction";
	private static final String APPLY_TRIGGERS_INTENT_ACTION = "org.sizzlelab.contextlogger.android.applyTriggersIntentAction";
	
	private TriggerManager()
	{
	}
	
	public static TriggerManager getInstance()
	{
		if (mInstance == null) {
			mInstance = new TriggerManager();
		}
		return mInstance;
	}
	
	public void enable(Context context, Map<String, String> initialTConfig)
	{
		isEnabled = true;
		mTMap = initialTConfig;
		mContext = context;
	}

	public void disable()
	{
		isEnabled = false;
	}

	public static boolean isEnabled() {
		return isEnabled;
	}
	
	public String getConfig(String action)
	{
		String ret = null;
		if (mTMap.containsKey(action))
		{
			ret = mTMap.get(action);
		}
		
		return ret;
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		final String intentAction = intent.getAction();
		if (TRIGGERS_INTENT_ACTION.equals(intentAction))
		{
			ArrayList<TriggerParcel> triggers = intent.getParcelableArrayListExtra("triggers_info");
			if (triggers != null)
			{
				mTMap.clear();
				for (TriggerParcel tp : triggers)
				{
					String action = tp.getAction();
					String config = tp.getConfig();
					if (action != null && !action.isEmpty() && config != null && !config.isEmpty())
					{
						mTMap.put(action, config);
					}
				}				
			}
		}
	}
	
	public void handleAction(String appAction) 
	{
		if (isEnabled()){
			if (mTMap.containsKey(appAction)){
				applyConfig(mTMap.get(appAction));
			}
		}
	}
	
	private void applyConfig(String config)
	{
		try {
			Intent rcp = new Intent();
			rcp.setAction(APPLY_TRIGGERS_INTENT_ACTION);
			rcp.putExtra("TriggerType", "1");
			mContext.sendBroadcast(rcp);
			
			JSONObject probesJsonObject = new JSONObject(config);
			Iterator probesIterator = probesJsonObject.keys();
			while (probesIterator.hasNext()) 
			{
				String probeName = (String)probesIterator.next();
				String value = probesJsonObject.getString(probeName);
				
				Intent enp = new Intent();
				enp.setAction(APPLY_TRIGGERS_INTENT_ACTION);
				enp.putExtra("TriggerType", "2");
				enp.putExtra("ProbeName", probeName);
				enp.putExtra("ProbeConfig", value);
				mContext.sendBroadcast(enp);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}