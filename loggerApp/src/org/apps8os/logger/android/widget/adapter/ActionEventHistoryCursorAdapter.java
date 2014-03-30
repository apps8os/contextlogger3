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
package org.apps8os.logger.android.widget.adapter;

import org.apps8os.logger.android.R;
import org.apps8os.logger.android.model.ActionEvent.EventState;
import org.apps8os.logger.android.model.EventInfo;
import org.apps8os.logger.android.storage.ActionEventCursor;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ActionEventHistoryCursorAdapter extends CursorAdapter {

	public ActionEventHistoryCursorAdapter(Context context) {
		this(context, null, false);
	}

	private ActionEventHistoryCursorAdapter(Context context, Cursor c, boolean autoRequery) {
		super(context, c, autoRequery);
	}
	
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		EventHistoryListViewHolder vh = (EventHistoryListViewHolder) view.getTag();
		ActionEventCursor aec = (ActionEventCursor) cursor;
		final boolean hasNote = aec.hasNote();
		if(hasNote) {
			vh.EventStart.setVisibility(View.GONE);
			vh.EventStop.setVisibility(View.GONE);
			
			EventInfo mi = new EventInfo(aec.getStartTimestamp(), aec.getNoteContent(), "");
			vh.EventNoteTime.setText(mi.getTimeToString());
			vh.EventNoteContent.setText(mi.getEventAndNote());
		} else {
			vh.EventNote.setVisibility(View.GONE);
			
			EventInfo infoStart = new EventInfo(aec.getStartTimestamp(), aec.getActionName(), EventState.START.toString());
			vh.EventStartTime.setText(infoStart.getTimeToString());
			vh.EventStartName.setText(infoStart.getEventAndNote());
			vh.EventStartActionHistory.setText(infoStart.getState());
			EventInfo infoStop = new EventInfo(aec.getBreakTimestamp(), aec.getActionName(), aec.getEventState());
			vh.EventStopTime.setText(infoStop.getTimeToString());
			vh.EventStopName.setText(infoStop.getEventAndNote());
			vh.EventStopActionHistory.setText(infoStop.getState());
		}
	}

	@Override
	public View newView(Context context, Cursor c, ViewGroup parent) {
		View v = LayoutInflater.from(context).inflate(R.layout.template_history_list_item2, parent, false);
		v.setTag(new EventHistoryListViewHolder(v));
		bindView(v, context, c);
		return v;
	}

	static class EventHistoryListViewHolder {
		final View EventStart;
		final TextView EventStartTime;
		final TextView EventStartName;
		final TextView EventStartActionHistory;
		
		final View EventStop;
		final TextView EventStopTime;
		final TextView EventStopName;
		final TextView EventStopActionHistory;
		
		final View EventNote;
		final TextView EventNoteTime;
		final TextView EventNoteContent;
		
		EventHistoryListViewHolder(View v) {
			EventStart = v.findViewById(R.id.layout_event_history_start);
			EventStartTime = (TextView) v.findViewById(R.id.text_view_event_history_time_start);
			EventStartName = (TextView) v.findViewById(R.id.text_view_event_history_name_start);
			EventStartActionHistory = (TextView) v.findViewById(R.id.text_view_event_action_history_start);
			
			EventStop = v.findViewById(R.id.layout_event_history_stop);
			EventStopTime = (TextView) v.findViewById(R.id.text_view_event_history_time_stop);
			EventStopName = (TextView) v.findViewById(R.id.text_view_event_history_name_stop);
			EventStopActionHistory = (TextView) v.findViewById(R.id.text_view_event_action_history_stop);
			
			EventNote = v.findViewById(R.id.layout_event_history_note);
			EventNoteTime = (TextView) v.findViewById(R.id.text_view_event_history_time_note);
			EventNoteContent = (TextView) v.findViewById(R.id.text_view_event_history_note);
		}
    }
}
