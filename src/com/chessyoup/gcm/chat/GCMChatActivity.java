package com.chessyoup.gcm.chat;

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
import com.chessyoup.connector.GenericDevice;
import com.chessyoup.connector.Message;
import com.chessyoup.connector.gcm.GCMConnectionManager;
import com.chessyoup.connector.gcm.GCMMessage;

public class GCMChatActivity extends Activity {

	private Handler handler;

	private ProgressDialog pd;

	private GenericDevice remoteDevice;

	private String ownerRegistrationId;

	private String ownerAccount;

	private static int sequnce;

	private Connection connection;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.handler = new Handler();
		pd = ProgressDialog.show(GCMChatActivity.this, null,
				"GCM registration...", true, false, null);
		setContentView(R.layout.chat);

		Intent intent = getIntent();
		ownerAccount = intent.getStringExtra("owner_account");
		ownerRegistrationId = intent
				.getStringExtra("owner_gcm_registration_id");
		this.remoteDevice = new GenericDevice();
		this.remoteDevice.setAccount(intent.getStringExtra("remote_account"));
		this.remoteDevice.setDeviceIdentifier(intent
				.getStringExtra("remote_device_id"));
		this.remoteDevice.setRegistrationId(intent
				.getStringExtra("remote_gcm_registration_id"));
		this.remoteDevice.setDevicePhoneNumber(intent
				.getStringExtra("remote_phone_number"));
		this.setTitle("Chat with :" + this.remoteDevice.getAccount());
		this.installListeners();
		this.runConnectTask();
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

	private void addMessage(final String source, final String text) {
		this.handler.post(new Runnable() {

			@Override
			public void run() {
				TextView display = (TextView) findViewById(R.id.chatDisplay);
				display.append(source);
				display.append(",");
				display.append(new Date().toString());
				display.append("\n");
				display.append(":");
				display.append(text);
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
				GCMMessage message = new GCMMessage();
				message.setSourceRegistrationID(ownerRegistrationId);
				message.setDestinationRegistrationID(remoteDevice
						.getRegistrationId());
				EditText editChatText = (EditText) findViewById(R.id.editChatText);
				message.setBody(editChatText.getEditableText().toString());
				message.setSequnce(sequnce++);
				runSendMessageTask(message);
			}
		});
	}

	private void runConnectTask() {

		this.showProgressDialog("Connecting...");

		AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {

				GCMConnectionManager.getManager().connect(remoteDevice,
						new ConnectionListener() {

							@Override
							public void onConnected(Connection connection,
									boolean status) {
								addMessage("system", status ? "Connected!!!"
										: "Error on conecting.");
								GCMChatActivity.this.connection = connection;
							}

							@Override
							public void messageReceived(Connection source,
									Message message) {
								addMessage(
										remoteDevice.getAccount() != null ? remoteDevice
												.getAccount() : remoteDevice
												.getDevicePhoneNumber(),
										message.getBody());
							}
						});

				return null;
			}

			protected void onPostExecute(Void result) {
				hideProgressDialog();
			}
		};

		task.execute();
	}

	private void runSendMessageTask(final Message message) {
		AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				addMessage(ownerAccount, message.getBody());

				try {
					connection.sendMessage(message);
				} catch (IOException exception) {
					addMessage("system", exception.getMessage());
				}

				return null;
			}
		};

		task.execute();
	}

	private void showProgressDialog(final String message) {
		this.handler.post(new Runnable() {

			@Override
			public void run() {
				pd.setMessage(message);
				pd.show();
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
}
