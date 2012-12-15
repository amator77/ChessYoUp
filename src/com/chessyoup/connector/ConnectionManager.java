package com.chessyoup.connector;

public interface ConnectionManager {
	
	/**
	 * Initialize this manager.
	 * @param listener
	 */
	public void initialize(ConnectionManagerListener listener);
	
	/**
	 * 
	 * @param remoteAddress
	 * @param listener
	 * @return
	 */
	public Connection newConnection(Device remoteDevice , ConnectionListener listener);
	
	/**
	 * Get remote ( third party service helper )
	 * @return - the helper
	 */
	public RemoteService getRemoteService();
	
}
