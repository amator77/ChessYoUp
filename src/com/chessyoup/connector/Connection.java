package com.chessyoup.connector;

public interface Connection {
	
	/**
	 * @return
	 */
	public boolean isConnected();
	
	/**
	 * 
	 * @return
	 */
	public Device getRemoteDevice();
	
	/**
	 * Send a message 
	 * @param message
	 */
	public void sendMessage(Message message);
}
