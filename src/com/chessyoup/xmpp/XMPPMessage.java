package com.chessyoup.xmpp;

import java.util.Map;

public class XMPPMessage {
	
	private String body; 
	
	private Map<String, String> header;
	
	public XMPPMessage(String body,Map<String, String> header){
		this.body = body;
		this.header = header;
	}
	
	/**
	 * Return the message data
	 * @return
	 */
	public String getBody(){
		return this.body;
	}
	
	/**
	 * Message header
	 * @return
	 */
	public Map<String, String> getHeader(){
		return this.header;
	}

	@Override
	public String toString() {
		return "XMPPMessage [body=" + body + ", header=" + header + "]";
	}		
}
