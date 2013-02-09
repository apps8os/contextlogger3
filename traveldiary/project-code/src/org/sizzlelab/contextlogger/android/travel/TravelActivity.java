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
 * Chaudhary Nalin (nalin.chaudhary@aalto.fi)
 * Chao Wei (chao.wei@aalto.fi)
 */
package org.sizzlelab.contextlogger.android.travel;

import org.sizzlelab.contextlogger.android.R;
import org.sizzlelab.contextlogger.android.io.MainPipeline;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;

import fi.aalto.chaow.android.app.BaseFragmentActivity;
import fi.aalto.chaow.android.app.BaseFragmentActivity.OnSupportFragmentListener;

public class TravelActivity extends BaseFragmentActivity implements OnSupportFragmentListener, 
												OnBackStackChangedListener, OnSharedPreferenceChangeListener{

	private ActionBar mActionBar = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		getWindow().setFormat(PixelFormat.RGBA_8888); 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_act);
		// add back stack changed listener
		getSupportFragmentManager().addOnBackStackChangedListener(this);
		initUI();
		MainPipeline.getSystemPrefs(getApplicationContext()).registerOnSharedPreferenceChangeListener(this);
	}
	
	private void initUI(){
	    // Set up the action bar.
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowTitleEnabled(true);
        mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | 
        				ActionBar.DISPLAY_USE_LOGO | ActionBar.DISPLAY_SHOW_TITLE);
        mActionBar.setNavigationMode(ActionBar.DISPLAY_SHOW_CUSTOM);
        mActionBar.setHomeButtonEnabled(false);
        mActionBar.setDisplayShowCustomEnabled(true);

        onFragmentChanged(R.layout.travel_diary, null);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		final int itemId = item.getItemId();
		if(itemId == android.R.id.home){
			getSupportFragmentManager().popBackStack();
			return true;
		}
		return super.onMenuItemSelected(featureId, item);
	}
	
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if (MainPipeline.COUNT_KEY.equals(key)){
		}
	}
	
	@Override
	public void onFragmentChanged(int layoutResId, Bundle args) {
		Fragment fragment = null;
		FragmentTransaction transaction = getSupportFragmentTransaction();
		if(layoutResId == R.layout.travel_history){
			fragment = new TravelHistoryFragment();
		} else if(layoutResId == R.layout.travel_parking_panel){
			fragment = new TravelParkingPanelFragment();
		} else if(layoutResId == R.layout.travel_diary){
			fragment = new TravelPanelFragment();
		}
		if(fragment != null){
			if(args != null){
				fragment.setArguments(args);
			}
			transaction.replace(R.id.screen_container, fragment, fragment.getTag());
			// Add to this transaction into BackStack
			transaction.addToBackStack(fragment.getTag());
			transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			// Commit this transaction
			transaction.commit();
			return;
		}
	}

	@Override
	public void onBackStackChanged() {
		final int backStackCount = getSupportFragmentManager().getBackStackEntryCount();
		if(backStackCount == 1){
			 mActionBar.setHomeButtonEnabled(false);
			 mActionBar.setDisplayHomeAsUpEnabled(false);
			 mActionBar.getCustomView().setVisibility(View.VISIBLE);
		}else if(backStackCount == 0){
			// no content shown, so quit
			finish();
		}
	}
	
}