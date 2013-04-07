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
package org.sizzlelab.contextlogger.android.model.handler;

import java.util.ArrayList;

import org.sizzlelab.contextlogger.android.model.ActionEvent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import fi.aalto.chaow.android.utils.DBUtils;


/**
 * Action event database.
 * 
 * Due to prototype, this class need to be re-design, 
 * and the package name need to be changed as well.
 * 
 * @author Chao Wei
 *
 */
@Deprecated
public class ActionEventHandler extends DBUtils{

	private static ActionEventHandler mHandler = null;
	
	private static final String CREATION_SQL = "create table actionEvent (_id integer primary key autoincrement, actionName varchar(80), startTimestamp long, breakTimestamp long, isHistory long, state varchar(80), actionNote text, startDelay long, breakDelay long)";
	private static final String TABLE_NAME = "actionEvent";	
	private static final String KEY_ID	= "_id";
	private static final String KEY_ACTION_NAME = "actionName";
	private static final String KEY_START_TIMESTAMP = "startTimestamp";
	private static final String KEY_BREAK_TIMESTAMP = "breakTimestamp";
	private static final String KEY_IS_HISTORY = "isHistory";
	private static final String KEY_STATE = "state";
	private static final String KEY_ACTION_NOTE = "actionNote";
	private static final String KEY_START_DEALY = "startDelay";
	private static final String KEY_BREAK_DEALY = "breakDelay";
	
	
	private ActionEventHandler(){
	}
	
	public static final ActionEventHandler getInstance(){
		if(mHandler == null){
			mHandler = new ActionEventHandler();
		}
		return mHandler;
	}

	public boolean insert(Context context, ActionEvent ae) {
		DatabaseHelper dh = new DatabaseHelper(context, TABLE_NAME);
		SQLiteDatabase db = dh.getWritableDatabase();
		
		ContentValues target = null;
		if(db.isOpen()){
			target = new ContentValues();
			target.put(KEY_ACTION_NAME, ae.getActionEventName());
			target.put(KEY_START_TIMESTAMP, ae.getStartTimestamp());
			target.put(KEY_STATE, ae.getEventState());
			target.put(KEY_BREAK_TIMESTAMP, ae.getBreakTimestamp());
			target.put(KEY_IS_HISTORY, getBooleanToLong(ae.isHistory()));
			target.put(KEY_ACTION_NOTE, ae.getNoteContent());
			target.put(KEY_START_DEALY, ae.getStartDelay());
			target.put(KEY_BREAK_DEALY, ae.getBreakDelay());
			db.insert(TABLE_NAME, null, target);
		} else {
			db.close();
			return false;
		}
		target = null;
		return isInsert(db, ae);
	}
	
	public boolean isInsert(Context context,  ActionEvent ae) {
		DatabaseHelper dh = new DatabaseHelper(context, TABLE_NAME);
		SQLiteDatabase db = dh.getReadableDatabase();
		return isInsert(db, ae);
	}
	
	private boolean isInsert(SQLiteDatabase db,  ActionEvent ae) {
		Cursor c = db.query(TABLE_NAME, new String[]{KEY_START_TIMESTAMP}, 
							KEY_START_TIMESTAMP + "=" + ae.getStartTimestamp(), 
							null, null, null, null);
		// check the record  
		if(c.getCount() > 0){
			c.close();
			db.close();			
			return true;
		} else {
			c.close();
			db.close();
			return false;			
		}
	}
	
	public boolean update(Context context, ActionEvent ae){
		ContentValues target = new ContentValues();
		target.put(KEY_STATE, ae.getEventState());
		target.put(KEY_BREAK_TIMESTAMP, ae.getBreakTimestamp());
		target.put(KEY_IS_HISTORY, getBooleanToLong(ae.isHistory()));
		target.put(KEY_BREAK_DEALY, ae.getBreakDelay());
		return update(context, ae, target);
	}
	
	private boolean update(Context context,  ActionEvent ae, final ContentValues target){
		DatabaseHelper dh = new DatabaseHelper(context, TABLE_NAME);
		SQLiteDatabase db = dh.getWritableDatabase();
 
		if(db.isOpen()){
			db.update(TABLE_NAME, target, KEY_START_TIMESTAMP + "=?", new String[]{Long.toString(ae.getStartTimestamp())});	
			db.close();
			return true;
		}else{
			db.close();
			return false;
		}
	}	
	
	public ArrayList<ActionEvent> getAllItems(Context context){
		ArrayList<ActionEvent> aeList = new ArrayList<ActionEvent>();
		aeList.addAll(getAllItems(context, false));
		aeList.addAll(getAllItems(context, true));
		return aeList;
	}
	
	public ArrayList<ActionEvent> getAllItems(Context context, boolean isHistory) {
		DatabaseHelper dh = new DatabaseHelper(context, TABLE_NAME);
		SQLiteDatabase db = dh.getReadableDatabase();
		
		ArrayList<ActionEvent> aeList = new ArrayList<ActionEvent>();
		Cursor c = db.query(TABLE_NAME, null, KEY_IS_HISTORY + "='" + getBooleanToLong(isHistory) + "'", 
																null, null, null, "_id" + " DESC" );
		while(c.moveToNext()){
			ActionEvent ae = new ActionEvent(c.getString(c.getColumnIndex(KEY_ACTION_NAME)), 
											 c.getLong(c.getColumnIndex(KEY_START_TIMESTAMP)),
											 c.getString(c.getColumnIndex(KEY_ACTION_NOTE)),
											 getLongToBoolean(c.getLong(c.getColumnIndex(KEY_IS_HISTORY))),
											 c.getLong(c.getColumnIndex(KEY_START_DEALY)),
											 c.getLong(c.getColumnIndex(KEY_START_DEALY)));  
			ae.setBreakTimestamp(c.getLong(c.getColumnIndex(KEY_BREAK_TIMESTAMP)));
			ae.setState(c.getString(c.getColumnIndex(KEY_STATE)));
			aeList.add(ae);
		}
		c.close();
		db.close();
		return aeList;
	}
	
	@Override
	protected boolean deleteAll(Context context) {
		DatabaseHelper dh = new DatabaseHelper(context, TABLE_NAME);
		SQLiteDatabase db = dh.getWritableDatabase();
		
		if(hasRecord(db, TABLE_NAME, KEY_ID)){
			SQLiteDatabase dbDel = dh.getWritableDatabase();
			dbDel.delete(TABLE_NAME, null, null);
			dbDel.close();
			return true;
		}
		return false;
	}
	
	private class DatabaseHelper extends SQLiteOpenHelper {

		private static final int VERSION = 1;

		public DatabaseHelper(Context context, String name, CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		public DatabaseHelper(Context context, String name) {
			this(context, name, null, VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// the table of the database is set by programmer
			db.execSQL(CREATION_SQL);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		}
		
	}
}
