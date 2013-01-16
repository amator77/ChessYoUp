package com.chessyoup.accounts;

import java.util.List;

import android.content.Context;

public class AccountManager {
	
	private static AccountManager manager;
	
	private Context applicationContext;
	
	private AccountManager(Context applicationContext){
		this.applicationContext = applicationContext;
	}
		
	public static synchronized void initialize(Context applicationContext){
		AccountManager.manager = new AccountManager(applicationContext);
	}
	
	public static AccountManager getManager(){
		
		if( AccountManager.manager == null ){
			throw new RuntimeException("Manager is not initialized!");
		}
		
		return AccountManager.manager;
	}
			
	
	public List<Account> listAccounts(){
		return null;
	}
}
