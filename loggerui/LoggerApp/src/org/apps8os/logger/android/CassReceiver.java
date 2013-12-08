package org.apps8os.logger.android;

import org.apps8os.logger.android.manager.AlarmSoundManager;
import org.apps8os.logger.android.manager.AppManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class CassReceiver extends BroadcastReceiver {

	public static String ALARM_INTENT = "org.apps8os.logger.android.CASS_ALARM";
	private static final String VOLUME_CHANGED = "android.media.VOLUME_CHANGED_ACTION";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		final String intentAction = intent.getAction();	
		if(ALARM_INTENT.equals(intentAction)) {
			AppManager.launchCass(context, intent);
			if(isOrderedBroadcast()) {
				abortBroadcast();
			}
		} else if(VOLUME_CHANGED.equals(intentAction)){
			AlarmSoundManager.getInstance().tryToStop(context);
		}
	}
	
}
