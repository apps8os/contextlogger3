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

import org.apps8os.logger.android.app.BaseFragmentActivity.OnSupportFragmentListener;
import org.apps8os.logger.android.manager.AppManager;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;



/**
 * 
 * Main activity (starting point) for the application.
 * 
 * @author Chao Wei
 *
 */
public class MainActivity extends AbstractMainActivity implements OnSupportFragmentListener {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// update the locale
		final String lan = AppManager.getCurrentLocaleLanguage();
		if(!TextUtils.isEmpty(lan)){
			LoggerApp.getInstance().updateLocale(new Locale(lan));			
		}
		AppManager.refreshLocale();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_main);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setupActionBar();
		// root fragment
		onFragmentChanged(R.layout.frag_logger_panel, null);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		final int itemId = item.getItemId();
		if (itemId == android.R.id.home) {
			popBackStack();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onFragmentChanged(int layoutResId, Bundle bundle) {
		if(layoutResId == R.layout.frag_logger_panel) {
			changeFragment(R.id.screen_container, new LoggerPanelFragment(), 
					LoggerPanelFragment.class.getSimpleName(), bundle, true);
		} else if (layoutResId == R.layout.frag_logger_history) {
			changeFragment(R.id.screen_container, new LoggerHistoryFragment(), 
					LoggerHistoryFragment.class.getSimpleName(), bundle, true);
		} else if (layoutResId == R.layout.frag_logger_history2) {
			changeFragment(R.id.screen_container, new LoggerHistoryFragment2(), 
					LoggerHistoryFragment2.class.getSimpleName(), bundle, true);
		}
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	    super.onConfigurationChanged(newConfig);
	    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}

	@Override
	protected String getClassName() {
		return MainActivity.class.getSimpleName();
	}
	
}