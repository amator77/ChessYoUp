package com.chessyoup.server.gcm;

import com.chessyoup.connector.gcm.GCMDevice;
import com.chessyoup.server.User;
import com.chessyoup.server.UserStatus;

public class GCMUser implements User {
	
	private GCMDevice device;
	
	private UserStatus userStatus;
	
	public GCMUser(GCMDevice device, UserStatus status){
		this.device = device;
		this.userStatus = status;
	}
	
	@Override
	public String getId() {
		return this.device.getDeviceIdentifier();
	}

	@Override
	public String getUsername() {
		return this.device.getAccount() != null ? this.device.getAccount() : this.device.getDevicePhoneNumber() != null ? this.device.getDevicePhoneNumber() : this.device.getDeviceIdentifier();
	}

	@Override
	public UserStatus getStatus() {
		return this.userStatus;
	}
}
