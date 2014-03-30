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
package org.apps8os.logger.android;

import java.util.Locale;

import org.apps8os.logger.android.app.BaseApplication;
import org.apps8os.logger.android.manager.AppManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.text.TextUtils;
import android.util.Log;

/**
 * The logger application itself, and also
 * contains some helper methods.
 *
 */
public class LoggerApp extends BaseApplication {

	public static enum ReleaseType {
		NORMAL, DEVELOPMENT, CASS, OTHER
	}
	
	private static LoggerApp mInstance = null;

	private static final String PREFS_NAME = "ClientPrefs";
	
	private static final String EVENT_TAG = "eventTag";
	
	private static final String LANGUANG_SETTING = "languageSetting";
	
	private ReleaseType mReleaseType = ReleaseType.DEVELOPMENT;

	public final boolean isCassRelease() {
		return (mReleaseType == ReleaseType.CASS);
	}
	
	public static LoggerApp getInstance(){
		return mInstance;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		mInstance = this;

		Log.i(LoggerApp.class.getSimpleName(), "Release type is " + mReleaseType.toString());
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// if the configuration (locale language setting) changed
		// we need to refresh the locale 
		final String lan = AppManager.getCurrentLocaleLanguage();
		if(!TextUtils.isEmpty(lan)){
			updateLocale(new Locale(lan));			
		}
		AppManager.refreshLocale();
	};
	
	public final int getEventTagsJSONFile(final String lan){
		if(TextUtils.isEmpty(lan)){
			return R.string.event_json_file_name_en;
		}
		if(lan.toLowerCase(Locale.getDefault()).equals(getString(R.string.lang_cn))){
			return R.string.event_json_file_name_cn;
		} else if (lan.toLowerCase(Locale.getDefault()).equals(getString(R.string.lang_fi))){
			return R.string.event_json_file_name_fi;
		} else {
			return R.string.event_json_file_name_en;
		}
	}
	
	/**
	 * Update the locale for the application
	 * 
	 * @param locale
	 */
	public void updateLocale(Locale locale){
		if(locale == null) return;
		Configuration config = new Configuration(getResources().getConfiguration());
		config.locale = locale;
		Locale.setDefault(locale);
		getBaseContext().getResources().updateConfiguration(config,  
					getBaseContext().getResources().getDisplayMetrics());
		saveLanguageSetting(locale.getISO3Language());
	}

	public synchronized String getLanguageSetting(){
		SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		return prefs.getString(LANGUANG_SETTING, null);
	}
	
	public synchronized void saveLanguageSetting(String setting){
		SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(LANGUANG_SETTING, setting);
		editor.commit();
	}
	
	public synchronized String getEventTags(){
		SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		return prefs.getString(EVENT_TAG, null);
	}
	
	public synchronized void saveEventTag(String tag) {
		SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(EVENT_TAG, tag);
		editor.commit();
	}

	@Override
	protected String getClassName() {
		return LoggerApp.class.getSimpleName();
	}
}