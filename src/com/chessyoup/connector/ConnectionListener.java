package com.chessyoup.connector;

public interface ConnectionListener {
	
	/**
	 * 
	 * @param status
	 */
	public void onConnected(Connection source,boolean status);
	
	/**
	 * 
	 * @param source
	 * @param message
	 */
	public void messageReceived(Connection source,Message message);	
	
}
