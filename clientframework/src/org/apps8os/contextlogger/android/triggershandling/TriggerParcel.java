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
package org.apps8os.contextlogger.android.triggershandling;

import android.os.Parcel;
import android.os.Parcelable;

public class TriggerParcel implements Parcelable {

	String mAction = null;
	String mConfig =  null;

	public TriggerParcel() {
	}

    private TriggerParcel(Parcel in) {
    	mAction = in.readString();
    	mConfig = in.readString();
    }
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) 
	{
		dest.writeString(mAction);
		dest.writeString(mConfig);
	}

    public static final Parcelable.Creator<TriggerParcel> CREATOR
		= new Parcelable.Creator<TriggerParcel>() {
			public TriggerParcel createFromParcel(Parcel in) {
				return new TriggerParcel(in);
			}

			public TriggerParcel[] newArray(int size) {
	             return new TriggerParcel[size];
	         }
	     };

 	public void setAction(String action) {
		this.mAction = action;
	}
 	
 	public String getAction() {
		return mAction;
	}

	public void setConfig(String config) {
		this.mConfig = config;
	}
	
	public String getConfig() {
		return mConfig;
	}
}