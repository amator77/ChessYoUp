package com.chessyoup.connector;

import java.io.IOException;

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
	 * @throws IOException 
	 */
	public void sendMessage(Message message) throws IOException;
	
	/**
	 * 
	 * @param listener
	 */
	public void setConnectionListener(ConnectionListener listener);
	
	/**
	 * 
	 * @return
	 */
	public ConnectionListener getConnectionListener();
	
}
