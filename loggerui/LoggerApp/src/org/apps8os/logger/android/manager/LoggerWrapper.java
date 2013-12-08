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

import org.apps8os.logger.android.manager.AppManager.LoggerNotificationBroadcastReceiver;
import org.apps8os.logger.android.storage.ActionEventDatabase;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;

abstract class LoggerWrapper extends CassManager {

	public static final String LOGGER_INTENT_FILTER = "Logger-Intent-Filter";
	
	private static ActionEventDatabase mActionEventDatabase = null;
	
	public static void init(final Context context) {
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				sendLocalNotificationBroadcast(context, true);
			}
		}, 100L);

		if(mActionEventDatabase == null) {
			mActionEventDatabase = new ActionEventDatabase(context.getApplicationContext());			
		}
//		if(AndroidVersionHelper.isICSAbove()) 	return;
		LoggerManager.init(context);
	}
	
	public static void createLoggerNotification(final Context context) {
		sendLocalNotificationBroadcast(context, true);
	}
	
	private static void sendLocalNotificationBroadcast(final Context context, final boolean enabled) {
		Intent it = new Intent(LOGGER_INTENT_FILTER);
		it.putExtra(LoggerNotificationBroadcastReceiver.NOTIFICATION_MESSAGE, enabled);
		LocalBroadcastManager.getInstance(context).sendBroadcast(it);
	}
	
	public static void toggleService(Context context, final boolean enabled) {
		sendLocalNotificationBroadcast(context, enabled);
//		if(AndroidVersionHelper.isICSAbove()) 	return;
		LoggerManager.toggleService(context, enabled);
	}
	
	public static void exportData(Context context) {
		LoggerManager.exportData(context);
	}
	
	public static final boolean isRunning(Context context) {
		return LoggerManager.isRunning(context);
//		return AndroidVersionHelper.isICSAbove() ? false : LoggerManager.isRunning(context);
	}
	
	public static void sendEventBoradcast(Context context, final String actionPayload, final String data) {
//		if(AndroidVersionHelper.isICSAbove()) 	return;
		LoggerManager.sendEventBoradcast(context, actionPayload, data);
	}
	
	public static ActionEventDatabase getActionEventDatabase() {
		if(mActionEventDatabase == null) {
			throw new IllegalStateException("Database object must be initialized first!");
		}
		return mActionEventDatabase;
	}
	
}