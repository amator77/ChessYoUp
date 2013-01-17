package com.chessyoup.transport;

import java.util.List;


public interface Roster {
	
	public List<? extends Contact> getContacts();
	
	public void addListener(RosterListener listener);	
}
