package com.chessyoup.transport;

public interface ConnectionListener {
	
	public void messageReceived( Connection source, Message message);
		
	public void onDisconect(Connection source);		
}
