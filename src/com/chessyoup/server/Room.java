package com.chessyoup.server;

import java.util.Map;

public interface Room {
	
	/**
	 * The unique id of this room.
	 * @return
	 */
	public String getId();
	
	/**
	 * Return the name of this room
	 * @return
	 */
	public String getName();
	
	/**
	 * Get max users limit for this room
	 * @return
	 */
	public int getSize();
	
	/**
	 * Count the total numbers of users (registered users)
	 * @return
	 */
	public int getJoinedUsers();
	
	/**
	 * Extra (customs , depends on implementation ) params for this room
	 * @return
	 */
	public Map<String, String> getExtras();
}
