package com.chessyoup.connector.gcm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.chessyoup.GCMIntentService;
import com.chessyoup.MainActivity;
import com.chessyoup.connector.ConnectionListener;
import com.chessyoup.connector.ConnectionManager;
import com.chessyoup.connector.ConnectionManagerListener;
import com.chessyoup.connector.Device;
import com.chessyoup.connector.Message;
import com.chessyoup.connector.RemoteService;
import com.chessyoup.gcm.chat.GCMChatActivity;
import com.chessyoup.utils.DeviceUuidFactory;
import com.google.android.gcm.GCMRegistrar;

public class GCMConnectionManager implements ConnectionManager {

	private static final GCMConnectionManager manager = new GCMConnectionManager();

	private List<ConnectionManagerListener> listeners;

	private List<GCMConnection> connections;

	private RemoteService remoteService;

	private Context applicationContext;

	private GCMDevice device;

	private static final String CONNECT_REQUEST = "connect";

	private static final String CONNECT_ACCEPTED = "connected";

	private GCMConnectionManager() {
		this.listeners = new ArrayList<ConnectionManagerListener>();
		this.connections = new ArrayList<GCMConnection>();
	}

	public static GCMConnectionManager getManager() {
		return GCMConnectionManager.manager;
	}

	@Override
	public void initialize() {
		if (applicationContext == null) {
			Log.e("GCMConnectionManager", "No application context !");
			this.fireoOnInitializeEvent(false);
			return;
		}

		TelephonyManager tManager = (TelephonyManager) applicationContext
				.getSystemService(Context.TELEPHONY_SERVICE);
		Log.d("GCMConnectionManager",
				"tManager : " + tManager.getSimSerialNumber());
		this.device = new GCMDevice(null, new DeviceUuidFactory(
				applicationContext).getDeviceUuid().toString(),
				tManager.getLine1Number(), getGoogleAccount());
		Log.d("GCMConnectionManager", "device : " + this.device.toString());

		boolean initialized = false;
		GCMRegistrar.checkDevice(applicationContext);
		GCMRegistrar.checkManifest(applicationContext);
		String regId = GCMRegistrar.getRegistrationId(applicationContext);
		Log.d("GCMConnectionManager", "regId : " + regId);

		if (regId.equals("")) {
			GCMRegistrar.register(applicationContext,
					GCMIntentService.config.get("sender_id"));
		} else {
			this.device.setRegistrationId(regId);

			if (GCMRegistrar.isRegisteredOnServer(applicationContext)) {
				Log.d("GCMConnectionManager",
						"app allready registerd on the server ");
				initialized = true;
			} else {
				Log.d("GCMConnectionManager",
						" trying to register on remote server");

				try {
					if (this.remoteService.register(this.device)) {
						GCMRegistrar.setRegisteredOnServer(applicationContext,
								true);
						initialized = true;
					}

				} catch (IOException e) {
					e.printStackTrace();
					Log.e("GCMConnectionManager",
							"Error on registering on remote server");
				}
			}

			this.fireoOnInitializeEvent(initialized);
		}
	}

	private String getGoogleAccount() {
		String googleAccount = null;

		Account[] accounts = AccountManager.get(getApplicationContext())
				.getAccounts();

		for (Account ac : accounts) {
			if (ac.type.equals("com.google")) {
				googleAccount = ac.name;
				Log.d("GCMConnectionManager", "Google account :"
						+ googleAccount);
			}

		}

		return googleAccount;
	}

	private void fireoOnInitializeEvent(boolean initialized) {

		for (ConnectionManagerListener listener : this.listeners) {
			listener.onInitialize(initialized);
		}
	}

	public void gcmRegistered(String regId) {
		boolean initialized = false;

		this.device.setRegistrationId(regId);

		if (GCMRegistrar.isRegisteredOnServer(applicationContext)) {
			Log.d("GCMConnectionManager",
					"app allready registerd on the server ");
			initialized = true;
		} else {
			Log.d("GCMConnectionManager",
					" trying to register on remote server");

			try {
				if (this.remoteService.register(this.device)) {
					GCMRegistrar
							.setRegisteredOnServer(applicationContext, true);
					initialized = true;
				}

			} catch (IOException e) {
				Log.e("GCMConnectionManager",
						"Error on registering on remote server");
			}
		}

		this.fireoOnInitializeEvent(initialized);
	}

	public void gcmUnRegistered(String regId) {
		GCMRegistrar.setRegisteredOnServer(applicationContext, false);
		this.device = null;
		this.fireoOnDispose(true);
	}

	@Override
	public void dispose() {
		if (this.device == null) {
			Log.e("GCMConnectionManager", "The manager is not initialized!");
			this.fireoOnDispose(false);
			return;
		}

		GCMRegistrar.unregister(this.applicationContext);
	}

	private void fireoOnDispose(boolean disposed) {
		for (ConnectionManagerListener listener : this.listeners) {
			listener.onDispose(disposed);
		}
	}

	@Override
	public void connect(Device remoteDevice, ConnectionListener listener) {

		try {
			this.connections.add(new GCMConnection(remoteDevice, listener));
			GCMMesssageSender.getSender().sendMessage(remoteDevice,
					CONNECT_REQUEST);
		} catch (IOException e) {
			Log.d("GCMConnectionManager",
					"Error on sendind connect request to :"
							+ remoteDevice.toString());
			e.printStackTrace();
			listener.onConnected(null, false);
		}
	}

	public Context getApplicationContext() {
		return applicationContext;
	}

	public void setApplicationContext(Context applicationContext) {
		this.applicationContext = applicationContext;
	}

	public void setRemoteService(RemoteService remoteService) {
		this.remoteService = remoteService;
	}

	@Override
	public RemoteService getRemoteService() {
		return this.remoteService;
	}

	@Override
	public void registerListener(ConnectionManagerListener listener) {

		if (!this.listeners.contains(listener)) {
			this.listeners.add(listener);
		}
	}

	@Override
	public void removeListener(ConnectionManagerListener listener) {

		if (this.listeners.contains(listener)) {
			this.listeners.remove(listener);
		}
	}

	@Override
	public Device getLocalDevice() {
		return this.device;
	}

	public void hadleIncomingMessage(Context context, Intent intent) {

		Bundle extra = intent.getExtras();
		GCMMessage message = new GCMMessage();
		message.setSourceRegistrationID(extra
				.getString(GCMMesssageSender.SOURCE_REGISTRATION_ID));
		message.setSequnce(Integer.valueOf(extra
				.getString(GCMMesssageSender.MESSAGE_ID)));
		message.setBody(extra.getString(GCMMesssageSender.MESSAGE_PAYLOAD));
		message.setDestinationRegistrationID(device.getRegistrationId());

		Log.d("GCMConnectionManager",
				"Hanlde new mesage :" + message.toString());

		for (GCMConnection conn : this.connections) {
			
			if (conn.getRemoteDevice().getRegistrationId() .equals(message.getSourceId())) {
				
				if( extra.getString(GCMMesssageSender.MESSAGE_PAYLOAD).equals(
						CONNECT_ACCEPTED)){
					if( conn.getListener() != null ){
						conn.getListener().onConnected(conn, true);						
					}
				}
				else{
					conn.messageReceived(message);
				}
								
				return;
			}
		}

		Log.d("GCMConnectionManager", "No connection for the  message!");
		
		GCMDevice remoteDevice = new GCMDevice();
		remoteDevice.setDeviceIdentifier(extra
				.getString(GCMMesssageSender.SOURCE_DEVICE_ID));
		remoteDevice.setRegistrationId(extra
				.getString(GCMMesssageSender.SOURCE_REGISTRATION_ID));
		remoteDevice.setDevicePhoneNumber(extra
				.getString(GCMMesssageSender.SOURCE_PHONE_NUMBER));
		remoteDevice.setGoogleAccount(extra
				.getString(GCMMesssageSender.SOURCE_ACCOUNT_ID));
		
		if (extra.getString(GCMMesssageSender.MESSAGE_PAYLOAD).equals(
				CONNECT_REQUEST)) {			
						
				this.sendAsynkMessage(remoteDevice, CONNECT_ACCEPTED);
				
				Intent chatIntent = new Intent(applicationContext, GCMChatActivity.class);
				chatIntent.putExtra("remote_device_id",
						remoteDevice.getDeviceIdentifier());
				chatIntent.putExtra("remote_phone_number",
						remoteDevice.getDevicePhoneNumber());
				chatIntent.putExtra("remote_gcm_registration_id",
						remoteDevice.getRegistrationId());
				chatIntent
						.putExtra("remote_account", remoteDevice.getAccount());
				chatIntent.putExtra(
						"owner_account",
						this.device.getAccount() != null ? this.device
								.getAccount() : this.device
								.getDevicePhoneNumber());
				this.connections.add(new GCMConnection(remoteDevice, null));
				chatIntent.putExtra("connected", "true");
				applicationContext.startActivity(chatIntent);			
		}		
	}

	public GCMConnection getConnection(String registrationId) {

		for (GCMConnection conn : this.connections) {
			if (conn.getRemoteDevice().getRegistrationId()
					.equals(registrationId)) {
				return conn;
			}
		}

		return null;
	}
	
	public void sendAsynkMessage(final Device remoteDevice ,final String message){
		
		AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				try {
					GCMMesssageSender.getSender().sendMessage(remoteDevice,message);
				} catch (IOException e) {
					e.printStackTrace();
				}
				return null;
			}

			protected void onPostExecute(Void result) {				
			}
		};

		task.execute();
	}
}