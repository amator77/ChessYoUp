package com.chessyoup.transport;

import java.util.Map;

public interface Message {
	
	public String getBody();
	
	public Map<String, String> getHeader();
}
