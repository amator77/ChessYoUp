package com.chessyoup;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;

public class StartActivity extends Activity {

	private ProgressDialog pd;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onStart() {
		super.onStart();
		
	}

	@Override
	public void onPause() {
		super.onPause(); // Always call the superclass method first
	}

	@Override
	public void onResume() {
		super.onResume(); // Always call the superclass method first

	}

	protected void onStop() {
		super.onStop(); // Always call the superclass method first
	}
	
	private void runGCMRegistrationTask(){
		pd = ProgressDialog.show(StartActivity.this, null,
				"GCM registration...", true, false, null);

		AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}

			protected void onPostExecute(Void result) {
				pd.dismiss();
			}
		};

		task.execute();
	}
		
}
