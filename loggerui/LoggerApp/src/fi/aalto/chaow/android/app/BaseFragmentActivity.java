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
package fi.aalto.chaow.android.app;

import org.holoeverywhere.app.Activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;


public abstract class BaseFragmentActivity extends Activity{
	
	protected FragmentTransaction getSupportFragmentTransaction(){
		return getSupportFragmentManager().beginTransaction();
	}

	protected final int getBackStackEntryCount(){
		return getSupportFragmentManager().getBackStackEntryCount();
	}
	
	protected void popBackStack(){
		getSupportFragmentManager().popBackStack();
	}
	
	/**
	 * Fragment changed listener
	 * 
	 * @author Chao Wei
	 *
	 */
	public interface OnSupportFragmentListener {
		
		/**
		 * Notify the current fragment need to be changed to another one
		 * 
		 * @param layoutResId
		 * @param bundle
		 */
		void onFragmentChanged(int layoutResId, Bundle bundle);
	}
	
	
}
