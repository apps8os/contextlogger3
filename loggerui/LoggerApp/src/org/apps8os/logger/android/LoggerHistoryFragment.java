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
package org.apps8os.logger.android;

import java.util.ArrayList;
import java.util.HashMap;

import org.apps8os.logger.android.app.BaseFragment;
import org.apps8os.logger.android.manager.AppManager;
import org.apps8os.logger.android.model.ActionEvent;
import org.apps8os.logger.android.model.ActionEvent.EventState;
import org.apps8os.logger.android.model.EventInfo;
import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Activity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;

/**
 * Display the record history in a list view
 * 
 * @author Chao Wei
 *
 */
public class LoggerHistoryFragment extends BaseFragment {
	
	private ArrayList<HashMap<String, Object>> mShownContent = null;
	private SimpleAdapter mAdapter = null;
	private ArrayList<ActionEvent> mActionEventList = new ArrayList<ActionEvent>();
	private ListView mListView = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		final ActionBar actionBar = getSupportActivity().getSupportActionBar();
		actionBar.setTitle(R.string.history);
		actionBar.getCustomView().setVisibility(View.GONE);
		actionBar.setDisplayHomeAsUpEnabled(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.frag_logger_history, container, false); 
		mListView = (ListView)view.findViewById(android.R.id.list);
		mListView.setEmptyView(view.findViewById(R.id.text_veiw_no_history_data));
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
		mActionEventList = AppManager.getAllHistoryEvents();
		
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
				R.layout.template_history_list_item, new String[]{"Time", "EventAndNote" , "State"}, 
				new int[]{R.id.text_view_event_history_time, R.id.text_view_event_history_name_and_note,
				R.id.text_view_event_action_history});

		mListView.setAdapter(mAdapter);
	}
	
	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		if(menu != null){
			menu.clear();
		}
	}
}
