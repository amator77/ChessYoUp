package com.chessyoup.server;

import java.util.List;

public interface RoomListener {
	
	/**
	 * Callback called when an user joined.
	 * @param sourceRoom
	 */
	public void roomJoined(Room sourceRoom , boolean status);
	
	/**
	 * Callback called when an user leave an room.
	 * @param sourceRoom
	 */
	public void roomLeaved(Room sourceRoom);
	
	/**
	 * 
	 * @param users
	 */
	public void usersReceived(List<User> users);
	
}
