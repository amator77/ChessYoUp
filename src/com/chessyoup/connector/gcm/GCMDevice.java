package com.chessyoup.connector.gcm;

import com.chessyoup.connector.Device;

public class GCMDevice implements Device{
	
	private String registrationId;
	
	private String deviceIdentifier;
	
	private String devicePhoneNumber;
	
	private String googleAccount;
	
	public GCMDevice(String registrationId,String deviceIdentifier,String devicePhoneNumber,String googleAccount){
		this.registrationId = registrationId;
		this.deviceIdentifier = deviceIdentifier;
		this.devicePhoneNumber = devicePhoneNumber;
		this.googleAccount = googleAccount;
	}
	
	public GCMDevice(){		
	}
	
	@Override
	public String getDeviceIdentifier() {
		return this.deviceIdentifier;
	}

	@Override
	public String getRegistrationId() {
		 return this.registrationId;
	}
		
	public void setRegistrationId(String registrationId) {
		 this.registrationId = registrationId;
	}
	
	@Override
	public String getDevicePhoneNumber() {
		return this.devicePhoneNumber;
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
	public String getAccount() {
		return this.googleAccount;
	}

	@Override
	public String toString() {
		return "GCMDevice [registeredId=" + registrationId
				+ ", deviceIdentifier=" + deviceIdentifier
				+ ", devicePhoneNumber=" + devicePhoneNumber
				+ ", googleAccount=" + googleAccount + "]";
	}
}
