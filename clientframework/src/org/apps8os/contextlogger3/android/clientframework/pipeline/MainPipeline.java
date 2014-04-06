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

package org.apps8os.contextlogger3.android.clientframework.pipeline;

import java.util.Map;

import org.apps8os.contextlogger3.android.clientframework.probe.AppProbe;
import org.apps8os.contextlogger3.android.clientframework.probe.GoogleActivityRecognitionProbe;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.google.gson.IJsonObject;
import com.google.gson.JsonElement;

import edu.mit.media.funf.FunfManager;
import edu.mit.media.funf.pipeline.BasicPipeline;
import edu.mit.media.funf.probe.Probe.DataListener;

public class MainPipeline extends BasicPipeline {

	public static final String getPipelineName() {
		return "default";
	}

	private AppProbe mAppProb = null;
	private GoogleActivityRecognitionProbe mGoogleActivityRecognitionProbe = null;
	
	static class CustomprobeDataListener implements DataListener {

		@Override
		public void onDataReceived(IJsonObject probeConfig, IJsonObject data) {
		}

		@Override
		public void onDataCompleted(IJsonObject probeConfig, JsonElement checkpoint) {
		}
	}
	
	String getClassName() {
		return MainPipeline.class.getSimpleName();
	}
	
	@Override
	public void onCreate(FunfManager manager) {
		super.onCreate(manager);

		Map<String, String> preferencesConfig = manager.getConfigStringsFromPreferences();
		Log.d(getClassName(), "preference");
		for (Map.Entry<String, String> entry : preferencesConfig.entrySet()) {
			Log.d(getClassName(), "-----");
			Log.d(getClassName(), "" + entry.getKey() + "/" + entry.getValue());
			Log.d(getClassName(), "-----");
		}
		
		if(preferencesConfig.isEmpty()) {
			Map<String, String> metadataConfig = manager.getConfigStringsFromMetadata();
			Log.d(getClassName(), "metadata");
			for (Map.Entry<String, String> entry : metadataConfig.entrySet()) {
				Log.d(getClassName(), "-----");
				Log.d(getClassName(), "" + entry.getKey() + "/" + entry.getValue());
				Log.d(getClassName(), "-----");
			}		
			
			mAppProb = manager.getGson().fromJson(metadataConfig.get(MainPipeline.getPipelineName()), 
													AppProbe.class);
			mAppProb.registerListener(this);
			
			if(Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD) {
				// TODO load configuration?
				mGoogleActivityRecognitionProbe = manager.getGson().fromJson(metadataConfig.get(MainPipeline.getPipelineName()), 
														GoogleActivityRecognitionProbe.class);
				
				if(mGoogleActivityRecognitionProbe != null) {
					mGoogleActivityRecognitionProbe.registerListener(this);
				} else {
					Log.d(getClassName(), "unable to create Google Activity Recognition Probe");
				}
			}
		} else {
			mAppProb = manager.getGson().fromJson(preferencesConfig.get(MainPipeline.getPipelineName()),
					AppProbe.class);
			mAppProb.registerListener(this);

			if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD) {
				// TODO load configuration?
				mGoogleActivityRecognitionProbe = manager.getGson().fromJson(preferencesConfig.get(MainPipeline.getPipelineName()),
						GoogleActivityRecognitionProbe.class);

				if (mGoogleActivityRecognitionProbe != null) {
					mGoogleActivityRecognitionProbe.registerListener(this);
				} else {
					Log.d(getClassName(), "unable to create Google Activity Recognition Probe");
				}
			}
		}
	}
	
	
	/**
	 * Service connection 
	 *
	 */
	public static final class ContextLogger3ServiceConnection implements ServiceConnection {

		private static ContextLogger3ServiceConnection mConnection = null;

		public static ContextLogger3ServiceConnection getInstance() {
			if(mConnection == null) {
				mConnection = new ContextLogger3ServiceConnection();
			}
			return mConnection;
		}
		
		private FunfManager mFunfManager = null;
		private MainPipeline mMainPipeline = null;
		
		private ContextLogger3ServiceConnection() {
		}
		
		public MainPipeline getMainPipeline() {
			return mMainPipeline;
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.v(getClassName(), "initialize");
			mFunfManager = ((FunfManager.LocalBinder)service).getManager();
			if(mFunfManager != null) {
				// enable pipeline
				mFunfManager.enablePipeline(MainPipeline.getPipelineName());
				
				// get pipeline instance
				mMainPipeline = (MainPipeline) mFunfManager.getRegisteredPipeline(MainPipeline.getPipelineName());
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			destory();
		}

		String getClassName() {
			return ContextLogger3ServiceConnection.class.getSimpleName();
		}
		
		void destory() {
			mFunfManager = null;
			mMainPipeline = null;
		}
	}
	
	
}
