package org.sizzlelab.contextlogger.android.model;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

public class ActionEvent implements Parcelable{

	private long mStartTimestamp = -1L;
	private long mBreakTimestamp = -1L;
	private String mEventName = "";
	private boolean mHistory = false;
	private EventState mState = EventState.UNKNOWN;
	private String mNoteContent = "";
	
	public ActionEvent(final String name, final long startTime){
		mEventName = name;
		mStartTimestamp = startTime;
	}

	public ActionEvent(final String name, final long startTime, final String note, final boolean isHistory){
		this(name, startTime);
		mHistory = isHistory;
		mNoteContent = note;
	}	
	
	public final String getNoteContent(){
		return mNoteContent;
	}
	
	public final boolean hasNote(){
		return !TextUtils.isEmpty(mNoteContent);
	}
	
	public final String getActionEventName(){
		return mEventName;
	}
	
	public void setState(EventState es){
		mState = es;
	}
	
	public void setState(String state){
		try{
			mState = EventState.valueOf(state);			
		}catch(Exception e){
			e.printStackTrace();
			mState = EventState.UNKNOWN;
		}
	}
	
	public final String getEventState(){
		return mState.toString();
	}
	
	public final String getMessagePayload(){
		String msg = "";
		try {
			msg = getActionEventName().replace(" ", "_");
			msg += "_" + getEventState();
		}catch(NullPointerException e){
			return msg;
		}
		return msg.toUpperCase();
	}
	
	public final long getStartTimestamp(){
		return mStartTimestamp;
	}
	
	public void setBreakTimestamp(final long time){
		mBreakTimestamp = time;
	}
	
	public void confirmBreakTimestamp(){
		setBreakTimestamp(System.currentTimeMillis());
		mHistory = true;
	}
	
	public final long getBreakTimestamp(){
		return mBreakTimestamp;
	}

	
	public final boolean isHistory(){
		return mHistory;
	}
	
	public final long getEventDuration(){
		if(mBreakTimestamp > 0){
			return (mBreakTimestamp - mStartTimestamp);
		} else {
			return (System.currentTimeMillis() - mStartTimestamp);
		}
	}

	public final String getDuration(){
		int days = 0;
		int hours = 0;
		int mins = 0;
		final long eventDuration = getEventDuration();
		days = (int)(eventDuration / (24 * 60 * 60 * 1000));
		hours = (int) ((eventDuration - days * (24 * 60 * 60 * 1000)) / (60 * 60 * 1000));
		mins = (int) ((eventDuration - days * (24 * 60 * 60 * 1000) - hours * (60 * 60 * 1000)) / (60 * 1000));
		if((days == 0) && (hours == 0) && (mins == 0)){
			return "0 h, 1 min";
		}
		return new String(hours + " h, " + mins + " min");
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(mEventName);
		dest.writeLong(mStartTimestamp);
		dest.writeLong(mBreakTimestamp);
		dest.writeLong(mHistory ? 1 : 0);
		dest.writeSerializable(mState);
		dest.writeString(mNoteContent);
	}
	
	public static final Parcelable.Creator<ActionEvent> CREATOR = new Parcelable.Creator<ActionEvent>() {
		public ActionEvent createFromParcel(Parcel in) {
			return new ActionEvent(in);
		}

		public ActionEvent[] newArray(int size) {
			return new ActionEvent[size];
		}
	};
	
	private ActionEvent(Parcel in){
		mEventName = in.readString();
		mStartTimestamp = in.readLong();
		mBreakTimestamp = in.readLong();
		mHistory = (in.readLong() == 1);
		mState = (EventState) in.readSerializable();
		mNoteContent = in.readString();
	}
	
	private static boolean isToday(final long time){
		final int currentDay = Calendar.getInstance(TimeZone.getDefault()).get(Calendar.DAY_OF_YEAR);
		final int currentYear = Calendar.getInstance(TimeZone.getDefault()).get(Calendar.YEAR);
		Calendar target = Calendar.getInstance(TimeZone.getDefault());
		target.setTimeInMillis(time);
		return (currentDay == target.get(Calendar.DAY_OF_YEAR) 
				&& (currentYear == target.get(Calendar.YEAR)));
	}
	
	public static final String getTimeToString(final long time){
		if(isToday(time)) {
			return new String (new SimpleDateFormat("HH:mm").format(new Date(time)));
		}else{
			return new String (new SimpleDateFormat("dd MMM").format(new Date(time)));
		}
	}
}
