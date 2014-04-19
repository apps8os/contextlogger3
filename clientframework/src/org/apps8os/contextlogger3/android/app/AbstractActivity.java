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
import org.holoeverywhere.widget.Toast;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import edu.mit.media.funf.FunfManager;


/**
 * Generic abstract Activity that connect to FunfManager ({@link Service}).
 * 
 * @param <T>
 */
public abstract class AbstractActivity <T extends Activity> extends Activity implements ServiceConnection {

	private FunfManager mFunfManager = null;
	private MainPipeline mMainPipeline = null;
	private boolean mIsBound = false;
	
	/**
	 * Whether FunfManager ({@link Service}) is bound to UI ({@link Activity}).
	 * 
	 * @return isBound ({@link Boolean})
	 */
	public boolean isFunfBound() {
		return mIsBound;
	}
	
	/**
	 * Return an instance of MainPipeline. The value can be null.
	 * 
	 * @return 
	 */
	public MainPipeline getMainPipeline() {
		return mMainPipeline;
	}
	
	/**
	 * Whether FunfManager is running.
	 * 
	 * @return
	 */
	public boolean isRunning() {
		return (mFunfManager == null) ? false : mFunfManager.isEnabled(MainPipeline.getPipelineName());
	}
	
	/**
	 * Enable/disable MainPipeline.
	 * 
	 * @param enable
	 */
	public void togglePipeline(boolean enable) {
		String pipelineName = MainPipeline.getPipelineName();
		if(enable) {
			mFunfManager.enablePipeline(pipelineName);
			// get pipeline instance again
			mMainPipeline = (MainPipeline) mFunfManager.getRegisteredPipeline(MainPipeline.getPipelineName());
		} else {
			mFunfManager.disablePipeline(pipelineName);
			mMainPipeline = null;
		}
	}
	
	/**
	 * export the data from internal memory to SD-card
	 */
	public void exportData() {
		final Context context = getApplicationContext();
		mMainPipeline = (mMainPipeline == null) ? (MainPipeline) mFunfManager.getRegisteredPipeline(MainPipeline.getPipelineName()) : mMainPipeline;
		
		if(mMainPipeline != null) {
			if (mMainPipeline.isEnabled()) {
				mMainPipeline.onRun(MainPipeline.ACTION_ARCHIVE, null);
				
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(context, "Archived!", Toast.LENGTH_SHORT).show();
					}
				}, 500L);
			} else {
				Toast.makeText(context, "Pipeline is not enabled.", Toast.LENGTH_SHORT).show();
			}					
		} else {
			Toast.makeText(context, "Pipeline is not available.", Toast.LENGTH_SHORT).show();
		}
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
		
		// get pipeline instance
		mMainPipeline = (MainPipeline) mFunfManager.getRegisteredPipeline(MainPipeline.getPipelineName());
		togglePipeline(true);
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
