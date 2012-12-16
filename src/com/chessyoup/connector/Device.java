package com.chessyoup.connector;

public interface Device {
	
	/**
	 * Get device unique id.
	 * @return - the unique device identifier;
	 */
	public String getDeviceIdentifier();
	
	/**
	 * Device phone number if this is a phone device
	 * @return
	 */
	public String getDevicePhoneNumber();
	
	/**
	 * Get the account of this device.( For GCM - google account )
	 * @return
	 */
	public String getAccount();
	
	/**
	 * The device unique registration id ( For GCM - registration ID)
	 * @return
	 */
	public String getRegistrationId();
}
