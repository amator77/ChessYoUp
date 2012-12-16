package com.chessyoup.connector;

public interface Message {
	/**
	 * Sequence uniqu order by.
	 * @return
	 */
	public int getSequence();
	
	/**
	 * Source of the message
	 * @return
	 */
	public String getSourceId();
	
	/**
	 * Destination of the message
	 * @return
	 */
	public String getDestinationId();
	
	/**
	 * Return the message data
	 * @return
	 */
	public byte[] getPayload();
}
