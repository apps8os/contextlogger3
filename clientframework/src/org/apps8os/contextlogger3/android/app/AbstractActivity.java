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

package org.apps8os.contextlogger3.android.app;

import org.apps8os.contextlogger3.android.clientframework.pipeline.MainPipeline;
import org.holoeverywhere.app.Activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import edu.mit.media.funf.FunfManager;


/**
 * Generic abstract Activity that connect to FunfManager ({@link Serivce}).
 * 
 * @param <T>
 */
public abstract class AbstractActivity <T extends Activity> extends Activity implements ServiceConnection {

	private FunfManager mFunfManager = null;
	private MainPipeline mMainPipeline = null;
	private boolean mIsBound = false;
	
	public boolean isFunfBound() {
		return mIsBound;
	}
	
	public MainPipeline getMainPipeline() {
		return mMainPipeline;
	}
	
	
	@Override
	protected void onStart() {
		super.onStart();
		// Bind to the service, to create the connection with FunfManager
		 bindService(new Intent(getApplicationContext(), FunfManager.class), this, BIND_AUTO_CREATE);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		unbindService(this);
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		mFunfManager = ((FunfManager.LocalBinder)service).getManager();
		mIsBound = true;
		mFunfManager.enablePipeline(MainPipeline.getPipelineName());
		// get pipeline instance
		mMainPipeline = (MainPipeline) mFunfManager.getRegisteredPipeline(MainPipeline.getPipelineName());
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		disconnect();
	}

	private void disconnect() {
		mFunfManager = null;
		mIsBound = false;
	}
}
