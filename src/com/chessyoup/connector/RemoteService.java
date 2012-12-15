package com.chessyoup.connector;

import java.io.IOException;

public interface RemoteService {
	
	public boolean register(Device device) throws IOException;
	
	public boolean unRegister(Device device) throws IOException;
	
	public Device lookup(String deviceIdentifier, String registrationId) throws IOException;
}
