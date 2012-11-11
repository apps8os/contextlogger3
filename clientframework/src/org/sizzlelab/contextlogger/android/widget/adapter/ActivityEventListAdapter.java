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

import android.app.Activity;
import android.content.Context;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

public class ActivityEventListAdapter extends BaseAdapter implements View.OnCreateContextMenuListener{

	private OnActivityEventUpdateListener mListener;
	private ActivityEventListViewHolder mListViewHolder; 
	
	private ArrayList<HashMap<String, Object>> mAppList;
    private LayoutInflater mInflater;
	private Activity mActivity;
	private String[] mKeyString;
	private int[] mValueViewID;
	private int mResourceId;
	
	public ActivityEventListAdapter(Activity activity, ArrayList<HashMap<String, Object>> appList, 
			int resource, String[] from, int[] to, OnActivityEventUpdateListener listener){
		mAppList = appList;
		mActivity = activity;
		mInflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mKeyString = new String[from.length];
		mValueViewID = new int[to.length];
		System.arraycopy(from, 0, mKeyString, 0, from.length);
		System.arraycopy(to, 0, mValueViewID, 0, to.length);
		mResourceId = resource;
		mListener = listener;  
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
			mListViewHolder = (ActivityEventListViewHolder) convertView.getTag();
		} else {
			convertView = mInflater.inflate(mResourceId, null);
			mListViewHolder = new ActivityEventListViewHolder();
			mListViewHolder.StartTime = (TextView)convertView.findViewById(mValueViewID[1]);
			mListViewHolder.ActivityEventName = (TextView)convertView.findViewById(mValueViewID[0]);
			mListViewHolder.EventDuration = (TextView)convertView.findViewById(mValueViewID[2]);
			mListViewHolder.Stop = (ImageButton)convertView.findViewById(mValueViewID[3]);
			convertView.setTag(mListViewHolder);
		}
		
		HashMap<String, Object> appInfo = mAppList.get(position);
		final int order = position;
        if (appInfo != null) {
        	mListViewHolder.StartTime.setText(String.valueOf(appInfo.get(mKeyString[1])));
        	mListViewHolder.ActivityEventName.setText(String.valueOf(appInfo.get(mKeyString[0])));
        	mListViewHolder.EventDuration.setText(String.valueOf(appInfo.get(mKeyString[2])));
        	mListViewHolder.Stop.setOnClickListener(new OnClickListener(){
				public void onClick(View v) {
					if(mListener != null) mListener.onStopButtonClick(order);
				}
        	});
        }
        convertView.setOnCreateContextMenuListener(this);
		return convertView;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
	}
	
	private class ActivityEventListViewHolder {
		TextView ActivityEventName;
		TextView StartTime;
		TextView EventDuration;
		ImageButton Stop;
    }

	public static interface OnActivityEventUpdateListener{
		void onStopButtonClick(int position);
	}
	
}
