package org.apps8os.logger.android;
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
import java.util.Locale;

import org.apps8os.logger.android.manager.AppManager;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;

import fi.aalto.chaow.android.app.BaseFragmentActivity;
import fi.aalto.chaow.android.app.BaseFragmentActivity.OnSupportFragmentListener;

/**
 * 
 * Main activity (starting point) for the application.
 * 
 * @author Chao Wei
 *
 */
public class MainActivity extends BaseFragmentActivity implements OnSupportFragmentListener, 
																  OnBackStackChangedListener{

	private ActionBar mActionBar = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getWindow().setFormat(PixelFormat.RGBA_8888); 
		// update the locale
		final String lan = AppManager.getCurrentLocaleLanguage();
		if(!TextUtils.isEmpty(lan)){
			LoggerApp.getInstance().updateLocale(new Locale(lan));			
		}
		AppManager.refreshLocale();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_main);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		getSupportFragmentManager().addOnBackStackChangedListener(this);
		setupActionBar();
		// root fragment
		onFragmentChanged(R.layout.frag_logger_panel, null);
	}

	private void setupActionBar(){
	    // Set up the action bar.
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowTitleEnabled(true);
        mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME 
        							| ActionBar.DISPLAY_USE_LOGO 
        							| ActionBar.DISPLAY_SHOW_TITLE);
        mActionBar.setNavigationMode(ActionBar.DISPLAY_SHOW_CUSTOM);
        mActionBar.setHomeButtonEnabled(false);
        mActionBar.setDisplayShowCustomEnabled(true);
        if(AppManager.isLowDensityDevice(getApplicationContext())){
            mActionBar.setDisplayShowTitleEnabled(false);        	
        }
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		final int itemId = item.getItemId();
		if(itemId == android.R.id.home){
			popBackStack();
			return true;
		}
		return super.onMenuItemSelected(featureId, item);
	}
	
	@Override
	public void onBackStackChanged() {
		final int backStackCount = getBackStackEntryCount();
		// if the current fragment is root, so change the action bar
		if(backStackCount == 1){
			 mActionBar.setHomeButtonEnabled(false);
			 mActionBar.setDisplayHomeAsUpEnabled(false);
			 mActionBar.getCustomView().setVisibility(View.VISIBLE);
		}else if(getBackStackEntryCount() == 0){
			// no content shown, so quit
			finish();
		}
	}

	@Override
	public void onFragmentChanged(int layoutResId, Bundle bundle) {
		
		Fragment fragment = null;
		if(layoutResId == R.layout.frag_logger_panel) {
			fragment = new LoggerPanelFragment();
		} else if (layoutResId == R.layout.frag_logger_history) {
			fragment = new LoggerHistoryFragment();
		}
		
		if(fragment != null){
			if(bundle != null){
				fragment.setArguments(bundle);
			}
			FragmentTransaction transaction = getSupportFragmentTransaction();
			transaction.replace(R.id.screen_container, fragment, fragment.getTag());
			// Add to this transaction into BackStack
			transaction.addToBackStack(fragment.getTag());
			transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			// Commit this transaction
			transaction.commit();
		}
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	    super.onConfigurationChanged(newConfig);
	    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}
	
}
