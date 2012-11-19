package org.sizzlelab.contextlogger.android;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.json.JSONException;
import org.sizzlelab.contextlogger.android.io.MainPipeline;
import org.sizzlelab.contextlogger.android.model.ActionEvent;
import org.sizzlelab.contextlogger.android.model.EventState;
import org.sizzlelab.contextlogger.android.utils.Constants;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends ListActivity implements OnClickListener, OnItemSelectedListener,
														OnSharedPreferenceChangeListener, Constants{
	
	private static final int ADDING_TAG_DIALOG = 0x1000;
	private static final int HISTORY_DIALOG = 0x1001;
	private static final int QUIT_APP_ALERT = 0x1002;
	
	private ArrayList<ActionEvent> mActionEventList = new ArrayList<ActionEvent>();
	private ArrayList<ActionEvent> mHistoryList = new ArrayList<ActionEvent>();
	
	private SimpleAdapter mAdapter = null;
	private ArrayList<HashMap<String, Object>> mShownContent = null;
	private String[] mTagArray = null;
	
	private Button mButtonStartActivity = null;
	private Spinner mSpinner = null;
	private TextView mTextViewCount = null;
	private TextView mTextViewStatus = null;
	private String mCurrentTag = null;
	private View mNoData = null;
	private HashSet<String> mEventTagList = new HashSet<String>();
	
	private String mCountWords = null;

	private final BroadcastReceiver mTotalCountReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			long totalCount = intent.getExtras().getLong("totalCount", -1);
			if(totalCount > 0){
				mCountWords = new String("Total count: " + Long.toString(totalCount));
				if(mTextViewCount != null){
					mTextViewCount.setText(mCountWords);				
				}				
			}
		}
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_act);
		initUI();
		reloadData(savedInstanceState);
		MainPipeline.getSystemPrefs(getApplicationContext()).registerOnSharedPreferenceChangeListener(this);
		setupReveiver();
		startService();
	}
	
	private void reloadData(Bundle savedInstanceState){
		// reload data, if any
		if(savedInstanceState != null){
			ArrayList<ActionEvent> actionList = savedInstanceState.getParcelableArrayList("eventActionList");
			if(!mActionEventList.isEmpty()){
				 mActionEventList.clear();
			}
			mActionEventList.addAll(actionList);
			ArrayList<ActionEvent> historyList = savedInstanceState.getParcelableArrayList("eventHistory");
			if(!mHistoryList.isEmpty()){
				mHistoryList.clear();
			}
			mHistoryList.addAll(historyList);
			final String strTag = savedInstanceState.getString("strTag");
			if(!TextUtils.isEmpty(strTag)){
				String[] tagArray = TextUtils.split(strTag, ";");
				if(tagArray != null){
					for(String tag : tagArray){
						mEventTagList.add(tag);
					}
				}
			}
			mCountWords = savedInstanceState.getString("countWords");
			if(!TextUtils.isEmpty(mCountWords)){
				mTextViewCount.setText(mCountWords);
			}		
		}
	}
	
	private void setupReveiver(){
		IntentFilter filter = new IntentFilter();
		filter.addAction(COUNT_ACTION);
		registerReceiver(mTotalCountReceiver, filter);		
	}
	
	@Override
	protected void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putParcelableArrayList("eventActionList", mActionEventList);
		savedInstanceState.putParcelableArrayList("eventHistory", mHistoryList);
		final String strTag = TextUtils.join(";", mEventTagList.toArray(new String[mEventTagList.size()]));
		savedInstanceState.putString("strTag", strTag);
		savedInstanceState.putString("countWords", mCountWords);
	}

	@Override
	protected void onRestoreInstanceState(Bundle state) {
		super.onRestoreInstanceState(state);
		reloadData(state);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if (MainPipeline.COUNT_KEY.equals(key)) {
			Intent intent = new Intent();
			intent.setAction(COUNT_ACTION);
			intent.putExtra("totalCount", MainPipeline.getScanCount(getApplicationContext()));
			sendBroadcast(intent);
		}
	}
	
	private void initUI(){
		mButtonStartActivity = (Button)findViewById(R.id.button_start_event);
		mButtonStartActivity.setOnClickListener(this);
		mTextViewCount = (TextView)findViewById(R.id.text_veiw_data_count);
		mTextViewStatus = (TextView)findViewById(R.id.text_view_service_state);
		mSpinner = (Spinner)findViewById(R.id.spinner_action_list);
		mSpinner.setOnItemSelectedListener(this);
		mNoData = findViewById(R.id.text_veiw_no_data);
	}

	@Override
	protected void onResume() {
		super.onResume();
		updateEventList();
		refreshSpinner();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		unregisterForContextMenu(getListView());
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mTotalCountReceiver);
	}

	private void updateEventList(){
		if( mShownContent == null){
			 mShownContent = new ArrayList<HashMap<String, Object>>();
		}
		if(!mShownContent.isEmpty()){
			mShownContent.clear();
			mAdapter = null;
		}
		for(ActionEvent ae : mActionEventList){
       		HashMap<String, Object> data = new HashMap<String, Object>();
       		data.put("DateTime", ae.getStartTime());
    		data.put("Event", ae.getActionEventName());
    		data.put("Duartion", ae.getDuration());
    		mShownContent.add(data);
		}
		if(mActionEventList.isEmpty()){
			mNoData.setVisibility(View.VISIBLE);
		} else{
			mNoData.setVisibility(View.GONE);
		}
		mAdapter =  new SimpleAdapter(getApplicationContext(), mShownContent, 
									R.layout.event_list_item, new String[]{"DateTime", "Event" , "Duartion"}, 
									new int[]{R.id.text_view_event_start_datetime, R.id.text_view_event_tag_name,
									R.id.text_view_event_duration});

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
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getApplicationContext(),
											android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
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
		mTextViewStatus.setText(R.string.service_running);
		Intent archiveIntent = new Intent(getApplicationContext(), MainPipeline.class);
		archiveIntent.setAction(MainPipeline.ACTION_ENABLE);
		startService(archiveIntent);
		if(mSpinner != null){
			mSpinner.setEnabled(true);
		}
		if(mButtonStartActivity != null){
			mButtonStartActivity.setEnabled(true);
		}
	}
	
	private void stopService(){
		mTextViewStatus.setText(R.string.service_stopped);
		Intent archiveIntent = new Intent(getApplicationContext(), MainPipeline.class);
		archiveIntent.setAction(MainPipeline.ACTION_DISABLE);
		startService(archiveIntent);
		if(mSpinner != null){
			mSpinner.setEnabled(false);
		}
		if(mButtonStartActivity != null){
			mButtonStartActivity.setEnabled(false);
		}
		for(ActionEvent ae : mActionEventList){
			ae.confirmBreakTimestamp();
			ae.setState(EventState.STOP);
			mHistoryList.add(ae);
			notifyEvent(ae);
		}
		mActionEventList.clear();
		mEventTagList.clear();
		mCurrentTag = null;
		updateEventList();
	}
	
	private void toggleService(final boolean isRunning){
		// check the service flag
		if(isRunning){
			stopService();
		} else {
			startService();
		}
	}
	
	private void startEvent(){
		if(!TextUtils.isEmpty(mCurrentTag)){
			ActionEvent ae = new ActionEvent(mCurrentTag);
			ae.setState(EventState.START);
			mActionEventList.add(ae);
			mButtonStartActivity.setEnabled(false);
			notifyEvent(ae);
			mEventTagList.add(mCurrentTag);
		}
	}

	private void notifyEvent(ActionEvent ae){
		final String payload = ae.getMessagePayload();
		if(!TextUtils.isEmpty(payload)){
			Intent i = new Intent();
			i.setAction(CUSTOM_INTENT_ACTION);
			i.putExtra("APPLICATION_ACTION", payload);
			sendBroadcast(i);
//			exportData();
			updateEventList();
		} else {
			ClientApp.getInstance().showToastMessage("Client error!");
		}
	}
	
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
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
	public void onNothingSelected(AdapterView<?> arg0) {
	}
	
	private void exportData(){
		System.out.println("MainActivity: exportData called...");
		Intent archiveIntent = new Intent(this, MainPipeline.class);
		archiveIntent.setAction(MainPipeline.ACTION_ARCHIVE_DATA);
		startService(archiveIntent);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		if (v.getId() == android.R.id.list) {
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.list_context_menu, menu);
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
		    menu.setHeaderTitle(mActionEventList.get(info.position).getActionEventName());
		}
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		ActionEvent ae = mActionEventList.get(info.position);
		switch(item.getItemId()){
			case R.id.context_menu_stop:
				ae.setState(EventState.STOP);
				break;
			case R.id.context_menu_cancel:
				ae.setState(EventState.CANCEL);
				break;
		}
		for(String tag : mEventTagList){
			if(tag.equals(ae.getActionEventName())){
				mEventTagList.remove(tag);
				break;
			}
		}
		ae.confirmBreakTimestamp();
		mHistoryList.add(ae);
		notifyEvent(ae);
		mActionEventList.remove(info.position);
		updateEventList();
		refreshSpinner();
		return true;
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.button_start_event:
				startEvent();
				break;
		}
	}
	
	private Dialog BuildAddingTagDialog(Activity activity){
	  	AlertDialog.Builder builder = new AlertDialog.Builder(activity);
	  	LayoutInflater inflater = LayoutInflater.from(activity);
	  	final View noteView = inflater.inflate(R.layout.add_tag_dialog, null);
	  	final EditText tagContent = (EditText)noteView.findViewById(R.id.edit_text_tag);
	  	builder.setTitle(R.string.add_tag);
	  	builder.setView(noteView);
	  	builder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				final String tagName = tagContent.getEditableText().toString();
				if(!TextUtils.isEmpty(tagName)){
					saveEventTag(tagName);
				}
				removeDialog(ADDING_TAG_DIALOG);
			}
		});
	  	builder.setOnCancelListener(new OnCancelListener(){
			@Override
			public void onCancel(DialogInterface dialog) {
				removeDialog(ADDING_TAG_DIALOG);
			}
	  	});
		return builder.create();
	}
	
	private Dialog BuildHistoryDialog(Activity activity){
	  	AlertDialog.Builder builder = new AlertDialog.Builder(activity);
	  	ListView listView = new ListView(activity.getApplicationContext());
	  	ArrayList<HashMap<String, Object>> history = new ArrayList<HashMap<String, Object>>();
	  	for(ActionEvent ae : mHistoryList){
    		HashMap<String, Object> data = new HashMap<String, Object>();
    		data.put("DateTime", ae.getStartTime());
    		data.put("Event", ae.getActionEventName());
    		data.put("Duartion", ae.getDuration());
        	data.put("Action", ae.getEventState());  		
        	history.add(data);
	  	}
	  	SimpleAdapter adapter = new SimpleAdapter(activity.getApplicationContext(), history,
	  								R.layout.history_list_item, new String[]{"DateTime", "Event" , "Duartion", "Action"}, 
	  								new int[]{R.id.text_view_event_start_datetime_history, R.id.text_view_event_tag_name_history,
									R.id.text_view_event_duration_history, R.id.text_view_event_action_history});
	  	listView.setAdapter(adapter);
	  	builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				removeDialog(HISTORY_DIALOG);
			}
		});
	  	builder.setOnCancelListener(new OnCancelListener(){
			@Override
			public void onCancel(DialogInterface dialog) {
				removeDialog(HISTORY_DIALOG);
			}
	  	});
	  	builder.setView(listView);
		return builder.create();
	}

	private Dialog BuildQuitComfiramtionDialog(Activity activity){
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
    	builder.setCancelable(false);
    	builder.setTitle(R.string.app_quit_title);
    	builder.setIcon(android.R.drawable.ic_dialog_info);
    	builder.setMessage(R.string.app_quit_content);
    	builder.setPositiveButton(R.string.ok, 
    			new DialogInterface.OnClickListener() {					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						removeDialog(QUIT_APP_ALERT);
						 MainActivity.this.finish();
					}
				});
    	builder.setNegativeButton(R.string.event_cancel,     			
    			new DialogInterface.OnClickListener() {					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						removeDialog(QUIT_APP_ALERT);
					}
				});				
		return builder.create();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK ){
			showDialog(QUIT_APP_ALERT);
			return true;
		} 
		return super.onKeyDown(keyCode, event);
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
		refreshSpinner();
	}
	
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if(menu != null){
			menu.clear();
		}
		updateMenuItem(menu);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		updateMenuItem(menu);
		return super.onCreateOptionsMenu(menu);
	}

	private void updateMenuItem(Menu menu){
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		MenuItem item = menu.findItem(R.id.menu_toggle_service);
		// check the service flag
		if(MainPipeline.isEnabled(getApplicationContext())){
			item.setTitle(R.string.btn_stop_service);			
		}else {
			item.setTitle(R.string.btn_start_service);
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()){
			case R.id.menu_edit:
				showDialog(ADDING_TAG_DIALOG);
				break;
			case R.id.menu_export_data:
				exportData();
				break;
			case R.id.menu_history:
				if(mHistoryList.isEmpty()){
					ClientApp.getInstance().showToastMessage("No history record.");
				}else {
					showDialog(HISTORY_DIALOG);					
				}
				break;
			case R.id.menu_toggle_service:
				toggleService(MainPipeline.isEnabled(getApplicationContext()));
				break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id){
			case ADDING_TAG_DIALOG:
				return BuildAddingTagDialog(this);
			case HISTORY_DIALOG:
				return BuildHistoryDialog(this);
			case QUIT_APP_ALERT:
				return BuildQuitComfiramtionDialog(this);
		}
		return null;
	}

}
