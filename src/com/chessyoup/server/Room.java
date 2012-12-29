package com.chessyoup.server;

public interface Room {
	
	/**
	 * Return the name of this room
	 * @return
	 */
	public String getName();
	
	/**
	 * The unique id of this room.
	 * @return
	 */
	public String getId();
	
	/**
	 * Count the total numbers of users (registered users)
	 * @return
	 */
	public int countUsers();
	
	/**
	 * Register a listener to this room for receving asynk notifications
	 * @param listener
	 */
	public void addRommListener(RoomListener listener);
	
	/**
	 * Remove this listener from room
	 * @param listener
	 */
	public void removeRommListener(RoomListener listener);
	
}
