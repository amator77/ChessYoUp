package com.chessyoup.transport.xmpp;

import org.jivesoftware.smack.packet.Presence;

public class XMPPStatus {

	public enum MODE { ONLINE , AWAY , BUSY , OFFLINE }
	
	private String status;
	
	private Presence.Type type;
	
	private MODE mode;
	
	public XMPPStatus(String status,Presence.Type type,MODE mode){
		this.status=status;
		this.type = type;
		this.mode = mode;
	}
	
	public XMPPStatus() {
		this.status="";
		this.type = Presence.Type.unavailable;
		this.mode = MODE.OFFLINE;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Presence.Type getType() {
		return type;
	}

	public void setType(Presence.Type type) {
		this.type = type;
	}

	public MODE getMode() {
		return mode;
	}

	public void setMode(MODE mode) {
		this.mode = mode;
	}

	@Override
	public String toString() {
		return "XMPPStatus [status=" + status + ", type=" + type + ", mode="
				+ mode + "]";
	}
}
