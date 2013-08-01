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

import java.util.ArrayList;
import java.util.Collections;

import org.apps8os.logger.android.model.IActionEvent.EventInfoComparator;

public class EventInfo extends EventTimeUtil implements Comparable<EventInfo> {
	
	private long mEventTime = -1;
	private String mEventAndNote = "";
	private String mEventState = "";
	
	public EventInfo(final long time, final String eventNote, final String state){
		mEventTime = time;
		mEventAndNote = eventNote;
		mEventState = state;
	}

	public final String getTimeToString(){
		return getTimeToStringBase(mEventTime);
	}
	
	public final String getEventAndNote(){
		return mEventAndNote;
	}
	
	public final String getState(){
		return mEventState;
	}
	
	protected final long getTime(){
		return mEventTime;
	}

	@Override
	public int compareTo(EventInfo another) {
		return Long.valueOf(mEventTime).compareTo(Long.valueOf(another.getTime()));
	}

	/**
	 * sort the object list
	 * @param ArrayList<EventInfo> eventInfoList
	 */
	public static void sortList(final ArrayList<EventInfo> eventInfoList){
		if(!eventInfoList.isEmpty()){
			Collections.sort(eventInfoList, new EventInfoComparator());
			Collections.reverse(eventInfoList);
		}
	}
}