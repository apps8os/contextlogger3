package org.sizzlelab.contextlogger.android;

import java.util.ArrayList;
import java.util.HashMap;

import org.sizzlelab.contextlogger.android.model.ActionEvent;
import org.sizzlelab.contextlogger.android.model.EventState;
import org.sizzlelab.contextlogger.android.model.handler.ActionEventHandler;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;

import com.actionbarsherlock.app.SherlockListFragment;

public class LoggerHistoryFragment extends SherlockListFragment{

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
		mActionEventList = ActionEventHandler.getInstance().getAllItems(getSherlockActivity().getApplicationContext(), true);
		if(mActionEventList.isEmpty()){
			mNoData.setVisibility(View.VISIBLE);
			return;
		}else{
			mNoData.setVisibility(View.GONE);
		}
		for(ActionEvent ae : mActionEventList){
			if(ae.hasNote()){
				HashMap<String, Object> data = new HashMap<String, Object>();
	       		data.put("Time", ActionEvent.getTimeToString(ae.getStartTimestamp()));
	       		data.put("EventAndNote", ae.getNoteContent());
	       		mShownContent.add(data);
			}else{
				HashMap<String, Object> dataStart = new HashMap<String, Object>();
				dataStart.put("Time", ActionEvent.getTimeToString(ae.getStartTimestamp()));
				dataStart.put("EventAndNote", ae.getActionEventName());
				dataStart.put("State", EventState.START);
	       		mShownContent.add(dataStart);
				HashMap<String, Object> dataEnd = new HashMap<String, Object>();
				dataEnd.put("Time", ActionEvent.getTimeToString(ae.getBreakTimestamp()));
				dataEnd.put("EventAndNote", ae.getActionEventName());
				dataEnd.put("State", ae.getEventState());
	       		mShownContent.add(dataEnd);
			}
		}
		mAdapter =  new SimpleAdapter(getSherlockActivity().getApplicationContext(), mShownContent, 
				R.layout.history_list_item, new String[]{"Time", "EventAndNote" , "State"}, 
				new int[]{R.id.text_view_event_history_time, R.id.text_view_event_history_name_and_note,
				R.id.text_view_event_action_history});

		getListView().setAdapter(mAdapter);
	}
	
	
}
