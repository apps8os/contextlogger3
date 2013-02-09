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

package org.sizzlelab.contextlogger.android.travel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

import org.json.JSONObject;
import org.sizzlelab.contextlogger.android.R;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

public class TravelApp extends Application {

	private static TravelApp mInstance = null;
	private static final String PREFS_INDICATOR = "TravelPrefs";
	
	public static TravelApp getInstance(){
		return mInstance;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		mInstance = this;
	}

	public synchronized String getTravelModes(){
		SharedPreferences prefs = getSharedPreferences(PREFS_INDICATOR, Context.MODE_PRIVATE);
		return prefs.getString("travelMode", null);
	}
	
	public synchronized void saveTravelMode(String mode) {
		SharedPreferences prefs = getSharedPreferences(PREFS_INDICATOR, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("travelMode", mode);
		editor.commit();
	}

	public synchronized String getTravelPurposes(){
		SharedPreferences prefs = getSharedPreferences(PREFS_INDICATOR, Context.MODE_PRIVATE);
		return prefs.getString("travelPurpose", null);
	}
	
	public synchronized void saveTravelPurpose(String purpose) {
		SharedPreferences prefs = getSharedPreferences(PREFS_INDICATOR, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("travelPurpose", purpose);
		editor.commit();
	}
	
	public synchronized String getTravelDestinations(){
		SharedPreferences prefs = getSharedPreferences(PREFS_INDICATOR, Context.MODE_PRIVATE);
		return prefs.getString("travelDestination", null);
	}
	
	public synchronized void saveTravelDestination(String destination) {
		SharedPreferences prefs = getSharedPreferences(PREFS_INDICATOR, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("travelDestination", destination);
		editor.commit();
	}
	
	public synchronized String getTravelReasons(){
		SharedPreferences prefs = getSharedPreferences(PREFS_INDICATOR, Context.MODE_PRIVATE);
		return prefs.getString("travelReason", null);
	}
	
	public synchronized void saveTravelReason(String reason) {
		SharedPreferences prefs = getSharedPreferences(PREFS_INDICATOR, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("travelReason", reason);
		editor.commit();
	}
	
	public synchronized int getTravelStatus(){
		SharedPreferences prefs = getSharedPreferences(PREFS_INDICATOR, Context.MODE_PRIVATE);
		return prefs.getInt("travelStatus", -1);
	}
	
	public synchronized void saveTravelStauts(int travelStatusEnumIndex) {
		SharedPreferences prefs = getSharedPreferences(PREFS_INDICATOR, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt("travelStatus", travelStatusEnumIndex);
		editor.commit();
	}	

	public synchronized String getTravelSelectionInfo(){
		SharedPreferences prefs = getSharedPreferences(PREFS_INDICATOR, Context.MODE_PRIVATE);
		return prefs.getString("travelSelectionInfo", null);
	}
	
	public synchronized void saveTravelSelectionInfo(final String info) {
		SharedPreferences prefs = getSharedPreferences(PREFS_INDICATOR, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("travelSelectionInfo", info);
		editor.commit();
	}		
	
	public synchronized String getParkingInfo(){
		SharedPreferences prefs = getSharedPreferences(PREFS_INDICATOR, Context.MODE_PRIVATE);
		return prefs.getString("parkInfo", null);
	}
	
	public synchronized void saveParkingInfo(final String info) {
		SharedPreferences prefs = getSharedPreferences(PREFS_INDICATOR, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("parkInfo", info);
		editor.commit();
	}	
	
	public synchronized boolean isCheatingModeOn(){
		SharedPreferences prefs = getSharedPreferences(PREFS_INDICATOR, Context.MODE_PRIVATE);
		return prefs.getBoolean("cheatingMode", false);
	}
	
	public synchronized void saveCheatingMode(final boolean cheating){
		SharedPreferences prefs = getSharedPreferences(PREFS_INDICATOR, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean("cheatingMode", cheating);
		editor.commit();
	}
	
	public synchronized String getParkingPayment(){
		SharedPreferences prefs = getSharedPreferences(PREFS_INDICATOR, Context.MODE_PRIVATE);
		return prefs.getString("parkingPayment", null);
	}
	
	public synchronized void saveParkingPayment(String payment) {
		SharedPreferences prefs = getSharedPreferences(PREFS_INDICATOR, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("parkingPayment", payment);
		editor.commit();
	}
	
	public synchronized String getParkingPrice(){
		SharedPreferences prefs = getSharedPreferences(PREFS_INDICATOR, Context.MODE_PRIVATE);
		return prefs.getString("parkingPrice", null);
	}
	
	public synchronized void saveParkingPrice(String price) {
		SharedPreferences prefs = getSharedPreferences(PREFS_INDICATOR, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("parkingPrice", price);
		editor.commit();
	}
	
	public synchronized String getParkingPlace(){
		SharedPreferences prefs = getSharedPreferences(PREFS_INDICATOR, Context.MODE_PRIVATE);
		return prefs.getString("parkingPlace", null);
	}
	
	public synchronized void saveParkingPlace(String place) {
		SharedPreferences prefs = getSharedPreferences(PREFS_INDICATOR, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("parkingPlace", place);
		editor.commit();
	}
	public void sendLoggingEventBoradcast(final Intent intent){
		if(isCheatingModeOn()) return;
		if(intent != null){
			sendBroadcast(intent);
		}
	}
	
	public static ArrayList<String> getStringToList(final String array){
		ArrayList<String> list = null;
		if(TextUtils.isEmpty(array)){
			return null;
		}
		try{
			String[] strArray = array.split(";");
			list = new ArrayList<String>(Arrays.asList(strArray));
		}catch(Exception e){
			return list;
		}
		return list;
	}
	
	
	public synchronized void startFadeInAnimation(final View view){
		if(view != null){
			Animation anim = AnimationUtils.loadAnimation(this, R.anim.tween);
			view.startAnimation(anim);
		}
	}
	
	public synchronized void clearAnimation(final View view){
		if(view != null){
			view.clearAnimation();
		}
	}
	
	public static final JSONObject getFormattedJsonObject(HashMap<String, String> data, String msgHeader){
		try{
			JSONObject ret = new JSONObject();		
			JSONObject jdata = new JSONObject();
			Set<String> keys = data.keySet();
			for(String k : keys){
				jdata.put(k, data.get(k));
			}
			if(TextUtils.isEmpty(msgHeader)){
				return jdata;				
			}else{
				ret.put(msgHeader, jdata);
				return ret;
			}
		}catch(Exception e){
		}
		return null;
	}
	
	public void showToastMessage(final int msgResId){
		Toast t = Toast.makeText(getApplicationContext(), msgResId, Toast.LENGTH_LONG);
		t.setGravity(Gravity.CENTER, 0, 0);
		t.show();
	}

	public void showToastMessage(final String msg){
		Toast t = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG);
		t.setGravity(Gravity.CENTER, 0, 0);
		t.show();
	}
}
