package com.chessyoup.connector;

public interface ConnectionManagerListener {
	
	/**
	 * Callback by connection manager. 
	 * @param status - true for success
	 */
	public void onInitialize(boolean status);
	
	/**
	 * 
	 * @param status
	 */
	public void onDispose(boolean status);
}
