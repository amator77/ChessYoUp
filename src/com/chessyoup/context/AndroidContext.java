package com.chessyoup.context;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.gamelib.accounts.Account;
import com.gamelib.application.Context;
import com.gamelib.application.Logger;

public class AndroidContext implements Context {
	
	private android.content.Context androidContext;
	
	private AndroidLogger logger;
	
	private List<Account> accounts;
	
	public AndroidContext(){
		this.logger = new AndroidLogger();
		this.accounts = new ArrayList<Account>();
	}
	
	@Override
	public void initialize(Object contextData) {
		this.androidContext = (android.content.Context)contextData;		
	}

	@Override
	public InputStream getResourceAsInputStream(String resource) throws IOException {		
		return androidContext.getAssets().open(resource);				
	}

	@Override
	public Logger getLogger() {
		return this.logger;
	}
		
	public void registerAccount(Account account) {		
		this.accounts.add(account);
	}
	
	@Override
	public List<Account> listAccounts() {		
		return this.accounts;
	}

	@Override
	public String getApplicationName() {		
		return "cyp";
	}

	@Override
	public String getVersion() {
		return "1";
	}

	@Override
	public PLATFORM getPlatform() {
		return PLATFORM.MOBILE_ANDROID;
	}
}
