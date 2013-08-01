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

import org.apps8os.logger.android.model.ActionEvent;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ActionEventDatabase extends SQLiteOpenHelper implements IActionEventDatebase {
	
	private static final String TAG = ActionEventDatabase.class.getSimpleName();

	private SQLiteDatabase mDatabase;
	private final Object mLock = new Object();

	public ActionEventDatabase(Context context) {
		super(context.getApplicationContext(), "actionEvent.db", null, 1);
		mDatabase = getWritableDatabase();
	}

	@Override
	public void add(ActionEvent ae) {
		if(ae == null) return;
		
		ContentValues target = new ContentValues();
		target.put(ActionEventTable.Col.ACTION_NAME.name, ae.getActionEventName());
		target.put(ActionEventTable.Col.START_TIMESTAMP.name, ae.getStartTimestamp());
		target.put(ActionEventTable.Col.STATE.name, ae.getEventState());
		target.put(ActionEventTable.Col.BREAK_TIMESTAMP.name, ae.getBreakTimestamp());
		target.put(ActionEventTable.Col.IS_HISTORY.name, (ae.isHistory() ? 1 : 0));
		target.put(ActionEventTable.Col.ACTION_NOTE.name, ae.getNoteContent());
		target.put(ActionEventTable.Col.START_DELAY.name, ae.getStartDelay());
		target.put(ActionEventTable.Col.BREAK_DELAY.name, ae.getBreakDelay());
		
		synchronized (mLock) {		
			long returnValue = mDatabase.insert(ActionEventTable.name, null, target);	
			Log.d(TAG, (returnValue == -1) ? "error for insert." : "insert done.");
		}
	}

	@Override
	public void updateActionEventBreak(ActionEvent ae) {
		if(ae == null) return;
		
		ActionEventCursor allSavedCursor = getAllActionEventCursor(false);
		
		ContentValues target = new ContentValues();
		target.put(ActionEventTable.Col.STATE.name, ae.getEventState());
		target.put(ActionEventTable.Col.START_TIMESTAMP.name, ae.getStartTimestamp());
		target.put(ActionEventTable.Col.BREAK_TIMESTAMP.name, ae.getBreakTimestamp());
		target.put(ActionEventTable.Col.ACTION_NAME.name, ae.getActionEventName());
		target.put(ActionEventTable.Col.IS_HISTORY.name, (ae.isHistory() ? 1 : 0));
		target.put(ActionEventTable.Col.BREAK_DELAY.name, ae.getBreakDelay());

		while(allSavedCursor.moveToNext()) {
			if((ae.getStartTimestamp() == allSavedCursor.getStartTimestamp()) 
					&& ae.getActionEventName().equals(allSavedCursor.getActionName())) {
				target.put(ActionEventTable.Col._ID.name, allSavedCursor.getDatabaseIndex());
				synchronized (mLock) {	
					long returnValue = mDatabase.replace(ActionEventTable.name, null, target);	
					Log.d(TAG, (returnValue == -1) ? "error for replacement." : "replacement done.");
				}
			}
		}
		allSavedCursor.close();
	}	
	
	@Override
	public void addActionEventNote(ActionEvent ae) {
		add(ae);
	}
	
	@Override
	public ActionEventCursor getAllActionEventCursor() {
		synchronized (mLock) {							
			return new ActionEventCursor(mDatabase.query(ActionEventTable.name, null, null, null, null, null, ActionEventTable.Col._ID.name + " DESC" ));
		}
	}

	@Override
	public ActionEventCursor getAllActionEventCursor(boolean isHistory) {
		String selection = ActionEventTable.Col.IS_HISTORY + " = ?";
		String[] selectionArgs = new String[] { String.valueOf(isHistory ? 1 : 0) };
		synchronized (mLock) {							
			return new ActionEventCursor(mDatabase.query(ActionEventTable.name, null, selection, selectionArgs, null, null, null));
		}
	}
	
	@Override
	public synchronized void close() {
		super.close();
		Log.w(TAG, "Database closed.");
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(ActionEventTable.createSQL());
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		switch (oldVersion) {
		default:
			break;
		}
		Log.i(TAG, "Database upgraded.");
	}
}
