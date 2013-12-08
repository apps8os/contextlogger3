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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apps8os.logger.android.LoggerApp;
import org.apps8os.logger.android.MainActivity;
import org.apps8os.logger.android.NfcMainActivity;
import org.apps8os.logger.android.R;
import org.apps8os.logger.android.model.ActionEvent;
import org.apps8os.logger.android.storage.ActionEventCursor;
import org.apps8os.logger.android.util.AndroidVersionHelper;
import org.apps8os.logger.android.util.IOUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

/**
 * Helper methods for the application ONLY on some 
 * general concerns such as reading the configuration file,
 * and localization stuff.
 * 
 * @author Chao Wei
 *
 */
public final class AppManager extends LoggerWrapper {

	private static final String TAG = AppManager.class.getSimpleName();
	
	private AppManager(){
	}
	
	private static String mISO3Lang = null;;
	
	private static int mPreCount = 0;

	
	/**
	 * Get the total number of the pre-defined actions (activities)
	 * 
	 * @return
	 */
	public static final int getPreCount(){
		return mPreCount;
	}
	
	/**
	 * Refresh the local for the application 
	 * 
	 */
	public static void refreshLocale(){
		if(!TextUtils.isEmpty(mISO3Lang)){
			mISO3Lang = null;
		}
		final String setting = LoggerApp.getInstance().getLanguageSetting();
		if(!TextUtils.isEmpty(setting)){
			mISO3Lang = setting;
		} else {
			mISO3Lang = Locale.getDefault().getISO3Language();
		}
	}
	
	/**
	 * Get the current local language
	 * 
	 * @return
	 */
	public static final String getCurrentLocaleLanguage(){
		return mISO3Lang;
	}
	
	/**
	 * Get the pre-defined file internal resource id 
	 * of the action events (activities) based
	 * on the current application's local
	 * 
	 * @return
	 */
	public static final int getEventTagsJsonFileResId(){
		return LoggerApp.getInstance().getEventTagsJSONFile(mISO3Lang);
	}
	
	/**
	 * Get the action events (activities) from the pre-defined file.
	 * 
	 * @param context
	 * @param jsonFileNameResId
	 * @param eventTagNameResId
	 * @param nameResId
	 * @return
	 * @throws JSONException
	 * @throws IOException
	 */
	public static final ArrayList<String> getEventTagsFromAsset(final Context context, 
																final int jsonFileNameResId, 
																final int eventTagNameResId, 
																final int nameResId) 
																		throws JSONException, IOException {
		return parsingJsonToList(IOUtil.getJSONString(context.getApplicationContext().getAssets().open(getString(context, jsonFileNameResId))), 
						getString(context, eventTagNameResId), getString(context, nameResId));
	}
	
	private static final ArrayList<String> parsingJsonToList(final String jsonString, 
															final String eventTagName, 
															final String name) throws JSONException {
		if(TextUtils.isEmpty(jsonString) 
				|| TextUtils.isEmpty(eventTagName) 
				|| TextUtils.isEmpty(name)){
			throw new IllegalArgumentException("Invalid parameters");
		}
		ArrayList<String> array = null;
		if(jsonString != null){
			JSONObject jobject = new JSONObject(jsonString);
			if(!jobject.isNull(eventTagName)){
				JSONArray jarray = jobject.getJSONArray(eventTagName);					
				array = new ArrayList<String>();
				mPreCount = jarray.length();
				for(int i = 0; i < jarray.length(); i++){
					JSONObject o = jarray.getJSONObject(i);
					if(!o.isNull(name)){
						array.add(o.getString(name));
					}
				}
			}
		}
		return array;
	}

	public static List<HashMap<String, String>> getLoggerNfcDemoList(final Context context,
												final int jsonFileNameResId) throws IOException, JSONException {
		JSONObject jsonData = new JSONObject(IOUtil.getJSONString(context.getApplicationContext().getAssets().open(getString(context, jsonFileNameResId))));
		if(jsonData.has("contextLogger3NfcDemoList")) {
			ArrayList<HashMap<String, String>> ret = new ArrayList<HashMap<String,String>>();
			JSONArray jsonArray = jsonData.getJSONArray("contextLogger3NfcDemoList");
			for(int i = 0; i < jsonArray.length(); i++) {
				HashMap<String, String> data = new HashMap<String, String>();
				JSONObject jo = jsonArray.getJSONObject(i);
				Iterator<?> keys = jo.keys();
		        while(keys.hasNext()) {
		            String key = (String)keys.next();
					data.put(key, jo.getString(key));		            
		        }
		        ret.add(data);
			}
			return ret;
		}
		return Collections.emptyList();
	}
	
	
	private static final String getString(final Context context, final int resId){
		return context.getApplicationContext().getResources().getString(resId);
	}
	
	/**
	 * Check if the device has the smaller screen, such as HTC Widefire, etc.
	 * 
	 * @param context
	 * @return
	 */
	public static final boolean isLowDensityDevice(Context context){
		return (context.getResources().getDisplayMetrics().widthPixels <= 300) 
				&& (context.getResources().getDisplayMetrics().densityDpi == DisplayMetrics.DENSITY_LOW);
	}
	
	public static ArrayList<ActionEvent> getAllHistoryEvents() {
		return getEvents(true);
	}
	
	public static ArrayList<ActionEvent> getAllLiveEvents() {
		return getEvents(false);
	}

	private static ArrayList<ActionEvent> getEvents(boolean history) {
		ArrayList<ActionEvent> aeList = new ArrayList<ActionEvent>();
		ActionEventCursor aec = null;
		try {
			aec =  getActionEventDatabase().getAllActionEventCursor(history);			
			while(aec.moveToNext()) {
				aeList.add(new ActionEvent(aec.getActionName(), aec.getStartTimestamp(), 
											aec.getBreakTimestamp(), aec.getEventState(),
											aec.getNoteContent(), aec.isHistory(), 
											aec.getStartDelay(), aec.getBreakDelay()));
			}	
		} catch (Exception e) {
			Log.e(TAG, "we got error: ", e);
		} finally {
			if(aec != null) {
				try {
					aec.close();
				} catch (Exception e) {
					Log.e(TAG, "we got error: ", e);		
				}
			}
		}
		return aeList;
	}
	
	public static void addALiveEvent(final ActionEvent ae) {
		getActionEventDatabase().add(ae);
	}
	
	public static void updateLiveEvent(final ActionEvent ae) {
		getActionEventDatabase().updateActionEventBreak(ae);
	}
	
//	public static void addEventNote(final ActionEvent ae) {
//		getActionEventDatabase().addActionEventNote(ae);
//	}
	
	public static void sendNfcTagEvent(Context context, String eventName) {
		if(TextUtils.isEmpty(eventName)) return;
		Intent it = new Intent(AppManager.LOGGER_INTENT_FILTER);
		it.putExtra(LoggerNFCBroadcastReceiver.NFC_MESSAGE, eventName);
		LocalBroadcastManager.getInstance(context).sendBroadcast(it);
	}
	
	
	public static class LoggerNotificationBroadcastReceiver extends BroadcastReceiver {

		public static final String NOTIFICATION_MESSAGE = "notificationMessage";
		
		private static int NOTIF_LOGGER_ID = 0x911;
		private static NotificationManager mNotificationManager = null;
		private static Context mContext = null;
		
		@Override
		public void onReceive(Context context, Intent intent) {
			if(mNotificationManager == null) {
				mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);				
			}
			mContext = context;
			if(intent.hasExtra(NOTIFICATION_MESSAGE)) {
				if(intent.getBooleanExtra(NOTIFICATION_MESSAGE, false)) {
					createLoggerNotification();
				} else {
					cancelLoggerNotification();
				}				
			}
		}
		
		private void createLoggerNotification() {
			Builder notificationBuilder = new NotificationCompat.Builder(mContext);
			notificationBuilder.setContentTitle(mContext.getResources().getString(R.string.app_name));
			notificationBuilder.setContentText(mContext.getResources().getString(R.string.app_running_notification));
			notificationBuilder.setSmallIcon(R.drawable.ic_launcher);
			notificationBuilder.setOngoing(true);
			Intent notificationIntent = new Intent(mContext, 
					AndroidVersionHelper.isHoneycombAbove() ? NfcMainActivity.class : MainActivity.class);
			PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0, notificationIntent, 0);
			notificationBuilder.setContentIntent(contentIntent);
			Notification notification = notificationBuilder.build();
			mNotificationManager.notify(NOTIF_LOGGER_ID, notification);
		}
		
		private void cancelLoggerNotification() {
			mNotificationManager.cancel(NOTIF_LOGGER_ID);
		}
	}
	
	public static abstract class LoggerNFCBroadcastReceiver extends BroadcastReceiver {

		public static final String NFC_MESSAGE = "nfcMessage";
		
		private static final long NEXT_MESSAGE_DELAY = 1000L;
		
		private long mReceivingTime = -1L;
		
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.hasExtra(NFC_MESSAGE)) {
				final String eventName = intent.getStringExtra(NFC_MESSAGE);
				if(!TextUtils.isEmpty(eventName)) {
					if(mReceivingTime < 0 ) {
						mReceivingTime = System.currentTimeMillis();						
					} else {
						if((System.currentTimeMillis() - mReceivingTime) 
								< NEXT_MESSAGE_DELAY) {
							return;
						} else {
							mReceivingTime = System.currentTimeMillis();													
						}
					}
					handleNfcTagEvent(eventName);
				}				
			}
		}
		
		public abstract void handleNfcTagEvent(String eventName);
	}
	
}