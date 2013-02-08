package com.chessyoup.game;

import java.util.ArrayList;
import java.util.List;

import com.cyp.chess.game.ChessGame;
import com.cyp.chess.game.ChessGameController;

public class GameManager {
	
	private static final GameManager instance = new GameManager();
	
	private List<ChessGameController> ctrls;
	
	private GameManager(){
		this.ctrls = new ArrayList<ChessGameController>();
	}
	
	public static GameManager getManager(){
		return GameManager.instance;
	}
	
	public ChessGame findGame(String remoteId, long gameId){
		System.out.println("Find game :"+remoteId+" , "+gameId);
		for( ChessGameController ctrl : ctrls ){
			ChessGame game = ctrl.findGame(remoteId,gameId);
			
			if( game != null ){
				return game;
			}
		}
		
		return null;
	}

	public void addGameController(ChessGameController gameController) {		
		ctrls.add(gameController);
	}
}
