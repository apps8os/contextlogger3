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
package org.apps8os.contextlogger3.android.clientframework;

import java.util.List;

import org.apps8os.contextlogger3.android.clientframework.probe.GoogleActivityRecognitionProbe;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

public class ActivityRecognitionService extends IntentService {

	/**
	 * Similar activity type like {@link DetectedActivity }
	 */
	static enum ActivityType {
		IN_VEHICLE, ON_BICYCLE, ON_FOOT, STILL, TILTING, UNKNOWN;

		public static String getActivityTypeByReference(final int activityTypeReference) {
			switch (activityTypeReference) {
				case DetectedActivity.IN_VEHICLE:
					return ActivityType.IN_VEHICLE.name();
				case DetectedActivity.ON_BICYCLE:
					return ActivityType.ON_BICYCLE.name();
				case DetectedActivity.ON_FOOT:
					return ActivityType.ON_FOOT.name();
				case DetectedActivity.STILL:
					return ActivityType.STILL.name();
				case DetectedActivity.TILTING:
					return ActivityType.TILTING.name();
				case DetectedActivity.UNKNOWN:
				default:
					return ActivityType.UNKNOWN.name();
			}
		}
	}
	
	public ActivityRecognitionService() {
		super("ActivityRecognitionService");
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		if (ActivityRecognitionResult.hasResult(intent)) {
			Log.i(ActivityRecognitionService.class.getCanonicalName(), "result");
			handleActivityRecognitionResult(ActivityRecognitionResult.extractResult(intent));
		}
	}

	private void handleActivityRecognitionResult(final ActivityRecognitionResult result) {
		Bundle data = new Bundle();
		// Get the most probable activity
        DetectedActivity mostProbableActivity = result.getMostProbableActivity();
        String activityName = ActivityType.getActivityTypeByReference(mostProbableActivity.getType());
        data.putString("Activity", activityName);
        data.putString("ActivityConfidence", String.valueOf(mostProbableActivity.getConfidence()));
        try {
            List<DetectedActivity> daList = result.getProbableActivities();
            if((daList != null) && (!daList.isEmpty())) {
            	JSONArray jsonArray = new JSONArray();
            	for (DetectedActivity da : daList) {
            		JSONObject daObject = new JSONObject();
            		daObject.put("Activity",  ActivityType.getActivityTypeByReference(da.getType()));
            		daObject.put("ActivityConfidence", String.valueOf(da.getConfidence()));
            		jsonArray.put(daObject);
            	}
            	if(jsonArray.length() > 0) {
            		 data.putString("ProbableActivities", jsonArray.toString());
            	}
            }
       	
        } catch (Exception e) {
        	Log.e(ActivityRecognitionService.class.getCanonicalName(), "error: ", e);
            String probableActivities = TextUtils.join("|", result.getProbableActivities());        	
            data.putString("ProbableActivities", probableActivities);
        }
        data.putString("timestamp", String.valueOf(result.getTime()));
		Intent intent = new Intent(GoogleActivityRecognitionProbe.INTENT_ACTION);
		intent.putExtras(data);
		LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
	}
}
