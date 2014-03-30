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
package org.apps8os.logger.android.fragment.dialog;

import java.util.Calendar;
import java.util.Locale;

import org.apps8os.logger.android.R;
import org.apps8os.logger.android.model.ActionEvent;
import org.apps8os.logger.android.widget.legacy.TimePicker;
import org.apps8os.logger.android.widget.legacy.TimePicker.OnTimeChangedListener;
import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.widget.CheckBox;
import org.holoeverywhere.widget.ImageButton;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;



public class EventTimePickerDialogFragment extends LoggerBaseDialogFragment
		implements OnTimeChangedListener,
		android.widget.CompoundButton.OnCheckedChangeListener {

	public static EventTimePickerDialogFragment newInstance(
															final OnEventTimeChangedListener l, 
															final EventTimeMode em,
															final ActionEvent ae) {
		EventTimePickerDialogFragment etpfd = new EventTimePickerDialogFragment();
		etpfd.setupEventTimePicker(l, em, ae);
		return etpfd;
	}

	public enum EventTimeMode {
		START, STOP
	}

	private OnEventTimeChangedListener mListener = null;

	private TimePicker mTimePicker = null;
	private int mHour = -1;
	private int mMin = -1;
	private ActionEvent mEvent = null;

	private EventTimeMode mMode = EventTimeMode.START;

	private ImageButton mButtonNow = null;
	private CheckBox mCheckBox = null;

	private void confirmEventTime() {
		if (mListener != null) {
			final Calendar c = Calendar.getInstance();
			c.set(Calendar.HOUR_OF_DAY, mHour);
			c.set(Calendar.MINUTE, mMin);
			mListener.onConfirmed(c.getTimeInMillis());
		}
	}

	public void setupEventTimePicker(final OnEventTimeChangedListener l,
			final EventTimeMode em, final ActionEvent event) {
		mListener = l;
		mMode = em;
		mEvent = event;
	}

	private void setTimeAsNow() {
		if (mTimePicker != null) {
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
		if (mMode == EventTimeMode.START) {
			mode = getString(R.string.start).toLowerCase(Locale.getDefault());
		} else if (mMode == EventTimeMode.STOP) {
			mode = getString(R.string.stop).toLowerCase(Locale.getDefault());
		}
		builder.setTitle(String.format(
				getString(R.string.event_time_picker_message), mode,
				mEvent.getActionEventName()));
		LayoutInflater inflater = LayoutInflater.from(getSupportActivity());
		final View view = inflater.inflate(R.layout.event_time_picker, null);
		mCheckBox = (CheckBox) view
				.findViewById(R.id.check_box_discard_activty_event);
		if (mMode == EventTimeMode.STOP) {
			mCheckBox.setOnCheckedChangeListener(this);
			mCheckBox.setVisibility(View.VISIBLE);
		}
		mTimePicker = (TimePicker) view.findViewById(R.id.event_time_picker);
		mTimePicker.setOnTimeChangedListener(this);
		// use 24-hour format
		mTimePicker.setIs24HourView(Boolean.TRUE);
		setTimeAsNow();
		mButtonNow = (ImageButton) view
				.findViewById(R.id.button_reset_time_now);
		mButtonNow.setOnClickListener(new OnClickListener() {
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
						if (mMode == EventTimeMode.STOP) {
							if (mCheckBox.isChecked()) {
								if (mListener != null) {
									mListener.onDiscard();
								}
							} else {
								confirmEventTime();
							}
						} else {
							confirmEventTime();
						}
					}
				});
		builder.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (mListener != null) {
							mListener.onCancel();
						}
					}
				});
		// if(mMode == EventTimeMode.STOP) {
		// builder.setNeutralButton(R.string.discard,
		// new DialogInterface.OnClickListener() {
		// @Override
		// public void onClick(DialogInterface dialog, int which) {
		// if (mListener != null) {
		// mListener.onDiscard();
		// }
		// }
		// });
		// }
		return builder.create();
	}

	public static interface OnEventTimeChangedListener extends
			AlertDialogListener {
		void onConfirmed(final long timestamp);

		void onDiscard();
	}

	@Override
	protected String getClassName() {
		return EventTimePickerDialogFragment.class.getSimpleName();
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (buttonView == null)
			return;
		final int viewId = buttonView.getId();
		if (viewId == R.id.check_box_discard_activty_event) {
			mButtonNow.setEnabled(!isChecked);
			mTimePicker.setEnabled(!isChecked);
		}
	}
}