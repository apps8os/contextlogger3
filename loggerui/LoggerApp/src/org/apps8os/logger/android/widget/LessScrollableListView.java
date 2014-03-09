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
package org.apps8os.logger.android.widget;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.holoeverywhere.widget.ListView;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;


/**
 * ListView without Samsung over-scroll feature
 *
 */
public class LessScrollableListView extends ListView {

	/**
	 * 
	 * @param context
	 */
	public LessScrollableListView(Context context) {
		super(context);
		initialize();
	}
	
	/**
	 * 
	 * @param context
	 * @param attrs
	 */
	public LessScrollableListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize();
	}
	
	/**
	 * 
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public LessScrollableListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initialize();
	}
	
	/**
	 * 
	 */
	private void initialize() {
		// http://stackoverflow.com/questions/10161232/disable-listview-overscroll-on-samsung-galaxy-tab-2-3-3-android
		try {
			// find the method
			Method setEnableExcessScroll = getClass().getMethod("setEnableExcessScroll", Boolean.TYPE);

			// call the method with parameter set to false
			setEnableExcessScroll.invoke(this, Boolean.valueOf(false));

			Log.v("ListView", "Overscroll removed.");

		} catch (SecurityException e) {
		} catch (NoSuchMethodException e) {
		} catch (IllegalArgumentException e) {
		} catch (IllegalAccessException e) {
		} catch (InvocationTargetException e) {
		}
	}
}