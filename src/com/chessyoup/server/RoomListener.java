package com.chessyoup.server;

public interface RoomListener {
	
	/**
	 * Callback called when an user joined.
	 * @param sourceRoom
	 */
	public void userJoined(Room sourceRoom);
	
	/**
	 * Callback called when an user leave an room.
	 * @param sourceRoom
	 */
	public void userLeaved(Room sourceRoom);
	
}
