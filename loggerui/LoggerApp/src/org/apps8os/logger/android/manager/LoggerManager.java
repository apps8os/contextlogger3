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

import org.apps8os.contextlogger3.android.Postman;
import org.apps8os.contextlogger3.android.pipeline.MainPipeline;
import org.apps8os.contextlogger3.android.pipeline.MainPipeline.ContextLogger3ServiceConnection;
import org.apps8os.contextlogger3.android.probe.AppProbe;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.Toast;

/**
 * Helper methods for the application that 
 * handle the record framework.
 */
final class LoggerManager {

	private static boolean mIsRunning = false;
	
	private LoggerManager(){
	}

	public static void init(final Context context) {
		startLogger(context);
	}
	
	/**
	 * Start the logging
	 * 
	 * @param context
	 */
	private static void startLogger(final Context context) {
		toggleService(context, true);
	}
	
	/**
	 * Toggle the logging service
	 * 
	 * @param context
	 * @param enabled
	 */
	public static void toggleService(Context context, final boolean enabled) {
//		invokeLoggerService(context, enabled ? MainPipeline.ACTION_ENABLE : MainPipeline.ACTION_DISABLE);
	}
	
    private static void invokeLoggerService(Context context, final String action) {
        Intent archiveIntent = new Intent(context, MainPipeline.class);
        archiveIntent.setAction(action);
        context.startService(archiveIntent);                
    }
	
	/**
	 * Ask to export the data from internal memory to SD-card
	 * 
	 * @param context
	 */
	public static void exportData(final Context context) {
		if(context == null) return;
		MainPipeline mp = ContextLogger3ServiceConnection.getInstance().getMainPipeline();
		if(mp != null) {
			if (mp.isEnabled()) {
				mp.onRun(MainPipeline.ACTION_ARCHIVE, null);

				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(context, "Archived!", Toast.LENGTH_SHORT).show();
					}
				}, 500L);
			} else {
				Toast.makeText(context, "Pipeline is not enabled.", Toast.LENGTH_SHORT).show();
			}					
		} else {
			Toast.makeText(context, "Pipeline is not available.", Toast.LENGTH_SHORT).show();
		}
	}
	
	/**
	 * Check if the logging is running
	 * 
	 * @param context
	 * @return
	 */
	public static final boolean isRunning(Context context) {
//        mIsRunning = MainPipeline.isEnabled(context);                        
        return mIsRunning;
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
	
}