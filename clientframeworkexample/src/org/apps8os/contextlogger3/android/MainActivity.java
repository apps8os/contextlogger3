package org.apps8os.contextlogger3.android;

import org.apps8os.contextlogger3.android.app.AbstractActivity;
import org.apps8os.contextlogger3.android.clientframework.Postman;
import org.apps8os.contextlogger3.android.clientframework.probe.AppProbe;
import org.holoeverywhere.app.Activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class MainActivity extends AbstractActivity<Activity> implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_main);
		findViewById(R.id.sendAppData).setOnClickListener(this);
		// Runs an archive if pipeline is enabled
		findViewById(R.id.archiveButton).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if(v == null) return;
		final int viewId = v.getId();
		if(viewId == R.id.sendAppData) {
			Bundle b = new Bundle();
			b.putString("A", "AALTO");
			b.putString("B", "TKK");
			b.putString("C", "CSE");
			b.putString("D", "SDK");
			Postman.getInstance().send(v.getContext().getApplicationContext(), AppProbe.INTENT_ACTION, b);
		} else if (viewId == R.id.archiveButton) {
			exportData();
		}
	}
}
