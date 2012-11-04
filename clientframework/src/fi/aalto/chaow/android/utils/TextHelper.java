package fi.aalto.chaow.android.utils;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public final class TextHelper {	
	
	private TextHelper(){
	}

	public static void hideSoftInput(final Context context, final EditText et){
		InputMethodManager mgr = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		mgr.hideSoftInputFromWindow(et.getWindowToken(), 0);
	}
	
	public static void showSoftInput(final Context context, final EditText et){
		InputMethodManager mgr = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		mgr.showSoftInput(et, InputMethodManager.SHOW_FORCED);
		et.requestFocus();
	}
}
