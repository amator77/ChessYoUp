package com.chessyoup.connector.gcm;

import java.io.IOException;

import android.util.Log;

import com.chessyoup.connector.Connection;
import com.chessyoup.connector.ConnectionListener;
import com.chessyoup.connector.Device;

public class GCMConnection implements Connection {

	private Device remoteDevice;

	private ConnectionListener listener;

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
	public void sendMessage(String message) throws IOException {

		if (isConnected()) {
			GCMConnectionManager.getManager().sendAsynkMessage(remoteDevice,
					message);
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
