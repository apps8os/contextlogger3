package org.apps8os.logger.android.util;

import android.content.Context;
import android.os.Vibrator;

public final class VibratorUtils {

	private static Vibrator mVibrator = null;

	private VibratorUtils() {
	}

	/**
	 * vibrate 2500 ms
	 * 
	 * @param context
	 */
	public static void active(Context context) {
		try {
			if (mVibrator == null) {
				mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
			}
			mVibrator.vibrate(2500L);
		} catch (Exception e) {
		} catch (Error e) {
		}
	}

	public static void active(Context context, boolean repeat) {
		try {
			if (mVibrator == null) {
				mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
			}
			final long[] duration = new long[] { 100, 500, 100, 500, 100, 500, 100, 500 };
			mVibrator.vibrate(duration, repeat ? 0 : -1);
		} catch (Exception e) {
		} catch (Error e) {
		}
	}

	public static void deactive(Context context) {
		try {
			if (mVibrator == null) {
				mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
			}
			mVibrator.cancel();
		} catch (Exception e) {
		} catch (Error e) {
		}
	}
}
