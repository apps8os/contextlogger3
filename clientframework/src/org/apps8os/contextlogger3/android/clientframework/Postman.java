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

package org.apps8os.contextlogger3.android.clientframework;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

public final class Postman {

	private static Postman mPostman = null;
	
	private Postman() {
	}

	public static Postman getInstance() {
		if(mPostman == null) {
			mPostman = new Postman();
		}
		return mPostman;
	}
	
	/**
	 * Send the message out via local broadcast
	 * 
	 * @param context
	 * @param intentAction
	 * @param data
	 */
	public void send(final Context context, final String intentAction, final Bundle data) {
		if(TextUtils.isEmpty(intentAction) || (data == null) || (context == null)) return;
		
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent(intentAction);
				intent.putExtras(data);
				LocalBroadcastManager.getInstance(context).sendBroadcast(intent);			
			}
		}, 10L);
	}
}
