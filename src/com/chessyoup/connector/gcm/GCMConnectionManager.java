package com.chessyoup.connector.gcm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.chessyoup.GCMIntentService;
import com.chessyoup.connector.Connection;
import com.chessyoup.connector.ConnectionListener;
import com.chessyoup.connector.ConnectionManager;
import com.chessyoup.connector.ConnectionManagerListener;
import com.chessyoup.connector.Device;
import com.chessyoup.connector.Message;
import com.chessyoup.connector.RemoteService;
import com.chessyoup.utils.DeviceUuidFactory;
import com.google.android.gcm.GCMRegistrar;

public class GCMConnectionManager implements ConnectionManager {

	private static final GCMConnectionManager manager = new GCMConnectionManager();

	private List<ConnectionManagerListener> listeners;

	private List<GCMConnection> connections;
	
	private RemoteService remoteService;

	private Context applicationContext;
	
	private GCMDevice device;
	
	private GCMConnectionManager() {
		this.listeners = new ArrayList<ConnectionManagerListener>();
		this.connections = new ArrayList<GCMConnection>();
	}

	public static GCMConnectionManager getManager() {
		return GCMConnectionManager.manager;
	}

	@Override
	public void initialize() {
		if( applicationContext == null ){
			Log.e("GCMConnectionManager", "No application context !");
			this.fireoOnInitializeEvent(false);
			return;
		}
		
		TelephonyManager tManager = (TelephonyManager)applicationContext.getSystemService(Context.TELEPHONY_SERVICE);
		Log.d("GCMConnectionManager", "tManager : "+tManager.getSimSerialNumber());
		this.device = new GCMDevice(null, new DeviceUuidFactory(applicationContext).getDeviceUuid().toString(), tManager.getLine1Number(),getGoogleAccount());
		Log.d("GCMConnectionManager", "device : "+this.device.toString());
		
		boolean initialized = false;		
		GCMRegistrar.checkDevice(applicationContext);
		GCMRegistrar.checkManifest(applicationContext);
		String regId = GCMRegistrar.getRegistrationId(applicationContext);
		Log.d("GCMConnectionManager", "regId : "+regId);
				
		if (regId.equals("")) {			
			GCMRegistrar.register(applicationContext,
					GCMIntentService.config.get("sender_id"));
		} else {
			this.device.setRegisteredId(regId);
			
			if (GCMRegistrar.isRegisteredOnServer(applicationContext)) {
				Log.d("GCMConnectionManager",
						"app allready registerd on the server ");
				initialized = true;
			} else {
				Log.d("GCMConnectionManager",
						" trying to register on remote server");
								
				try {
					if(this.remoteService.register(this.device)){
						GCMRegistrar.setRegisteredOnServer(applicationContext, true);
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
		
		Account[] accounts = AccountManager.get(getApplicationContext()).getAccounts();
		
		for(Account ac : accounts ){
			if(ac.type.equals("com.google")){
				googleAccount = ac.name;
				Log.d("GCMConnectionManager", "Google account :"+googleAccount);
			}
			
		}
		
		return googleAccount;
	}

	private void fireoOnInitializeEvent(boolean initialized) {
		
		for(ConnectionManagerListener listener : this.listeners ){
			listener.onInitialize(initialized);
		}		
	}

	public void gcmRegistered(String regId){
		boolean initialized = false;
		
		this.device.setRegisteredId(regId);
		
		if (GCMRegistrar.isRegisteredOnServer(applicationContext)) {
			Log.d("GCMConnectionManager",
					"app allready registerd on the server ");
			initialized = true;
		} else {
			Log.d("GCMConnectionManager",
					" trying to register on remote server");
							
			try {
				if(this.remoteService.register(this.device)){
					GCMRegistrar.setRegisteredOnServer(applicationContext, true);
					initialized = true;
				}					
				
			} catch (IOException e) {
				Log.e("GCMConnectionManager",
						"Error on registering on remote server");
			}
		}
		
		this.fireoOnInitializeEvent(initialized);
	}
	
	public void gcmUnRegistered(String regId){
		GCMRegistrar.setRegisteredOnServer(applicationContext, false);
		this.device = null;
		this.fireoOnDispose(true);
	}
	
	@Override
	public void dispose() {
		if( this.device == null ){
			Log.e("GCMConnectionManager", "The manager is not initialized!");
			this.fireoOnDispose(false);
			return;
		}
		
		GCMRegistrar.unregister(this.applicationContext);
	}
	
	private void fireoOnDispose(boolean disposed) {
		for(ConnectionManagerListener listener : this.listeners ){
			listener.onDispose(disposed);
		}	
	}

	@Override
	public Connection connect(Device remoteDevice,
			ConnectionListener listener) {
		
		GCMConnection conn =  new GCMConnection(remoteDevice, listener);
		this.connections.add(conn);
		
		return conn;
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
		if(!this.listeners.contains(listener)){
			this.listeners.add(listener);
		}		
	}

	@Override
	public void removeListener(ConnectionManagerListener listener) {
		if( this.listeners.contains(listener)){
			this.listeners.remove(listener);
		}			
	}
	
	@Override
	public Device getLocalDevice() {		
		return this.device;
	}

	public void hadleIncomingMessage(Message newMessage) {
		for(GCMConnection conn : this.connections ){
			if(conn.getRemoteDevice().getDeviceIdentifier().equals(newMessage.getDestinationId())){
				conn.messageReceived(newMessage);
				break;
			}
		}		
	}

	public void hadleIncomingMessage(String source_id, String payload) {
		
		for(GCMConnection conn : this.connections ){
						
			if(conn.getRemoteDevice().getRegistrationId().equals(source_id)){
				GCMMessage gcmMessage = new GCMMessage();
				gcmMessage.setDestinationRegistrationID(GCMConnectionManager.getManager().getLocalDevice().getRegistrationId());
				gcmMessage.setBody(payload);
				gcmMessage.setSourceRegistrationID(source_id);
				
				conn.messageReceived(gcmMessage);
				break;
			}
		}
		
		// if we are here - there is a new connection start incoming
		
	}
}