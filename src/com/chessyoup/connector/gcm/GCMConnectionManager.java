package com.chessyoup.connector.gcm;

import java.io.IOException;

import android.content.Context;
import android.util.Log;

import com.chessyoup.GCMIntentService;
import com.chessyoup.connector.Connection;
import com.chessyoup.connector.ConnectionListener;
import com.chessyoup.connector.ConnectionManager;
import com.chessyoup.connector.ConnectionManagerListener;
import com.chessyoup.connector.Device;
import com.chessyoup.connector.RemoteService;
import com.chessyoup.utils.DeviceUuidFactory;
import com.google.android.gcm.GCMRegistrar;

public class GCMConnectionManager implements ConnectionManager {

	private static final GCMConnectionManager manager = new GCMConnectionManager();

	private ConnectionManagerListener listener;

	private RemoteService remoteService;

	private Context applicationContext;
	
	private GCMDevice device;
	
	private GCMConnectionManager() {
			
	}

	public static GCMConnectionManager getManager() {
		return GCMConnectionManager.manager;
	}

	@Override
	public void initialize(ConnectionManagerListener listener) {
		this.listener = listener;
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
			} else {
				Log.d("GCMConnectionManager",
						" trying to register on remote server");
								
				try {
					if(this.remoteService.register(this.device)){
						GCMRegistrar.setRegisteredOnServer(applicationContext, true);
					}					
					
				} catch (IOException e) {
					Log.e("GCMConnectionManager",
							"Error on registering on remote server");
				}
			}
			
			listener.onInitialize(true);
		}
	}

	@Override
	public Connection newConnection(Device remoteDevice,
			ConnectionListener listener) {

		return new GCMConnection(remoteDevice, listener);
	}

	public Context getApplicationContext() {
		return applicationContext;
	}

	public void setApplicationContext(Context applicationContext) {
		this.applicationContext = applicationContext;
		this.device = new GCMDevice(null, new DeviceUuidFactory(applicationContext).getDeviceUuid().toString(), null);
	}

	public void setRemoteService(RemoteService remoteService) {
		this.remoteService = remoteService;
	}

	@Override
	public RemoteService getRemoteService() {
		return this.remoteService;
	}

	public ConnectionManagerListener getListener() {
		return this.listener;
	}
}