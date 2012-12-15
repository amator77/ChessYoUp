package com.chessyoup.connector;

public interface Device {
	
	/**
	 * Get device unique id. This can be phone number or IMEI or MAC address. 
	 * @return - the unique device identifier;
	 */
	public String getDeviceIdentifier();
	
	/**
	 * 
	 * @return
	 */
	public String getDevicePhoneNumber();
	
	/**
	 * 
	 * @return
	 */
	public String getGoogleAccount();
	
	/**
	 * The device unique registration id ( For example GCM registration ID)
	 * @return
	 */
	public String getRegistrationId();
}
