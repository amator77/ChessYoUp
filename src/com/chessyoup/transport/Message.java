package com.chessyoup.transport;

import java.util.Collection;


public interface Message {
			
	public String getFrom();
	
	public String getTo();
			
	public String getBody();
	
	public String getHeader(String key);
	
	public Collection<String> getHeadersKeys();
}
