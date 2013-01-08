package com.chessyoup.connector.gcm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.chessyoup.connector.Connection;
import com.chessyoup.connector.ConnectionListener;
import com.chessyoup.connector.ConnectionManager;
import com.chessyoup.connector.ConnectionManagerListener;
import com.chessyoup.connector.Device;
import com.chessyoup.connector.Message;
import com.chessyoup.utils.DeviceUuidFactory;
import com.google.android.gcm.GCMRegistrar;

public class GCMConnectionManager implements ConnectionManager {

	private static GCMConnectionManager instance;

	private List<ConnectionManagerListener> listeners;

	private List<GCMConnection> connections;	
	
	private Context applicationContext;

	private GCMDevice device;

	private String gcmSenderId;
	
	private String apiKey;
		
	public GCMConnectionManager(Context appContext, String gcmSenderId , String apiKey) {
		this.applicationContext = appContext;
		this.gcmSenderId = gcmSenderId;
		this.apiKey = apiKey;
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

	public static GCMConnectionManager getCurrentManager() {
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
			GCMMessage connectMessage = new GCMMessage();
			connectMessage.setHeader(GCMMesssageSender.GCM_HEADER_COMMAND, GCMMesssageSender.CONNECT_REQUEST);
			connectMessage.setBody("");
			GCMMesssageSender.getSender().sendMessage(remoteDevice,connectMessage);
		} catch (IOException e) {
			Log.d("GCMConnectionManager",
					"Error on sendind connect request to :"
							+ remoteDevice.toString());
			e.printStackTrace();
			listener.onConnected(null, false);
		}
	}
	
	@Override
	public void acceptConnection(Device remoteDevice) {
		try {	
			this.connections.add(new GCMConnection(device, remoteDevice,
					null));
			GCMMessage connectMessage = new GCMMessage();
			connectMessage.setHeader(GCMMesssageSender.GCM_HEADER_COMMAND, GCMMesssageSender.CONNECT_ACCEPTED);
			connectMessage.setBody("");
			GCMMesssageSender.getSender().sendMessage(remoteDevice,connectMessage);
		} catch (IOException e) {
			Log.d("GCMConnectionManager",
					"Error on sendind connect request to :"
							+ remoteDevice.toString());
			e.printStackTrace();
		}
	}
	
	@Override
	public void closeConnection(Connection connection) {
		try {
			this.connections.remove(connection);
			GCMMessage disconnectMessage = new GCMMessage();
			disconnectMessage.setHeader(GCMMesssageSender.GCM_HEADER_COMMAND, GCMMesssageSender.CONNECTION_CLOSED);
			disconnectMessage.setBody("");
			GCMMesssageSender.getSender().sendMessage(connection.getRemoteDevice(), disconnectMessage);
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
		String sourceId = intent.getExtras().getString(GCMMesssageSender.SOURCE_REGISTRATION_ID);
		GCMDevice remoteDevice = this.extractRemoteDevice(intent);
		GCMMessage message = this.extractMessage(intent);
		
		Log.d("GCMConnectionManager",
				"Hanlde new mesage :" + message.toString() +" from :"+sourceId);
		
		GCMConnection connection = null;
		String gcmCommand = message.getHeader().get(GCMMesssageSender.GCM_HEADER_COMMAND);
		
		for (GCMConnection conn : this.connections) {

			if (conn.getRemoteDevice().getRegistrationId()
					.equals(sourceId)) {
				connection = conn;
				
				if( connection.isConnected() ){
					Log.d("GCMConnectionManager",
							"Connection found  :" + conn.toString());
					
					if( gcmCommand != null && gcmCommand.equals(GCMMesssageSender.CONNECTION_CLOSED) ){
						connection.getConnectionListener().onDisconnected(connection);
					}
					else{
						conn.messageReceived(message);
					}
									
					return;
				}
			}
		}

		Log.d("GCMConnectionManager", "No open connection for the  message!");
				
		if ( gcmCommand != null && gcmCommand.equals(GCMMesssageSender.CONNECT_REQUEST )) {
			this.fireoOnNewConnectionRequest(remoteDevice, message);
			return;
		}else if (gcmCommand != null && gcmCommand.equals(GCMMesssageSender.CONNECT_ACCEPTED )) {				
			if( connection != null ){
				connection.getConnectionListener().onConnected(connection, true);
			}			
		}
		else if (gcmCommand != null && gcmCommand.equals(GCMMesssageSender.CONNECT_REJECTED )) {			
			if( connection != null ){
				connection.getConnectionListener().onDisconnected(connection);
			}			
		}				
		else{
			Log.d("GCMConnectionManager",
					"Message discarded :" + message.toString());
		}
		
//		if (message.getBody().equals(
//				CONNECT_REQUEST)) {
//
//			try {
//				GCMMesssageSender.getSender().sendMessage(device, remoteDevice,
//						CONNECT_ACCEPTED);
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//
//			Intent chatIntent = new Intent(applicationContext,
//					ChessboardActivity.class);
//			chatIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//			chatIntent.putExtra("remote_device_id",
//					remoteDevice.getDeviceIdentifier());
//			chatIntent.putExtra("remote_phone_number",
//					remoteDevice.getDevicePhoneNumber());
//			chatIntent.putExtra("remote_gcm_registration_id",
//					remoteDevice.getRegistrationId());
//			chatIntent.putExtra("remote_account", remoteDevice.getAccount());
//			chatIntent.putExtra("owner_account",
//					this.device.getAccount() != null ? this.device.getAccount()
//							: this.device.getDevicePhoneNumber());
//			this.connections.add(new GCMConnection(device, remoteDevice, null));
//			chatIntent.putExtra("connected", "true");
//
//			applicationContext.startActivity(chatIntent);
//		}
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
	
	private void fireoOnNewConnectionRequest(Device remoteDevice , Message message) {
		for (ConnectionManagerListener listener : this.listeners) {
			listener.onNewConnectionRequest(remoteDevice, message);
		}
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

	public String getApiKey() {
		return apiKey;
	}		
	
	private GCMMessage extractMessage( Intent gcmIntent){
		Bundle extra = gcmIntent.getExtras();
		
		GCMMessage message = new GCMMessage();		
		message.setSequence(Integer.valueOf(extra
				.getString(GCMMesssageSender.MESSAGE_ID)).intValue());
		message.setBody(extra.getString(GCMMesssageSender.MESSAGE_PAYLOAD));
				
		Iterator<String> it  = extra.keySet().iterator();
		while(it.hasNext()){
			String key = it.next();
			String value = extra.getString(key);
			
			if( !key.equals(GCMMesssageSender.MESSAGE_PAYLOAD) ){
				message.setHeader(key, value);
			}
		}
				
		return message;
	}
	
	private GCMDevice extractRemoteDevice( Intent gcmIntent){
		Bundle extra = gcmIntent.getExtras();		
		
		GCMDevice remoteDevice = new GCMDevice();
		remoteDevice.setDeviceIdentifier(extra
				.getString(GCMMesssageSender.SOURCE_DEVICE_ID));
		remoteDevice.setRegistrationId(extra
				.getString(GCMMesssageSender.SOURCE_REGISTRATION_ID));
		remoteDevice.setDevicePhoneNumber(extra
				.getString(GCMMesssageSender.SOURCE_PHONE_NUMBER));
		remoteDevice.setGoogleAccount(extra
				.getString(GCMMesssageSender.SOURCE_ACCOUNT_ID));
		
		return remoteDevice;
	}
}