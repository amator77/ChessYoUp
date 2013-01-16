package com.chessyoup.accounts;

public interface Account {
	
	public enum TYPE { XMPP_GOOGLE , XMPP_FACEBOOK }
	
	public enum STATUS { ONLINE , AWAY , BUSY , OFFLINE }
	
	public String getId();
	
	public Roster getRoster();
	
	public TYPE getType();
	
	public STATUS getStatus();
	
	public void login(LoginCallback callback);
	
	public void logout();
	
	public interface LoginCallback{
		
		public void onLogginSuccess();
		
		public void onLogginError();
	}
}
