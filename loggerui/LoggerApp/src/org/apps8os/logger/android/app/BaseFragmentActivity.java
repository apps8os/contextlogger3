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
package org.apps8os.logger.android.app;

import org.holoeverywhere.app.Activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

public abstract class BaseFragmentActivity extends Activity {

	protected FragmentTransaction getSupportFragmentTransaction() {
		return getSupportFragmentManager().beginTransaction();
	}

	protected final int getBackStackEntryCount() {
		return getSupportFragmentManager().getBackStackEntryCount();
	}

	protected void popBackStack() {
		getSupportFragmentManager().popBackStack();
	}

	/**
	 * 
	 * @return Class name
	 */
	protected abstract String getClassName();
	
	/**
	 * Change the fragment for the current activity.
	 * 
	 * @param containerResId is the resource id of the screen container
	 * @param fragment is an instance of a subclass of android.support.v4.app.Fragment
	 * @param tag is a string title that indicates the fragment 
	 * @param args is a container for passing objects
	 * @param addToStack is indicating adding the fragment to back stack, if needed
	 */
	protected <T extends Fragment> void changeFragment(final int containerResId, T fragment, String tag, Bundle args, boolean addToStack) {
		FragmentTransaction transaction = getSupportFragmentTransaction();
		if(args != null){
			fragment.setArguments(args);			
		}
		transaction.replace(containerResId, fragment, tag);
		if(addToStack){
			// add to this transaction into BackStack
			transaction.addToBackStack(tag);			
		}
		// add the animation 
		transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		// commit this transaction
		transaction.commit();
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
