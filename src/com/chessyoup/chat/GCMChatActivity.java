package com.chessyoup.chat;

import com.chessyoup.R;
import com.chessyoup.connector.GenericDevice;
import com.chessyoup.connector.gcm.GCMConnectionManager;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class GCMChatActivity extends Activity {
	
	private ProgressDialog pd;
	
	private GenericDevice remoteDevice;
	
	private String ownerAccount;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		pd = ProgressDialog.show(GCMChatActivity.this, null,"GCM registration...", true, false, null);		
		Intent intent = getIntent();
		ownerAccount = intent.getStringExtra("owner_account");
		this.remoteDevice = new GenericDevice();
		this.remoteDevice.setAccount(intent.getStringExtra("remote_account"));
		this.remoteDevice.setDeviceIdentifier(intent.getStringExtra("remote_device_id"));
		this.remoteDevice.setRegisteredId(intent.getStringExtra("remote_gcm_registration_id"));
		this.remoteDevice.setDevicePhoneNumber(intent.getStringExtra("remote_phone_number"));
		
//		this.connection = GCMConnectionManager.getManager().connect(this.remoteDevice, this);
		
		final Button sendChatButton = (Button) findViewById(R.id.sendChatButton);

		this.setTitle("Chat with :" + this.remoteDevice.getAccount());
		
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
}
