package com.chessyoup.server;

import java.io.IOException;
import java.util.List;

import com.chessyoup.connector.Device;

public interface RemoteService {
	
	public boolean register(Device device,String roomId) throws IOException;
	
	public boolean unRegister(Device device) throws IOException;
	
	public Device findByPhoneNumber(String phoneNumber) throws IOException;
	
	public Device findByAccount(String account) throws IOException;
			
	public List<Device> devices(String roomId) throws IOException;
	
	public List<Room> rooms() throws IOException;
}