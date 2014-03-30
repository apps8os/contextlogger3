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

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apps8os.logger.android.manager.AppManager;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

@TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
public class NfcMainActivity extends AbstractNfcAdapterActivity {
	
	private List<HashMap<String, String>> mNfcDemoList = null;
	
	@Override
	protected String getClassName() {
		return NfcMainActivity.class.getSimpleName();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent it = getIntent();
		resolveNFC(it);
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		setIntent(intent);
		resolveNFC(intent);
	}

	@Override
	void handleNfcTagId(String nfcId) {
		if(TextUtils.isEmpty(nfcId)) {
			
			return;
		}
		
		if(mNfcDemoList == null) {
			try {
				mNfcDemoList = AppManager.getLoggerNfcDemoList(getApplicationContext(), 
																R.string.nfc_demo_list);
			} catch (Exception e) {
				Log.e(getClassName(), "NFC demo list error: ", e);
			}
		}
		
		HashMap<String, String> matchData = null;
		for(HashMap<String, String> data : mNfcDemoList) {
			Set<String> keys = data.keySet();
			for (String s : keys) {
				if(nfcId.equals(data.get(s))) {
					matchData = data;
					break;
				}
			}
		}
		if(matchData != null) {
			final String tagName = matchData.get(getString(R.string.lang_cn).equals(Locale.getDefault().getISO3Language()) 
					? getString(R.string.lang_cn) : getString(R.string.lang_en));

			Log.d(getClassName(), tagName + "");

			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					AppManager.sendNfcTagEvent(getApplicationContext(), tagName);
				}
			}, 200L);			
		}
	}

	@Override
	public void onBackStackChanged() {
		final int backStackCount = getBackStackEntryCount();
		if(backStackCount > 1) {
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					setNfcFeatureEnable(false);
				}
			}, 800L);
		} else if(backStackCount == 1) {
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					if(hasNfcSupport() && !isNfcDispatched()) {
						setNfcFeatureEnable(true);						
					}
				}
			}, 800L);
		}
		super.onBackStackChanged();
	}
}