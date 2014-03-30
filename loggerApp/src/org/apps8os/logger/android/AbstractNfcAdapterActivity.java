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

import java.io.ByteArrayInputStream;
import java.io.IOException;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

@TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
public abstract class AbstractNfcAdapterActivity extends MainActivity {

	private NfcAdapter mNfcAdapter = null;
	private PendingIntent mPendingIntent = null;
	private boolean mNfcDispatched = false;

	private boolean mInForground = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initializeNfcFeature();
	}
	 
	private void initializeNfcFeature() {
		mNfcAdapter = NfcAdapter.getDefaultAdapter(getApplicationContext());
		
		if(mNfcAdapter == null) return;
		
		Intent intent = new Intent(getApplicationContext(), getClass());
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
				| Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_NO_HISTORY);
		mPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);		
	}
	
	void resolveNFC(Intent intent) {
		if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction()) ||
				NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
			ByteArrayInputStream bin = new ByteArrayInputStream(
					intent.getByteArrayExtra(NfcAdapter.EXTRA_ID));
			String id = "";
			int c;
			while ((c = bin.read()) != -1) {
				if (c < 16)
					id += "0";
				id += Integer.toString(c, 16);
			}
			try {
				bin.close();
			} catch (IOException o) {
			}
			Log.d(getClassName(), "NFC tag id: " + id);
			if(!TextUtils.isEmpty(id)) { 
				LoggerApp.getInstance().showToastMessage(String.valueOf("NFC tag id: " + id));
				handleNfcTagId(id);
			}
		}
	}
	
	abstract void handleNfcTagId(String nfcId);
	
    @Override
    protected void onResume() {
    	mInForground = true;
        super.onResume();
    	setNfcFeatureEnable(true);
    }
	
    @Override
    protected void onPause() {
    	mInForground = false;
    	super.onPause();
    	setNfcFeatureEnable(false);
    }
    
    void setNfcFeatureEnable(boolean enable) {
    	if(mNfcAdapter == null) return;
    	
    	if(!mInForground) return;
    	
    	if(enable) {
            if(mNfcAdapter.isEnabled()) {
                mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);    
                mNfcDispatched = true;
    			Log.d(getClassName(), "NFC enable.");
            } else {
            	// TODO pop up dialog
            }
    	} else {
        	if(mNfcAdapter.isEnabled()) {
        		mNfcAdapter.disableForegroundDispatch(this);
    			mNfcDispatched = false;
    			Log.d(getClassName(), "NFC disable.");
        	}
    	}
    }
    
    final boolean isNfcDispatched() {
    	return mNfcDispatched;
    }
    
    final boolean hasNfcSupport() {
    	return (mNfcAdapter != null);
    }
}