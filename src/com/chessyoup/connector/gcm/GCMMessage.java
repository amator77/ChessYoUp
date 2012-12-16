package com.chessyoup.connector.gcm;

import com.chessyoup.connector.Message;

public class GCMMessage implements Message {
	
	private String sourceRegistrationID;
	
	private String destinationRegistrationID;
	
	private int sequnce;
	
	private String message;
	
	@Override
	public int getSequence() {
		return this.sequnce;
	}

	@Override
	public String getSourceId() {
		return this.sourceRegistrationID;
	}

	@Override
	public String getDestinationId() {
		return this.destinationRegistrationID;
	}

	@Override
	public byte[] getPayload() {
		return this.message.getBytes();
	}

	public void setSourceRegistrationID(String sourceRegistrationID) {
		this.sourceRegistrationID = sourceRegistrationID;
	}

	public void setDestinationRegistrationID(String destinationRegistrationID) {
		this.destinationRegistrationID = destinationRegistrationID;
	}

	public void setSequnce(int sequnce) {
		this.sequnce = sequnce;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
