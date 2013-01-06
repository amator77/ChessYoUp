package com.chessyoup.game;

public interface Player {
	
	public enum TYPE { LOCAL , REMOTE  }
	
	public TYPE getPlayerType();
}
