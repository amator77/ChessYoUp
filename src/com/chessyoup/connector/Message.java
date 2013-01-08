package com.chessyoup.connector;

import java.util.Map;

public interface Message {
			
	/**
	 * Return the message data
	 * @return
	 */
	public String getBody();
	
	/**
	 * Message header
	 * @return
	 */
	public Map<String, String> getHeader();
}
