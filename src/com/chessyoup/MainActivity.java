package com.chessyoup;

import java.io.IOException;
import java.util.Date;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.chessyoup.connector.ConnectionManager;
import com.chessyoup.connector.ConnectionManagerListener;
import com.chessyoup.connector.Device;
import com.chessyoup.gcm.chat.GCMChatActivity;
import com.chessyoup.server.gcm.GCMRemoteService;

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
		this.connManager = ConnectionManagerFactory.getFactory()
				.getGCMConnectionManager(
						new GCMRemoteService(
								this.chatProperties
										.getProperty("chessyoup_url")),
						this.getApplicationContext());
		
		setContentView(R.layout.main);

		Spinner spinner = (Spinner) findViewById(R.id.spinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.spinner_array,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);

		this.installListeners();
		pd = ProgressDialog.show(MainActivity.this, null, "Registering...", true,
				false, null);
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
				log(status ? "Registration is OK" : "Error on registration!");
			}
		});
	}

	@Override
	public void onDispose(final boolean status) {
		this.handler.post(new Runnable() {

			@Override
			public void run() {
				log("onDispose :" + status);
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
		connManager.addListener(this);
		final Button sendButton = (Button) findViewById(R.id.send);

		sendButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				EditText editText = (EditText) findViewById(R.id.editText);
				Spinner spinner = (Spinner) findViewById(R.id.spinner);

				if (spinner.getSelectedItem().toString()
						.equalsIgnoreCase("account")) {
					runFindAccountTask(editText.getEditableText().toString());
				} else {
					runFindPhoneTask(editText.getEditableText().toString());
				}
			}
		});

	}

	private void runFindPhoneTask(final String phone) {
		AsyncTask<Void, Void, Device> task = new AsyncTask<Void, Void, Device>() {

			@Override
			protected Device doInBackground(Void... params) {

				handler.post(new Runnable() {

					@Override
					public void run() {
						pd.setMessage("Find :" + phone);
						pd.show();
					}
				});
//
//				try {
//					return connManager.getRemoteService().findByPhoneNumber(
//							phone);
//
//				} catch (IOException e) {
//					Log.d("MainActivity",
//							"Error on searching :" + e.getMessage());
//					log(e.getMessage());
//					e.printStackTrace();
//				}

				return null;
			}

			protected void onPostExecute(Device result) {
				pd.dismiss();

				if (result != null) {
					log("Finded account with id :" + result.getRegistrationId());

					Intent intent = new Intent(MainActivity.this,
							GCMChatActivity.class);
					intent.putExtra("remote_device_id",
							result.getDeviceIdentifier());
					intent.putExtra("remote_phone_number",
							result.getDevicePhoneNumber());
					intent.putExtra("remote_gcm_registration_id",
							result.getRegistrationId());
					intent.putExtra("remote_account", result.getAccount());

					intent.putExtra("owner_account", connManager
							.getLocalDevice().getAccount());
					startActivity(intent);

				} else {
					log("Phone not found!");
				}
			}
		};

		task.execute();

	}

	private void runFindAccountTask(final String account) {

		AsyncTask<Void, Void, Device> task = new AsyncTask<Void, Void, Device>() {

			@Override
			protected Device doInBackground(Void... params) {

				handler.post(new Runnable() {

					@Override
					public void run() {
						pd.setMessage("Find :" + account);
						pd.show();
					}
				});

//				try {
//					return connManager.getRemoteService()
//							.findByAccount(account);
//					
//				} catch (IOException e) {
//					Log.d("MainActivity",
//							"Error on searching :" + e.getMessage());
//
//					log(e.getMessage());
//					e.printStackTrace();
//				}

				return null;
			}

			protected void onPostExecute(Device result) {
				pd.dismiss();

				if (result != null) {
					log("Finded account with id :" + result.getRegistrationId());

					Intent intent = new Intent(MainActivity.this,
							GCMChatActivity.class);
					intent.putExtra("remote_device_id",
							result.getDeviceIdentifier());
					intent.putExtra("remote_phone_number",
							result.getDevicePhoneNumber());
					intent.putExtra("remote_gcm_registration_id",
							result.getRegistrationId());
					intent.putExtra("remote_account", result.getAccount());

					intent.putExtra("owner_account", connManager
							.getLocalDevice().getAccount());
					startActivity(intent);

				} else {
					log("Account not found!");
				}
			}
		};

		task.execute();
	}
	
	private void runFindRoomsTask() {

		AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {

//				try {
//					connManager.getRemoteService().rooms();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}

				return null;
			}

			protected void onPostExecute(Void result) {
			}
		};

		task.execute();
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

	private void log(final String text) {
		this.handler.post(new Runnable() {

			@Override
			public void run() {
				TextView display = (TextView) findViewById(R.id.display);
				display.append(new Date().toString());
				display.append("\n");
				display.append(text);
				display.append("\n");
			}
		});
	}
}
