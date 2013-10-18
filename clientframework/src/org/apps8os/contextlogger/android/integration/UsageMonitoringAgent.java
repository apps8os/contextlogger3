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
package org.apps8os.contextlogger.android.integration;

import java.util.List;

import org.apps8os.contextlogger.android.utils.Constants;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

public class UsageMonitoringAgent implements Constants {
	// private static final String CUSTOM_INTENT_ACTION = "org.apps8os.contextlogger.android.customIntentAction";
	private static final String APPLICATION_ACTION = "APPLICATION_ACTION";
	private static final String APPLICATION_DATA = "APPLICATION_DATA";
	private static final String UNDERSCORE_START = "_START";
	private static final String UNDERSCORE_STOP = "_STOP";
	private static final String OVERHEAD = "Overhead_";
	private static final String SEPARATOR = "|";
	private static final long MILLION = 1000 * 1000;
	
	private static UsageMonitoringAgent mInstance = null;
	
	private UsageMonitoringAgent() {
	}
	
	public static synchronized UsageMonitoringAgent getInstance()
	{
		if (mInstance == null) {
			mInstance = new UsageMonitoringAgent();
		}
		return mInstance;
	}	
	
	public void onEvent(Context context, String actionName) {
		onEvent(context, actionName, null);
	}
	
	public void onEvent(Context context, String actionName, boolean monitorLatency) {

		
		if (!monitorLatency) {
			onEvent(context, actionName, null);
		} else {
			long t1 = System.nanoTime();
			onEvent(context, actionName, null);
			long t2 = System.nanoTime();			
			
			storeLatency(actionName, (t2 - t1));
		}
	}

	public void onEvent(Context context, String actionName, List<String> additionalInfoList) {
		Intent i = new Intent();
		i.setAction(CUSTOM_INTENT_ACTION);
		i.putExtra(APPLICATION_ACTION, getActionTag(actionName));
		i.putExtra(APPLICATION_DATA, getStringFromList(additionalInfoList));
		
		context.sendBroadcast(i);
	}
	
	public void onEvent(Context context, String actionName, List<String> additionalInfoList, boolean monitorLatency) {
		
		if (!monitorLatency) {
			onEvent(context, actionName, additionalInfoList);			
		} else {
			long t1 = System.nanoTime();
			onEvent(context, actionName, additionalInfoList);
			long t2 = System.nanoTime();
			
			storeLatency(actionName, (t2 - t1));
		}

	}
	
	public void onTimedEventStart(Context context, String actionName) {
		onTimedEventStart(context, actionName, null);
	}
	
	public void onTimedEventStart(Context context, String actionName, boolean monitorLatency) {
		
		if (!monitorLatency) {
			onTimedEventStart(context, actionName, null);			
		} else {
			long t1 = System.nanoTime();
			onTimedEventStart(context, actionName, null);
			long t2 = System.nanoTime();
			
			storeLatency(getActionStartTag(actionName), (t2 - t1));
		}
	}
	
	public void onTimedEventStart(Context context, String actionName, List<String> additionalInfoList) {
		Intent i = new Intent();
		i.setAction(CUSTOM_INTENT_ACTION);
		i.putExtra(APPLICATION_ACTION, getActionStartTag(actionName));
		i.putExtra(APPLICATION_DATA, getStringFromList(additionalInfoList));
		
		context.sendBroadcast(i);
	}
	
	public void onTimedEventStart(Context context, String actionName, List<String> additionalInfoList, boolean monitorLatency) {
		if (!monitorLatency) {
			onTimedEventStart(context, actionName, additionalInfoList);			
		} else {
			long t1 = System.nanoTime();
			onTimedEventStart(context, actionName, additionalInfoList);
			long t2 = System.nanoTime();
			
			storeLatency(getActionStartTag(actionName), (t2 - t1));
		}
	}
	
	public void onTimedEventStop(Context context, String actionName) {
		onTimedEventStop(context, actionName, null);
	}
	
	public void onTimedEventStop(Context context, String actionName, boolean monitorLatency) {
		if (!monitorLatency) {
			onTimedEventStop(context, actionName, null);
		} else {
			long t1 = System.nanoTime();
			onTimedEventStop(context, actionName, null);
			long t2 = System.nanoTime();
			
			storeLatency(getActionStopTag(actionName), (t2 - t1));
		}
	}
	
	public void onTimedEventStop(Context context, String actionName, List<String> additionalInfoList) {
		Intent i = new Intent();
		i.setAction(CUSTOM_INTENT_ACTION);
		i.putExtra(APPLICATION_ACTION, getActionStopTag(actionName));
		i.putExtra(APPLICATION_DATA, getStringFromList(additionalInfoList));
		
		context.sendBroadcast(i);
	}
	
	public void onTimedEventStop(Context context, String actionName, List<String> additionalInfoList, boolean monitorLatency) {
		if (!monitorLatency) {
			onTimedEventStop(context, actionName, additionalInfoList);
		} else {
			long t1 = System.nanoTime();
			onTimedEventStop(context, actionName, additionalInfoList);
			long t2 = System.nanoTime();
			
			storeLatency(getActionStopTag(actionName), (t2 - t1));
		}
	}
	
	private String getActionTag(String action)
	{
		String ret = "";
		if (action != null && !TextUtils.isEmpty(action)) {
			action = action.replaceAll(" ", "_");
			ret = action.toUpperCase();
		}
		return ret;
	}
	
	private String getActionStartTag(String action) {
		return getActionTag(action) + UNDERSCORE_START;
	}
	
	private String getActionStopTag(String action) {
		return getActionTag(action) + UNDERSCORE_STOP;
	}
	
	private String getStringFromList(List<String> list)	{
		String ret = "";
		if ((list != null) && (list.size() > 0)) {
			for (String l : list)
			{
				ret = ret + l + SEPARATOR;
			}
		}
		return ret;
	}
	
	private void storeLatency(String actionName, long timePeriod) {
		Intent i = new Intent();
		i.setAction(CUSTOM_INTENT_ACTION);
		i.putExtra(APPLICATION_ACTION, OVERHEAD + getActionTag(actionName));
		i.putExtra(APPLICATION_DATA, String.valueOf(timePeriod/MILLION));		// store latency in mill-seconds
	}
}
