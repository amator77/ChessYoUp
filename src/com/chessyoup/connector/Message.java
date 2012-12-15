package com.chessyoup.connector;

public interface Message {
	
	/**
	 * Return the message unique id.
	 * @return
	 */
	public int getId();
	
	/**
	 * Return the message data
	 * @return
	 */
	public byte[] getPayload();
}
