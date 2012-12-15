package com.chessyoup.connector;

public interface ConnectionManager {
	
	/**
	 * 
	 * @param listener
	 */
	public void registerListener(ConnectionManagerListener listener);
	
	/**
	 * 
	 * @param listener
	 */
	public void removeListener(ConnectionManagerListener listener);
	
	/**
	 * Initialize this manager.
	 * @param listener
	 */
	public void initialize();
	
	/**
	 * Close and release any resources .
	 * @param listener
	 */
	public void dispose();
	
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
	
	/**
	 * Get device info of this manager .
	 * @return - an device only if this manager is initialized
	 */
	public Device getDevice();
	
}
