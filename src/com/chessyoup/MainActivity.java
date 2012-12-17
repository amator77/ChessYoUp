package com.chessyoup;

import java.io.IOException;
import java.util.Properties;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.chessyoup.chat.ChatActivity;
import com.chessyoup.chat.GCMChatActivity;
import com.chessyoup.connector.ConnectionManager;
import com.chessyoup.connector.ConnectionManagerListener;
import com.chessyoup.connector.Device;
import com.google.android.gcm.GCMRegistrar;

public class MainActivity extends Activity implements ConnectionManagerListener {

	private ConnectionManager connManager;

	private Handler handler;
	
	private ProgressDialog pd;
	
	private Properties chatProperties;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		Log.d("MainActivity", "on create");		
		this.chatProperties = this.loadAssets();
		this.handler = new Handler();
		this.connManager = ConnectionManagerFactory.getFactory().getGCMConnectionManager(new ChessYoUpRemoteService(this.chatProperties.getProperty("chessyoup_url")),this.getApplicationContext());
		pd = ProgressDialog.show(MainActivity.this, null, "message", true,false, null);				
		setContentView(R.layout.main);
		this.installListeners();
		pd.setMessage("Registering.aa..");
		pd.show();
		this.runGCMRegistrationTask();
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

	@Override
	public void onInitialize(final boolean status) {
				
		this.handler.post(new Runnable() {

			@Override
			public void run() {
				pd.dismiss();
				TextView display = (TextView) findViewById(R.id.display);				
				display.append("onInitialize :" + status + "\n");

				if (status) {
					display.append("Device  :"
							+ connManager.getLocalDevice().toString() + "\n");
				}
			}
		});
	}

	@Override
	public void onDispose(final boolean status) {
		this.handler.post(new Runnable() {

			@Override
			public void run() {
				TextView display = (TextView) findViewById(R.id.display);
				display.append("onDispose :" + status + "\n");
			}
		});
	}
	
	private void runGCMRegistrationTask() {
		
		AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				connManager.initialize();
				return null;
			}
		};

		task.execute();		
	}
	
	private void installListeners() {
		
		connManager.registerListener(this);
		
		final Button registerButton = (Button) findViewById(R.id.register);
		final Button unRegisterButton = (Button) findViewById(R.id.unregister);
		final Button sendButton = (Button) findViewById(R.id.send);

		registerButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.d("MainActivity", "register onClick event");

				AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

					@Override
					protected Void doInBackground(Void... params) {
						connManager.initialize();
						return null;
					}
				};

				task.execute();
			}

		});

		unRegisterButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.d("MainActivity", "unRegisterButton onClick event");

				AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

					@Override
					protected Void doInBackground(Void... params) {
						connManager.dispose();
						return null;
					}
				};

				task.execute();
			}
		});

		sendButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.d("MainActivity", "sendButton onClick event");

				AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

					@Override
					protected Void doInBackground(Void... params) {

						final EditText editText = (EditText) findViewById(R.id.editText);

						// try {
						// Device device =
						// connManager.getRemoteService().findByAccount(editText.getEditableText().toString());
						Intent intent = new Intent(MainActivity.this,
								StartActivity.class);
						// intent.putExtra("remote_device_id",
						// device.getDeviceIdentifier());
						// intent.putExtra("remote_phone_number",
						// device.getDevicePhoneNumber());
						// intent.putExtra("remote_gcm_registration_id",
						// device.getRegistrationId());
						// intent.putExtra("remote_account",
						// device.getAccount());
						//
						// intent.putExtra("owner_account",
						// connManager.getLocalDevice().getAccount());

						startActivity(intent);
						// } catch (IOException e) {
						// Log.d("MainActivity",
						// "Error on searching :"+e.getMessage());
						//
						// e.printStackTrace();
						// }

						return null;
					}
				};

				task.execute();

			}
		});
	}

	private Properties loadAssets() {
		AssetManager manager = this.getAssets();
		Properties p = new Properties();

		try {
			p.load(manager.open("chessyoup.properties"));
		} catch (IOException e) {
			Log.e("MainActivity", "Error on reading configuration!");
		}

		return p;
	}
}
