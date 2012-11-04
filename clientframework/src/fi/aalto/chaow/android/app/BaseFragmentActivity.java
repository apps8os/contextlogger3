package fi.aalto.chaow.android.app;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.actionbarsherlock.app.SherlockFragmentActivity;


public abstract class BaseFragmentActivity extends SherlockFragmentActivity {
	
	protected FragmentTransaction getSupportFragmentTransaction(){
		return getSupportFragmentManager().beginTransaction();
	}

	public interface OnSupportFragmentListener {
		void onFragmentChanged(int layoutResId, Bundle bundle);
	}
}
