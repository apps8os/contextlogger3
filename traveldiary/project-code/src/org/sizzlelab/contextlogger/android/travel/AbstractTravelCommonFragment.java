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

package org.sizzlelab.contextlogger.android.travel;

import java.util.HashMap;

import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.widget.Spinner;
import org.sizzlelab.contextlogger.android.R;
import org.sizzlelab.contextlogger.android.io.MainPipeline;
import org.sizzlelab.contextlogger.android.utils.Constants;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import fi.aalto.chaow.android.app.BaseAlertDialog;

abstract class AbstractTravelCommonFragment extends Fragment implements Constants{
	
	enum TravelStatus {
		IDLE, MOVING, PAUSE, STOP;
		public static TravelStatus getTravelStatus(final int index)
				throws IndexOutOfBoundsException {
			if (index < 0 || index > (TravelStatus.values()).length) {
				throw new IndexOutOfBoundsException();
			}
			return TravelStatus.values()[index];
		}
	}
	
	void notifyEvent(final String payload, HashMap<String, String> msg){
		TravelApp app = TravelApp.getInstance();
		if(!TextUtils.isEmpty(payload)){
			Intent intent = new Intent();
			intent.setAction(CUSTOM_INTENT_ACTION);
			intent.putExtra("APPLICATION_ACTION", payload);
			if((msg != null) && (!msg.isEmpty())){
				String appData = null;
				try {
					appData = TravelApp.getFormattedJsonObject(msg, "message").toString();	
				}catch(Exception e){
				}
				if(!TextUtils.isEmpty(appData)){
					intent.putExtra("APPLICATION_DATA", appData); 
					app.saveParkingInfo(appData);
				}
			}else{
				app.saveParkingInfo(null);
			}
			sendEventBoradcast(intent);
		} else {
			app.showToastMessage(R.string.client_error);
		}
	}

	void startLoggingService(){
		Intent archiveIntent = new Intent(getSupportActivity().getApplicationContext(), MainPipeline.class);
		archiveIntent.setAction(MainPipeline.ACTION_ENABLE);
		getSupportActivity().startService(archiveIntent);	
	}
	
	void stopLoggingService(){
		Intent archiveIntent = new Intent(getSupportActivity().getApplicationContext(), MainPipeline.class);
		archiveIntent.setAction(MainPipeline.ACTION_DISABLE);
		getSupportActivity().startService(archiveIntent);
	}
	
	private void sendEventBoradcast(final Intent intent){
		TravelApp.getInstance().sendLoggingEventBoradcast(intent);
	}
	
	void sendEventBoradcast(final String actionPlayload, final String data){
		Intent intent = new Intent();
		intent.setAction(CUSTOM_INTENT_ACTION);
		intent.putExtra("APPLICATION_ACTION", actionPlayload);
		intent.putExtra("APPLICATION_DATA", data);
		TravelApp.getInstance().sendLoggingEventBoradcast(intent);
	}
	
	void exportData() {
		Intent archiveIntent = new Intent(getSupportActivity().getApplicationContext(), MainPipeline.class);
		archiveIntent.setAction(MainPipeline.ACTION_ARCHIVE_DATA);
		getSupportActivity().startService(archiveIntent);
	}
	
	void fillSpinnerValue(final String value, final Spinner s){
		if(TextUtils.isEmpty(value) || (s == null)) return;
		for(int i = 0; i < s.getCount(); i++){
			String item = s.getItemAtPosition(i).toString();
			if((!TextUtils.isEmpty(item)) && value.equals(item)){
				s.setSelection(i, false);
				break;
			}
		}		
	}	
		
	public static class QuitAppDialog extends BaseAlertDialog{

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    	builder.setTitle(R.string.app_travel_quit_title);
	    	builder.setIcon(android.R.drawable.ic_dialog_info);
	    	builder.setMessage(R.string.app_travel_quit_content);
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
