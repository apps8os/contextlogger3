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
package org.sizzlelab.contextlogger.android.model;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

abstract class EventTimeUtil {

	protected static String getTimeToStringBase(long time){
		if(isToday(time)) {
			return new String (new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date(time)));
		}else{
			return new String (new SimpleDateFormat("dd MMM", Locale.getDefault()).format(new Date(time)));
		}
	}
	
	/**
	 * Check the time whether belongs to today  
	 * 
	 * @param time
	 * @return true, if it is today's time. Otherwise, false.
	 */
	private static boolean isToday(final long time){
		final int currentDay = Calendar.getInstance(TimeZone.getDefault()).get(Calendar.DAY_OF_YEAR);
		final int currentYear = Calendar.getInstance(TimeZone.getDefault()).get(Calendar.YEAR);
		Calendar target = Calendar.getInstance(TimeZone.getDefault());
		target.setTimeInMillis(time);
		return (currentDay == target.get(Calendar.DAY_OF_YEAR) 
				&& (currentYear == target.get(Calendar.YEAR)));
	}
	
	private static final int TIME_IN_ONE_DAY = 24 * 60 * 60 * 1000;
	private static final int TIME_IN_ONE_HOUR = 60 * 60 * 1000;
	private static final int TIME_IN_ONE_MIN = 60 * 1000;
	
	protected static String getEventDurationInClockFormat(long time){
		int days = 0, hours = 0, mins = 0, secs = 0;
		final long eventDuration = time;
		days = (int)(eventDuration / TIME_IN_ONE_DAY);
		hours = (int) ((eventDuration - days * TIME_IN_ONE_DAY) / TIME_IN_ONE_HOUR);
		mins = (int) ((eventDuration - days * TIME_IN_ONE_DAY - hours * TIME_IN_ONE_HOUR) / TIME_IN_ONE_MIN);
		secs = (int) ((eventDuration - days * TIME_IN_ONE_DAY - hours * TIME_IN_ONE_HOUR - mins * TIME_IN_ONE_MIN) / 1000 );
		return new String(getStringTimeNumberValue(hours) + ":" + getStringTimeNumberValue(mins) + ":" + getStringTimeNumberValue(secs));
	}
	
	private static String getStringTimeNumberValue(int num){
		if(num < 10){
			return new String("0" + String.valueOf(num));
		}else{
			return String.valueOf(num);
		}
	}
	
}
