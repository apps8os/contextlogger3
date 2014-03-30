package org.apps8os.contextlogger3.android;

import org.apps8os.contextlogger3.android.pipeline.MainPipeline;
import org.apps8os.contextlogger3.android.pipeline.MainPipeline.ContextLogger3ServiceConnection;
import org.apps8os.contextlogger3.android.probe.AppProbe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import edu.mit.media.funf.FunfManager;
import edu.mit.media.funf.pipeline.BasicPipeline;

public class MainActivity extends Activity {

	private Button archiveButton;
	private Handler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_main);

		((Button) findViewById(R.id.sendAppData)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Bundle b = new Bundle();
				b.putString("A", "AALTO");
				b.putString("B", "TKK");
				b.putString("C", "CSE");
				b.putString("D", "SDK");
				Postman.getInstance().send(v.getContext().getApplicationContext(), AppProbe.INTENT_ACTION, b);
			}
		});

		// Used to make interface changes on main thread
		handler = new Handler();

		// Runs an archive if pipeline is enabled
		archiveButton = (Button) findViewById(R.id.archiveButton);
		archiveButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MainPipeline mp = ContextLogger3ServiceConnection.getInstance().getMainPipeline();
				if(mp != null) {
					if (mp.isEnabled()) {
						mp.onRun(BasicPipeline.ACTION_ARCHIVE, null);

						// Wait 1 second for archive to finish, then refresh the UI
						// (Note: this is kind of a hack since archiving is seamless
						// and there are no messages when it occurs)
						handler.postDelayed(new Runnable() {
							@Override
							public void run() {
								Toast.makeText(getBaseContext(), "Archived!", Toast.LENGTH_SHORT).show();
							}
						}, 1000L);
					} else {
						Toast.makeText(getBaseContext(), "Pipeline is not enabled.", Toast.LENGTH_SHORT).show();
					}					
				} else {
					Toast.makeText(getBaseContext(), "Pipeline is not available.", Toast.LENGTH_SHORT).show();
				}
			}
		});

		// Bind to the service, to create the connection with FunfManager
		 bindService(new Intent(getApplicationContext(), FunfManager.class), 
				 ContextLogger3ServiceConnection.getInstance(), BIND_AUTO_CREATE);
	}

	@Override
	protected void onDestroy() {
		unbindService(ContextLogger3ServiceConnection.getInstance());
		super.onDestroy();
	}
}
