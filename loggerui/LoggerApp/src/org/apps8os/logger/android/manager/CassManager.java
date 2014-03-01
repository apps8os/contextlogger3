package org.apps8os.logger.android.manager;

import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.TimeZone;

import org.apps8os.contextlogger3.android.utils.IOUtil;
import org.apps8os.logger.android.CassReceiver;
import org.apps8os.logger.android.LoggerApp;
import org.apps8os.logger.android.R;
import org.apps8os.logger.android.storage.CassCaseDatabase;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.Toast;

abstract class CassManager {

	private static CassCaseDatabase mCassCaseDatabase = null;
	
	private static HashSet<HashMap<String, String>> CassiConfigs = null;
	
	private static void initCassDatabase(Context context) {
		if(mCassCaseDatabase == null) {
			mCassCaseDatabase = new CassCaseDatabase(context);
		}
	}
	
	private static void initCassConfig(Context context) {
		if(CassiConfigs == null) {
			CassiConfigs = new HashSet<HashMap<String,String>>();
			// read content out
			String jsonString = null;
			try {
				String filename = context.getResources().getString(R.string.cass_config_list);
				jsonString = IOUtil.getJSONString(context.getApplicationContext().getAssets().open(filename));
				if(!TextUtils.isEmpty(jsonString)) {
					JSONObject jsonData = new JSONObject(jsonString);
					if(jsonData.has("CassiConfig")) {
						JSONArray jsonArray = jsonData.getJSONArray("CassiConfig");
						for(int i = 0; i < jsonArray.length(); i++) {
							HashMap<String, String> data = new HashMap<String, String>();
							JSONObject jo = jsonArray.getJSONObject(i);
							Iterator<?> keys = jo.keys();
					        while(keys.hasNext()) {
					            String key = (String)keys.next();
								data.put(key, jo.getString(key));		            
					        }
					        CassiConfigs.add(data);
						}
					}
				}
			} catch (Exception e) {
			}
		}
	}
	
	private static void removeAllScheduledCassEvent(Context context) {
		for(HashMap<String, String> data : CassiConfigs) {
			int requestCode = -1;
			try {
				requestCode = Integer.parseInt(data.get("requestId"));
			} catch (Exception e) {
			}
			
			if(requestCode > 0) {
				// disable scheduled alarm
				Intent it = new Intent(CassReceiver.ALARM_INTENT);
				it.setClass(context, CassReceiver.class);
				it.putExtra("eventName", String.valueOf(data.get("name"))); 
				PendingIntent pi = PendingIntent.getBroadcast(context, requestCode, it, 0);
				getAlarmManager(context).cancel(pi);
			}
		}
	}
	
	private static void removeScheduledCassEvent(Context context, String eventName) { 
		if(TextUtils.isEmpty(eventName)) return;

		boolean isEventActive = mCassCaseDatabase.isEventActive(eventName);
		
		if(isEventActive) {
			for(HashMap<String, String> data : CassiConfigs) {
				if(eventName.equals(data.get("name"))) {
					int requestCode = -1;
					try {
						requestCode = Integer.parseInt(data.get("requestId"));
					} catch (Exception e) {
					}
					
					if(requestCode > 0) {
						// disable scheduled alarm
						Intent it = new Intent(CassReceiver.ALARM_INTENT);
						it.setClass(context, CassReceiver.class);
						it.putExtra("eventName", eventName);
						PendingIntent pi = PendingIntent.getBroadcast(context, requestCode, it, 0);
						getAlarmManager(context).cancel(pi);
						return;
					}
				}
			}	
		}
	}
	
	public static void scheduleCass(Context context, String eventName, boolean start) {
		if(!LoggerApp.getInstance().isCassRelease()) return;
		
		if(TextUtils.isEmpty(eventName)) return;
		
		initCassConfig(context);
		
		initCassDatabase(context);
		
		if(start) {
			// clean all scheduled event
			removeAllScheduledCassEvent(context);
			// clean database
			mCassCaseDatabase.deleteAllEvent();
		} else {
			removeScheduledCassEvent(context, eventName);
			mCassCaseDatabase.deleteEvent(eventName);
			return;			
		}
		
		for(HashMap<String, String> data : CassiConfigs) {
			if(eventName.equals(data.get("name"))) {
				
				String time = null;
				time = data.get("delay");
				int delaySeconds = -1;
				int requestCode = -1;
				try {
					delaySeconds = Integer.parseInt(time);
					requestCode = Integer.parseInt(data.get("requestId"));
				} catch (Exception e) {
				}

				mCassCaseDatabase.addEvent(eventName, delaySeconds);

				
				if(delaySeconds > 0) {
					int hours = delaySeconds / 3600;
					int mins = (delaySeconds - 60 * hours) / 60;
					int seconds = (delaySeconds - 60 * hours) % 60;
					
					// schedule alarm
					Intent it = new Intent(CassReceiver.ALARM_INTENT);
					it.setClass(context, CassReceiver.class);
					it.putExtra("eventName", eventName);
					PendingIntent pi = PendingIntent.getBroadcast(context, requestCode, it, 0);
					// the calendar supports local setup 
					// relying on the local/default time zone
					Calendar c = Calendar.getInstance(TimeZone.getDefault());
					c.set(Calendar.YEAR, c.get(Calendar.YEAR));
					c.set(Calendar.DAY_OF_YEAR, c.get(Calendar.DAY_OF_YEAR));
//					c.set(Calendar.HOUR_OF_DAY, c.get(Calendar.HOUR_OF_DAY));	
					// correct time
					seconds = (c.get(Calendar.SECOND) + seconds);
					if(seconds >= 60) {
						mins += seconds / 60;
						seconds = seconds % 60;
					}
					mins = (c.get(Calendar.MINUTE) + mins);
					if(mins >= 60) {
						hours += mins / 60;
						mins = mins % 60;
					}
					if((c.get(Calendar.HOUR_OF_DAY) + hours) >= 24) {
						// case next day
					}
					c.set(Calendar.HOUR_OF_DAY, c.get(Calendar.HOUR_OF_DAY) + hours);
					c.set(Calendar.MINUTE, mins);
					c.set(Calendar.SECOND, seconds);
					getAlarmManager(context.getApplicationContext()).setRepeating(AlarmManager.RTC_WAKEUP, 
										c.getTimeInMillis() , c.getTimeInMillis(),  pi);
					return;
				}
			}
		}
	}
	
	public static void rescheduleCass(Context context, String eventName) {
		if(!LoggerApp.getInstance().isCassRelease()) return;
		
		if(TextUtils.isEmpty(eventName)) return;
		
		initCassConfig(context);
		
		initCassDatabase(context);
		
		for(HashMap<String, String> data : CassiConfigs) {
			if(eventName.equals(data.get("name"))) {
			
				String time = null;
				time = data.get("interval");
				int delaySeconds = -1;
				int requestCode = -1;
				try {
					delaySeconds = Integer.parseInt(time);
					requestCode = Integer.parseInt(data.get("requestId"));
				} catch (Exception e) {
				}
		
				mCassCaseDatabase.updateEvent(eventName, delaySeconds);

				if(delaySeconds > 0) {
					int hours = delaySeconds / 3600;
					int mins = (delaySeconds - 60 * hours) / 60;
					int seconds = (delaySeconds - 60 * hours) % 60;
					
					// schedule alarm
					Intent it = new Intent(CassReceiver.ALARM_INTENT);
					it.setClass(context, CassReceiver.class);
					it.putExtra("eventName", eventName);
					PendingIntent pi = PendingIntent.getBroadcast(context, requestCode, it, 0);
					// the calendar supports local setup 
					// relying on the local/default time zone
					Calendar c = Calendar.getInstance(TimeZone.getDefault());
					c.set(Calendar.YEAR, c.get(Calendar.YEAR));
					c.set(Calendar.DAY_OF_YEAR, c.get(Calendar.DAY_OF_YEAR));
//					c.set(Calendar.HOUR_OF_DAY, c.get(Calendar.HOUR_OF_DAY));	
					// correct time
					seconds = (c.get(Calendar.SECOND) + seconds);
					if(seconds >= 60) {
						mins += seconds / 60;
						seconds = seconds % 60;
					}
					mins = (c.get(Calendar.MINUTE) + mins);
					if(mins >= 60) {
						hours += mins / 60;
						mins = mins % 60;
					}
					if((c.get(Calendar.HOUR_OF_DAY) + hours) >= 24) {
						// case next day
					}
					c.set(Calendar.HOUR_OF_DAY, c.get(Calendar.HOUR_OF_DAY) + hours);
					c.set(Calendar.MINUTE, mins);
					c.set(Calendar.SECOND, seconds);
					getAlarmManager(context.getApplicationContext()).setRepeating(AlarmManager.RTC_WAKEUP, 
										c.getTimeInMillis() , c.getTimeInMillis(),  pi);
					return;
				}
			}
		}
	}
	
	
	private static AlarmManager getAlarmManager(Context context){
		return (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
	}

	public static void launchCass(Context context, Intent intent) {
		if(!LoggerApp.getInstance().isCassRelease()) return;
		
		String eventName = intent.getStringExtra("eventName");
		if(TextUtils.isEmpty(eventName)) return;
				
		try {
			AlarmSoundManager.getInstance().play(context);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}			
		
		boolean isUpdateEvent = mCassCaseDatabase.isUpdatedEvent(eventName);
		
		rescheduleCass(context, eventName);
		
		if(launch(context)) {
			Toast.makeText(context, "Event name: " + eventName, Toast.LENGTH_SHORT).show();
			// whenever CASS is launched, store event using ApplicationProbe 
			// (CASS, for example WALKINGCASS, WALKING_CASS_REPEAT
			StringBuilder sb = new StringBuilder();
			sb.append(eventName.replace(" ", "_"));
			sb.append("_CASS" + (isUpdateEvent ? "_REPEAT" : ""));
			AppManager.sendEventBoradcast(context, sb.toString().toUpperCase(Locale.getDefault()), null);
		} else {
			Toast.makeText(context, "You shall install CASS first.", Toast.LENGTH_SHORT).show();
		}
	}
	
	private static boolean launch(Activity activity) {
		boolean ret = false;
		try {
			Intent intent = new Intent();
			intent.setAction("fi.metropolia.cass.main.CASSI");
			activity.startActivity(intent);
			ret = true;
		} catch (ActivityNotFoundException e) {
		}
		return ret;
	}

	private static boolean launch(Context context) {
		boolean ret = false;
		try {
			Intent intent = new Intent();
			intent.setAction("fi.metropolia.cass.main.CASSI");
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
			ret = true;
		} catch (ActivityNotFoundException e) {
		}
		return ret;
	}
	
}
