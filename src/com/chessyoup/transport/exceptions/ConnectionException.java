package com.chessyoup.transport.exceptions;

public class ConnectionException extends Exception {
	
	private static final long serialVersionUID = 2L;
	
	public ConnectionException(String message,Throwable ex){
		super(message,ex);
	}	
}
