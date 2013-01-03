package com.chessyoup.connector;

public interface ConnectionManager {
	
	/**
	 * 
	 * @param listener
	 */
	public void addListener(ConnectionManagerListener listener);
	
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
	public void connect(Device remoteDevice , ConnectionListener listener);
	
	/**
	 * Close this connection.
	 * @return
	 */
	public void closeConnection(Connection connection);
	
	/**
	 * 
	 * @param connectionId
	 * @return
	 */
	public Connection getConnection(String connectionId);
	
	/**
	 * Get device info of this manager .
	 * @return - an device only if this manager is initialized
	 */
	public Device getLocalDevice();
}
