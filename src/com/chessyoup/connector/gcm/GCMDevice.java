package com.chessyoup.connector.gcm;

import com.chessyoup.connector.Device;

public class GCMDevice implements Device{
	
	private String registeredId;
	
	private String deviceIdentifier;
	
	private String devicePhoneNumber;
	
	private String googleAccount;
	
	public GCMDevice(String registeredId,String deviceIdentifier,String devicePhoneNumber,String googleAccount){
		this.registeredId = registeredId;
		this.deviceIdentifier = deviceIdentifier;
		this.devicePhoneNumber = devicePhoneNumber;
		this.googleAccount = googleAccount;
	}

	@Override
	public String getDeviceIdentifier() {
		return this.deviceIdentifier;
	}

	@Override
	public String getRegistrationId() {
		 return this.registeredId;
	}

	@Override
	public String getDevicePhoneNumber() {
		return this.devicePhoneNumber;
	}

	public String getRegisteredId() {
		return registeredId;
	}

	public void setRegisteredId(String registeredId) {
		this.registeredId = registeredId;
	}

	public void setDeviceIdentifier(String deviceIdentifier) {
		this.deviceIdentifier = deviceIdentifier;
	}

	public void setDevicePhoneNumber(String devicePhoneNumber) {
		this.devicePhoneNumber = devicePhoneNumber;
	}		
	
	public void setGoogleAccount(String googleAccount) {
		this.googleAccount = googleAccount;
	}

	@Override
	public String getGoogleAccount() {
		return this.googleAccount;
	}

	@Override
	public String toString() {
		return "GCMDevice [registeredId=" + registeredId
				+ ", deviceIdentifier=" + deviceIdentifier
				+ ", devicePhoneNumber=" + devicePhoneNumber
				+ ", googleAccount=" + googleAccount + "]";
	}
}
