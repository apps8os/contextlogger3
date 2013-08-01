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
package org.apps8os.logger.android.storage;

import org.apps8os.logger.android.storage.IActionEventDatebase.ActionEventTable;

import android.database.Cursor;
import android.database.CursorWrapper;
import android.text.TextUtils;
import android.util.Log;

public class ActionEventCursor extends CursorWrapper {

	private static final String TAG = ActionEventCursor.class.getSimpleName();
	
	private final int colId;
	private final int colActionName;
	private final int colStartTimestamp;
	private final int colBreakTimestamp;
	private final int colIsHistory;
	private final int colState;
	private final int colActionNote;
	private final int colStartDelay;
	private final int colBreakDelay;
	
	public ActionEventCursor(Cursor cursor) {
		super(cursor);
		colId = cursor.getColumnIndex(ActionEventTable.Col._ID.name);
		colActionName = cursor.getColumnIndex(ActionEventTable.Col.ACTION_NAME.name);
		colStartTimestamp = cursor.getColumnIndex(ActionEventTable.Col.START_TIMESTAMP.name);
		colBreakTimestamp = cursor.getColumnIndex(ActionEventTable.Col.BREAK_TIMESTAMP.name);
		colIsHistory = cursor.getColumnIndex(ActionEventTable.Col.IS_HISTORY.name);
		colState = cursor.getColumnIndex(ActionEventTable.Col.STATE.name);
		colActionNote = cursor.getColumnIndex(ActionEventTable.Col.ACTION_NOTE.name);
		colStartDelay = cursor.getColumnIndex(ActionEventTable.Col.START_DELAY.name);
		colBreakDelay = cursor.getColumnIndex(ActionEventTable.Col.BREAK_DELAY.name);
	}

	final int getDatabaseIndex() {
		return getInt(colId);
	}

	public final String getEventState() {
		return getString(colState);
	}
	
	public final long getStartDelay() {
		return getLong(colBreakDelay);
	}
	
	public final long getBreakDelay() {
		return getLong(colStartDelay);
	}
	
	public final String getActionName() {
		return getString(colActionName);
	}
	
	public final boolean hasNote() {
		return !TextUtils.isEmpty(getString(colActionNote));
	}
	
	public final boolean isHistory() {
		return (getLong(colIsHistory) == 1);
	}
	
	public final long getStartTimestamp() {
		return getLong(colStartTimestamp);
	}
	
	public final String getNoteContent() {
		return getString(colActionNote);
	}
	
	public final long getBreakTimestamp() {
		return getLong(colBreakTimestamp);
	}
	
	public ActionEventCursor moveToFirstCursor() {
		moveToFirst();
		return this;
	}
	
	@Override
	public void close() {
		super.close();
		Log.v(TAG, "Cursor close"); 
	}
}