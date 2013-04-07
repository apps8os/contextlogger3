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
package org.sizzlelab.contextlogger.android.widget.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import org.apps8os.logger.android.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Store the action (activity) data, and populate the 
 * data for the the list.
 * 
 * Due to prototype, this class need to be re-design, 
 * and the package name need to be changed as well.
 * 
 * @author Chao Wei
 *
 */
@Deprecated
public class ActionEventListAdapter extends BaseAdapter {

	public static final String EVENT = "Event";
	public static final String DURATION = "Duration";
	public static final String CHECK = "Check";
	public static final String CUSTOM = "Custom";	
	
	private EventListViewHolder mListViewHolder; 
	private ArrayList<HashMap<String, Object>> mAppList;
    private LayoutInflater mInflater;
	private Context mContext;

	private OnCustomEventChangeListener mListener = null;
	
	public ActionEventListAdapter(Context context, 
				ArrayList<HashMap<String, Object>> appList, OnCustomEventChangeListener listner){
		mAppList = appList;
		mContext = context;
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mListener = listner;
	}
	
	@Override
	public int getCount() {
		return mAppList.size();
	}

	@Override
	public Object getItem(int position) {
		return mAppList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView != null){
			mListViewHolder = (EventListViewHolder) convertView.getTag();
		} else {
			convertView = mInflater.inflate(R.layout.template_event_list_item, null);
			mListViewHolder = new EventListViewHolder();
			mListViewHolder.Icon = (ImageView) convertView.findViewById(R.id.image_button_list_item_icon);
			mListViewHolder.EventName = (TextView)convertView.findViewById(R.id.text_view_event_name);
			mListViewHolder.EventDuration = (TextView)convertView.findViewById(R.id.text_view_event_duration);
			mListViewHolder.Indicator = (CheckBox)convertView.findViewById(R.id.check_box_list_item_indicator);
//			mListViewHolder.Modify = convertView.findViewById(R.id.image_view_modify);
//			mListViewHolder.Modify.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					mListener
//				}
//			});
			mListViewHolder.Delete = convertView.findViewById(R.id.image_view_delete);
			convertView.setTag(mListViewHolder);
		}
		
		final HashMap<String, Object> appInfo = mAppList.get(position);
		if (appInfo != null) {
        	mListViewHolder.EventName.setText(String.valueOf(appInfo.get(EVENT)));
        	mListViewHolder.EventDuration.setText(String.valueOf(appInfo.get(DURATION)));
        	mListViewHolder.EventDuration.postInvalidate();
        	boolean check = (Boolean)appInfo.get(CHECK);
        	mListViewHolder.Indicator.setChecked(check);
        	mListViewHolder.Indicator.postInvalidate();
        	boolean custom = false;
        	try{
        		custom = (Boolean)appInfo.get(CUSTOM);
        	}catch(Exception e){
        	}
//        	mListViewHolder.Modify.setVisibility(custom ? (check ? View.GONE : View.VISIBLE) : View.GONE);
        	mListViewHolder.Delete.setVisibility(custom ? (check ? View.GONE : View.VISIBLE) : View.GONE);
			mListViewHolder.Delete.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(mListener != null) {
						mListener.onRemove(String.valueOf(appInfo.get(EVENT)));
					}
				}
			});
        }
        toggleVisibility((position == mAppList.size() - 1));
        convertView.postInvalidate();
 		return convertView;
	}

	private void toggleVisibility(boolean b){
    	mListViewHolder.Icon.setVisibility(b ? View.VISIBLE : View.GONE);
    	mListViewHolder.EventDuration.setVisibility(b ? View.GONE : View.VISIBLE);
    	mListViewHolder.Indicator.setVisibility(b ? View.GONE : View.VISIBLE);
    }
	
	static class EventListViewHolder {
		TextView EventName;
		TextView EventDuration;
		ImageView Icon;
		CheckBox Indicator;
		View Delete;
//		View Modify;
    }
	
	public static interface OnCustomEventChangeListener {
		void onRemove(final String tag);
//		void onModify(final String tag);
	}
}
