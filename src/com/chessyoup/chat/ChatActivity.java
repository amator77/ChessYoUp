package com.chessyoup.chat;

import java.io.IOException;

import android.app.Activity;
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

public class ChatActivity extends Activity implements ConnectionListener{

	private Handler handler;

	private GenericDevice remoteDevice;

	private String ownerAccount;
	
	private Connection connection;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat);
		this.handler = new Handler();
		Intent intent = getIntent();

		ownerAccount = intent.getStringExtra("owner_account");

		this.remoteDevice = new GenericDevice();
		this.remoteDevice.setAccount(intent.getStringExtra("remote_account"));
		this.remoteDevice.setDeviceIdentifier(intent
				.getStringExtra("remote_device_id"));
		this.remoteDevice.setRegisteredId(intent
				.getStringExtra("remote_gcm_registration_id"));
		this.remoteDevice.setDevicePhoneNumber(intent
				.getStringExtra("remote_phone_number"));
		
		this.connection = GCMConnectionManager.getManager().connect(this.remoteDevice, this);
		
		final Button sendChatButton = (Button) findViewById(R.id.sendChatButton);

		this.setTitle("Chat with :" + this.remoteDevice.getAccount());

		sendChatButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.d("MainActivity", "register onClick event");

				AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

					@Override
					protected Void doInBackground(Void... params) {
						final EditText editChatText = (EditText) findViewById(R.id.editChatText);
						
						handler.post(new Runnable() {

							@Override
							public void run() {
								TextView display = (TextView) findViewById(R.id.chatDisplay);
								
								display.append(ownerAccount
										+ ":"
										+ editChatText.getEditableText()
												.toString() + "\n");
							}
						});
						
						Message newMessage = new Message() {
							
							@Override
							public String getSourceId() {								
								return null;
							}
							
							@Override
							public int getSequence() {								
								return 0;
							}
							
							@Override
							public byte[] getPayload() {								
								return editChatText.getEditableText()
										.toString().getBytes();
							}
							
							@Override
							public String getDestinationId() {								
								return remoteDevice.getRegistrationId();
							}
						};
						
						try {
							ChatActivity.this.connection.sendMessage(newMessage);
						} catch (IOException e) {							
							e.printStackTrace();
						}
						
						return null;
					}
				};

				task.execute();
			}

		});
	}

	@Override
	public void onConnected(boolean status) {
		handler.post(new Runnable() {

			@Override
			public void run() {
				TextView display = (TextView) findViewById(R.id.chatDisplay);				
				display.append("connected!\n");
			}
		});		
	}

	@Override
	public void messageReceived(Connection source, final Message message) {
		
		handler.post(new Runnable() {

			@Override
			public void run() {
				TextView display = (TextView) findViewById(R.id.chatDisplay);				
				display.append(remoteDevice.getAccount() +":"+String.valueOf(message.getPayload()));
			}
		});
	}
}