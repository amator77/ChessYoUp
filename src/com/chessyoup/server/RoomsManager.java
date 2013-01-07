package com.chessyoup.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.chessyoup.connector.ConnectionManager;
import com.chessyoup.connector.ConnectionManagerListener;
import com.chessyoup.connector.Device;
import com.chessyoup.connector.Message;
import com.chessyoup.connector.gcm.GCMConnectionManager;
import com.chessyoup.server.gcm.GCMRemoteService;
import com.chessyoup.server.gcm.GCMRoom;
import com.chessyoup.server.gcm.GCMUser;

public class RoomsManager implements ConnectionManagerListener {

	private static RoomsManager manager;

	private GCMRemoteService remoteService;

	private List<Room> rooms;

	private Room joinedRoom;

	private RoomListener roomListener;

	private GCMConnectionManager connectionManager;

	private Context applicationContext;

	private static final String TAG = "GCMRoomsManager";

	public Room getJoinedRoom() {
		return joinedRoom;
	}

	public void setJoinedRoom(Room joinedRoom) {
		this.joinedRoom = joinedRoom;
	}

	private RoomsManager(Context appContext) {
		this.applicationContext = appContext;
		Properties appProperties = this.loadAppProperties(appContext);
		this.remoteService = new GCMRemoteService(
				appProperties.getProperty("chessyoup_url"));
	}

	public synchronized static RoomsManager getManager(Context appContext) {

		if (RoomsManager.manager == null) {
			RoomsManager.manager = new RoomsManager(appContext);
		}

		return RoomsManager.manager;
	}
	
	public synchronized static RoomsManager getManager() {
		
		if (RoomsManager.manager == null) {
			throw new RuntimeException("The manager is not initialized!");
		}

		return RoomsManager.manager;
	}
	
	public void joinRoom(Room room, RoomListener roomListener) {
		this.joinedRoom = room;
		this.roomListener = roomListener;
		this.connectionManager = new GCMConnectionManager(
				this.applicationContext, room.getExtras()
						.get(GCMRoom.SENDER_ID),room.getExtras()
						.get(GCMRoom.API_KEY));
		this.connectionManager.addListener(this);
		this.connectionManager.initialize();
	}

	public void leaveRoom() {
		if( this.joinedRoom != null){
			this.connectionManager.dispose();
		}
	}

	public List<Room> getRooms() {

		if (this.rooms != null && this.rooms.size() > 0) {
			return this.rooms;
		} else {
			try {
				return this.remoteService.rooms();
			} catch (IOException e) {
				e.printStackTrace();
				Log.e("RoomsManager", e.getMessage());
				return null;
			}
		}
	}

	private Properties loadAppProperties(Context appContext) {
		AssetManager manager = appContext.getAssets();
		Properties p = new Properties();

		try {
			p.load(manager.open("chessyoup.properties"));
		} catch (IOException e) {
			Log.e("RoomsManager", "Error on reading configuration!");
		}

		return p;
	}

	@Override
	public void onInitialize(boolean status) {

		if (this.remoteService.register(
				this.connectionManager.getLocalDevice(),
				this.joinedRoom.getId())) {
			if (this.roomListener != null) {
				this.roomListener.roomJoined(this.joinedRoom, true);				
				this.loadUsers();
			}
		} else {
			if (this.roomListener != null) {
				this.roomListener.roomJoined(this.joinedRoom, false);
			}
		}
	}

	@Override
	public void onDispose(boolean status) {
		this.remoteService.unRegister(this.connectionManager.getLocalDevice());
		
		if (this.roomListener != null) {
			this.roomListener.roomLeaved(this.joinedRoom);
		}
		
		this.joinedRoom = null;
	}
	
	
	
	public ConnectionManager getConnectionManager() {
		return this.connectionManager;
	}
	
	public void loadUsers(){
		try {
			List<Device> devices = this.remoteService.devices(this.joinedRoom.getId());
			List<User> users = new ArrayList<User>();
			
			for( Device d : devices ){
				GCMUser user = new GCMUser(d, UserStatus.ONLINE);
				users.add(user);
			}
			
			this.roomListener.usersReceived(users);
			
		} catch (IOException e) {					
			e.printStackTrace();
		}
	}

	@Override
	public void onNewConnectionRequest(Device remoteDevice,Message mesage) {
		// TODO Auto-generated method stub
		
	}
}
