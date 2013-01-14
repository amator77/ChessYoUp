package com.chessyoup.facebook;

public class SessionManager {
	private static SessionManager manager;
	
	private SessionManager(){
		
	}
	
	public static SessionManager getManager(){
		return SessionManager.manager;
	}
}
