package com.chessyoup.server.gcm;

import com.chessyoup.connector.Device;
import com.chessyoup.server.User;
import com.chessyoup.server.UserStatus;

public class GCMUser implements User {
	
	private Device device;
	
	private UserStatus userStatus;
	
	public GCMUser(Device device, UserStatus status){
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

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		if( device.getAccount() != null && !device.getAccount().equals("null")){
			sb.append(device.getAccount());
		}
		else if( device.getDevicePhoneNumber() != null && !device.getDevicePhoneNumber().equals("null")){
			sb.append(device.getDevicePhoneNumber());
		}
		else{
			sb.append(device.getDeviceIdentifier());
		}
		
		return sb.toString();
	}

	@Override
	public Device getDevice() {
		return this.device;
	}
}
