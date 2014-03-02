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

import org.apps8os.contextlogger3.android.ActivityRecognitionService;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.ActivityRecognitionClient;

import edu.mit.media.funf.config.Configurable;
import edu.mit.media.funf.probe.Probe.Description;
import edu.mit.media.funf.probe.Probe.DisplayName;
import edu.mit.media.funf.probe.Probe.RequiredPermissions;

@DisplayName("ContextLogger3 Google activity recognition probe")
@Description("Record Google activity recognition data")
@RequiredPermissions("com.google.android.gms.permission.ACTIVITY_RECOGNITION")
public class GoogleActivityRecognitionProbe extends ContextLogger3Probe implements 
										GooglePlayServicesClient.ConnectionCallbacks, 
										GooglePlayServicesClient.OnConnectionFailedListener {
	
	private ActivityRecognitionClient mActivityRecognitionClient = null;
	
	// FIXME how to load the actual value from configuration 
	@Configurable
	private int detectionInterval = 60; // unit, second
	
	public int getDetectionInterval() {
		return detectionInterval;
	}

	public void setDetectionInterval(int interval) {
		detectionInterval = interval;
	}
	
	@Override
	protected void onEnable() {
		super.onEnable();
		// TODO this is wrong way 
		// load interval from json file
		try {
			JSONArray ja = getConfigurationContent();
			if(ja != null) {
				for (int i = 0; i < ja.length(); i++) {
					JSONObject jobject = ja.getJSONObject(i);
					if(jobject.has("detectionInterval")) {
						detectionInterval = Integer.parseInt(jobject.getString("detectionInterval"));
						Log.i(getClassName(), "new detection interval is " + detectionInterval + " seconds");
						break;
					}
				}
			}
		} catch (Exception e) {
			Log.e(getClassName(), "Read detectionInterval value failed", e);
		} catch (NoSuchFieldError e) {
			Log.e(getClassName(), "Configration is unavailable", e);
		}
		registerGoogleActivityRecognitionClient();
	}
	
	/**
	 * register Google activity recognition client
	 */
	public void registerGoogleActivityRecognitionClient() {
		int resp = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getContext().getApplicationContext());
		if (resp == ConnectionResult.SUCCESS) {
			// Connect to the ActivityRecognitionService
			mActivityRecognitionClient = new ActivityRecognitionClient(getContext().getApplicationContext(), this, this);
			mActivityRecognitionClient.connect();
		} else {
			Toast.makeText(getContext(), "Please install Google Play Service.", Toast.LENGTH_SHORT).show();
			
			// TODO make an interface that allow user to install Google play service package,
			// and after that, re-register Google activity recognition client.
		}		
	}
	
	@Override
	public void onConnectionFailed(ConnectionResult result) {   
		Log.i(getClassName(), "Google Activity Recognition failed: " + result.toString());
	}

	public void onConnected(Bundle connectionHint) {
		Intent intent = new Intent(getContext().getApplicationContext(), ActivityRecognitionService.class);
		PendingIntent callbackIntent = PendingIntent.getService(getContext(), 0, 
											intent, PendingIntent.FLAG_UPDATE_CURRENT);
		mActivityRecognitionClient.requestActivityUpdates((detectionInterval * 1000L), callbackIntent);
		Log.i(getClassName(), "Google Activity Recognition connect");
	}
	
	/**
	 * unregister Google activity recognition client
	 */
	public void unregisterGoogleActivityRecognitionClient() {
		if((mActivityRecognitionClient != null) 
			&& (mActivityRecognitionClient.isConnected())) {
			mActivityRecognitionClient.disconnect();
		}
	}
	
	@Override
	public void onDisconnected() {
		Log.i(getClassName(), "Google Activity Recognition disconnected.");
		
		if(mActivityRecognitionClient != null) {
			mActivityRecognitionClient = null;
		}
	}

	public final static String INTENT_ACTION = "org.apps8os.contextlogger3.android.GoogleActivityRecognitionProbe";
	
	@Override
	String getClassName() {
		return GoogleActivityRecognitionProbe.class.getSimpleName();
	}

	@Override
	String getIntentAction() {
		return INTENT_ACTION;
	}
	
}
