package org.apps8os.logger.android.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

public class CassCaseDatabase extends SQLiteOpenHelper implements ICassDatabase {
	
	private static final String TAG = CassCaseDatabase.class.getSimpleName();
	
	private SQLiteDatabase mDatabase;
	private final Object mLock = new Object();
	
	public CassCaseDatabase(Context context){
		super(context.getApplicationContext(), "cassCase.db", null, 1);
		mDatabase = getWritableDatabase();
	}

	@Override
	public void addEvent(String eventName, long interval) {
		if(TextUtils.isEmpty(eventName)) return;
		
		ContentValues target = new ContentValues();
		target.put(CassEventTable.Col.EVENT_NAME.name, eventName);
		target.put(CassEventTable.Col.ADD_TIMESTAMP.name, System.currentTimeMillis());
		target.put(CassEventTable.Col.INTERVAL.name, interval);
		
		synchronized (mLock) {		
			long returnValue = mDatabase.insert(CassEventTable.name, null, target);	
			Log.d(TAG, (returnValue == -1) ? "error for insert." : "insert done.");
		}
	}

	@Override
	public void deleteEvent(String eventName) {
		if(TextUtils.isEmpty(eventName)) return;
		
		String whereClause = CassEventTable.Col.EVENT_NAME + " = ?";
		String[] whereArgs = new String[] { eventName };
		synchronized (mLock) {
			mDatabase.delete(CassEventTable.name, whereClause, whereArgs);			
		}
	}
	
	@Override
	public void deleteAllEvent() {
		synchronized (mLock) {
			mDatabase.delete(CassEventTable.name, null, null);			
		}
	}
	
	@Override
	public void updateEvent(String eventName, long interval) {
		if(TextUtils.isEmpty(eventName)) return;
		
		CassEventCursor cec = getAllEventCursor();
		
		ContentValues target = new ContentValues();
		target.put(CassEventTable.Col.UPDATE_TIMESTAMP.name, System.currentTimeMillis());
		target.put(CassEventTable.Col.INTERVAL.name, interval);

		synchronized (mLock) {	
			while(cec.moveToNext()) {
				if(eventName.equals(cec.getEventName())) {
					target.put(CassEventTable.Col._ID.name, cec.getDatabaseIndex());
					target.put(CassEventTable.Col.EVENT_NAME.name, cec.getEventName());
					target.put(CassEventTable.Col.ADD_TIMESTAMP.name, cec.getAddingTimestamp());
					long returnValue = mDatabase.replace(CassEventTable.name, null, target);	
					Log.d(TAG, (returnValue == -1) ? "error for replacement." : "replacement done.");
				}
			}
		}
		cec.close();
	}

	@Override
	public boolean isUpdatedEvent(String eventName) {
		boolean ret = false;
		String selection = CassEventTable.Col.EVENT_NAME + " = ?";
		String[] selectionArgs = new String[] { eventName };
		synchronized (mLock) {							
			CassEventCursor cec = new CassEventCursor(mDatabase.query(CassEventTable.name, 
										null, selection, selectionArgs, null, null, null));
			if(cec.moveToFirst()) {
				long update = cec.getUpdateTimestamp();				
				if(update > 0) {
					ret = true;
				}
			}
			cec.close();
		}
		return ret;
	}
	
	@Override
	public CassEventCursor getAllEventCursor() {
		synchronized (mLock) {							
			return new CassEventCursor(mDatabase.query(CassEventTable.name, null, null, null, null, null, null));
		}
	}
	
	@Override
	public boolean isEventActive(String eventName) {
		boolean ret = false;
		
		String selection = CassEventTable.Col.EVENT_NAME + " = ?";
		String[] selectionArgs = new String[] { eventName };
		synchronized (mLock) {							
			Cursor c = mDatabase.query(CassEventTable.name, null, selection, selectionArgs, null, null, null);
			if(c.getCount() > 0) {
				ret = true;
			}
			c.close();
		}
		return ret;
	}
	

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CassEventTable.createSQL());
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		switch (oldVersion) {
		default:
			break;
		}
		Log.i(TAG, "Database upgraded.");
	}

	@Override
	public synchronized void close() {
		super.close();
		Log.w(TAG, "Database closed.");
	}

}
