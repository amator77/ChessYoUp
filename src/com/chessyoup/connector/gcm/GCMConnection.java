package com.chessyoup.connector.gcm;

import com.chessyoup.connector.Connection;
import com.chessyoup.connector.ConnectionListener;
import com.chessyoup.connector.Device;
import com.chessyoup.connector.Message;

public class GCMConnection implements Connection {
	
	private Device remoteDevice;
	
	private ConnectionListener listener;
	
	public GCMConnection(Device remoteDevice , ConnectionListener listener){
		this.remoteDevice = remoteDevice;
		this.listener = listener;
	}
	
	@Override
	public boolean isConnected() {
		return true;
	}

	@Override
	public Device getRemoteDevice() {
		return this.remoteDevice;
	}

	@Override
	public void sendMessage(Message message) {
		// TODO Auto-generated method stub
		
	}
}
