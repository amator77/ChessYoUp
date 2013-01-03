package com.chessyoup.connector.gcm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.chessyoup.chat.GCMChatActivity;
import com.chessyoup.connector.Connection;
import com.chessyoup.connector.ConnectionListener;
import com.chessyoup.connector.ConnectionManager;
import com.chessyoup.connector.ConnectionManagerListener;
import com.chessyoup.connector.Device;
import com.chessyoup.utils.DeviceUuidFactory;
import com.google.android.gcm.GCMRegistrar;

public class GCMConnectionManager implements ConnectionManager {
	
	private static GCMConnectionManager instance;
	
	private List<ConnectionManagerListener> listeners;

	private List<GCMConnection> connections;

	private Context applicationContext;

	private GCMDevice device;

	private String gcmSenderId;

	private static final String CONNECT_REQUEST = "connect";

	private static final String CONNECT_ACCEPTED = "connected";

	private static final String CONNECTION_CLOSED = "connection_closed";
	
	public GCMConnectionManager(Context appContext, String gcmSenderId) {
		this.applicationContext = appContext;
		this.gcmSenderId = gcmSenderId;
		this.listeners = new ArrayList<ConnectionManagerListener>();
		this.connections = new ArrayList<GCMConnection>();
		Log.d("GCMConnectionManager", "context : " + this.applicationContext);
		TelephonyManager tManager = (TelephonyManager) this.applicationContext
				.getSystemService(Context.TELEPHONY_SERVICE);
		this.device = new GCMDevice(null, new DeviceUuidFactory(
				applicationContext).getDeviceUuid().toString(),
				tManager.getLine1Number(), getGoogleAccount());
		Log.d("GCMConnectionManager", "device : " + this.device.toString());
		instance = this;
	}
	
	public static GCMConnectionManager getCurrentManager(){
		return GCMConnectionManager.instance;
	}
	
	@Override
	public void initialize() {

		boolean initialized = false;

		GCMRegistrar.checkDevice(applicationContext);
		GCMRegistrar.checkManifest(applicationContext);
		String regId = GCMRegistrar.getRegistrationId(applicationContext);
		Log.d("GCMConnectionManager", "regId : " + regId);

		if (regId.equals("")) {
			GCMRegistrar.register(applicationContext, this.gcmSenderId);
		} else {
			this.device.setRegistrationId(regId);
			initialized = true;
			this.fireoOnInitializeEvent(initialized);
		}
	}

	/**
	 * 
	 * @param regId
	 */
	public void gcmRegistered(String regId) {
		this.device.setRegistrationId(regId);
		this.fireoOnInitializeEvent(true);
	}

	/**
	 * 
	 * @param regId
	 */
	public void gcmUnRegistered(String regId) {
		this.device.setRegistrationId(null);
		this.connections.clear();
		this.fireoOnDispose(true);
	}

	@Override
	public void dispose() {
		GCMRegistrar.unregister(this.applicationContext);
	}

	@Override
	public void connect(Device remoteDevice, ConnectionListener listener) {

		try {
			this.connections.add(new GCMConnection(device, remoteDevice,
					listener));
			GCMMesssageSender.getSender().sendMessage(device, remoteDevice,
					CONNECT_REQUEST);
		} catch (IOException e) {
			Log.d("GCMConnectionManager",
					"Error on sendind connect request to :"
							+ remoteDevice.toString());
			e.printStackTrace();
			listener.onConnected(null, false);
		}
	}

	@Override
	public void closeConnection(Connection connection) {
		try {
			this.connections.remove(connection);
			GCMMesssageSender.getSender().sendMessage(device, connection.getRemoteDevice(),
					CONNECTION_CLOSED);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Context getApplicationContext() {
		return applicationContext;
	}

	public void setApplicationContext(Context applicationContext) {
		this.applicationContext = applicationContext;
	}

	@Override
	public void addListener(ConnectionManagerListener listener) {

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

	@Override
	public String toString() {
		return "GCMConnectionManager [applicationContext=" + applicationContext
				+ ", device=" + device + ", gcmSenderId=" + gcmSenderId + "]";
	}

	public void hadleIncomingMessage(Context context, Intent intent) {
		
		Bundle extra = intent.getExtras();
		
		GCMDevice remoteDevice = new GCMDevice();
		remoteDevice.setDeviceIdentifier(extra
				.getString(GCMMesssageSender.SOURCE_DEVICE_ID));
		remoteDevice.setRegistrationId(extra
				.getString(GCMMesssageSender.SOURCE_REGISTRATION_ID));
		remoteDevice.setDevicePhoneNumber(extra
				.getString(GCMMesssageSender.SOURCE_PHONE_NUMBER));
		remoteDevice.setGoogleAccount(extra
				.getString(GCMMesssageSender.SOURCE_ACCOUNT_ID));
		
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

			if (conn.getRemoteDevice().getRegistrationId()
					.equals(message.getSourceId())) {
				
				Log.d("GCMConnectionManager",
						"Connection found  :" +conn.toString());
				
				if (extra.getString(GCMMesssageSender.MESSAGE_PAYLOAD).equals(
						CONNECT_REQUEST)) {
					
					try {
						GCMMesssageSender.getSender().sendMessage(device, remoteDevice,
								CONNECT_ACCEPTED);
						if (conn.getConnectionListener() != null) {
							conn.getConnectionListener().onConnected(conn, true);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					return;
				}
				else if (extra.getString(GCMMesssageSender.MESSAGE_PAYLOAD).equals(
						CONNECT_ACCEPTED)) {
					if (conn.getConnectionListener() != null) {
						conn.getConnectionListener().onConnected(conn, true);
					}
				}
				else if (extra.getString(GCMMesssageSender.MESSAGE_PAYLOAD).equals(
						CONNECTION_CLOSED)) {
					if (conn.getConnectionListener() != null) {
						conn.getConnectionListener().onDisconnected(conn);
					}
				}else {
					conn.messageReceived(message);
				}

				return;
			}
		}

		Log.d("GCMConnectionManager", "No connection for the  message!");

		

		if (extra.getString(GCMMesssageSender.MESSAGE_PAYLOAD).equals(
				CONNECT_REQUEST)) {

			try {
				GCMMesssageSender.getSender().sendMessage(device, remoteDevice,
						CONNECT_ACCEPTED);
			} catch (IOException e) {
				e.printStackTrace();
			}

			Intent chatIntent = new Intent(applicationContext,
					GCMChatActivity.class);
			chatIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			chatIntent.putExtra("remote_device_id",
					remoteDevice.getDeviceIdentifier());
			chatIntent.putExtra("remote_phone_number",
					remoteDevice.getDevicePhoneNumber());
			chatIntent.putExtra("remote_gcm_registration_id",
					remoteDevice.getRegistrationId());
			chatIntent.putExtra("remote_account", remoteDevice.getAccount());
			chatIntent.putExtra("owner_account",
					this.device.getAccount() != null ? this.device.getAccount()
							: this.device.getDevicePhoneNumber());
			this.connections.add(new GCMConnection(device, remoteDevice, null));
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

	private void fireoOnDispose(boolean disposed) {
		for (ConnectionManagerListener listener : this.listeners) {
			listener.onDispose(disposed);
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

	public String getSenderId() {
		return this.gcmSenderId;
	}

	
}