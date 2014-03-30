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

import java.util.Locale;

import org.apps8os.logger.android.model.ActionEvent;

import android.database.CursorWrapper;

interface IActionEventDatebase {
	
	
	public void add(ActionEvent ae);
	
	public void updateActionEventBreak(ActionEvent ae);
	
	public void addActionEventNote(ActionEvent ae);
	
	public CursorWrapper getAllActionEventCursor();
	
	public CursorWrapper getAllActionEventCursor(boolean isHistory);
	
	
	static final class ActionEventTable extends AbstractTable {
		
		public final static String name = "actionEvent";
		
		static enum Col implements Column {
			
			_ID             (ColType.INTEGER),
			ACTION_NAME		(ColType.TEXT),
			START_TIMESTAMP	(ColType.INTEGER),
			BREAK_TIMESTAMP	(ColType.TEXT),
			IS_HISTORY		(ColType.INTEGER, "0"),
			STATE			(ColType.TEXT),
			ACTION_NOTE		(ColType.TEXT),
			START_DELAY		(ColType.INTEGER),
			BREAK_DELAY		(ColType.INTEGER)
			;

			final String name;
			final ColType type;
			final String def;
			
			private Col(ColType colType) {
				this(colType, null);
			}

			private Col(ColType colType, String defaultValue) {
				name = name().toLowerCase(Locale.ENGLISH);
				type = colType;
				def = defaultValue;
			}
			
			@Override
			public String getSQLLine() {
				String line = name + " " + type.name();
				if (name.equals(_ID.name)) {
					line += " PRIMARY KEY AUTOINCREMENT";
				}
				if (def != null) {
					line += " DEFAULT " + def;
				}
				return line;
			}
		}

		static String createSQL() {
			StringBuilder sql = new StringBuilder();
			sql.append("CREATE TABLE ").append(name).append(" (");

			for (Col c : Col.values()) {
				sql.append(c.getSQLLine()).append(",");
			}
			sql.deleteCharAt(sql.length() - 1); // remove last
			sql.append(")");
			return sql.toString();
		}
	}

}