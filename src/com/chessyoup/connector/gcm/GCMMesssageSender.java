package com.chessyoup.connector.gcm;

import java.io.IOException;

import android.util.Log;

import com.chessyoup.connector.Device;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Sender;

public class GCMMesssageSender {
	
	private static final GCMMesssageSender instance = new GCMMesssageSender();
	
	private static int sequence;
	
	private Sender sender;
	
	private static final String API_KEY = "AIzaSyB6HkBz-SrHpnMgaX1-Wff3E-WQM2z2VIM";	
	public static final String SOURCE_REGISTRATION_ID = "sid";
	public static final String SOURCE_DEVICE_ID = "sdi";
	public static final String SOURCE_ACCOUNT_ID = "sai";
	public static final String SOURCE_PHONE_NUMBER = "spn";
	public static final String MESSAGE_ID = "mid";	
	public static final String MESSAGE_PAYLOAD = "mpl";		
	
	private GCMMesssageSender(){
		this.sender = new Sender(API_KEY);
	}
	
	public static GCMMesssageSender getSender(){
		return GCMMesssageSender.instance;
	}
	
	public void sendMessage(Device sourceDevice , Device remoteDevice , String message) throws IOException{		
		
		Message gcmMessage = new Message.Builder().timeToLive(5)
				.delayWhileIdle(true)
				.addData(SOURCE_REGISTRATION_ID, sourceDevice.getRegistrationId())
				.addData(SOURCE_DEVICE_ID, sourceDevice.getDeviceIdentifier())
				.addData(SOURCE_ACCOUNT_ID, sourceDevice.getAccount() != null ? sourceDevice.getAccount() : "")
				.addData(SOURCE_PHONE_NUMBER, sourceDevice.getDevicePhoneNumber() != null ? sourceDevice.getDevicePhoneNumber() : "")
				.addData(MESSAGE_ID, String.valueOf(sequence++))
				.addData(MESSAGE_PAYLOAD, message).build();
		
		Log.d("GCMMesssageSender", "Message to send :"+gcmMessage.toString()+" to :"+remoteDevice.toString());	
		sender.sendNoRetry(gcmMessage, remoteDevice.getRegistrationId());							
	}		
}
