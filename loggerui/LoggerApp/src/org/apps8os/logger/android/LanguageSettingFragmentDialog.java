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
import java.util.Locale;

import org.apps8os.logger.android.manager.AppManager;
import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.widget.RadioButton;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import fi.aalto.chaow.android.app.BaseAlertDialog;

/**
 * Dilog for asking users to change the language setting
 * 
 * @author Chao Wei
 *
 */
public class LanguageSettingFragmentDialog extends BaseAlertDialog implements OnCheckedChangeListener {
	
	public static LanguageSettingFragmentDialog newInstance(AlertDialogListener l){
		LanguageSettingFragmentDialog lsfd = new LanguageSettingFragmentDialog();
		lsfd.setAlertDialogListener(l);
		return lsfd;
	}
	
	private RadioButton mRadioButtonEng = null;
	private RadioButton mRadioButtonCn = null;
	private boolean mInit = false;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Dialog dialog = getDialog();
		dialog.requestWindowFeature(STYLE_NO_TITLE);
    	final View view = inflater.inflate(R.layout.language_setting, null);
    	((RadioGroup)view.findViewById(R.id.radio_group_language)).setOnCheckedChangeListener(this);
    	mRadioButtonEng = (RadioButton) view.findViewById(R.id.radio_button_lang_en);
    	mRadioButtonCn = (RadioButton) view.findViewById(R.id.radio_button_lang_cn);
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
    	mInit = true;
    	final String localeLang = AppManager.getCurrentLocaleLanguage();
    	if(TextUtils.isEmpty(localeLang)){
    		mRadioButtonEng.setChecked(true);
    	} else {
    		if(localeLang.toLowerCase(Locale.getDefault()).equals(getString(R.string.lang_cn))){
    			mRadioButtonCn.setChecked(true);
    		} else if (localeLang.toLowerCase(Locale.getDefault()).equals(getString(R.string.lang_fi))){
    			mRadioButtonEng.setChecked(true);
    		} else {
    			mRadioButtonEng.setChecked(true);
    		}
    	}
	};

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		if(group == null) return;
		// filter the first time that UI just in place
		if(mInit){
			mInit = false;
			return;
		}
		Locale locale = null;
		final int gourpId = group.getId();
		if(gourpId == R.id.radio_group_language){
			if(checkedId == R.id.radio_button_lang_en) {
				locale = new Locale(getString(R.string.lang_en));
			} else if (checkedId == R.id.radio_button_lang_cn) {
				locale = new Locale(getString(R.string.lang_cn));
			}
		}
		if(locale != null){
			LoggerApp.getInstance().updateLocale(locale);
			AppManager.refreshLocale();
			if(getDialogListener() != null){
				getDialogListener().onPositiveClick();
				dismiss();
			}
		}
	}
	
}
