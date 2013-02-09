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
 * Chao Wei (chao.wei@aalto.fi)
 */
package org.sizzlelab.contextlogger.android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.widget.Toast;

public class ClientApp extends Application{

	private static ClientApp mInstance = null;
	private static final String PREFS_NAME = "ClientPrefs";
	
	public static ClientApp getInstance(){
		return mInstance;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		mInstance = this;
	}
	
	public synchronized String getEventTags(){
		SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		return prefs.getString("eventTag", null);
	}
	
	public synchronized void saveEventTag(String tag) {
		SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("eventTag", tag);
		editor.commit();
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
	
	public final ArrayList<String> getEventTagsFromAsset() throws JSONException, IOException{
		InputStream is = null;
		String jsonString = null;
		ArrayList<String> array = null;
		try {
			is = getAssets().open("event_tag.json");
			Writer w = new StringWriter();
			char[] buffer = new char[1024];
			Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8000);
			int n;
			while((n = reader.read(buffer)) != -1){
				w.write(buffer, 0, n);
			}
			jsonString = w.toString();
		} catch (IOException e) {
			throw new IOException(e.getMessage());
		}finally{
			if(is != null){
				try {
					is.close();
				} catch (IOException e) {
					throw new IOException(e.getMessage());
				}
			}
		}
		if(jsonString != null){
			JSONObject jobject = new JSONObject(jsonString);
			if(!jobject.isNull("eventTags")){
				JSONArray jarray = jobject.getJSONArray("eventTags");					
				array = new ArrayList<String>();
				for(int i = 0; i < jarray.length(); i++){
					JSONObject o = jarray.getJSONObject(i);
					if(!o.isNull("name")){
						array.add(o.getString("name"));
					}
				}
			}
		}
		return array;
	}
	
}
