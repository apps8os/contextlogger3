package org.apps8os.logger.android.storage;

import org.apps8os.logger.android.storage.ICassDatabase.CassEventTable;

import android.database.Cursor;
import android.database.CursorWrapper;
import android.util.Log;

public class CassEventCursor extends CursorWrapper {

	private static final String TAG = CassEventCursor.class.getSimpleName();
	
	private final int colId;
	private final int colEventName;
	private final int colAddTimeStamp;
	private final int colUpdateTimeStamp;
	private final int colInterval;
	
	public CassEventCursor(Cursor cursor) {
		super(cursor);
		colId = cursor.getColumnIndex(CassEventTable.Col._ID.name);
		colEventName = cursor.getColumnIndex(CassEventTable.Col.EVENT_NAME.name);
		colAddTimeStamp = cursor.getColumnIndex(CassEventTable.Col.ADD_TIMESTAMP.name);
		colUpdateTimeStamp = cursor.getColumnIndex(CassEventTable.Col.UPDATE_TIMESTAMP.name);
		colInterval = cursor.getColumnIndex(CassEventTable.Col.INTERVAL.name);
	}

	final int getDatabaseIndex() {
		return getInt(colId);
	}

	public String getEventName() {
		return getString(colEventName);
	}
	
	public long getAddingTimestamp() {
		return getLong(colAddTimeStamp);
	}
	
	public long getUpdateTimestamp() {
		return getLong(colUpdateTimeStamp);
	}
	
	public long getInterval() {
		return getLong(colInterval);
	}
	
	public CassEventCursor moveToFirstCursor() {
		moveToFirst();
		return this;
	}
	
	@Override
	public void close() {
		super.close();
		Log.v(TAG, "Cursor close"); 
	}
}
