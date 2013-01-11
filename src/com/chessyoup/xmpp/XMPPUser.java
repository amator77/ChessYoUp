package com.chessyoup.xmpp;

import android.util.Log;

public class XMPPUser {
	
	private String username;
	
	private String domain;
	
	private String resource;
	
	private String status;
	
	public XMPPUser(String jabberId,String status){
		this.setJabberId(jabberId);		
		this.status = status;
	}
			
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getUsername() {
		return username;
	}
	
	public void setJabberId(String jabberId) {			
		String[] parts = jabberId.split("@");
		username = parts[0];
		parts = parts[1].split("/");
		
		if( parts.length == 2 ){
			domain = parts[0];
			resource = parts[1];
		}
		else{
			domain = parts[0];
		}
	}
	
	public String getJabberId() {
		return new StringBuffer(this.username).append("@").append(domain).append("/").append(resource).toString();
	}
	
	@Override
	public String toString(){
		return new StringBuffer(this.username).append("@").append(domain).append("/").append(resource).append("(").append(status).append(")").toString();
	}
}
