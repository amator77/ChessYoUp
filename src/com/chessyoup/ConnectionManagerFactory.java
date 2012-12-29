package com.chessyoup;

import android.content.Context;

import com.chessyoup.connector.ConnectionManager;
import com.chessyoup.connector.gcm.GCMConnectionManager;
import com.chessyoup.server.RemoteService;

public class ConnectionManagerFactory {
	
	public static final ConnectionManagerFactory factory = new ConnectionManagerFactory();
	
	private GCMConnectionManager gcmConnectionManager;
	
	public static ConnectionManagerFactory getFactory(){
		return ConnectionManagerFactory.factory;
	}
	
	public ConnectionManager getGCMConnectionManager(RemoteService remoteService,Context
			appContext){
		
		if( gcmConnectionManager == null ){
			this.gcmConnectionManager = GCMConnectionManager.getManager();			
			this.gcmConnectionManager.setApplicationContext(appContext);
		}
		
		return this.gcmConnectionManager;
	}
}
