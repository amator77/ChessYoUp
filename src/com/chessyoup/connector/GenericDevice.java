package com.chessyoup.connector;

public class GenericDevice implements Device {
	
	private String registeredId;
	
	private String deviceIdentifier;
	
	private String devicePhoneNumber;
	
	private String account;
	
	
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
	
	public void setAccount(String account) {
		this.account = account;
	}

	@Override
	public String getAccount() {
		return this.account;
	}

	@Override
	public String toString() {
		return "GenericDevice [registeredId=" + registeredId
				+ ", deviceIdentifier=" + deviceIdentifier
				+ ", devicePhoneNumber=" + devicePhoneNumber + ", account="
				+ account + "]";
	}
}
