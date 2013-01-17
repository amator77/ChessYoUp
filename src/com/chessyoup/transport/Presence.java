package com.chessyoup.transport;

public interface Presence {
			
	public enum MODE { ONLINE , OFFLINE , AWAY , BUSY }
	
	public String getContactId();
	
	public MODE getMode();
	
	public String getStatus();
}
