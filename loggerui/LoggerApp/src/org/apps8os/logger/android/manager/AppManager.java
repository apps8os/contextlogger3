package org.apps8os.logger.android.manager;

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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Locale;

import org.apps8os.logger.android.LoggerApp;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;
import android.util.DisplayMetrics;

/**
 * Helper methods for the application ONLY on some 
 * general concerns such as reading the configuration file,
 * and localization stuff.
 * 
 * @author Chao Wei
 *
 */
public final class AppManager {

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
	 * <p><b>To be changed still.</b></p>
	 * 
	 * @param context
	 * @param jsonFileNameResId
	 * @param eventTagNameResId
	 * @param nameResId
	 * @return
	 * @throws JSONException
	 * @throws IOException
	 */
	@Deprecated
	public static final ArrayList<String> getEventTagsFromAsset(final Context context, final int jsonFileNameResId, 
									final int eventTagNameResId, final int nameResId) throws JSONException, IOException{
		return parsingJsonToList(getJSONString(context, jsonFileNameResId), 
						getString(context, eventTagNameResId), getString(context, nameResId));
	}
	
	private static final ArrayList<String> parsingJsonToList(final String jsonString, 
										final String eventTagName, final String name) throws JSONException{
		if(TextUtils.isEmpty(jsonString) || TextUtils.isEmpty(eventTagName) || TextUtils.isEmpty(name)){
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
	
	private static final String getJSONString(final Context context, final int jsonFileNameResId) throws IOException{
		InputStream is = null;
		String jsonString = null;
		try {
			is = context.getAssets().open(getString(context, jsonFileNameResId));
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
		return jsonString;
	}
	
	private static final String getString(final Context context, final int resId){
		return context.getResources().getString(resId);
	}

	/**
	 * Check if the device OS is Honeycomb (API level 11 or 12).
	 * 
	 * @return
	 */
	public static final boolean isHoneycomb(){
		boolean ret = false;
		final int sdkInt = android.os.Build.VERSION.SDK_INT;
		try{
			ret = ((sdkInt == android.os.Build.VERSION_CODES.HONEYCOMB_MR2)
					|| (sdkInt == android.os.Build.VERSION_CODES.HONEYCOMB_MR1));
		} catch (Exception e){ 
		}
		return ret;
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
	
}
