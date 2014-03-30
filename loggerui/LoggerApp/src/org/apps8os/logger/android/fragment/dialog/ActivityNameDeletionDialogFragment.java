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
package org.apps8os.logger.android.fragment.dialog;

import org.apps8os.logger.android.R;
import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.app.Dialog;

import android.content.DialogInterface;
import android.os.Bundle;

public class ActivityNameDeletionDialogFragment extends LoggerBaseDialogFragment {
	
	public static ActivityNameDeletionDialogFragment newInstance(final AlertDialogListener l, final String tagName){
		ActivityNameDeletionDialogFragment and = new ActivityNameDeletionDialogFragment ();
		and.setAlertDialogListener(l);
		Bundle b = new Bundle();
		b.putString(and.getClassName(), tagName);
		and.setArguments(b);
		return and;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getSupportActivity());
    	builder.setTitle(R.string.remove_tag);
    	builder.setIcon(android.R.drawable.ic_dialog_info);
    	Bundle b = getArguments();
    	if(b != null){
	    	builder.setMessage(b.getString(getClassName()));	    		
    	} else {
    		dismiss();
    	}
    	builder.setPositiveButton(R.string.ok, 
    			new DialogInterface.OnClickListener() {					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						getDialogListener().onPositiveClick();
					}
				});
    	builder.setNegativeButton(R.string.cancel,     			
    			new DialogInterface.OnClickListener() {					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						getDialogListener().onNegativeClick();
					}
				});				
		return builder.create(); 
	}

	@Override
	protected String getClassName() {
		return ActivityNameDeletionDialogFragment.class.getSimpleName();
	}
}
