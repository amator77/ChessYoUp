package com.chessyoup.transport.exceptions;

public class LoginException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public LoginException(String message,Throwable ex){
		super(message,ex);
	}	
}
