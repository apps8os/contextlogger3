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
