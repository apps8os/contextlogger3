package org.apps8os.logger.android.storage;

import java.util.Locale;

interface ICassDatabase {

	public void addEvent(String eventName, long interval);
	
	public void deleteEvent(String eventName);
	
	public void deleteAllEvent();
	
	public void updateEvent(String eventName, long interval);
	
	public boolean isEventActive(String eventName);
	
	public boolean isUpdatedEvent(String eventName);
	
	public CassEventCursor getAllEventCursor();
	
	static final class CassEventTable extends AbstractTable {
		
		public final static String name = "CassEvent";
		
		static enum Col implements Column {
			
			_ID             	(ColType.INTEGER),
			EVENT_NAME			(ColType.TEXT),
			ADD_TIMESTAMP		(ColType.INTEGER, "0"),
			UPDATE_TIMESTAMP	(ColType.INTEGER, "0"),
			INTERVAL			(ColType.INTEGER)
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
