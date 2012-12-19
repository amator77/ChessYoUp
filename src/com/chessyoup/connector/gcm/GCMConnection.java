package com.chessyoup.connector.gcm;

import java.io.IOException;

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
		this.listener.onConnected(this,true);
	}

	@Override
	public boolean isConnected() {
		return remoteDevice != null;
	}

	@Override
	public Device getRemoteDevice() {
		return this.remoteDevice;
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
		this.listener.messageReceived(this,message);
	}
}
