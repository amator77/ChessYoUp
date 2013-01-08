package com.chessyoup.connector.gcm;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import android.util.Log;

import com.chessyoup.connector.Device;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Sender;

public class GCMMesssageSender {
	
	private static final GCMMesssageSender instance = new GCMMesssageSender();
	
	private static int sequence;
	
	private Sender sender;
	
	private String lastApiKey;
	
	public static final String SOURCE_REGISTRATION_ID = "sid";
	public static final String SOURCE_DEVICE_ID = "sdi";
	public static final String SOURCE_ACCOUNT_ID = "sai";
	public static final String SOURCE_PHONE_NUMBER = "spn";
	public static final String MESSAGE_ID = "mid";	
	public static final String MESSAGE_PAYLOAD = "mpl";		
	public static final String CONNECT_REQUEST = "connect";
	public static final String CONNECT_ACCEPTED = "connected";	
	public static final String CONNECT_REJECTED = "connected_rejected";	
	public static final String CONNECTION_CLOSED = "connection_closed";
	public static final String GCM_HEADER_COMMAND = "gcm_cmd";
	
	private GCMMesssageSender(){
		
	}
	
	public static GCMMesssageSender getSender(){
		return GCMMesssageSender.instance;
	}
	
	public void sendMessage( Device remoteDevice , com.chessyoup.connector.Message message) throws IOException{								
		if( this.sender == null ){
			this.lastApiKey = GCMConnectionManager.getCurrentManager().getApiKey();
			this.sender = new Sender(this.lastApiKey);
			
		}
		else{
			if( !GCMConnectionManager.getCurrentManager().getApiKey().equals(this.lastApiKey)){
				this.lastApiKey = GCMConnectionManager.getCurrentManager().getApiKey();
				this.sender = new Sender(this.lastApiKey);
			}
		}				
				
				
		Iterator<String> it = message.getHeader().keySet().iterator();		
		Message.Builder builder = new Message.Builder();
		
		while(it.hasNext()){
			String key = it.next();
			String value = message.getHeader().get(key);
			builder.addData(key, value);
		}
		
		Message gcmMessage = builder.timeToLive(5)
				.delayWhileIdle(true)
				.addData(SOURCE_REGISTRATION_ID, GCMConnectionManager.getCurrentManager().getLocalDevice().getRegistrationId())				
				.addData(MESSAGE_ID, String.valueOf(sequence++))
				.addData(MESSAGE_PAYLOAD, message.getBody()).build();
						
		sender.sendNoRetry(gcmMessage, remoteDevice.getRegistrationId());							
	}		
}
