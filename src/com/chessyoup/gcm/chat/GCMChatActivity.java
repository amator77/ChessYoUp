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
import com.chessyoup.connector.Device;
import com.chessyoup.connector.GenericDevice;
import com.chessyoup.connector.Message;
import com.chessyoup.connector.gcm.GCMConnection;
import com.chessyoup.connector.gcm.GCMConnectionManager;
import com.chessyoup.connector.gcm.GCMMessage;

public class GCMChatActivity extends Activity implements ConnectionListener {

	private Handler handler;

	private ProgressDialog pd;

	private String ownerRegistrationId;

	private String ownerAccount;

	private static int sequnce;

	private GCMConnection connection;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.handler = new Handler();				
		setContentView(R.layout.chat);

		Intent intent = getIntent();
		ownerAccount = intent.getStringExtra("owner_account");			
		this.installListeners();
		
		if( intent.getExtras().getString("connected") != null &&  intent.getExtras().getString("connected").equals("true")){
			this.connection = GCMConnectionManager.getManager().getConnection(intent.getStringExtra("remote_gcm_registration_id"));
			this.connection.setListener(this);	
			this.setTitle("Chat with :" +deviceLabel(this.connection.getRemoteDevice()));
		}
		else{		
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
	public void onConnected(Connection connection,
			boolean status) {
		
		addMessage("system", status ? "Connected!!!" : "Error on conecting.");
		GCMChatActivity.this.connection = (GCMConnection)connection;
		
		this.handler.post(new Runnable() {
			
			@Override
			public void run() {
				GCMChatActivity.this.setTitle("Chat with :" + deviceLabel(GCMChatActivity.this.connection.getRemoteDevice()));				
			}
		});		
	}

	@Override
	public void messageReceived(Connection source, Message message) {
		addMessage( deviceLabel(this.connection.getRemoteDevice()), message.getBody());
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
				message.setDestinationRegistrationID(connection.getRemoteDevice()
						.getRegistrationId());
				EditText editChatText = (EditText) findViewById(R.id.editChatText);
				message.setBody(editChatText.getEditableText().toString());
				message.setSequnce(sequnce++);
				runSendMessageTask(message);
			}
		});
	}

	private void runConnectTask(final Intent onCreateIntent) {
		this.showProgressDialog("Connecting...");
				
		AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {

				GenericDevice remoteDevice = new GenericDevice(); 				
				remoteDevice.setAccount(onCreateIntent.getStringExtra("remote_account"));
				remoteDevice.setDeviceIdentifier(onCreateIntent
						.getStringExtra("remote_device_id"));
				remoteDevice.setRegistrationId(onCreateIntent
						.getStringExtra("remote_gcm_registration_id"));
				remoteDevice.setDevicePhoneNumber(onCreateIntent
						.getStringExtra("remote_phone_number"));	
				
				GCMConnectionManager.getManager().connect(remoteDevice,GCMChatActivity.this);						
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
				if( pd == null ){
					pd =  ProgressDialog.show(GCMChatActivity.this, null,
							message, true, false, null);							
				}
				else{
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
	
	private String deviceLabel(Device device){
		StringBuffer sb = new StringBuffer();
		
		if( device.getAccount() != null && device.getAccount().trim().length() > 0 ){
			sb.append(device.getAccount());
		}
		else if( device.getDevicePhoneNumber() != null && device.getDevicePhoneNumber().trim().length() > 0 ){
			sb.append(device.getDevicePhoneNumber());
		}
		else{
			sb.append(device.getDeviceIdentifier());
		}
		
		return sb.toString();
	}
}
