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
	
	/**
	 * 
	 * @param remoteDevice
	 */
	public void onNewConnectionRequest(Device remoteDevice,Message message);
	
	/**
	 * 
	 * @param remoteDevice
	 */
	public void onConnectionAccepted(Device remoteDevice,Message message);
	
	/**
	 * 
	 * @param remoteDevice
	 */
	public void onConnectionRejected(Device remoteDevice,Message message);
}
