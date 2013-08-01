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
package org.apps8os.logger.android.model;

import java.util.Locale;

import org.apps8os.logger.android.util.TimeUtil;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

public class ActionEvent extends EventTimeUtil implements IActionEvent, Parcelable {
	
	/**
	 * 
	 * 
	 * @author Chao Wei
	 *
	 */
	public static enum EventState {
		UNKNOWN, START, STOP, INVALIDATE;
		
		static EventState getEventState(final String value) {
			if(START.toString().equals(value)) {
				return START;
			} else if(STOP.toString().equals(value)) {
				return STOP;
			} else if(INVALIDATE.toString().equals(value)) {
				return INVALIDATE;	
			}
			return UNKNOWN;
		}
	}
	
	private long mStartTimestamp = -1L;
	private long mBreakTimestamp = -1L;
	private String mEventName = "";
	private boolean mHistory = false;
	private EventState mState = EventState.UNKNOWN;
	private String mNoteContent = "";
	private long mStartDelay = 0L;
	private long mBreakDelay = 0L;
	
	public ActionEvent(final String name, final long startTime) {
		mEventName = name;
		mStartTimestamp = startTime;
		final long diff = startTime - System.currentTimeMillis();
		mStartDelay = ((diff > TimeUtil.TIME_IN_ONE_MIN) || (diff < -TimeUtil.TIME_IN_ONE_MIN)) ? diff : 0;
	}

	public ActionEvent(final String name, final long startTime, final long breakTime,
					   final String state, final String note, final boolean isHistory,
					   final long startDelay, final long breakDelay) {
		mEventName = name;
		mStartTimestamp = startTime;
		mHistory = isHistory;
		mNoteContent = note;
		mStartDelay = startDelay;
		mBreakDelay = breakDelay;
		mBreakTimestamp = breakTime;
		mState = EventState.getEventState(state);
	}	
	
	public final String getNoteContent() {
		return mNoteContent;
	}
	
	@Override
	public final boolean hasNote() {
		return !TextUtils.isEmpty(mNoteContent);
	}
	
	public final String getActionEventName() {
		return mEventName;
	}
	
	public void setState(EventState es) {
		mState = es;
	}
	
	public void setState(String state) {
		try{
			mState = EventState.valueOf(state);			
		}catch(Exception e){
			mState = EventState.UNKNOWN;
		}
	}
	
	public final String getEventState() {
		return mState.toString();
	}
	
	@Override
	public final String getMessagePayload() {
		StringBuilder sb = new StringBuilder();
		sb.append(getActionEventName().replace(" ", "_"));
		sb.append("_" + getEventState());
		sb.append(getExtraPayload());
		return sb.toString().toUpperCase(Locale.getDefault());
	}
	
	private final String getExtraPayload() {
		if((mState == EventState.START) && (mStartDelay != 0L)){
			return getDelayToString(mStartDelay);
		} else if ((mState == EventState.STOP) && (mBreakDelay != 0L)){
			return getDelayToString(mBreakDelay);
		}
		return "";
	}
	
	private final String getDelayToString(final long delayTime) {
		if(delayTime > 0){
			return "_+" + Long.toString(delayTime);
		}
		return "_" + Long.toString(delayTime);
	}
	
	public final long getStartDelay() {
		return mStartDelay;
	}
	
	public final long getBreakDelay() {
		return mBreakDelay;
	}

	public final long getStartTimestamp() {
		return mStartTimestamp;
	}
	
	public void setBreakTimestamp(final long time) {
		mBreakTimestamp = time;
		mHistory = true;
		mBreakDelay = time - System.currentTimeMillis();
	}
	
	public void confirmBreakTimestamp() {
		setBreakTimestamp(System.currentTimeMillis());
	}
	
	public final long getBreakTimestamp() {
		return mBreakTimestamp;
	}
	
	public final boolean isHistory() {
		return mHistory;
	}
	
	private final long getInternalDuration() {
		return ((mBreakTimestamp > 0) 
				? (mBreakTimestamp - mStartTimestamp) 
				: (System.currentTimeMillis() - mStartTimestamp));
	}
	
	@Override
	public final String getEventDuration() {
		final long eventDuration = getInternalDuration();
		if(eventDuration < 0){ 
			return String.valueOf("-" + getEventDurationInClockFormat(Math.abs(eventDuration)));
		}
		return getEventDurationInClockFormat(eventDuration);
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(mEventName);
		dest.writeLong(mStartTimestamp);
		dest.writeLong(mBreakTimestamp);
		dest.writeLong(mHistory ? 1 : 0);
		dest.writeSerializable(mState);
		dest.writeString(mNoteContent);
		dest.writeLong(mStartDelay);
		dest.writeLong(mBreakDelay);
	}
	
	public static final Parcelable.Creator<ActionEvent> CREATOR = new Parcelable.Creator<ActionEvent>() {
		public ActionEvent createFromParcel(Parcel in) {
			return new ActionEvent(in);
		}

		public ActionEvent[] newArray(int size) {
			return new ActionEvent[size];
		}
	};
	
	private ActionEvent(Parcel in) {
		mEventName = in.readString();
		mStartTimestamp = in.readLong();
		mBreakTimestamp = in.readLong();
		mHistory = (in.readLong() == 1);
		mState = (EventState) in.readSerializable();
		mNoteContent = in.readString();
		mStartDelay = in.readLong();
		mBreakDelay = in.readLong();
	}
	
	@Override
	public int describeContents() {
		return 0;
	}	
	
}