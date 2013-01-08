package com.chessyoup.connector.gcm;

import java.io.IOException;

import android.util.Log;

import com.chessyoup.connector.Connection;
import com.chessyoup.connector.ConnectionListener;
import com.chessyoup.connector.Device;
import com.chessyoup.connector.Message;

public class GCMConnection implements Connection {

	private Device remoteDevice;
	
	private ConnectionListener listener;
	
	private boolean connected;
	
	public GCMConnection(Device localDevice , Device remoteDevice, ConnectionListener listener) {
		this.remoteDevice = remoteDevice;		
		this.listener = listener;
	}

	@Override
	public boolean isConnected() {
		return connected;
	}
	
	
	
	public void setConnected(boolean connected) {
		this.connected = connected;
	}

	@Override
	public Device getRemoteDevice() {
		return this.remoteDevice;
	}
	
	@Override
	public void setConnectionListener(ConnectionListener listener) {
		this.listener = listener;
	}

	@Override
	public ConnectionListener getConnectionListener() {
		return listener;
	}

	@Override
	public void sendMessage(Message message) throws IOException {

		if (isConnected()) {
			try { 
				GCMMesssageSender.getSender().sendMessage(this.remoteDevice, message);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void messageReceived(com.chessyoup.connector.Message message) {
		if (this.listener != null) {
			this.listener.messageReceived(this, message);
		} else {
			Log.w("GCMConnection",
					"Listener for this connection is null! Conenction :"
							+ this.toString());
		}
	}

	@Override
	public String toString() {
		return "GCMConnection [remoteDevice=" + remoteDevice + ", listener="
				+ listener + "]";
	}
}
