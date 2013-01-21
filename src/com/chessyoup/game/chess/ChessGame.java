package com.chessyoup.game.chess;

import com.chessyoup.game.IGame;
import com.chessyoup.game.IGameCommand;
import com.chessyoup.game.IGameListener;
import com.chessyoup.transport.Contact;

public class ChessGame implements IGame {
	
	private String id;
			
	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Contact getLocalContact() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Contact getRemoteContact() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setGameListener(IGameListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendCommand(IGameCommand command) {
		// TODO Auto-generated method stub
		
	}
}
