package com.chessyoup.game;

import com.chessyoup.transport.Contact;

public interface IGame {
	
	public enum STATE { NOT_STARTED , STARTED , FINISHED }
	
	public String getId();
	
	public Contact getLocalContact();
	
	public Contact getRemoteContact();
	
	public void setGameListener(IGameListener listener);
	
	public void sendCommand(IGameCommand command);
}
