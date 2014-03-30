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

import org.apps8os.logger.android.LoggerApp;
import org.apps8os.logger.android.R;
import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.widget.EditText;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

public class ActivityNamingDialogFragment extends LoggerBaseDialogFragment {
	
	public static ActivityNamingDialogFragment newInstance(final ActivityNamingListener cb1, 
														final AlertDialogListener cb2){
		ActivityNamingDialogFragment and = new ActivityNamingDialogFragment();
		and.setCallbacks(cb1, cb2);
		return and;
	}
	
	private ActivityNamingListener mActivityNamingListener = null;
	
	public void setCallbacks(ActivityNamingListener cb1, AlertDialogListener cb2){
		mActivityNamingListener = cb1;
		setAlertDialogListener(cb2);
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getSupportActivity());
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setTitle(R.string.add_tag);
		LayoutInflater inflater = LayoutInflater.from(getSupportActivity());
		final View noteView = inflater.inflate(R.layout.add_tag_dialog, null);
		final EditText tagContent = (EditText) noteView.findViewById(R.id.edit_text_tag);
		builder.setTitle(R.string.add_tag);
		builder.setView(noteView);
		builder.setPositiveButton(R.string.save,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						final String tagName = tagContent.getEditableText().toString();
						if (!TextUtils.isEmpty(tagName)) {
							if(mActivityNamingListener != null){
								mActivityNamingListener.OnTagNameInputCompleted(tagName);
							}
						} else {
							 LoggerApp.getInstance().showToastMessage(R.string.give_input);
						}
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
	
	public static interface ActivityNamingListener {
		void OnTagNameInputCompleted(final String tagName);
	}

	@Override
	protected String getClassName() {
		return ActivityNamingDialogFragment.class.getSimpleName();
	}
}
