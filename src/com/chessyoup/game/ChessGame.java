package com.chessyoup.game;

public interface ChessGame {
	
	public Player getWhitePlayer();
	
	public Player getBlackPlayer();
	
	public int getTimeControl();

	public int getMovesPerSession();

	public int getTimeIncrement();
	
}