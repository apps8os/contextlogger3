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

import android.content.DialogInterface;

import com.actionbarsherlock.app.SherlockDialogFragment;

public class BaseAlertDialog extends SherlockDialogFragment {

	private AlertDialogListener mListener = null;

	public BaseAlertDialog(AlertDialogListener listener){
		mListener = listener;
		setCancelable(true);
	}

	protected AlertDialogListener getDialogListener(){
		return mListener;
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		mListener.onCancel();
	}

	public interface AlertDialogListener {
		void onPositiveClick();
		void onNegativeClick();
		void onCancel();
	}

}
