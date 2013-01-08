package com.chessyoup.chat;

import java.io.IOException;
import java.util.Date;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.chessyoup.R;
import com.chessyoup.connector.Connection;
import com.chessyoup.connector.ConnectionListener;
import com.chessyoup.connector.Device;
import com.chessyoup.connector.GenericDevice;
import com.chessyoup.connector.Message;
import com.chessyoup.server.RoomsManager;

public class GCMChatActivity extends Activity implements ConnectionListener {

	private Handler handler;

	private ProgressDialog pd;

	private String ownerRegistrationId;

	private String ownerAccount;

	private static int sequnce;

	private Connection connection;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.handler = new Handler();
		setContentView(R.layout.chat);

		Intent intent = getIntent();
		ownerAccount = intent.getStringExtra("owner_account");
		this.installListeners();

		if (intent.getExtras().getString("connected") != null
				&& intent.getExtras().getString("connected").equals("true")) {

			this.connection = RoomsManager
					.getManager()
					.getConnectionManager()
					.getConnection(
							intent.getStringExtra("remote_gcm_registration_id"));

			this.connection.setConnectionListener(this);
			this.setTitle("Chat with :"
					+ deviceLabel(this.connection.getRemoteDevice()));
		} else {
			this.runConnectTask(intent);
		}
	}

	@Override
	public void onStart() {
		super.onStart();

	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();

	}

	protected void onStop() {
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.runOnDisconectTask();
	}
	
	@Override
	public void onConnected(Connection connection, boolean status) {

		this.hideProgressDialog();

		addMessage("system", status ? "Connected!!!" : "Error on conecting.");
		this.connection = connection;

		this.handler.post(new Runnable() {

			@Override
			public void run() {
				GCMChatActivity.this.setTitle("Chat with :"
						+ deviceLabel(GCMChatActivity.this.connection
								.getRemoteDevice()));
			}
		});
	}

	@Override
	public void messageReceived(Connection source, Message message) {
		addMessage(deviceLabel(this.connection.getRemoteDevice()),
				message.getBody());
	}

	private void addMessage(final String source, final String text) {
		this.handler.post(new Runnable() {

			@Override
			public void run() {
				TextView display = (TextView) findViewById(R.id.chatDisplay);
				display.append(source != null ? source : "null");
				display.append(",");
				display.append(new Date().toString());
				display.append("\n");
				display.append(text != null ? text : "null");
				display.append("\n");
			}
		});
	}

	private void installListeners() {

		final Button sendChatButton = (Button) findViewById(R.id.sendChatButton);

		sendChatButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.d("GCMChatActivity", "Send message request.");
				EditText editChatText = (EditText) findViewById(R.id.editChatText);
				runSendMessageTask(editChatText.getEditableText().toString());
			}
		});
	}

	private void runConnectTask(final Intent onCreateIntent) {
		this.showProgressDialog("Connecting...");

		AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {

				GenericDevice remoteDevice = new GenericDevice();
				remoteDevice.setAccount(onCreateIntent
						.getStringExtra("remote_account"));
				remoteDevice.setDeviceIdentifier(onCreateIntent
						.getStringExtra("remote_device_id"));
				remoteDevice.setRegistrationId(onCreateIntent
						.getStringExtra("remote_gcm_registration_id"));
				remoteDevice.setDevicePhoneNumber(onCreateIntent
						.getStringExtra("remote_phone_number"));

				RoomsManager.getManager().getConnectionManager()
						.connect(remoteDevice, GCMChatActivity.this);

				return null;
			}
		};

		task.execute();
	}

	
	private void runOnDisconectTask() {

		AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {

				if( GCMChatActivity.this.connection != null ){
					RoomsManager.getManager().getConnectionManager().closeConnection(GCMChatActivity.this.connection);
				}

				return null;
			}
		};

		task.execute();
	}
	
	private void runSendMessageTask(final String message) {
		AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				addMessage(ownerAccount, message);

//				try {
//					connection.sendMessage(message);
//				} catch (IOException exception) {
//					addMessage("system", exception.getMessage());
//				}

				return null;
			}
		};

		task.execute();
	}

	private void showProgressDialog(final String message) {
		this.handler.post(new Runnable() {

			@Override
			public void run() {
				if (pd == null) {
					pd = ProgressDialog.show(GCMChatActivity.this, null,
							message, true, false, null);
				} else {
					pd.setMessage(message);
					pd.show();
				}
			}
		});
	}

	private void hideProgressDialog() {
		this.handler.post(new Runnable() {

			@Override
			public void run() {
				pd.dismiss();
			}
		});
	}

	private String deviceLabel(Device device) {
		StringBuffer sb = new StringBuffer();

		if (device.getAccount() != null
				&& device.getAccount().trim().length() > 0) {
			sb.append(device.getAccount());
		} else if (device.getDevicePhoneNumber() != null
				&& device.getDevicePhoneNumber().trim().length() > 0) {
			sb.append(device.getDevicePhoneNumber());
		} else {
			sb.append(device.getDeviceIdentifier());
		}

		return sb.toString();
	}

	@Override
	public void onDisconnected(Connection source) {
		addMessage(deviceLabel(source.getRemoteDevice()), "Connections is closed!!!");
		finish();
	}
}
