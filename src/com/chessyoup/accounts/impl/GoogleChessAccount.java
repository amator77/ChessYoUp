package com.chessyoup.accounts.impl;

import android.content.Context;
import android.os.AsyncTask;

import com.chessyoup.R;
import com.chessyoup.accounts.Account;
import com.chessyoup.game.IGameController;
import com.chessyoup.transport.Connection;
import com.chessyoup.transport.Roster;
import com.chessyoup.transport.exceptions.ConnectionException;
import com.chessyoup.transport.exceptions.LoginException;
import com.chessyoup.transport.xmpp.google.XMPPMD5Connection;

public class GoogleChessAccount implements Account {
	
	private String id;
	
	private String credentials;
	
	private XMPPMD5Connection connection;
	
	private STATUS status;
	
	private LoginCallback loginCallback;
	
	public GoogleChessAccount(String id,String credentials,Context appContext){
		this.id = id;
		this.credentials = credentials;
		this.connection = new XMPPMD5Connection(appContext);
		this.status = STATUS.OFFLINE;
	}
	
	@Override
	public String getId() {
		return this.id;
	}
	
	@Override
	public Connection getConnection() {		
		return this.connection;
	}
	
	@Override
	public Roster getRoster() {
		if( this.connection.isConnected() ){
			return this.connection.getRoster();
		}
		else{
			return null;
		}
	}

	@Override
	public TYPE getType() {
		return TYPE.XMPP_GOOGLE;
	}

	@Override
	public STATUS getStatus() {
		return this.status;
	}

	@Override
	public void login(LoginCallback callback) {
		if( !this.connection.isConnected() && this.loginCallback == null ){
			this.loginCallback = callback;
			this.runLoginTask();
		}
	}

	@Override
	public void logout() {
		if( this.connection.isConnected()){
			this.connection.logout();
		}
	}

	@Override
	public int getImageIconId() {
		return R.drawable.gtalk;
	}
	
	private void runLoginTask(){
		AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
			String errorMessage;
			
			@Override
			protected Boolean doInBackground(Void... params) {
				
				try {
					connection.login(id, credentials);
					return true;
				} catch (ConnectionException e) {
					errorMessage = "ConnectionException";
					e.printStackTrace();
					return false;
				} catch (LoginException e) {
					errorMessage = "Invalid credentials!";
					e.printStackTrace();
					return false;
				}
			}
			
			protected void onPostExecute(Boolean result) {
				if( result ){
					loginCallback.onLogginSuccess();					
				}
				else{
					loginCallback.onLogginError(errorMessage);
				}
				
				loginCallback = null;
			}
		};

		task.execute();
	}

	@Override
	public String toString() {
		return "GoogleAccount [id=" + id + ", credentials=" + credentials
				+ ", connection=" + connection + ", status=" + status
				+ ", loginCallback=" + loginCallback + "]";
	}

	@Override
	public IGameController getGameController() {
		// TODO Auto-generated method stub
		return null;
	}	
}
