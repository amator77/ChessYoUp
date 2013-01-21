package com.chessyoup.game;

public interface IGameController {
	
	/**
	 * 
	 * @param listener
	 */
	public void addGameControllerListener(IGameControllerListener listener);
	
	/**
	 * Send accept for this challange.The client of this method must wait for an
	 * challengeAccepted event in order to start the game.
	 * 
	 * @param challenge
	 */
	public void acceptChallenge(IChallenge challenge);
	
	/**
	 * 
	 * @param challenge
	 * @param reason
	 */
	public void rejectChallenge(IChallenge challenge);
}
