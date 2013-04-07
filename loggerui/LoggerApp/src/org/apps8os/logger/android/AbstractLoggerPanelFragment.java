package org.apps8os.logger.android;
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
import org.apps8os.logger.android.AbstractLoggerPanelFragment.ActivityNamingDialog.ActivityNamingListener;
import org.apps8os.logger.android.EventTimePickerFragmentDialog.EventTimeMode;
import org.apps8os.logger.android.EventTimePickerFragmentDialog.OnEventTimeChangedListener;
import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.widget.EditText;
import org.sizzlelab.contextlogger.android.model.ActionEvent;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import fi.aalto.chaow.android.app.BaseAlertDialog;
import fi.aalto.chaow.android.app.BaseAlertDialog.AlertDialogListener;
import fi.aalto.chaow.android.app.BaseFragment;

/**
 * Separate code from the LoggerPanelFragment.java
 * 
 * @author Chao Wei
 *
 */

public abstract class AbstractLoggerPanelFragment extends BaseFragment{	
	
	void showEventTimePickerDialog(final EventTimeMode em, final ActionEvent ae){
		if(ae == null){
			throw new IllegalArgumentException("Activity event can not be null or empty.");
		}
		EventTimePickerFragmentDialog.newInstance(new OnEventTimeChangedListener(){
			@Override
			public void onPositiveClick() {
			}
			@Override
			public void onNegativeClick() {
			}
			@Override
			public void onCancel() {
			}
			@Override
			public void onConfirmed(long timestamp) {
				onEventConfirmed(em, ae, timestamp);
			}
			@Override
			public void onDiscard() {
				onEventDiscard(em, ae);
			}
		}, em, ae).show(getFragmentManager());
	}

	abstract void onEventConfirmed(final EventTimeMode em, final ActionEvent event, long timestamp);
	
	abstract void onEventDiscard(final EventTimeMode em, final ActionEvent event);

	void showQuitAppDialog(){
		QuitAppDialog.newInstance(new AlertDialogListener(){
			@Override
			public void onPositiveClick() {
				quitApp();
			}
			@Override
			public void onNegativeClick() {
			}
			@Override
			public void onCancel() {
			}
		}).show(getFragmentManager());
	}
	
	abstract void quitApp();
	
	
	public static class QuitAppDialog extends BaseAlertDialog{
		
		public static QuitAppDialog newInstance(final AlertDialogListener l){
			QuitAppDialog aqd = new QuitAppDialog();
			aqd.setAlertDialogListener(l);
			return aqd;
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getSupportActivity());
	    	builder.setTitle(R.string.quit);
	    	builder.setIcon(android.R.drawable.ic_dialog_info);
	    	builder.setMessage(R.string.app_quit_content);
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
	}
	
	void showEventNameCreationDialog(){
		ActivityNamingDialog.newInstance((ActivityNamingListener) this,
				new AlertDialogListener() {
					@Override
					public void onPositiveClick() {
					}

					@Override
					public void onNegativeClick() {
					}

					@Override
					public void onCancel() {
					}
				}).show(getFragmentManager());
	}
	
	public static class ActivityNamingDialog extends BaseAlertDialog {

		public static ActivityNamingDialog newInstance(final ActivityNamingListener cb1, 
															final AlertDialogListener cb2){
			ActivityNamingDialog and = new ActivityNamingDialog();
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
	}
	
	void showActivityNameDeletionDialog(AlertDialogListener l, String tagName){
		ActivityNameDeletionDialog.newInstance(l, tagName).show(getFragmentManager());
	}
	
	public static class ActivityNameDeletionDialog extends BaseAlertDialog {
		
		public static ActivityNameDeletionDialog newInstance(final AlertDialogListener l, final String tagName){
			ActivityNameDeletionDialog and = new ActivityNameDeletionDialog ();
			and.setAlertDialogListener(l);
			Bundle b = new Bundle();
			b.putString(TAG_NAME, tagName);
			and.setArguments(b);
			return and;
		}
		
		static final String TAG_NAME = "tagName";
		
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getSupportActivity());
	    	builder.setTitle(R.string.remove_tag);
	    	builder.setIcon(android.R.drawable.ic_dialog_info);
	    	Bundle b = getArguments();
	    	if(b != null){
		    	builder.setMessage(b.getString(TAG_NAME));	    		
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
	}
	
}