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
 * Chao Wei (chao.wei@aalto.fi)
 */
package org.sizzlelab.contextlogger.android;

import java.util.ArrayList;
import java.util.HashMap;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.ListFragment;
import org.sizzlelab.contextlogger.android.model.ActionEvent;
import org.sizzlelab.contextlogger.android.model.EventInfo;
import org.sizzlelab.contextlogger.android.model.EventState;
import org.sizzlelab.contextlogger.android.model.handler.ActionEventHandler;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
@Deprecated 
public class LoggerHistoryFragment extends ListFragment{

	private ArrayList<HashMap<String, Object>> mShownContent = null;
	private SimpleAdapter mAdapter = null;
	private ArrayList<ActionEvent> mActionEventList = new ArrayList<ActionEvent>();
	private View mNoData = null;
	
	public LoggerHistoryFragment(){
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.logger_history, container, false); 
		mNoData = view.findViewById(R.id.text_veiw_no_history_data);
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		refreshUI();
	}
	
	private void refreshUI(){
		if( mShownContent == null){
			 mShownContent = new ArrayList<HashMap<String, Object>>();
		}
		if(!mShownContent.isEmpty()){
			mShownContent.clear();
			mAdapter = null;
			mActionEventList.clear();
		}
		mActionEventList = ActionEventHandler.getInstance().getAllItems(getSupportActivity().getApplicationContext(), true);
		if(mActionEventList.isEmpty()){
			mNoData.setVisibility(View.VISIBLE);
			return;
		}else{
			mNoData.setVisibility(View.GONE);
		}
		
		ArrayList<EventInfo> mic = new ArrayList<EventInfo>();
		for(ActionEvent ae : mActionEventList){
			if(ae.hasNote()){
				EventInfo mi = new EventInfo(ae.getStartTimestamp(), ae.getNoteContent(), "");
				mic.add(mi);
			}else{
				EventInfo infoStart = new EventInfo(ae.getStartTimestamp(), ae.getActionEventName(), EventState.START.toString());
				mic.add(infoStart);
				EventInfo infoEnd = new EventInfo(ae.getBreakTimestamp(), ae.getActionEventName(), ae.getEventState());
				mic.add(infoEnd);
			}
		}
		EventInfo.sortList(mic);
		for(EventInfo ei : mic){
			HashMap<String, Object> data = new HashMap<String, Object>();
       		data.put("Time", ei.getTimeToString());
       		data.put("EventAndNote", ei.getEventAndNote());
       		data.put("State", ei.getState());
       		mShownContent.add(data);
		}

		mAdapter =  new SimpleAdapter(getSupportActivity().getApplicationContext(), mShownContent, 
				R.layout.history_list_item, new String[]{"Time", "EventAndNote" , "State"}, 
				new int[]{R.id.text_view_event_history_time, R.id.text_view_event_history_name_and_note,
				R.id.text_view_event_action_history});

		getListView().setAdapter(mAdapter);
	}
	
	
}
