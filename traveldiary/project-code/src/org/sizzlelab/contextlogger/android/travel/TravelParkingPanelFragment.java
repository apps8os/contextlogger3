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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.widget.AdapterView;
import org.holoeverywhere.widget.AdapterView.OnItemSelectedListener;
import org.holoeverywhere.widget.Spinner;
import org.json.JSONException;
import org.json.JSONObject;
import org.sizzlelab.contextlogger.android.R;
import org.sizzlelab.contextlogger.android.model.ActionEvent;
import org.sizzlelab.contextlogger.android.model.EventState;
import org.sizzlelab.contextlogger.android.model.handler.ActionEventHandler;
import org.sizzlelab.contextlogger.android.travel.TravelCustomSubjectFragmentDialog.CustomSubject;
import org.sizzlelab.contextlogger.android.travel.TravelCustomSubjectFragmentDialog.TravelCustomSubjectListener;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;

import fi.aalto.chaow.android.app.BaseAlertDialog.AlertDialogListener;

public class TravelParkingPanelFragment extends AbstractTravelCommonFragment implements OnClickListener, OnItemSelectedListener{

	private Handler mHandler = new Handler();
	private Runnable mTimedTask = new Runnable(){
		@Override
		public void run() {
			ArrayList<ActionEvent> parkingList = ActionEventHandler.getInstance().getAllItems(getSupportActivity().getApplicationContext(), false);
			for(ActionEvent ae : parkingList){
				if(TravelApp.getInstance().getString(R.string.travel_parking).equals(ae.getActionEventName())){
					if(mTextViewParkingDuration != null){
						mTextViewParkingDuration.setText(ae.getDuration(true));						
					}
				}
			}
			mHandler.postDelayed(mTimedTask , 500);				
		}
	};
	
	private Spinner mSpinnerParkingPlace = null;
	private Spinner mSpinnerPayment = null;
	private Spinner mSpinnerMode = null;
	private Spinner mSpinnerModePerson = null;
	private Spinner mSpinnerPrice = null;
	
	private ImageButton mButtonPlay = null;
	private ImageButton mButtonStop = null;
	
	private TravelApp mApp = null;
	
	private View mViewNoParking = null;
	private View mViewParkingDurationContainer = null;
	private TextView mTextViewParkingDuration = null;
	
	private String mCurrentMode = null;
	private String mCurrentPersonNumber = null;
	private String mParkingPlace = null;
	private String mParkingPrice = null;
	private String mPaymentMethod = null;
	
	private String mNewMode = null;
	private String mNewPlace = null;
	private String mNewPrice = null;
	private String mNewPayment = null;
	
	public TravelParkingPanelFragment(){
	}
		
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		mApp = TravelApp.getInstance();
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		// prevent the same menu pop up
		menu.clear();
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		final ActionBar actionbar = activity.getSupportActionBar();
		actionbar.setHomeButtonEnabled(true);
		actionbar.setDisplayHomeAsUpEnabled(true);
		actionbar.getCustomView().setVisibility(View.GONE);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.travel_parking_panel, container, false); 
		mSpinnerParkingPlace = (Spinner)view.findViewById(R.id.spinner_travel_parking_place_list);
		mSpinnerParkingPlace.setOnItemSelectedListener(this);
		mSpinnerPayment = (Spinner)view.findViewById(R.id.spinner_travel_parking_payment_list);
		mSpinnerPayment.setOnItemSelectedListener(this);
		mSpinnerPrice = (Spinner)view.findViewById(R.id.spinner_travel_parking_price_list);
		mSpinnerPrice.setOnItemSelectedListener(this);
		mSpinnerMode = (Spinner)view.findViewById(R.id.spinner_travel_transport_mode_list);
		mSpinnerMode.setOnItemSelectedListener(this);
		mSpinnerModePerson = (Spinner)view.findViewById(R.id.spinner_travel_transport_mode_person_list);
		mSpinnerModePerson.setOnItemSelectedListener(this);
		mButtonPlay = (ImageButton) view.findViewById(R.id.image_button_travel_parking_play);
		mButtonPlay.setOnClickListener(this);
		mButtonStop = (ImageButton) view.findViewById(R.id.image_button_travel_parking_stop);
		mButtonStop.setOnClickListener(this);
		mViewNoParking = (TextView) view.findViewById(R.id.text_view_no_parking);
		mViewParkingDurationContainer = view.findViewById(R.id.layout_parking_duration_container);
		mTextViewParkingDuration = (TextView)view.findViewById(R.id.text_view_travel_parking_duration_value);
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		refreshUI();
		mHandler.post(mTimedTask);
	}
	
	@Override
	public void onStop() {
		super.onStop();
		mHandler.removeCallbacks(mTimedTask);
	}

	private void toggleUIComponent(final boolean enable){
		mSpinnerPayment.setEnabled(enable);
		mSpinnerParkingPlace.setEnabled(enable);
		mButtonStop.setEnabled(enable);
		mButtonPlay.setEnabled(enable);
		mSpinnerMode.setEnabled(enable);
		mSpinnerModePerson.setEnabled(enable);
		mSpinnerPrice.setEnabled(enable);
	}
	
	private void refreshUI(){
		ActionEvent parking = null;
		ArrayList<ActionEvent> parkingList = ActionEventHandler.getInstance().getAllItems(getSupportActivity().getApplicationContext(), false);
		for(ActionEvent ae : parkingList){
			if(getString(R.string.travel_parking).equals(ae.getActionEventName())){
				parking = ae;
				break;
			}
		} 
		refreshSpinners();	
		if(parking == null){
			mViewNoParking.setVisibility(View.VISIBLE);
			mViewParkingDurationContainer.setVisibility(View.GONE);
			mButtonStop.setEnabled(false);
		}else{
			toggleUIComponent(false);
			final String parkingInfo = mApp.getParkingInfo();
			if(!TextUtils.isEmpty(parkingInfo)){
				try {
					JSONObject object = new JSONObject(parkingInfo);
					if(object.isNull("message")){
						fillValue(object);
					}else{
						fillValue(object.getJSONObject("message")); 
					}
				} catch (JSONException e) {
				}
			}
			mButtonStop.setEnabled(true);
			mSpinnerPayment.setEnabled(true);
			mSpinnerPrice.setEnabled(true);
			mViewNoParking.setVisibility(View.GONE);
			mViewParkingDurationContainer.setVisibility(View.VISIBLE);
		}
	}

	private void fillValue(JSONObject data)throws JSONException{
		if(!data.isNull("mode")){
			fillSpinnerValue(data.getString("mode"), mSpinnerMode);
		}
		if(!data.isNull("persons")){
			fillSpinnerValue(data.getString("persons"), mSpinnerModePerson);
		}
		if(!data.isNull("place")){
			fillSpinnerValue(data.getString("place"), mSpinnerParkingPlace);
		}
		if(!data.isNull("price")){
			fillSpinnerValue(data.getString("price"), mSpinnerPrice);
		}
		if(!data.isNull("payment")){
			fillSpinnerValue(data.getString("payment"), mSpinnerPayment);
		}
	}
	
	private void refreshSpinners(){
		refreshModeSpinner();
		refreshParkingPlaceSpinner();
		refreshPaymentMethodSpinner();
		refreshPriceSpinner();
	}
	
	private void refreshPriceSpinner(){
		String[] arrayPrice = getResources().getStringArray(R.array.travel_parking_price);
		ArrayList<String> listPrice = new ArrayList<String>(Arrays.asList(arrayPrice));
		// load the saved data, if any
		ArrayList<String> priceTempList = TravelApp.getStringToList(mApp.getParkingPrice());
		if((priceTempList != null) && (!priceTempList.isEmpty())){
			for(String m : priceTempList){
				listPrice.add(listPrice.size() - 1, m);
			}
		}
		ArrayAdapter<String> dataAdapterPlace = new ArrayAdapter<String>(getSupportActivity().getApplicationContext(),
				R.layout.spinner_item, listPrice);
		int priceListSelectionOffset = 0;
		if((!TextUtils.isEmpty(mNewPrice)) && (listPrice != null) && (!listPrice.isEmpty())){
			for(int i = 0; i < listPrice.size(); i++){
				String s = listPrice.get(i);
				if(s.equals(mNewPrice)){
					priceListSelectionOffset = i;
					mNewPrice = null;
					break;
				}
			} 
		}		
		mSpinnerPrice.setAdapter(dataAdapterPlace);
		if(priceListSelectionOffset > 0){
			mSpinnerPrice.setSelection(priceListSelectionOffset, false);
		} else {
			mSpinnerPrice.setSelection(0);
		}
		mSpinnerPrice.invalidate();
	}
	
	private void refreshParkingPlaceSpinner(){
		String[] arrayPlace = getResources().getStringArray(R.array.travel_parking_place);
		ArrayList<String> listPlace = new ArrayList<String>(Arrays.asList(arrayPlace));
		// load the saved data, if any
		ArrayList<String> placeTempList = TravelApp.getStringToList(mApp.getParkingPlace());
		if((placeTempList != null) && (!placeTempList.isEmpty())){
			for(String m : placeTempList){
				listPlace.add(listPlace.size() - 1, m);
			}
		}
		ArrayAdapter<String> dataAdapterPlace = new ArrayAdapter<String>(getSupportActivity().getApplicationContext(),
				R.layout.spinner_item, listPlace);
		int placeListSelectionOffset = 0;
		if((!TextUtils.isEmpty(mNewPlace)) && (listPlace != null) && (!listPlace.isEmpty())){
			for(int i = 0; i < listPlace.size(); i++){
				String s = listPlace.get(i);
				if(s.equals(mNewPlace)){
					placeListSelectionOffset = i;
					mNewPlace = null;
					break;
				}
			} 
		}		
		mSpinnerParkingPlace.setAdapter(dataAdapterPlace);
		if(placeListSelectionOffset > 0){
			mSpinnerParkingPlace.setSelection(placeListSelectionOffset, false);
		} else {
			mSpinnerParkingPlace.setSelection(0);
		}
		mSpinnerParkingPlace.invalidate();
	}
	
	private void refreshPaymentMethodSpinner(){
		String[] arrayPayment = getResources().getStringArray(R.array.travel_parking_payment_method);
		ArrayList<String> listPayment = new ArrayList<String>(Arrays.asList(arrayPayment));
		// load the saved data, if any
		ArrayList<String> paymentTempList = TravelApp.getStringToList(mApp.getParkingPayment());
		if((paymentTempList != null) && (!paymentTempList.isEmpty())){
			for(String m : paymentTempList){
				listPayment.add(listPayment.size() - 1, m);
			}
		}
		ArrayAdapter<String> dataAdapterPayment = new ArrayAdapter<String>(getSupportActivity().getApplicationContext(),
				R.layout.spinner_item, listPayment);
		int paymentListSelectionOffset = 0;
		if((!TextUtils.isEmpty(mNewPayment)) && (listPayment != null) && (!listPayment.isEmpty())){
			for(int i = 0; i < listPayment.size(); i++){
				String s = listPayment.get(i);
				if(s.equals(mNewPayment)){
					paymentListSelectionOffset = i;
					mNewPayment = null;
					break;
				}
			} 
		}
		mSpinnerPayment.setAdapter(dataAdapterPayment);
		if(paymentListSelectionOffset > 0){
			mSpinnerPayment.setSelection(paymentListSelectionOffset, false);
		} else {
			mSpinnerPayment.setSelection(0);
		}
		mSpinnerPayment.invalidate();		
	}
	
	private void refreshModeSpinner(){
		// mode
		String[] arrayMode = getResources().getStringArray(R.array.travel_mode_array);
		ArrayList<String> listMode = new ArrayList<String>(Arrays.asList(arrayMode));
		// load the saved data, if any
		ArrayList<String> modeTempList = TravelApp.getStringToList(mApp.getTravelModes());
		if((modeTempList != null) && (!modeTempList.isEmpty())){
			for(String m : modeTempList){
				listMode.add(listMode.size() - 1, m);
			}
		}
		ArrayAdapter<String> dataAdapterMode = new ArrayAdapter<String>(getSupportActivity().getApplicationContext(),
				R.layout.spinner_item, listMode);
		int modeListSelectionOffset = 0;
		if((!TextUtils.isEmpty(mNewMode)) && (listMode != null) && (!listMode.isEmpty())){
			for(int i = 0; i < listMode.size(); i++){
				String s = listMode.get(i);
				if(s.equals(mNewMode)){
					modeListSelectionOffset = i;
					mNewMode = null;
					break;
				}
			}
		}
		mSpinnerMode.setAdapter(dataAdapterMode);
		if(modeListSelectionOffset > 0){
			mSpinnerMode.setSelection(modeListSelectionOffset, false);
		} else {
			mSpinnerMode.setSelection(0);
		}
		mSpinnerMode.invalidate();		
		
		// hard-code for person
		String[] arrayPerson = {"1", "2", "3", "4", "5", "6", "7", "8"};
		ArrayList<String> listPerson = new ArrayList<String>(Arrays.asList(arrayPerson));
		ArrayAdapter<String> dataAdapterPerson = new ArrayAdapter<String>(getSupportActivity().getApplicationContext(),
				R.layout.spinner_item, listPerson);
		mSpinnerModePerson.setAdapter(dataAdapterPerson);
	}
	
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		final AdapterView<?> arg = parent;
		if(parent != null){
			boolean isShown = false;
			CustomSubject pcs = CustomSubject.UNKONWN;
			final String itemName = parent.getItemAtPosition(pos).toString();
			final int viewId = parent.getId();
			if(viewId == R.id.spinner_travel_transport_mode_list){
				if(pos == parent.getCount() - 1){
					isShown = true;
					pcs = CustomSubject.MODE;
				}
				mCurrentMode = itemName;
			}else if(viewId == R.id.spinner_travel_parking_payment_list){
				if(pos == parent.getCount() - 1){
					isShown = true;
					pcs = CustomSubject.PAYMENT;
				}
				mPaymentMethod = itemName;
			}else if(viewId == R.id.spinner_travel_parking_place_list){
				if(pos == parent.getCount() - 1){
					isShown = true;
					pcs = CustomSubject.PLACE;
				}
				mParkingPlace = itemName;
			}else if(viewId == R.id.spinner_travel_transport_mode_person_list){
				mCurrentPersonNumber = itemName;
			}else if(viewId == R.id.spinner_travel_parking_price_list){
				if(pos == parent.getCount() - 1){
					isShown = true;
					pcs = CustomSubject.PRICE;
				}
				mParkingPrice = itemName;
			}
			
			if(isShown){
				TravelCustomSubjectFragmentDialog tcsfg = new TravelCustomSubjectFragmentDialog();
				tcsfg.config(new TravelCustomSubjectListener (){
					@Override
					public void OnTagNameInputCompleted(String itemName, CustomSubject subject) {
						handleParkingCustomeSubjectItem(itemName, subject);
					}
				}, pcs);
				tcsfg.setAlertDialogListener(new AlertDialogListener(){
					@Override
					public void onPositiveClick() { }
					@Override
					public void onNegativeClick() {
						resetSpinner(arg);
					}
					@Override
					public void onCancel() { 
						resetSpinner(arg);
					}
				});
				tcsfg.show(getFragmentManager());				
			}
		}
	}

	private void handleParkingCustomeSubjectItem(final String itemName, final CustomSubject subject){	
		switch(subject){
			case MODE:
				mNewMode = itemName;
				ArrayList<String> modeList = TravelApp.getStringToList(mApp.getTravelModes());
				if(modeList == null){
					mApp.saveTravelMode(itemName);
				}else{
					modeList.add(itemName);
					String strMode = TextUtils.join(";", modeList.toArray());
					mApp.saveTravelMode(strMode);
				}
				mCurrentMode = itemName;
				refreshModeSpinner();
				break;
			case PLACE:
				mNewPlace = itemName;
				ArrayList<String> placeList = TravelApp.getStringToList(mApp.getParkingPlace());
				if(placeList == null){
					mApp.saveParkingPlace(itemName);
				}else{
					placeList.add(itemName);
					String strPlace = TextUtils.join(";", placeList.toArray());
					mApp.saveParkingPlace(strPlace);
				}
				mParkingPlace = itemName;
				refreshParkingPlaceSpinner();
				break;
			case PAYMENT:
				mNewPayment = itemName;
				ArrayList<String> paymentList = TravelApp.getStringToList(mApp.getParkingPayment());
				if(paymentList == null){
					mApp.saveParkingPayment(itemName);
				}else{
					paymentList.add(itemName);
					String strPayment = TextUtils.join(";", paymentList.toArray());
					mApp.saveParkingPayment(strPayment);
				}
				mPaymentMethod = itemName;
				refreshPaymentMethodSpinner();
				break;
			case PRICE:
				mNewPrice = itemName;
				ArrayList<String> priceList = TravelApp.getStringToList(mApp.getParkingPrice());
				if(priceList == null){
					mApp.saveParkingPrice(itemName);
				}else{
					priceList.add(itemName);
					String strPrice = TextUtils.join(";", priceList.toArray());
					mApp.saveParkingPrice(strPrice);
				}
				mParkingPrice = itemName;
				refreshPriceSpinner();
				break;					
			default:
				return;
		}
	}
	
	private void resetSpinner(AdapterView<?> parent){
		if(parent != null){
			final int viewId = parent.getId();
			if(viewId == R.id.spinner_travel_transport_mode_list){
				mSpinnerMode.setSelection(0, false);
			}else if(viewId == R.id.spinner_travel_parking_payment_list){
				mSpinnerPayment.setSelection(0, false);
			}else if(viewId == R.id.spinner_travel_parking_place_list){
				mSpinnerParkingPlace.setSelection(0, false);
			}else if(viewId == R.id.spinner_travel_parking_price_list){
				mSpinnerPrice.setSelection(0, false);
			}
		}
	}
	
	@Override
	public void onNothingSelected(AdapterView<?> parent) {
	}

	@Override
	public void onClick(View v) {
		if(v != null){
			final int viewId = v.getId();
			if(viewId == R.id.image_button_travel_parking_play){
				startParkingEvent();
			}else if(viewId == R.id.image_button_travel_parking_stop){
				stopParkingEvent();
			}			
		}
	}
	
	private void startParkingEvent(){
		toggleUIComponent(false);
		ActionEvent ae = new ActionEvent(getString(R.string.travel_parking), System.currentTimeMillis());
		ae.setState(EventState.START);
		ActionEventHandler.getInstance().insert(getSupportActivity().getApplicationContext(), ae);	

		HashMap<String, String> userMsg = new HashMap<String,String>();
		
		if(!TextUtils.isEmpty(mCurrentMode)){
			userMsg.put("mode", mCurrentMode);
			mCurrentMode = null;
		}else{
			userMsg.put("mode", mSpinnerMode.getItemAtPosition(0).toString());
		}
		
		if(!TextUtils.isEmpty(mCurrentPersonNumber)){
			userMsg.put("persons", mCurrentPersonNumber);
			mCurrentPersonNumber = null;
		}else{
			userMsg.put("persons", mSpinnerModePerson.getItemAtPosition(0).toString());
		}

		if(!TextUtils.isEmpty(mParkingPlace)){
			userMsg.put("place", mParkingPlace);
			mParkingPlace = null;
		}else{
			userMsg.put("place", mSpinnerParkingPlace.getItemAtPosition(0).toString());
		}			
		
		fetchParkingPaymentPrice(userMsg, false);
		
		notifyEvent(ae.getMessagePayload(), userMsg);	
		refreshUI();
	}

	private void fetchParkingPaymentPrice(HashMap<String, String> userMsg, boolean needed){
		if(!TextUtils.isEmpty(mParkingPrice)){
			userMsg.put("price", mParkingPrice);
			mParkingPrice = null;
		}else{
			if(needed) userMsg.put("price", mSpinnerPrice.getItemAtPosition(0).toString());
		}

		if(!TextUtils.isEmpty(mPaymentMethod)){
			userMsg.put("payment", mPaymentMethod);
			mPaymentMethod = null;
		}else{
			if(needed) 	userMsg.put("paymentMethod", mSpinnerPayment.getItemAtPosition(0).toString());
		}
	}
	
	private void stopParkingEvent(){
		toggleUIComponent(false);
		ArrayList<ActionEvent> stopList = ActionEventHandler.getInstance().getAllItems(getSupportActivity().getApplicationContext(), false);
		for(ActionEvent ae : stopList){
			if(getString(R.string.travel_parking).equals(ae.getActionEventName())){
				ae.confirmBreakTimestamp();
				ae.setState(EventState.STOP);
				ActionEventHandler.getInstance().update(getSupportActivity().getApplicationContext(), ae);
				HashMap<String, String> userMsg = new HashMap<String,String>();
				fetchParkingPaymentPrice(userMsg, true);
				notifyEvent(ae.getMessagePayload(), userMsg);
				mTextViewParkingDuration.setText("");
				break;
			}
		} 
		// pop up this out of the stack
		getSupportActivity().getSupportFragmentManager().popBackStack();
	}
	
}
