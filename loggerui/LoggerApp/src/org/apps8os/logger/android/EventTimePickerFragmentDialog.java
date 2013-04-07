package org.apps8os.logger.android;
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
import java.util.Calendar;
import java.util.Locale;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.widget.Button;
import org.holoeverywhere.widget.TimePicker;
import org.holoeverywhere.widget.TimePicker.OnTimeChangedListener;
import org.sizzlelab.contextlogger.android.model.ActionEvent;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import fi.aalto.chaow.android.app.BaseAlertDialog;

/**
 * Dialog for asking user to choose the (starting/stop) 
 * time for the action event (activity).
 * 
 * @author Chao Wei
 *
 */
public class EventTimePickerFragmentDialog extends BaseAlertDialog implements OnTimeChangedListener{
	
	public static EventTimePickerFragmentDialog newInstance(final OnEventTimeChangedListener l, 
													final EventTimeMode em, final ActionEvent ae){
		EventTimePickerFragmentDialog etpfd = new EventTimePickerFragmentDialog();
		etpfd.setupEventTimePicker(l, em, ae); 
		return etpfd;
	}
	
	public enum EventTimeMode{
		START, STOP
	}
	
	private OnEventTimeChangedListener mListener = null;
	
	private TimePicker mTimePicker = null;
	private int mHour = -1;
	private int mMin = -1;
	private ActionEvent mEvent = null;
	
	private EventTimeMode mMode = EventTimeMode.START;
	
	private Button mButtonNow = null;
	
	private void confirmEventTime(){
		if(mListener != null){
			final Calendar c = Calendar.getInstance();
			c.set(Calendar.HOUR_OF_DAY, mHour);
			c.set(Calendar.MINUTE, mMin);
			mListener.onConfirmed(c.getTimeInMillis());
		}
	}
	
	public void setupEventTimePicker(final OnEventTimeChangedListener l, 
							final EventTimeMode em, final ActionEvent event){
		mListener = l;
		mMode = em;
		mEvent = event;
	}	
	
	private void setTimeAsNow(){
		if(mTimePicker != null){
			final Calendar c = Calendar.getInstance();
			mHour = c.get(Calendar.HOUR_OF_DAY);
			mTimePicker.setCurrentHour(mHour);			
			mTimePicker.invalidate();
			mMin = c.get(Calendar.MINUTE);
			mTimePicker.setCurrentMinute(mMin);
			mTimePicker.invalidate();
		}
	}
	
	@Override
	public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
		mHour = hourOfDay;
		mMin = minute;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getSupportActivity());
		builder.setIcon(android.R.drawable.ic_dialog_info);
		// title message
		String mode = null;
		if(mMode == EventTimeMode.START){
			mode = getString(R.string.start).toLowerCase(Locale.getDefault());
		}else if(mMode == EventTimeMode.STOP){
			mode = getString(R.string.stop).toLowerCase(Locale.getDefault());
		} 
		builder.setTitle(String.format(getString(R.string.event_time_picker_message), mode, mEvent.getActionEventName()));
		LayoutInflater inflater = LayoutInflater.from(getSupportActivity());
    	final View view = inflater.inflate(R.layout.event_time_picker, null);
      	mTimePicker = (TimePicker)view.findViewById(R.id.event_time_picker);
    	mTimePicker.setOnTimeChangedListener(this);
    	// use 24-hour format
    	mTimePicker.setIs24HourView(Boolean.TRUE);
    	setTimeAsNow();
    	mButtonNow = (Button)view.findViewById(R.id.button_reset_time_now);
    	mButtonNow.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
	    		setTimeAsNow();
			}
    	});
    	builder.setView(view);
    	builder.setPositiveButton(R.string.ok, 
    			new DialogInterface.OnClickListener() {					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						confirmEventTime();
					}
				});
    	builder.setNegativeButton(R.string.cancel,     			
    			new DialogInterface.OnClickListener() {					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if(mListener != null){
							mListener.onCancel();
						}
					}
				});		
    	if(mMode == EventTimeMode.STOP){
			builder.setNeutralButton(R.string.discard,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (mListener != null) {
								mListener.onDiscard();
							}
						}
					});
		}
		return builder.create();
	}
	
	public static interface OnEventTimeChangedListener extends AlertDialogListener{
		void onConfirmed(final long timestamp);
		void onDiscard();
	}

}