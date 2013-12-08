package org.apps8os.logger.android.manager;

import java.io.IOException;

import org.apps8os.logger.android.util.VibratorUtils;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;

public class AlarmSoundManager {
	
	private static AlarmSoundManager mAlarmSoundManager = null;
	private static MediaPlayer mPlayer = null;
	private static Thread mMonitor = null;
	private static AudioManager mAudioManager = null;
	
	private AlarmSoundManager (){
	}
	
	public static AlarmSoundManager getInstance(){
		if(mAlarmSoundManager == null){
			mAlarmSoundManager = new AlarmSoundManager();
		}
		return mAlarmSoundManager;
	}
	
	public void tryToStop(Context context){
		if((mPlayer != null) && (mMonitor != null)) {
			stop(context);
		} else {
			VibratorUtils.deactive(context);
		}
	}
	
	public void stop(Context context) {
		if(mPlayer != null){
			if(mPlayer.isPlaying()){
				mPlayer.stop();
			}
			mPlayer.release();
			mPlayer = null;
		}
		if((mMonitor != null) && mMonitor.isAlive()) {
			mMonitor.interrupt(); 
			mMonitor = null;
		}
		VibratorUtils.deactive(context);
	}
	
	public void play(Context context) throws IllegalArgumentException, SecurityException, IllegalStateException{
		if((mMonitor != null) && mMonitor.isAlive()){
			mMonitor.interrupt();
			mMonitor = null;
		}
		VibratorUtils.deactive(context);
		if(mPlayer != null){
			if(mPlayer.isPlaying()){
				mPlayer.stop();
			}
			mPlayer.release();
			mPlayer = null;
		}
		Uri alter = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
		if(alter == null){
			alter = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			if(alter == null){
				alter = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
			}
		}
		mPlayer = new MediaPlayer();
		try {
			mPlayer.setDataSource(context, alter);
		} catch (IOException e) {
			alter = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
			try {
				mPlayer.setDataSource(context, alter);
			} catch (IOException e1) {
			}
		}
		try {
			getAudioManager(context);
			if(mAudioManager.getStreamVolume(AudioManager.STREAM_RING) != 0){	
				mPlayer.setAudioStreamType(AudioManager.STREAM_RING);				
				mPlayer.setLooping(false);
				mPlayer.prepare();
				mPlayer.start();
				VibratorUtils.active(context, true);
				mMonitor = new Thread(new VibratorMonitor(context));
				mMonitor.start();
			} else {
				mPlayer.release();
				mPlayer = null;
				VibratorUtils.active(context);
			}
		} catch (IOException e) {
			VibratorUtils.active(context);
		}
	}
		
	public final AudioManager getAudioManager(Context context) {
		if( mAudioManager == null){
			 mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		}
		return mAudioManager; 
	}
	
	private class VibratorMonitor implements Runnable {
		
		private Context mContext = null;
		public VibratorMonitor(Context context){
			mContext = context;
		}
		@Override
		public void run() {
			boolean run = true;
			int count = 0;
			while(run) {
				// 1 min for the sound and the vibration 
				if(count == 12){
					if(mPlayer.isPlaying()) {
						mPlayer.stop();
						mPlayer.release();
						mPlayer = null;
					}
				}
				
				if((mPlayer != null) && mPlayer.isPlaying()) {
					try {
						Thread.sleep(5000L);
						count++;
					} catch (InterruptedException e) {
					}
				} else {
					run = false;
					VibratorUtils.deactive(mContext);
				}
			}
		}
	}
	
}
