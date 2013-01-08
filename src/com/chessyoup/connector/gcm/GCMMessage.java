package com.chessyoup.connector.gcm;

import java.util.HashMap;
import java.util.Map;

import com.chessyoup.connector.Message;

public class GCMMessage implements Message {
				
	private String body;
	
	private Map<String, String> header;
	
	private int sequence;
	
	public GCMMessage(){
		this.header = new HashMap<String, String>();
		this.sequence = 0;
	}
	
	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	@Override
	public String getBody() {
		return this.body;
	}
	
	public void setBody(String body) {
		this.body = body;
	}	

	@Override
	public Map<String, String> getHeader() {
		return this.header;
	}		
	
	public void setHeader(String key,String value){
		this.header.put(key, value);
	}

	@Override
	public String toString() {
		return "GCMMessage [body=" + body + ", header=" + header
				+ ", sequence=" + sequence + "]";
	}	
}
