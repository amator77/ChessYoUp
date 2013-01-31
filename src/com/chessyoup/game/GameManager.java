package com.chessyoup.game;

import com.cyp.chess.game.ChessGame;

public class GameManager {
	
	private static final GameManager instance = new GameManager();
	
	
	private GameManager(){
		
	}
	
	public static GameManager getManager(){
		return GameManager.instance;
	}
	
	public ChessGame findGame(String gameId){
		return null;
	}
}
