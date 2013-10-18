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
 */
package org.apps8os.contextlogger.android.integration;

import org.apps8os.contextlogger.android.io.MainPipeline;

import android.content.Context;
import android.content.Intent;

public class MonitoringFrameworkAgent {
	
	private static MonitoringFrameworkAgent mInstance = null;
	
	private MonitoringFrameworkAgent() {
	}
	
	public static synchronized MonitoringFrameworkAgent getInstance()
	{
		if (mInstance == null) {
			mInstance = new MonitoringFrameworkAgent();
		}
		return mInstance;
	}

	public void start(Context context) {
		Intent intent = new Intent(context, MainPipeline.class);
		intent.setAction(MainPipeline.ACTION_ENABLE);
		context.startService(intent);
	}
	
	public void stop(Context context) {
		Intent intent = new Intent(context, MainPipeline.class);
		intent.setAction(MainPipeline.ACTION_DISABLE);
		context.startService(intent);
	}
	
	public boolean isRunning(Context context) {
		return MainPipeline.isEnabled(context);
	}
	
	public void archive(Context context) {
		Intent intent = new Intent(context, MainPipeline.class);
		intent.setAction(MainPipeline.ACTION_ARCHIVE_DATA);
		context.startService(intent);
	}
	
	public void upload(Context context) {
		Intent intent = new Intent(context, MainPipeline.class);
		intent.setAction(MainPipeline.ACTION_UPLOAD_DATA);
		context.startService(intent);
	}
	
	public long getSensorDataCount(Context context) {
		return MainPipeline.getScanCount(context);
	}
}
