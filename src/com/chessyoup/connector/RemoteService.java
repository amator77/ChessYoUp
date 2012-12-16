package com.chessyoup.connector;

import java.io.IOException;
import java.util.List;

public interface RemoteService {
	
	public boolean register(Device device) throws IOException;
	
	public boolean unRegister(Device device) throws IOException;
	
	public Device findByPhoneNumberlookup(String phoneNumber) throws IOException;
	
	public Device findByAccount(String account) throws IOException;
	
	public List<Device> search(String keyword) throws IOException;
}
