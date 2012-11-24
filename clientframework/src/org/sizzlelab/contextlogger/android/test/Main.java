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
package org.sizzlelab.contextlogger.android.test;

import java.util.Locale;

import org.sizzlelab.contextlogger.android.R;
import org.sizzlelab.contextlogger.android.CustomProbe.ApplicationProbe;
import org.sizzlelab.contextlogger.android.io.MainPipeline;
import org.sizzlelab.contextlogger.android.utils.Constants;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class Main extends Activity implements OnSharedPreferenceChangeListener, Constants {

	private int mPos = 0;
	private String mSelAct = "";
	private String mRunAct = "";
	private Context mContext = null;
	private boolean isRunning = false;
	private TextView lbl_status = null;
	private TextView tv_message = null;
	private TextView tv_dataCount = null;
	private Button btn_toggle = null;
	private Spinner spinner = null;
	private Button btn_toggleAct = null;

	private final BroadcastReceiver tcReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle b = intent.getExtras();
			long tc = b.getLong("totalCount");
			tv_dataCount.setText("Total count: " + tc);
		}
	};

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);
		lbl_status = (TextView) findViewById(R.id.lbl_status);
		btn_toggle = (Button) findViewById(R.id.btn_toggle);
		btn_toggleAct = (Button) findViewById(R.id.btn_toggle_activity);
		tv_message = (TextView) findViewById(R.id.tv_message);
		tv_dataCount = (TextView) findViewById(R.id.dataCountText);
		addListenerOnSpinnerItemSelection();

		if (mContext != null) {
			mContext = getApplicationContext();
		}

		setLabels(savedInstanceState);
		MainPipeline.getSystemPrefs(this)
				.registerOnSharedPreferenceChangeListener(this);
		tv_dataCount.setText("Total count: " + MainPipeline.getScanCount(this));

		IntentFilter filter = new IntentFilter();
		filter.addAction(COUNT_ACTION);
		registerReceiver(tcReceiver, filter);
	}

	@Override
	protected void onSaveInstanceState(Bundle savedInstanceState) {
		savedInstanceState.putString("mRunAct", mRunAct);
		savedInstanceState.putBoolean("isRunning", isRunning);
		super.onSaveInstanceState(savedInstanceState);
	}

	private void addListenerOnSpinnerItemSelection() {
		spinner = (Spinner) findViewById(R.id.activity_spinner);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				String nowSelectedActivity = parent.getItemAtPosition(pos)
						.toString();
				if (isRunning) {
					tv_message.setText("Stop activity '" + mSelAct
							+ "' before changing to new activity.");
					spinner.setSelection(mPos);
				} else {
					mPos = pos;
					mSelAct = nowSelectedActivity;
					tv_message.setText("");
				}
			}

			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(tcReceiver);
	}

	private void setLabels(Bundle savedInstanceState) {
		boolean isServiceRunning = false;
		isServiceRunning = MainPipeline.isEnabled(this);

		if (savedInstanceState != null) {
			isRunning = savedInstanceState
					.getBoolean("isRunning");
			mRunAct = savedInstanceState.getString("mRunAct");
		}

		if (isServiceRunning) {
			lbl_status.setText(R.string.service_running);
			btn_toggle.setText(R.string.btn_stop_service);
		} else {
			lbl_status.setText(R.string.service_stopped);
			btn_toggle.setText(R.string.btn_start_service);
		}

		if (isRunning && mRunAct.length() > 0) {
			ArrayAdapter<String> arrAdapter = (ArrayAdapter<String>) spinner
					.getAdapter();
			mPos = arrAdapter.getPosition(mRunAct.trim());
			spinner.setSelection(mPos);
			btn_toggleAct.setText(R.string.btn_stop_activity);
		}
	}

	private void setLabels(boolean value) {
		if (value) {
			lbl_status.setText(R.string.service_running);
			btn_toggle.setText(R.string.btn_stop_service);
			tv_message.setText("");

		} else {
			lbl_status.setText(R.string.service_stopped);
			btn_toggle.setText(R.string.btn_start_service);

			if (isRunning) {
				Intent i = new Intent();
				i.setAction(CUSTOM_INTENT_ACTION);
				i.putExtra("APPLICATION_ACTION", getActionTag(mRunAct) + "_STOP");
				i.putExtra("APPLICATION_DATA", "false");

				mContext = getApplicationContext();
				mContext.sendBroadcast(i);

				isRunning = false;
				btn_toggleAct.setText(R.string.btn_start_activity);
				tv_message.setText("");
			}
		}
	}

	public void btnToggleClicked(View v) {
		boolean isRunning = MainPipeline.isEnabled(this);

		if (isRunning) {
			setLabels(false);
			Intent archiveIntent = new Intent(this, MainPipeline.class);
			archiveIntent.setAction(MainPipeline.ACTION_DISABLE);
			startService(archiveIntent);

			isRunning = MainPipeline.isEnabled(this);

		} else {
			setLabels(true);
			Intent archiveIntent = new Intent(this, MainPipeline.class);
			archiveIntent.setAction(MainPipeline.ACTION_ENABLE);
			startService(archiveIntent);
		}
	}

	public void btnActivityClicked(View v) {
		boolean isServiceRunning = MainPipeline.isEnabled(this);
		if (!isServiceRunning) {
			tv_message
					.setText("Start ContextLogger service by pressing above button 'Start service'.");
		} else {
			if (!isRunning) {
				Intent i = new Intent();
				i.setAction(CUSTOM_INTENT_ACTION);
				i.putExtra("APPLICATION_ACTION", getActionTag(mSelAct) + "_START");
				i.putExtra("APPLICATION_DATA", "true");
				mContext = getApplicationContext();
				mContext.sendBroadcast(i);

				mRunAct = mSelAct;
				isRunning = true;

				btn_toggleAct.setText(R.string.btn_stop_activity);
			} else {
				Intent i = new Intent();
				i.setAction(CUSTOM_INTENT_ACTION);
				i.putExtra("APPLICATION_ACTION", getActionTag(mRunAct) + "_STOP");
				i.putExtra("APPLICATION_DATA", "false");
				mContext = getApplicationContext();
				mContext.sendBroadcast(i);

				isRunning = false;

				btn_toggleAct.setText(R.string.btn_start_activity);
			}
		}
	}

	public void btn_export_clicked(View v) {
		Intent archiveIntent = new Intent(this, MainPipeline.class);
		archiveIntent.setAction(MainPipeline.ACTION_ARCHIVE_DATA);
		startService(archiveIntent);
	}

	@Override
	protected void onResume() {
		boolean knownValue = isRunning;
		boolean actualValue = false;
		if (!isRunning) {
			String lastAction = ApplicationProbe.getLastAction();
			String lastActionData = ApplicationProbe.getLastActionData();
			if (lastAction != null && lastAction.length() > 0
					&& lastActionData.equals("true")) {
				mRunAct = lastAction;
				isRunning = Boolean.valueOf(lastActionData);
				actualValue = isRunning;
			}
		}

		if (knownValue == false && actualValue == true) {
			ArrayAdapter<String> arrAdapter = (ArrayAdapter<String>) spinner
					.getAdapter();
			mPos = arrAdapter.getPosition(mRunAct.trim());
			spinner.setSelection(mPos);
			btn_toggleAct.setText(R.string.btn_stop_activity);
		}

		super.onResume();
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if (MainPipeline.COUNT_KEY.equals(key)) {
			updateScanCount();
		}
	}

	private void updateScanCount() {
		Intent i = new Intent();
		i.setAction(COUNT_ACTION);
		i.putExtra("totalCount", MainPipeline.getScanCount(this));
		mContext = getApplicationContext();
		mContext.sendBroadcast(i);
	}
	
	private String getActionTag(String action)
	{
		String ret = "";
<<<<<<< HEAD
		if (action != null && !action.isEmpty())
		{
			action = action.replaceAll(" ", "_");
			ret = action.toUpperCase();
=======
		if (!TextUtils.isEmpty(action))
		{
			action = action.replaceAll(" ", "_");
			ret = action.toUpperCase(Locale.getDefault());
>>>>>>> chao_develop
		}
		
		return ret;
	}
}