package fi.aalto.chaow.android.utils;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

public abstract class DBUtils {
	
	protected abstract boolean deleteAll(final Context context);	
	
	/**
	 * internal method to handle boolean data type
	 * as SQLite database does not support the data type
	 * 
	 * @param value
	 * @return
	 */
	public final boolean getLongToBoolean(final long value){
		return (value == 1);
	}
	
	/**
	 * 
	 * 
	 * @param value
	 * @return
	 */
	public final long getBooleanToLong(final boolean value){
		if(value){
			return 1;
		} else {
			return 0;
		}
	}
	
	/**
	 * 
	 * 
	 * @param list
	 * @return
	 */
	final String getListToString(ArrayList<String> list){
		return TextUtils.join("-", list.toArray());
	}
	
	/**
	 * 
	 * 
	 * @param String list
	 * @return
	 */
	final ArrayList<String> getStringToList(final String stringList){
		String str[] = TextUtils.split(stringList, "-");
		ArrayList<String> array = new ArrayList<String>();
		for(String s : str){
			array.add(s);
		}
		return array;
	}
	
	/**
	 * 
	 * 
	 * @param db
	 * @param dbTable
	 * @param column
	 * @return
	 */
	public final boolean hasRecord(final SQLiteDatabase db, final String dbTable, final String column){
		if(db.isOpen()){
			Cursor c = db.query(dbTable, new String[]{column}, null, null, null, null, null);
			if(c.getCount() > 0){
				c.close();
				db.close();
				return true;
			}
			c.close();
			db.close();
		}
		return false;
	}
}
