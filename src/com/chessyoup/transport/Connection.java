package com.chessyoup.transport;


import com.chessyoup.transport.Presence.MODE;
import com.chessyoup.transport.exceptions.ConnectionException;
import com.chessyoup.transport.exceptions.LoginException;

public interface Connection {
	
	public void sendMessage(Message message) throws ConnectionException;
	
	public void sendPresence(MODE status) throws ConnectionException;
		
	public void login(String id,String credentials) throws ConnectionException,LoginException;
	
	public void logout();
	
	public void addConnectionListener(ConnectionListener listener);
	
	public Roster getRoster();
}
