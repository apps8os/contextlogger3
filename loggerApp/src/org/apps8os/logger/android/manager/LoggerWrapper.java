/**
 * Copyright (c) 2013 Aalto University and the authors
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
package org.apps8os.logger.android.manager;

import org.apps8os.contextlogger3.android.clientframework.Postman;
import org.apps8os.contextlogger3.android.clientframework.probe.AppProbe;
import org.apps8os.logger.android.MainActivity;
import org.apps8os.logger.android.manager.AppManager.LoggerNotificationBroadcastReceiver;
import org.apps8os.logger.android.storage.ActionEventDatabase;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

abstract class LoggerWrapper extends CassManager {

	public static final String LOGGER_INTENT_FILTER = "Logger-Intent-Filter";
	
	private static ActionEventDatabase mActionEventDatabase = null;
	
	public static void init(final Context context) {
		new Handler(context.getMainLooper()).postDelayed(new Runnable() {
			@Override
			public void run() {
				sendLocalNotificationBroadcast(context, true);
			}
		}, 100L);

		if(mActionEventDatabase == null) {
			mActionEventDatabase = new ActionEventDatabase(context.getApplicationContext());			
		}
	}
	
	public static void createLoggerNotification(final Context context) {
		sendLocalNotificationBroadcast(context, true);
	}
	
	private static void sendLocalNotificationBroadcast(final Context context, final boolean enabled) {
		Intent it = new Intent(LOGGER_INTENT_FILTER);
		it.putExtra(LoggerNotificationBroadcastReceiver.NOTIFICATION_MESSAGE, enabled);
		LocalBroadcastManager.getInstance(context).sendBroadcast(it);
	}

	/**
	 * Toggle the logging service
	 * 
	 * @param activity
	 * @param enabled
	 */
	public static <T extends MainActivity> void toggleService(T activity, final boolean enabled) {
		sendLocalNotificationBroadcast(activity.getApplicationContext(), enabled);
		invokeLoggerService(activity, enabled);
	}
	
    private static <T extends MainActivity> void invokeLoggerService(T activity, final boolean enabled) {
    	if(activity instanceof MainActivity) {
    		activity.togglePipeline(enabled);
    	}                
    }
	
   /**
	 * Ask to export the data from internal memory to SD-card
	 * 
	 * @param activity
	 */
    public static <T extends MainActivity> void exportData(T activity) {
    	if(activity instanceof MainActivity) {
			activity.exportData();
    	} 	
    }	
	
	/**
	 * Whether FunfManager is running.
	 * 
	 * @return
	 */
    public static <T extends MainActivity> boolean isRunning(T activity) {
    	if(activity instanceof MainActivity) {
    		return activity.isRunning();
    	} 
    	return false;
    }
    
    
    /**
	 * Notify the record framework that the data has been broadcast.
	 * 
	 * @param context
	 * @param actionPayload
	 * @param data
	 */
	public static void sendEventBoradcast(Context context, final String actionPayload, final String data) {
		if(TextUtils.isEmpty(actionPayload)) return;
		
		Bundle bundle = new Bundle();
		bundle.putString("APPLICATION_ACTION", actionPayload);
		if(!TextUtils.isEmpty(data)){
			bundle.putString("APPLICATION_DATA", data);			
		}
		Postman.getInstance().send(context, AppProbe.INTENT_ACTION, bundle);
	}

	public static ActionEventDatabase getActionEventDatabase() {
		if(mActionEventDatabase == null) {
			throw new IllegalStateException("Database object must be initialized first!");
		}
		return mActionEventDatabase;
	}
	
}