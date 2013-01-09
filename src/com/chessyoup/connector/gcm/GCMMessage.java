package com.chessyoup.connector.gcm;

import java.util.HashMap;
import java.util.Map;

import com.chessyoup.connector.Message;

public class GCMMessage implements Message {
				
	private String body;
	
	private Map<String, String> header;
		
	
	public GCMMessage(){
		this.header = new HashMap<String, String>();		
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
		return "GCMMessage [body=" + body + ", header=" + header + "]";
	}	
}
