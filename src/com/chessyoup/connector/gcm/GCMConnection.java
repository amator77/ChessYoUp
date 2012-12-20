package com.chessyoup.connector.gcm;

import java.io.IOException;

import android.util.Log;

import com.chessyoup.connector.Connection;
import com.chessyoup.connector.ConnectionListener;
import com.chessyoup.connector.Device;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Sender;

public class GCMConnection implements Connection {

	private Device remoteDevice;

	private ConnectionListener listener;

	private static final String API_KEY = "AIzaSyB6HkBz-SrHpnMgaX1-Wff3E-WQM2z2VIM";

	public GCMConnection(Device remoteDevice, ConnectionListener listener) {
		this.remoteDevice = remoteDevice;
		this.listener = listener;		
	}

	@Override
	public boolean isConnected() {
		return remoteDevice != null;
	}

	@Override
	public Device getRemoteDevice() {
		return this.remoteDevice;
	}
			
	public void setListener(ConnectionListener listener) {
		this.listener = listener;
	}
			
	public ConnectionListener getListener() {
		return listener;
	}

	@Override
	public void sendMessage(com.chessyoup.connector.Message message) throws IOException {

		if (isConnected()) {

			Message gcmMessage = new Message.Builder().timeToLive(0)
					.delayWhileIdle(true)
					.addData("source_id", GCMConnectionManager.getManager().getLocalDevice().getRegistrationId())
					.addData("payload", message.getBody()).build();
			Sender sender = new Sender(API_KEY);
			sender.sendNoRetry(gcmMessage, remoteDevice.getRegistrationId());
		}
	}
	
	public void messageReceived(com.chessyoup.connector.Message message){
		if( this.listener != null ){
			this.listener.messageReceived(this,message);
		}
		else{
			Log.w("GCMConnection", "Listener for this connection is null! Conenction :"+this.toString());
		}
	}

	@Override
	public String toString() {
		return "GCMConnection [remoteDevice=" + remoteDevice + ", listener="
				+ listener + "]";
	}
}
