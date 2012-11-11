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
 * Chao Wei (chao.wei@aalto.fi)
 */
package org.sizzlelab.contextlogger.android;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.json.JSONException;
import org.sizzlelab.contextlogger.android.MainActivity.OnContextLoggerStatusChangeListener;
import org.sizzlelab.contextlogger.android.io.MainPipeline;
import org.sizzlelab.contextlogger.android.model.ActionEvent;
import org.sizzlelab.contextlogger.android.model.EventState;
import org.sizzlelab.contextlogger.android.model.handler.ActionEventHandler;
import org.sizzlelab.contextlogger.android.utils.Constants;
import org.sizzlelab.contextlogger.android.widget.adapter.ActivityEventListAdapter;
import org.sizzlelab.contextlogger.android.widget.adapter.ActivityEventListAdapter.OnActivityEventUpdateListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.WazaBe.HoloEverywhere.widget.Button;
import com.WazaBe.HoloEverywhere.widget.EditText;
import com.WazaBe.HoloEverywhere.widget.Spinner;
import com.WazaBe.HoloEverywhere.widget.Switch;
import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import fi.aalto.chaow.android.app.BaseAlertDialog;
import fi.aalto.chaow.android.app.BaseAlertDialog.AlertDialogListener;
import fi.aalto.chaow.android.app.BaseFragmentActivity.OnSupportFragmentListener;
import fi.aalto.chaow.android.utils.TextHelper;

public class LoggerPanelFragment extends SherlockListFragment implements OnClickListener, Constants, OnContextLoggerStatusChangeListener,
																				OnCheckedChangeListener, OnActivityEventUpdateListener,
											com.WazaBe.HoloEverywhere.widget.AdapterView.OnItemSelectedListener {
	private Handler mHandler = new Handler();
	private Runnable mTimedTask = new Runnable(){
		@Override
		public void run() {
			if((mShownContent != null) && (mAdapter != null) && mIsRunning){
				for(int i = 0; i < mActionEventList.size(); i++){
					ActionEvent ae = mActionEventList.get(i);
					mShownContent.get(i).put("Duration", ae.getDuration());
				}	
				mAdapter.notifyDataSetChanged();
			}
			mHandler.postDelayed(mTimedTask , 6000);
		}
	};
	
	private OnSupportFragmentListener mListener = null;
	
	private ArrayList<ActionEvent> mActionEventList = new ArrayList<ActionEvent>();
	
	private ActivityEventListAdapter mAdapter = null;
	private ArrayList<HashMap<String, Object>> mShownContent = null;
	private String[] mTagArray = null;
	
	private Button mButtonStartActivity = null;
	private Button mButtonSaveNote = null;
	private EditText mEditTextNote = null;
	private Spinner mSpinner = null;
	private TextView mTextViewCount = null;
	private TextView mTextViewStatus = null;
	private String mCurrentTag = null;
	private View mNoData = null;
	private Switch mLoggerSwitch = null;
	private HashSet<String> mEventTagList = new HashSet<String>();
	
	private String mCountWords = null;
	
	private boolean mIsRunning = false;
	
	public LoggerPanelFragment(){
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mListener = (OnSupportFragmentListener) activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		Intent archiveIntent = new Intent(getSherlockActivity().getApplicationContext(), MainPipeline.class);
		archiveIntent.setAction(MainPipeline.ACTION_ENABLE);
		getSherlockActivity().startService(archiveIntent);	
		mIsRunning = true;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.logger_panel, container, false); 
		mButtonStartActivity = (Button)view.findViewById(R.id.button_start_event);
		mButtonStartActivity.setOnClickListener(this);
		mButtonStartActivity.setEnabled(false);
		mTextViewCount = (TextView)view.findViewById(R.id.text_veiw_data_count);
		mTextViewStatus = (TextView)view.findViewById(R.id.text_view_service_state);
		mSpinner = (Spinner)view.findViewById(R.id.spinner_action_list);
		mSpinner.setOnItemSelectedListener(this);
		mNoData = view.findViewById(R.id.text_veiw_no_data);
		mButtonSaveNote = (Button)view.findViewById(R.id.button_save_note);
		mButtonSaveNote.setOnClickListener(this);
		mButtonSaveNote.setEnabled(false);
		mEditTextNote = (EditText) view.findViewById(R.id.edit_text_note);
		mEditTextNote.setOnTouchListener(new OnTouchListener(){
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(!mIsRunning) {
					TextHelper.hideSoftInput(getSherlockActivity().getApplicationContext(), mEditTextNote);
					return true;
				}
				mEditTextNote.setInputType(InputType.TYPE_CLASS_TEXT);
				mEditTextNote.requestFocus();
				TextHelper.showSoftInput(getSherlockActivity().getApplicationContext(), mEditTextNote);
				return true;
			}
		});
		mEditTextNote.setInputType(InputType.TYPE_NULL);
		mEditTextNote.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				mButtonSaveNote.setEnabled(s.length() > 0 ? true : false );
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
		});
		updateUI();
		mTextViewStatus.requestFocus();
		mLoggerSwitch = (Switch)getSherlockActivity().getSupportActionBar().getCustomView().findViewById(R.id.logger_switcher);
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		updateEventList();
		refreshSpinner();						
		mHandler.post(mTimedTask);
		if(!mIsRunning){
			mSpinner.setEnabled(false);
			mButtonStartActivity.setEnabled(false);
			mButtonSaveNote.setEnabled(false);
			mEditTextNote.setText("");
			mEditTextNote.setEnabled(false);
		}
	}

	@Override
	public void onStop() {
		super.onStop();
		unregisterForContextMenu(getListView());
		mHandler.removeCallbacks(mTimedTask);
	}
	
	private void updateEventList(){
		if( mShownContent == null){
			 mShownContent = new ArrayList<HashMap<String, Object>>();
		}
		if(!mShownContent.isEmpty()){
			mShownContent.clear();
			mAdapter = null;
			mActionEventList.clear();
		}
		mActionEventList = ActionEventHandler.getInstance().getAllItems(getSherlockActivity().getApplicationContext(), false);
		if(mActionEventList.isEmpty()){
			mNoData.setVisibility(View.VISIBLE);
		} else{
			mNoData.setVisibility(View.GONE);
		}
		for(ActionEvent ae : mActionEventList){
       		HashMap<String, Object> data = new HashMap<String, Object>();
       		data.put("DateTime", ActionEvent.getTimeToString(ae.getStartTimestamp())); 
    		data.put("Event", ae.getActionEventName());
    		data.put("Duration", ae.getDuration());
    		mShownContent.add(data);
		}

		mAdapter = new ActivityEventListAdapter(getSherlockActivity(), mShownContent, R.layout.event_list_item,
												new String[]{"DateTime", "Event" , "Duration"},
												new int[]{R.id.text_view_event_start_datetime, R.id.text_view_event_tag_name,
												R.id.text_view_event_duration, R.id.image_button_activity_stop}, this);

		getListView().setAdapter(mAdapter);
		registerForContextMenu(getListView());
	}

	private void refreshSpinner(){
		List<String> list = new ArrayList<String>();
		String tags = ClientApp.getInstance().getEventTags();
		if(TextUtils.isEmpty(tags)){
			ArrayList<String> tagList = null;
			try {
				tagList = ClientApp.getInstance().getEventTagsFromAsset();
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(tagList != null){
				mTagArray = tagList.toArray(new String[tagList.size()]);
			}
			if(mTagArray != null){
				for(String str : mTagArray){
					list.add(str);
				}				
			}
		}else{
			String[] strArray = tags.split(";");
			for(String str : strArray){
				list.add(str);
			}
		}
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getSherlockActivity().getApplicationContext(),
															R.layout.spinner_item, list);
		mSpinner.setAdapter(dataAdapter);
		final String firstTag = list.get(0);
		for(String tag : mEventTagList){
			if(firstTag.equals(tag)){
				mButtonStartActivity.setEnabled(false);
				break;
			}
		}
	}

	private void startService(){
		if(!mIsRunning){
			Intent archiveIntent = new Intent(getSherlockActivity().getApplicationContext(), MainPipeline.class);
			archiveIntent.setAction(MainPipeline.ACTION_ENABLE);
			getSherlockActivity().startService(archiveIntent);	
			mIsRunning = true;
		}
		updateUI();
	}
	
	private void updateUI(){
		if(mIsRunning){
			mTextViewStatus.setText(R.string.service_running);
			mSpinner.setEnabled(true);
			mButtonStartActivity.setEnabled(true);
			mButtonSaveNote.setEnabled(false);
			mEditTextNote.setEnabled(true);
		}else{
			mTextViewStatus.setText(R.string.service_stopped);
		}
		mActionEventList = ActionEventHandler.getInstance().getAllItems(getSherlockActivity().getApplicationContext(), false);
		for(ActionEvent ae : mActionEventList){
			final String name = ae.getActionEventName();
			if(!"USER_NOTE".equals(name)){
				mEventTagList.add(name);							
			}
		}
	}
	
	private void stopService(){
		mTextViewStatus.setText(R.string.service_stopped);
		Intent archiveIntent = new Intent(getSherlockActivity().getApplicationContext(), MainPipeline.class);
		archiveIntent.setAction(MainPipeline.ACTION_DISABLE);
		getSherlockActivity().startService(archiveIntent);
		mIsRunning  = false;
		for(ActionEvent ae : mActionEventList){
			ae.confirmBreakTimestamp();
			ae.setState(EventState.STOP);
			ActionEventHandler.getInstance().update(getSherlockActivity().getApplicationContext(), ae);
		}
		if(mSpinner != null){
			mSpinner.setEnabled(false);
		}
		if(mButtonStartActivity != null){
			mButtonStartActivity.setEnabled(false);
		}
		mActionEventList.clear();
		mEventTagList.clear();
		mCurrentTag = null;
		updateEventList();
		mButtonSaveNote.setEnabled(false);
		mEditTextNote.setText("");
		mEditTextNote.setEnabled(false);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		if (v.getId() == android.R.id.list) {
			android.view.MenuInflater inflater = getSherlockActivity().getMenuInflater();
			inflater.inflate(R.menu.list_context_menu, menu);
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		    menu.setHeaderTitle(mActionEventList.get(info.position).getActionEventName());
		}
	}
	
	@Override
	public boolean onContextItemSelected(android.view.MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		ActionEvent ae = mActionEventList.get(info.position);
		final int itemId = item.getItemId();
		if(R.id.context_menu_stop == itemId){
			ae.setState(EventState.STOP);
		}else if(R.id.context_menu_cancel == itemId){
			ae.setState(EventState.INVALIDATE);
		}
		for(String tag : mEventTagList){
			if(tag.equals(ae.getActionEventName())){
				mEventTagList.remove(tag);
				break;
			}
		}
		ae.confirmBreakTimestamp();
		ActionEventHandler.getInstance().update(getSherlockActivity().getApplicationContext(), ae);
		notifyEvent(ae);
		updateEventList();
		refreshSpinner();
		return true;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu, menu);
		updateMenuItem(menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		updateMenuItem(menu);
	}

	private void updateMenuItem(Menu menu){
		MenuItem item = menu.findItem(R.id.menu_toggle_service);
		if(MainPipeline.isEnabled(getSherlockActivity().getApplicationContext())){
			item.setTitle(R.string.btn_stop_service);			
		}else {
			item.setTitle(R.string.btn_start_service);
		}
	}	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		final int itemId = item.getItemId();
		if (itemId == R.id.menu_add_tag) {
			new ActivityNamingDialog(new AlertDialogListener(){
				@Override
				public void onPositiveClick() {
					refreshSpinner();
				}
				@Override
				public void onNegativeClick() {
				}
				@Override
				public void onCancel() {
				}
			}).show(getFragmentManager(), "AddingNewActivityName"); 
			return true;
		} else if (itemId == R.id.menu_export_data) {
			exportData();
			return true;
		} else if (itemId == R.id.menu_history) {
			if(mListener != null){
				mListener.onFragmentChanged(R.layout.logger_history, null);
				return true;
			}
		} else if (itemId == R.id.menu_shutdown) {
			new QuitAppDialog(new AlertDialogListener(){
				@Override
				public void onPositiveClick() {
					quitApp();
				}
				@Override
				public void onNegativeClick() {
				}
				@Override
				public void onCancel() {
				}
			}).show(getFragmentManager(), "QuitApp");
			return true;
		}else if(itemId == R.id.menu_toggle_service){
			if(mLoggerSwitch != null){
				mLoggerSwitch.setChecked(!mIsRunning);
			} 
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
   
	private void exportData(){
		Intent archiveIntent = new Intent(getSherlockActivity().getApplicationContext(), MainPipeline.class);
		archiveIntent.setAction(MainPipeline.ACTION_ARCHIVE_DATA);
		getSherlockActivity().startService(archiveIntent);
	}
	
	private void quitApp(){
		if(mLoggerSwitch != null){
			mLoggerSwitch.setChecked(false);
		} 
		stopService();
		getSherlockActivity().finish();
	}
	
	@Override
	public void onClick(View v) {
		final int viewId = v.getId();
		if(R.id.button_start_event == viewId){
			if(!mIsRunning) {
				if(mButtonStartActivity.isEnabled()){
					mButtonStartActivity.setEnabled(false);					
				}
				return;
			}
			startEvent();
		}else if(R.id.button_save_note == viewId){	
			saveNote();
		}
	}
	
	private void saveNote(){
		final String note = mEditTextNote.getText().toString();
		mEditTextNote.setText("");
		TextHelper.hideSoftInput(getSherlockActivity().getApplicationContext(), mEditTextNote);
		mEditTextNote.setInputType(InputType.TYPE_NULL);
		mButtonSaveNote.setEnabled(false);
		ActionEventHandler.getInstance().insert(getSherlockActivity().getApplicationContext(), 
								new ActionEvent("USER_NOTE", System.currentTimeMillis(), note, true));
		Intent intent = new Intent();
		intent.setAction(CUSTOM_INTENT_ACTION);
		intent.putExtra("APPLICATION_ACTION", "USER_NOTE");
		intent.putExtra("APPLICATION_DATA", note);
		getSherlockActivity().sendBroadcast(intent);
	}
	
	private void startEvent(){
		if(!TextUtils.isEmpty(mCurrentTag)){
			ActionEvent ae = new ActionEvent(mCurrentTag, System.currentTimeMillis());
			ae.setState(EventState.START);
			ActionEventHandler.getInstance().insert(getSherlockActivity().getApplicationContext(), ae);
			mButtonStartActivity.setEnabled(false);
			notifyEvent(ae);
			updateEventList();
			mEventTagList.add(mCurrentTag);
		} 
	}

	private void notifyEvent(ActionEvent ae){
		final String payload = ae.getMessagePayload();
		if(!TextUtils.isEmpty(payload)){
			Intent i = new Intent();
			i.setAction(CUSTOM_INTENT_ACTION);
			i.putExtra("APPLICATION_ACTION", payload);
			getSherlockActivity().sendBroadcast(i);
		} else {
			ClientApp.getInstance().showToastMessage("Client error!");
		}
	}
	
	@Override
	public void onItemSelected(com.WazaBe.HoloEverywhere.widget.AdapterView<?> parent,  View view, int pos, long id) {
		mCurrentTag = parent.getItemAtPosition(pos).toString();
		boolean enable = true;
		for(String tag : mEventTagList){
			if(tag.equals(mCurrentTag)){
				enable = false;
				break;
			}
		}
		mButtonStartActivity.setEnabled(enable);
	}

	@Override
	public void onNothingSelected(com.WazaBe.HoloEverywhere.widget.AdapterView<?> parent) {
	}

	@Override
	public void onLoggerCountChanged(final long totalCount) {
		if(totalCount > 0){
			mHandler.postDelayed(new Runnable(){
				@Override
				public void run() {
					mCountWords = new String("Total count: " + Long.toString(totalCount));
					if(mTextViewCount != null){
						mTextViewCount.setText(mCountWords);
					}				
				}
			}, 1); 
		}				
	}

	@Override
	public void onStopButtonClick(int position) {
		ActionEvent ae = mActionEventList.get(position);
		ae.setState(EventState.STOP);
		for(String tag : mEventTagList){
			if(tag.equals(ae.getActionEventName())){
				mEventTagList.remove(tag);
				break;
			}
		}
		ae.confirmBreakTimestamp();
		ActionEventHandler.getInstance().update(getSherlockActivity().getApplicationContext(), ae);
		notifyEvent(ae);
		updateEventList();
		refreshSpinner();
	}
	
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if(buttonView.getId() == R.id.logger_switcher){
			if(isChecked){
				startService();
				refreshSpinner();
			}else{
				stopService();
			}
		}
	}
	
	private class QuitAppDialog extends BaseAlertDialog{

		public QuitAppDialog(AlertDialogListener listener) {
			super(listener);
		}
		
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getSherlockActivity());
	    	builder.setTitle(R.string.app_quit_title);
	    	builder.setIcon(android.R.drawable.ic_dialog_info);
	    	builder.setMessage(R.string.app_quit_content);
	    	builder.setPositiveButton(R.string.ok, 
	    			new DialogInterface.OnClickListener() {					
						@Override
						public void onClick(DialogInterface dialog, int which) {
							getDialogListener().onPositiveClick();
						}
					});
	    	builder.setNegativeButton(R.string.cancel,     			
	    			new DialogInterface.OnClickListener() {					
						@Override
						public void onClick(DialogInterface dialog, int which) {
							getDialogListener().onNegativeClick();
						}
					});				
			return builder.create(); 
		}
	}
	
	private class ActivityNamingDialog extends BaseAlertDialog{

		public ActivityNamingDialog(AlertDialogListener listener) {
			super(listener);
		}
		
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getSherlockActivity());
	    	builder.setIcon(android.R.drawable.ic_dialog_info);
	    	builder.setTitle(R.string.add_tag_dialog_title);
		  	LayoutInflater inflater = LayoutInflater.from(getSherlockActivity());
		  	final View noteView = inflater.inflate(R.layout.add_tag_dialog, null);
		  	final EditText tagContent = (EditText)noteView.findViewById(R.id.edit_text_tag);
		  	builder.setTitle(R.string.add_tag);
		  	builder.setView(noteView);
	    	builder.setPositiveButton(R.string.save, 
	    			new DialogInterface.OnClickListener() {					
						@Override
						public void onClick(DialogInterface dialog, int which) {
							final String tagName = tagContent.getEditableText().toString();
							if(!TextUtils.isEmpty(tagName)){
								saveEventTag(tagName);
							}else{
								ClientApp.getInstance().showToastMessage("Please give the input");
							}
							getDialogListener().onPositiveClick();
						}
					});
	    	builder.setNegativeButton(R.string.cancel,     			
	    			new DialogInterface.OnClickListener() {					
						@Override
						public void onClick(DialogInterface dialog, int which) {
							getDialogListener().onNegativeClick();
						}
					});	    	
			return builder.create();   
		}
		
		private void saveEventTag(final String tagName){
			ClientApp app = ClientApp.getInstance();
			String tags = app.getEventTags();
			if(TextUtils.isEmpty(tags)){
				ArrayList<String> tagList = null;
				try {
					tagList = ClientApp.getInstance().getEventTagsFromAsset();
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				if(tagList != null){
					mTagArray = tagList.toArray(new String[tagList.size()]);
				}
				if(mTagArray != null){
					tags = new String(tagName + ";" + TextUtils.join(";", mTagArray));				
				}
			}else{
				tags = new String(tagName + ";" + tags);
			}
			app.saveEventTag(tags);
		}
	}

}
