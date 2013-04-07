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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.apps8os.logger.android.AbstractLoggerPanelFragment.ActivityNamingDialog.ActivityNamingListener;
import org.apps8os.logger.android.EventTimePickerFragmentDialog.EventTimeMode;
import org.apps8os.logger.android.manager.AppManager;
import org.apps8os.logger.android.manager.LoggerManager;
import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.widget.Switch;
import org.json.JSONException;
import org.sizzlelab.contextlogger.android.model.ActionEvent;
import org.sizzlelab.contextlogger.android.model.EventState;
import org.sizzlelab.contextlogger.android.model.handler.ActionEventHandler;
import org.sizzlelab.contextlogger.android.widget.adapter.ActionEventListAdapter;
import org.sizzlelab.contextlogger.android.widget.adapter.ActionEventListAdapter.OnCustomEventChangeListener;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
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

import fi.aalto.chaow.android.app.BaseAlertDialog.AlertDialogListener;

public class LoggerPanelFragment extends AbstractLoggerPanelFragment implements OnCheckedChangeListener, ActivityNamingListener,
																	OnItemClickListener, OnItemLongClickListener{
	
	private Handler mHandler = new Handler();
	private Runnable mTimedTask = new Runnable(){
		@Override
		public void run() {
			List<ActionEvent> aeList = ActionEventHandler.getInstance().getAllItems(getApplicationContext(), false);
			if((mShownContent != null) && (mAdapter != null)){ //&& mIsRunning){
				for(int i = 0; i < aeList.size(); i++){
					ActionEvent ae = aeList.get(i);
					for(HashMap<String, Object> data : mShownContent){
						if(ae.getActionEventName().equals(data.get("Event"))){
							data.put(ActionEventListAdapter.DURATION, ae.getDuration(0));
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
			mHandler.postDelayed(mTimedTask , 500);
		}
	};
	
	private View mSwitcherView = null;
	private TextView mEventCount = null;
	private Switch mSwitch = null;
	
	private ArrayList<HashMap<String, Object>> mShownContent = null;
	private ArrayList<ActionEvent> mActionEventList = null;	
	
	private ActionEventListAdapter mAdapter = null;
	private ListView mListView = null;
	
	@Override
	protected  boolean hasOptionsMenu() {
		return true;
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
//		mListView.setOnItemLongClickListener(this);
		return view;
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
        mEventCount = (TextView)mSwitcherView.findViewById(R.id.text_view_running_event);
        
        // if the device is honeycomb, we need to
        // adjust the UI a little bit. Otherwise,
        // the switch would look weird.
        if(AppManager.isHoneycomb()){
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
        getSupportActivity().getSupportActionBar().setCustomView(mSwitcherView, lp);
        mSwitch.setOnCheckedChangeListener(this);
		getSupportActivity().getSupportActionBar().setTitle(R.string.app_name);
	}
	
	private void forceUpdateUI(){
		updateEventList();
		((ViewGroup)getSupportActivity().getSupportActionBar().getCustomView()).removeAllViews();
		initCustomView();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		updateEventList();
		mHandler.post(mTimedTask);
	}
	
	@Override
	public void onStop() {
		super.onStop();
		mHandler.removeCallbacks(mTimedTask);
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

		for(int i = 0; i < list.size(); i++){
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
						showActivityNameDeletionDialog(new AlertDialogListener(){
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
						}, tag);
					}
		});			
		mListView.setAdapter(mAdapter);
		mAdapter.notifyDataSetChanged();			
		mListView.invalidate();
		mActionEventList = ActionEventHandler.getInstance().getAllItems(getApplicationContext(), false);	
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
	void onEventConfirmed(EventTimeMode em, ActionEvent event, long timestamp) {
		if((em == null) || (event == null) || (timestamp <= 0)) return;
		ActionEvent actionEvent = null;
		ActionEventHandler handler = ActionEventHandler.getInstance();
		if(em == EventTimeMode.START){
			// start the event
			actionEvent = new ActionEvent(event.getActionEventName(), timestamp);
			actionEvent.setState(EventState.START);
			handler.insert(getApplicationContext(), actionEvent);
			mActionEventList.add(actionEvent);
			event = actionEvent;
		}else if(em == EventTimeMode.STOP){
			// stop the event
			event.setState(EventState.STOP);
			event.setBreakTimestamp(timestamp);
			handler.update(getApplicationContext(), event);
			// clear the time 
			clearEventDuration(event);		
			mActionEventList.remove(event);	
		}
		LoggerManager.sendEventBoradcast(getApplicationContext(), event.getMessagePayload(), null);
		mAdapter.notifyDataSetChanged();
	}

	@Override
	void onEventDiscard(EventTimeMode em, ActionEvent event) {
		if(event == null) return;
		event.confirmBreakTimestamp();
		event.setState(EventState.INVALIDATE);
		ActionEventHandler.getInstance().update(getApplicationContext(), event);
		clearEventDuration(event);
		mAdapter.notifyDataSetChanged();
		mActionEventList.remove(event);
		LoggerManager.sendEventBoradcast(getApplicationContext(), event.getMessagePayload(), null);
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
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (buttonView.getId() == R.id.logger_switcher) {
			LoggerManager.toggleService(getApplicationContext(), isChecked);
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
			showQuitAppDialog();
			return true;
		} else if (itemId == R.id.menu_export_data) {
			LoggerManager.exportData(getApplicationContext());
			return true;
		} else if (itemId == R.id.menu_language_setting) {
			LanguageSettingFragmentDialog.newInstance(new AlertDialogListener(){
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
		} else if (itemId == R.id.menu_toggle_service){
			if(mSwitch != null){
				final boolean disabled = !LoggerManager.isRunning(getApplicationContext());
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
		menu.findItem(R.id.menu_toggle_service).setTitle(LoggerManager.isRunning(getApplicationContext()) 
													? R.string.stop : R.string.start);	
		menu.findItem(R.id.menu_export_data).setTitle(R.string.export_data);
		menu.findItem(R.id.menu_shutdown).setTitle(R.string.quit);
		menu.findItem(R.id.menu_history).setTitle(R.string.view_history);
		menu.findItem(R.id.menu_language_setting).setTitle(R.string.language_setting);
	}	
		
	@Override
	void quitApp(){
		if(mSwitch != null){
			mSwitch.setChecked(false);
		} 
		if(mEventCount != null){
			mEventCount.setText("0");
			mEventCount.setVisibility(View.INVISIBLE);
		}
		getSupportActivity().finish();
	}

	private void cleanUp(){
		ActionEventHandler handler = ActionEventHandler.getInstance();
		List<ActionEvent> aeList = handler.getAllItems(getApplicationContext(), false);
		// stop all the event
		for(ActionEvent ae : aeList){
			ae.setState(EventState.STOP);
			ae.confirmBreakTimestamp();
			handler.update(getApplicationContext(), ae);
			LoggerManager.sendEventBoradcast(getApplicationContext(), ae.getMessagePayload(), null);
			clearEventDuration(ae);
			mActionEventList.remove(ae);	
		}
		mAdapter.notifyDataSetChanged();
	}
	
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int pos, long id) {
		if(mShownContent != null){
			if(pos == mShownContent.size() - 1){
				return true;
			}else{
				ActionEventHandler handler = ActionEventHandler.getInstance();
				
				// check flag
				HashMap<String, Object> data = mShownContent.get(pos);
				final String eventName = String.valueOf(data.get(ActionEventListAdapter.EVENT));
				ActionEvent actionEvent = null;
				if(!TextUtils.isEmpty(eventName)){
					boolean hasEvent = false;
					List<ActionEvent> aeList = handler.getAllItems(getApplicationContext(), false);	
					if(!aeList.isEmpty()){
						for(ActionEvent ae : aeList){
							if(ae.getActionEventName().equals(eventName)){
								hasEvent = true;
								actionEvent = ae;
							}
						}
					}
					
					if(hasEvent){
						// stop the event
						actionEvent.confirmBreakTimestamp();
						actionEvent.setState(EventState.STOP);
						handler.update(getApplicationContext(), actionEvent);
						mActionEventList.remove(actionEvent);
						
						// clear the time  
						data.put(ActionEventListAdapter.DURATION, "");
						data.put(ActionEventListAdapter.CHECK, false);
					}else{
						// start the event
						actionEvent = new ActionEvent(eventName, System.currentTimeMillis());
						actionEvent.setState(EventState.START);
						handler.insert(getApplicationContext(), actionEvent);
						mActionEventList.add(actionEvent);
						data.put(ActionEventListAdapter.CHECK, true);
					}
					LoggerManager.sendEventBoradcast(getApplicationContext(), actionEvent.getMessagePayload(), null);
					mAdapter.notifyDataSetChanged();
				}
			}
		}
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
		if(mShownContent != null){
			if(pos == mShownContent.size() - 1){
				showEventNameCreationDialog();
				return;
			}else{
				ActionEventHandler handler = ActionEventHandler.getInstance();
				
				// check flag
				HashMap<String, Object> data = mShownContent.get(pos);
				final String eventName = String.valueOf(data.get(ActionEventListAdapter.EVENT));
				ActionEvent actionEvent = null;
				if(!TextUtils.isEmpty(eventName)){
					boolean hasEvent = false;
					List<ActionEvent> aeList = handler.getAllItems(getApplicationContext(), false);	
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
					showEventTimePickerDialog((hasEvent ? EventTimeMode.STOP : EventTimeMode.START), actionEvent);
				}
			}
		}
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// if language has changed, UI has to be reloaded
		forceUpdateUI();
	};
}