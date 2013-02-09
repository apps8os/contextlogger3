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

package org.sizzlelab.contextlogger.android.travel;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.widget.EditText;
import org.sizzlelab.contextlogger.android.R;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import fi.aalto.chaow.android.app.BaseAlertDialog;

public class TravelCustomSubjectFragmentDialog extends BaseAlertDialog{
	
	public static enum CustomSubject{
		UNKONWN, MODE, REASON, PURPOSE, 
		DESTINATION, PAYMENT, PRICE, PLACE
	}

	private TravelCustomSubjectListener mListener = null;
	private CustomSubject mCustomSubject = CustomSubject.UNKONWN;
	
	public void config(final TravelCustomSubjectListener listener, CustomSubject subject){
		mListener = listener;
		mCustomSubject = subject;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getSupportActivity());
    	builder.setIcon(android.R.drawable.ic_dialog_info);
		switch(mCustomSubject){
			case MODE:
		    	builder.setTitle(R.string.travel_mode_dialog);
				break;
			case PURPOSE:
		    	builder.setTitle(R.string.travel_purpose_dialog);
				break;
			case REASON:
		    	builder.setTitle(R.string.travel_reason_dialog);
				break;
			case DESTINATION:
		    	builder.setTitle(R.string.travel_destination_dialog);					
				break;	
			case PAYMENT:
		    	builder.setTitle(R.string.travel_payment_dialog);
				break;
			case PLACE:
		    	builder.setTitle(R.string.travel_place_dialog);
				break;
			case PRICE:
		    	builder.setTitle(R.string.travel_price_dialog);					
				break;									
			default:
				return null;
		}
    	LayoutInflater inflater = LayoutInflater.from(getSupportActivity());
    	final View noteView = inflater.inflate(R.layout.custom_item_dialog, null);
    	final EditText travelItemContent = (EditText)noteView.findViewById(R.id.edit_text_travel_item);
    	builder.setView(noteView);
    	builder.setPositiveButton(R.string.travel_save, 
    			new DialogInterface.OnClickListener() {					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						final String itemName = travelItemContent.getEditableText().toString();
						if(!TextUtils.isEmpty(itemName)){
							if(mListener != null){
								mListener.OnTagNameInputCompleted(itemName, mCustomSubject);
							}
						}else{
							TravelApp.getInstance().showToastMessage(R.string.travel_custome_item_input_error);
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
	
	public static interface TravelCustomSubjectListener{
		void OnTagNameInputCompleted(final String itemName, final CustomSubject subject);
	}
}
