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

import org.apps8os.contextlogger3.android.clientframework.probe.AppProbe;
import org.apps8os.contextlogger3.android.clientframework.probe.GoogleActivityRecognitionProbe;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.IJsonObject;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import edu.mit.media.funf.FunfManager;
import edu.mit.media.funf.pipeline.BasicPipeline;
import edu.mit.media.funf.probe.Probe.DataListener;

public class MainPipeline extends BasicPipeline {

	public static final String getPipelineName() {
		return "default";
	}
	
	/**
	 * Service connection 
	 *
	 */
	public static final class ContextLogger3ServiceConnection extends AbstractProbeServiceConnection implements ServiceConnection {

		private static ContextLogger3ServiceConnection mConnection = null;

		public static ContextLogger3ServiceConnection getInstance() {
			if(mConnection == null) {
				mConnection = new ContextLogger3ServiceConnection();
			}
			return mConnection;
		}
		
		private ContextLogger3ServiceConnection() {
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			initialize(((FunfManager.LocalBinder)service).getManager());
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			destory();
		}

		@Override
		protected String getClassName() {
			return ContextLogger3ServiceConnection.class.getSimpleName();
		}
	}
	
	/**
	 * Service connection base
	 */
	static abstract class AbstractProbeServiceConnection implements DataListener {

		private FunfManager mFunfManager = null;
		private MainPipeline mMainPipeline = null;
		
		protected abstract String getClassName();
		
		// Built-in probes 
		/*
		private WifiProbe mWifiProbe = null;
		private AccelerometerSensorProbe mAccelerometerSensorProbe = null;
		private SimpleLocationProbe mLocationProbe = null;
		*/
		
		// Custom probes
		private AppProbe mAppProb = null;
		private GoogleActivityRecognitionProbe mGoogleActivityRecognitionProbe = null;
		
		@Override
		public void onDataCompleted(IJsonObject arg0, JsonElement arg1) {
			// Re-register to keep listening after probe completes.
			registerProbes();
		}

		@Override
		public void onDataReceived(IJsonObject arg0, IJsonObject arg1) {
		}
		
		public MainPipeline getMainPipeline() {
			if(mFunfManager == null) return null;
			return (MainPipeline) mFunfManager.getRegisteredPipeline(MainPipeline.getPipelineName());
		}
		
		void destory() {
			mFunfManager = null;
			mMainPipeline = null;
		}

		void initialize(FunfManager manager) {
			Log.v(getClassName(), "initialize");
			mFunfManager = manager;
			if(mFunfManager != null) {
				
				Gson gson = mFunfManager.getGson();	
				
				// Custom probes
				if(Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD) {
					// TODO load configuration file?
					mGoogleActivityRecognitionProbe = gson.fromJson(new JsonObject(), GoogleActivityRecognitionProbe.class);
				}
				mAppProb = gson.fromJson(new JsonObject(), AppProbe.class);
				
				// Built-in probes
				/*
				mWifiProbe = gson.fromJson(new JsonObject(), WifiProbe.class);
				mLocationProbe = gson.fromJson(new JsonObject(), SimpleLocationProbe.class);
				mAccelerometerSensorProbe = gson.fromJson(new JsonObject(), AccelerometerSensorProbe.class);
				*/
				
				// get pipeline instance
				mMainPipeline = (MainPipeline) mFunfManager.getRegisteredPipeline(MainPipeline.getPipelineName());
				
				// enable pipeline
				mFunfManager.enablePipeline(MainPipeline.getPipelineName());
			}
			
			registerProbes();
		}
		
		private void registerProbes() {
			Log.v(getClassName(), "register probes");
			if(mFunfManager != null) {
				// register probes
				
				// Custom probes
				mAppProb.registerListener(mMainPipeline);
				if(mGoogleActivityRecognitionProbe != null) {
					mGoogleActivityRecognitionProbe.registerListener(mMainPipeline);					
				}
				
				// Built-in probes
				/*
				mWifiProbe.registerPassiveListener(this);
				mAccelerometerSensorProbe.registerPassiveListener(this);
				mLocationProbe.registerPassiveListener(this);
				*/
			}
			
		}
	}
	
}
