package com.chessyoup.server.gcm;

import java.util.HashMap;
import java.util.Map;

import com.chessyoup.server.Room;

public class GCMRoom implements Room {

	public static final String SENDER_ID = "sender_id";

	public static final String API_KEY = "api_key";

	private String id;

	private String name;

	private int joinedUsers;

	public Map<String, String> extras;

	public GCMRoom() {
		this.extras = new HashMap<String, String>();
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String getId() {
		return this.id;
	}

	public void setSenderId(String senderId) {
		this.extras.put(SENDER_ID, senderId);
	}

	public void setApiKey(String apiKey) {
		this.extras.put(API_KEY, apiKey);
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int getSize() {
		return 1000;
	}

	@Override
	public int getJoinedUsers() {
		return this.joinedUsers;
	}

	@Override
	public Map<String, String> getExtras() {
		return this.extras;
	}

	public void setJoinedUsers(int joinedUsers) {
		this.joinedUsers = joinedUsers;
	}

	@Override
	public String toString() {
		return new StringBuffer(this.name).append("(").append(this.joinedUsers).append("/").append(this.getSize()).append(")").toString();
	}	
}
