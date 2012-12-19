package com.chessyoup.connector.gcm;

import com.chessyoup.connector.Message;

public class GCMMessage implements Message {
	
	private String sourceRegistrationID;
	
	private String destinationRegistrationID;
	
	private int sequnce;
	
	private String body;
	
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
	public String getBody() {
		return this.body;
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

	public void setBody(String body) {
		this.body = body;
	}

	@Override
	public String toString() {
		return "GCMMessage [sourceRegistrationID=" + sourceRegistrationID
				+ ", destinationRegistrationID=" + destinationRegistrationID
				+ ", sequnce=" + sequnce + ", body=" + body + "]";
	}		
}
