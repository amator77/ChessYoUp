package com.chessyoup.connector;

public class GenericDevice implements Device {

	private String registrationId;

	private String deviceIdentifier;

	private String devicePhoneNumber;

	private String account;

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
	public String getDeviceIdentifier() {
		return this.deviceIdentifier;
	}

	@Override
	public String getDevicePhoneNumber() {
		return this.devicePhoneNumber;
	}

	@Override
	public String getAccount() {
		return this.account;
	}

	@Override
	public String getRegistrationId() {
		return this.registrationId;
	}

	public void setRegistrationId(String registrationId) {
		this.registrationId = registrationId;
	}

	@Override
	public String toString() {
		return "GenericDevice [registrationId=" + registrationId
				+ ", deviceIdentifier=" + deviceIdentifier
				+ ", devicePhoneNumber=" + devicePhoneNumber + ", account="
				+ account + "]";
	}		
}
