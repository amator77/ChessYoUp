package com.chessyoup.xmpp;

public interface XMPPGameListener {
	
	public void startGameRequestReceived(String jabberID,String whitePlayerJID, String blackPlayerJID);
	
	public void gameStarted(String whitePlayerJID, String blackPlayerJID );
	
	public void moveReceived(String jabberID,String move);
	
	public void chatReceived(String jabberID,String text);
	
	public void drawRequestReceived(String jabberID);
	
	public void drawAccepted(String jabberID);
	
	public void resignReceived(String jabberID);
	
	public void abortRequestReceived(String jabberID);

	public void abortRequestAccepted(String jabberID);
}
