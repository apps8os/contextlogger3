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
package org.apps8os.logger.android.util;

public class AndroidVersionHelper {

	private AndroidVersionHelper() {
	}

	/**
	 * Check if the device OS is Honeycomb (API level 11 or 12).
	 * 
	 * @return
	 */
	public static final boolean isHoneycomb(){
		boolean ret = false;
		final int sdkInt = android.os.Build.VERSION.SDK_INT;
		try{
			ret = ((sdkInt == android.os.Build.VERSION_CODES.HONEYCOMB_MR2)
					|| (sdkInt == android.os.Build.VERSION_CODES.HONEYCOMB_MR1));
		} catch (Exception e){ 
		}
		return ret;
	}
	
	
	public static boolean isHoneycombAbove() {
		boolean ret = false;
		final int sdkInt = android.os.Build.VERSION.SDK_INT;
		try{
			ret = (sdkInt > android.os.Build.VERSION_CODES.GINGERBREAD_MR1);
		} catch (Exception e){ 
		}
		return ret;
	}
	
	public static boolean isICSAbove() {
		boolean ret = false;
		final int sdkInt = android.os.Build.VERSION.SDK_INT;
		try{
			ret = (sdkInt > android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1);
		} catch (Exception e){ 
		}
		return ret;
	}
	
}