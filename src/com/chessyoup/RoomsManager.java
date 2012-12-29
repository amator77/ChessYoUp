package com.chessyoup;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import com.chessyoup.connector.ConnectionManager;
import com.chessyoup.server.Room;
import com.chessyoup.server.User;
import com.chessyoup.server.gcm.GCMRemoteService;
import com.google.android.gcm.GCMRegistrar;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

public class RoomsManager {
	
	private static RoomsManager manager;
	
	private GCMRemoteService remoteService;
	
	private List<Room> rooms;
	
	private RoomsManager(Context appContext){
		Properties appProperties = this.loadAppProperties(appContext);
		this.remoteService = new GCMRemoteService(appProperties.getProperty("chessyoup_url"));
	}
	
	public synchronized static RoomsManager getManager(Context appContext){
		
		if( RoomsManager.manager == null ){
			RoomsManager.manager = new RoomsManager(appContext);
		}
		
		return RoomsManager.manager;
	}
	
	public void joinRoom(Room room,User user){
	
	}
	
	public void leaveRoom(Room room){
		
	}
	
	public List<Room> getRooms(){
		
		if( this.rooms != null && this.rooms.size() > 0 ){
			return this.rooms;
		}
		else{
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
}
