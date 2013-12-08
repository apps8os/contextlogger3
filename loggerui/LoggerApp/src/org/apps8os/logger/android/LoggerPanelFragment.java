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
package org.apps8os.logger.android;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.apps8os.logger.android.IDialogFragment.ActivityNameDeletionDialogFragment;
import org.apps8os.logger.android.IDialogFragment.ActivityNamingDialogFragment;
import org.apps8os.logger.android.IDialogFragment.ActivityNamingDialogFragment.ActivityNamingListener;
import org.apps8os.logger.android.IDialogFragment.EventTimePickerDialogFragment;
import org.apps8os.logger.android.IDialogFragment.EventTimePickerDialogFragment.EventTimeMode;
import org.apps8os.logger.android.IDialogFragment.EventTimePickerDialogFragment.OnEventTimeChangedListener;
import org.apps8os.logger.android.IDialogFragment.LanguageSettingDialogFragment;
import org.apps8os.logger.android.IDialogFragment.QuitAppDialogFragment;
import org.apps8os.logger.android.app.BaseAlertDialogFragment.AlertDialogListener;
import org.apps8os.logger.android.manager.AppManager;
import org.apps8os.logger.android.manager.AppManager.LoggerNFCBroadcastReceiver;
import org.apps8os.logger.android.model.ActionEvent;
import org.apps8os.logger.android.model.ActionEvent.EventState;
import org.apps8os.logger.android.util.AndroidVersionHelper;
import org.apps8os.logger.android.widget.adapter.ActionEventListAdapter;
import org.apps8os.logger.android.widget.adapter.ActionEventListAdapter.OnCustomEventChangeListener;
import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.widget.Switch;
import org.json.JSONException;

import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class LoggerPanelFragment extends LoggerBaseFragment implements OnCheckedChangeListener, 
																ActivityNamingListener, OnItemClickListener, 
																	OnItemLongClickListener {
	private View mSwitcherView = null;
	private TextView mEventCount = null;
	private Switch mSwitch = null;
	private boolean mSwitchCheckState = false;
	private ListView mListView = null;
	
	private ArrayList<HashMap<String, Object>> mShownContent = null;
	private ArrayList<ActionEvent> mActionEventList = null;	
	private ActionEventListAdapter mAdapter = null;
	
	private LoggerPanelNFCBroadcastReceiver mLoggerPanelNFCBroadcastReceiver = null;
	
	private Handler mHandler = new Handler();
	private Runnable mTimedTask = new Runnable(){
		@Override
		public void run() {
			List<ActionEvent> aeList = AppManager.getAllLiveEvents();
			if((mShownContent != null) && (mAdapter != null)){ 
				for(int i = 0; i < aeList.size(); i++){
					ActionEvent ae = aeList.get(i);
					for(HashMap<String, Object> data : mShownContent){
						if(ae.getActionEventName().equals(data.get("Event"))){
							data.put(ActionEventListAdapter.DURATION, ae.getEventDuration());
							data.put(ActionEventListAdapter.CHECK, true);
//							data.put(ActionEventListAdapter.CUSTOM, true);
						}
					}
				}	
				mAdapter.notifyDataSetChanged();
			}
			if((mEventCount != null) && (aeList != null)){
				if(aeList.isEmpty()){
					mEventCount.setText("0");
				}else{
					mEventCount.setText(String.valueOf(aeList.size()));	
				} 
			}
			mHandler.postDelayed(mTimedTask , 850);
		}
	};
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		if(AndroidVersionHelper.isHoneycombAbove()) {
			mLoggerPanelNFCBroadcastReceiver = new LoggerPanelNFCBroadcastReceiver();
		}
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		getSupportActivity().getSupportActionBar().setTitle(R.string.app_name);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.frag_logger_panel, container, false);
		mListView = (ListView)view.findViewById(android.R.id.list);
		mListView.setOnItemClickListener(this);
		mListView.setOnItemLongClickListener(this);
		mSwitchCheckState = true;
		return view;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		updateEventList();
		mHandler.post(mTimedTask);
		if(mLoggerPanelNFCBroadcastReceiver != null) {
			LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mLoggerPanelNFCBroadcastReceiver, 
					new IntentFilter(AppManager.LOGGER_INTENT_FILTER));			
		}
	}
	
	@Override
	public void onStop() {
		super.onStop();
		mHandler.removeCallbacks(mTimedTask);
		if(mLoggerPanelNFCBroadcastReceiver != null) {
			LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mLoggerPanelNFCBroadcastReceiver);			
		}
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initCustomView();
		if(mEventCount != null){
			mEventCount.setText("0");
			mEventCount.setVisibility(View.VISIBLE);
		}
		if(mActionEventList == null){
			mActionEventList = new ArrayList<ActionEvent>();	
			Collections.synchronizedCollection(mActionEventList);
		}
	}
	
	private void initCustomView(){
		mSwitcherView = null;
		mSwitch = null;
		mEventCount = null;
		mSwitcherView = getLayoutInflater().inflate(R.layout.logger_switcher, null);
        mSwitch = (Switch) mSwitcherView.findViewById(R.id.logger_switcher);
        mSwitch.setTextOn(getString(R.string.on));
        mSwitch.setTextOff(getString(R.string.off));
        mSwitch.setOnCheckedChangeListener(this);
        mSwitch.setChecked(mSwitchCheckState);
        mEventCount = (TextView)mSwitcherView.findViewById(R.id.text_view_running_event);
        
        // if the device is honeycomb, we need to
        // adjust the UI a little bit. Otherwise,
        // the switch would look weird.
        if(AndroidVersionHelper.isHoneycomb()){
            final LinearLayout.LayoutParams swLp = new LinearLayout.LayoutParams(
	        		ViewGroup.LayoutParams.WRAP_CONTENT,
	                ViewGroup.LayoutParams.WRAP_CONTENT);
			swLp.setMargins(8, -20, 0, 0);
			mSwitch.setLayoutParams(swLp);        	
        }
        
        final ActionBar.LayoutParams lp = new ActionBar.LayoutParams(
					        		ViewGroup.LayoutParams.WRAP_CONTENT,
					                ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.RIGHT|Gravity.CENTER_VERTICAL;
        lp.setMargins(0, 0, 16, 0); 
        
        final ActionBar actionbar = getSupportActionBar();
        if(actionbar != null) {
        	actionbar.setCustomView(mSwitcherView, lp);
    		actionbar.setTitle(R.string.app_name);
    		
//			if(AndroidVersionHelper.isICSAbove()) {
//				actionbar.setSubtitle(R.string.ui_demo);
//			}
		}	
	}
	
	private void updateEventList(){
		if(mShownContent == null){
			mShownContent = new ArrayList<HashMap<String, Object>>();			
		}else{
			if(!mShownContent.isEmpty()){
				mShownContent.clear();
			}			
		}
		
		// read data out 
		List<String> list = new ArrayList<String>();
		try {
			list = AppManager.getEventTagsFromAsset(getApplicationContext(), AppManager.getEventTagsJsonFileResId(), 
													R.string.event_json_first_tag, R.string.event_json_second_tag);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	
		String tags = LoggerApp.getInstance().getEventTags();
		if(!TextUtils.isEmpty(tags)){
			String[] strArray = tags.split(";");
			for(String str : strArray){
				list.add(str);
			}
		}
		list.add(getString(R.string.add_your_own_event));

		for(int i = 0; i < list.size(); i++) {
			String event = list.get(i);
			HashMap<String, Object> data = new HashMap<String, Object>();
    		data.put(ActionEventListAdapter.EVENT, event);
    		data.put(ActionEventListAdapter.DURATION, "");
    		data.put(ActionEventListAdapter.CHECK, false);
    		if((i == (list.size() - 1)) || (i < AppManager.getPreCount())){
        		data.put(ActionEventListAdapter.CUSTOM, false);    			
    		} else {
        		data.put(ActionEventListAdapter.CUSTOM, true);
    		}
    		mShownContent.add(data);

		} 
		mAdapter = new ActionEventListAdapter(getApplicationContext(), mShownContent, 
				new OnCustomEventChangeListener(){
					@Override
					public void onRemove(final String tag) {
						if (TextUtils.isEmpty(tag)) return; 
						ActivityNameDeletionDialogFragment.newInstance(new AlertDialogListener(){
							@Override
							public void onPositiveClick() {
								LoggerApp app = LoggerApp.getInstance();
								String tags = app.getEventTags();
								if(!TextUtils.isEmpty(tags)){
									String[] strArray = tags.split(";");
									ArrayList<String> tagList = new ArrayList<String>(Arrays.asList(strArray));
									tagList.remove(tag); 
									app.saveEventTag(tagList.isEmpty() ? null : TextUtils.join(";", tagList.toArray()));
									new Handler().postDelayed(new Runnable(){
										@Override
										public void run() {
											updateEventList();
										}
									}, 50L);
								}
							}
							@Override
							public void onNegativeClick() {

							}
							@Override
							public void onCancel() {
							}
						}, tag).show(getFragmentManager());
					}
		});			
		mListView.setAdapter(mAdapter);
		mAdapter.notifyDataSetChanged();			
		mListView.invalidate();
		mActionEventList = AppManager.getAllLiveEvents();
	}
	
	private void forceUpdateUI() {
		updateEventList();
		((ViewGroup)getSupportActivity().getSupportActionBar().getCustomView()).removeAllViews();
		initCustomView();
		if((mSwitch != null) && mSwitch.isChecked()) {
			AppManager.createLoggerNotification(getApplicationContext());
		}
	}
	
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int pos, long id) {
		if(mShownContent != null) {
			if(pos == mShownContent.size() - 1){
				return true;
			} else {
				// check flag
				HashMap<String, Object> data = mShownContent.get(pos);
				handleLoggerData(String.valueOf(data.get(ActionEventListAdapter.EVENT)), data);
			}
		}
		return true;
	}

	void handleLoggerData(final String eventName, HashMap<String, Object> data) {
		ActionEvent actionEvent = null;
		if(!TextUtils.isEmpty(eventName)) {
			boolean hasEvent = false;
			List<ActionEvent> aeList = AppManager.getAllLiveEvents();
			if(!aeList.isEmpty()){
				for(ActionEvent ae : aeList){
					if(ae.getActionEventName().equals(eventName)) {
						hasEvent = true;
						actionEvent = ae;
						break;
					}
				}
			}
			
			if(hasEvent) {
				// stop the event
				actionEvent.confirmBreakTimestamp();
				actionEvent.setState(ActionEvent.EventState.STOP);
				AppManager.updateLiveEvent(actionEvent);
				mActionEventList.remove(actionEvent);
				
				// clear the time  
				data.put(ActionEventListAdapter.DURATION, "");
				data.put(ActionEventListAdapter.CHECK, false);
			} else {
				// start the event
				actionEvent = new ActionEvent(eventName, System.currentTimeMillis());
				actionEvent.setState(ActionEvent.EventState.START);
				AppManager.addALiveEvent(actionEvent);
				mActionEventList.add(actionEvent);
				data.put(ActionEventListAdapter.CHECK, true);
			}
			AppManager.sendEventBoradcast(getApplicationContext(), actionEvent.getMessagePayload(), null);
			AppManager.scheduleCass(getApplicationContext(), actionEvent.getActionEventName(), 
					(actionEvent.getEventState().equals(EventState.START.toString()) ? true : false));
			mAdapter.notifyDataSetChanged();
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
		if(mShownContent != null){
			if(pos == mShownContent.size() - 1){
				ActivityNamingDialogFragment.newInstance((ActivityNamingListener) this,
						new AlertDialogListener() {
							@Override
							public void onPositiveClick() {
							}
							@Override
							public void onNegativeClick() {
							}
							@Override
							public void onCancel() {
							}
						}).show(getFragmentManager());
				return;
			} else {
				// check flag
				HashMap<String, Object> data = mShownContent.get(pos);
				final String eventName = String.valueOf(data.get(ActionEventListAdapter.EVENT));
				ActionEvent actionEvent = null;
				if(!TextUtils.isEmpty(eventName)){
					boolean hasEvent = false;
					List<ActionEvent> aeList = AppManager.getAllLiveEvents();
					if(!aeList.isEmpty()){
						for(ActionEvent ae : aeList){
							if(ae.getActionEventName().equals(eventName)){
								hasEvent = true;
								actionEvent = ae;
								break;
							}
						}
					}
					// in case, no match found, so create a new one
					if((actionEvent == null) && !hasEvent){
						actionEvent =  new ActionEvent(eventName, System.currentTimeMillis());
					}
					
					final EventTimeMode em = hasEvent ? EventTimeMode.STOP : EventTimeMode.START;
					final ActionEvent para = actionEvent;
					EventTimePickerDialogFragment.newInstance(new OnEventTimeChangedListener(){
						@Override
						public void onPositiveClick() {
						}
						@Override
						public void onNegativeClick() {
						}
						@Override
						public void onCancel() {
						}
						@Override
						public void onConfirmed(long timestamp) {
							onEventConfirmed(em, para, timestamp);
						}
						@Override
						public void onDiscard() {
							onEventDiscard(em, para);
						}
					}, em, actionEvent).show(getFragmentManager());
				}
			}
		}
	}

	@Override
	public void OnTagNameInputCompleted(String tagName) {
		// save the name and refresh list 
		LoggerApp app = LoggerApp.getInstance();
		String tags = app.getEventTags();
		if(TextUtils.isEmpty(tags)){
			tags = tagName;
		} else {
			tags = new String(tags + ";" + tagName);			
		}
		app.saveEventTag(tags);
		updateEventList();
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (buttonView.getId() == R.id.logger_switcher) {
			mSwitchCheckState = isChecked;
			AppManager.toggleService(getApplicationContext(), isChecked);
			mListView.setVisibility(isChecked ? View.VISIBLE : View.INVISIBLE); 
			if(!isChecked){
				// update database and UI
				cleanUp();
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		final int itemId = item.getItemId();
		if(itemId == R.id.menu_shutdown){
			QuitAppDialogFragment.newInstance(new AlertDialogListener(){
				@Override
				public void onPositiveClick() {
					if(mSwitch != null){
						mSwitch.setChecked(false);
					} 
					if(mEventCount != null){
						mEventCount.setText("0");
						mEventCount.setVisibility(View.INVISIBLE);
					}
					getSupportActivity().finish();
				}
				@Override
				public void onNegativeClick() {
				}
				@Override
				public void onCancel() {
				}
			}).show(getFragmentManager());
			return true;
		} else if (itemId == R.id.menu_export_data) {
			AppManager.exportData(getApplicationContext());
			return true;
		} else if (itemId == R.id.menu_language_setting) {
			LanguageSettingDialogFragment.newInstance(new AlertDialogListener(){
				@Override
				public void onPositiveClick() {
					new Handler().postDelayed(new Runnable(){
						@Override
						public void run() {
							forceUpdateUI();
						}
					}, 50L);
				}
				@Override
				public void onNegativeClick() {
				}
				@Override
				public void onCancel() {
				}
			}).show(getFragmentManager());
			return true;
		} else if (itemId == R.id.menu_history) {
			invokeFragmentChanged(R.layout.frag_logger_history, null);
			return true;
		} else if (itemId == R.id.menu_history2) {
			invokeFragmentChanged(R.layout.frag_logger_history2, null);
			return true; 
		} else if (itemId == R.id.menu_toggle_service){
			if(mSwitch != null){
				final boolean disabled = !AppManager.isRunning(getApplicationContext());
				mSwitch.setChecked(disabled);
			} 
			return true;
		} 
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.panel_menu, menu);
		updateMenuItem(menu);
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		updateMenuItem(menu);
	}

	private void updateMenuItem(Menu menu){
		menu.findItem(R.id.menu_toggle_service).setTitle(AppManager.isRunning(getApplicationContext()) 
													? R.string.stop : R.string.start);	
		menu.findItem(R.id.menu_export_data).setTitle(R.string.export_data);
		menu.findItem(R.id.menu_shutdown).setTitle(R.string.quit);
		menu.findItem(R.id.menu_history).setTitle(R.string.view_history);
		menu.findItem(R.id.menu_history2).setTitle(R.string.view_history2);
		menu.findItem(R.id.menu_language_setting).setTitle(R.string.language_setting);
	}	
	
	private void cleanUp(){
		List<ActionEvent> aeList = AppManager.getAllLiveEvents();
		// stop all the event
		for(ActionEvent ae : aeList){
			ae.setState(ActionEvent.EventState.STOP);
			ae.confirmBreakTimestamp();
			AppManager.updateLiveEvent(ae);
			AppManager.sendEventBoradcast(getApplicationContext(), ae.getMessagePayload(), null);
			AppManager.scheduleCass(getApplicationContext(), ae.getActionEventName(), false);
			clearEventDuration(ae);
			mActionEventList.remove(ae);	
		}
		mAdapter.notifyDataSetChanged();
	}
	

	void onEventConfirmed(EventTimeMode em, ActionEvent event, long timestamp) {
		if((em == null) || (event == null) || (timestamp <= 0)) return;
		ActionEvent actionEvent = null;
		if(em == EventTimeMode.START){
			// start the event
			actionEvent = new ActionEvent(event.getActionEventName(), timestamp);
			actionEvent.setState(ActionEvent.EventState.START);
			AppManager.addALiveEvent(actionEvent);
			mActionEventList.add(actionEvent);
			event = actionEvent;
		} else if(em == EventTimeMode.STOP){
			// stop the event
			event.setState(ActionEvent.EventState.STOP);
			event.setBreakTimestamp(timestamp);
			AppManager.updateLiveEvent(event);
			// clear the time 
			clearEventDuration(event);		
			mActionEventList.remove(event);	
		}
		AppManager.sendEventBoradcast(getApplicationContext(), event.getMessagePayload(), null);
		AppManager.scheduleCass(getApplicationContext(), event.getActionEventName(), 
						(event.getEventState().equals(EventState.START.toString()) ? true : false));
		mAdapter.notifyDataSetChanged();
	}

	void onEventDiscard(EventTimeMode em, ActionEvent event) {
		if(event == null) return;
		event.confirmBreakTimestamp();
		event.setState(ActionEvent.EventState.INVALIDATE);
		AppManager.updateLiveEvent(event);
		clearEventDuration(event);
		mAdapter.notifyDataSetChanged();
		mActionEventList.remove(event);
		AppManager.sendEventBoradcast(getApplicationContext(), event.getMessagePayload(), null);
		AppManager.scheduleCass(getApplicationContext(), event.getActionEventName(), false);
	}

	private void clearEventDuration(final ActionEvent event){
		if((mShownContent != null) && !mShownContent.isEmpty()){
			for(HashMap<String, Object> data : mShownContent){
				if(event.getActionEventName().equals(String.valueOf(data.get(ActionEventListAdapter.EVENT)))){
					data.put(ActionEventListAdapter.DURATION, "");
					data.put(ActionEventListAdapter.CHECK, false);
				}
			}
		}		
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// if language has changed, UI has to be reloaded
		forceUpdateUI();
	}
	
	private class LoggerPanelNFCBroadcastReceiver extends LoggerNFCBroadcastReceiver {
		
		@Override
		public void handleNfcTagEvent(String eventName) {

			HashMap<String, Object> data = null;
			for(HashMap<String, Object> d : mShownContent) {
				if(eventName.equals(d.get(ActionEventListAdapter.EVENT))) {
					data = d;					
					break;
				}
 			}
			
			if(data == null) return;
			
			handleLoggerData(eventName, data);
		}
	}
}