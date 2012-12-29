package com.chessyoup.server.gcm;

import java.util.ArrayList;
import java.util.List;

import com.chessyoup.server.Room;
import com.chessyoup.server.RoomListener;

public class GCMRoom implements Room {

	private String id;

	private String name;

	private String senderId;

	private String apiKey;

	private int usersCount;

	private List<RoomListener> listeners;

	public GCMRoom() {
		this.listeners = new ArrayList<RoomListener>();
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public int countUsers() {
		return this.usersCount;
	}

	public String getSenderId() {
		return senderId;
	}

	public void setSenderId(String senderId) {
		this.senderId = senderId;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public int getUsersCount() {
		return usersCount;
	}

	public void setUsersCount(int usersCount) {
		this.usersCount = usersCount;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void addRommListener(RoomListener listener) {
		if (!this.listeners.contains(listener)) {
			this.listeners.add(listener);
		}
	}

	@Override
	public void removeRommListener(RoomListener listener) {
		if (this.listeners.contains(listener)) {
			this.listeners.remove(listener);
		}
	}

	@Override
	public String toString() {
		return "GCMRoom [id=" + id + ", name=" + name + ", senderId="
				+ senderId + ", apiKey=" + apiKey + ", usersCount="
				+ usersCount + "]";
	}
}
