package com.chessyoup.server;

import com.chessyoup.connector.Device;

public interface User {
	
	public String getId();
	
	public String getUsername();
	
	public UserStatus getStatus();
	
	public Device getDevice();
	
}
