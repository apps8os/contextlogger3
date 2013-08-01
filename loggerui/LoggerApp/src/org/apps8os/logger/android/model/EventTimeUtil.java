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
package org.apps8os.logger.android.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apps8os.logger.android.util.TimeUtil;

abstract class EventTimeUtil {

	protected static String getTimeToStringBase(long time){
		if(TimeUtil.isToday(time)) {
			return new String (new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date(time)));
		}else{
			if(Locale.getDefault().getISO3Language().equals("zho")){
				return new String (new SimpleDateFormat("MMMdd日", Locale.CHINA).format(new Date(time)));				
			} else {
				return new String (new SimpleDateFormat("dd MMM", Locale.getDefault()).format(new Date(time)));				
			}
		}
	}
	
	protected static String getEventDurationInClockFormat(long time){
		int days = 0, hours = 0, mins = 0, secs = 0;
		final long eventDuration = time;
		days = (int)(eventDuration / TimeUtil.TIME_IN_ONE_DAY);
		hours = (int) ((eventDuration - days * TimeUtil.TIME_IN_ONE_DAY) / TimeUtil.TIME_IN_ONE_HOUR);
		mins = (int) ((eventDuration - days * TimeUtil.TIME_IN_ONE_DAY - hours * TimeUtil.TIME_IN_ONE_HOUR) / TimeUtil.TIME_IN_ONE_MIN);
		secs = (int) ((eventDuration - days * TimeUtil.TIME_IN_ONE_DAY - hours * TimeUtil.TIME_IN_ONE_HOUR - mins * TimeUtil.TIME_IN_ONE_MIN) / 1000 );
		return new String(getStringTimeNumberValue(hours) + ":" + getStringTimeNumberValue(mins) + ":" + getStringTimeNumberValue(secs));
	}
	
	private static String getStringTimeNumberValue(int num){
		return (num < 10) ? String.valueOf("0" + num) : String.valueOf(num); 
	}
	
}
