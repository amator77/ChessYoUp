package com.chessyoup.context;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.cyp.accounts.Account;
import com.cyp.application.Context;
import com.cyp.application.Logger;

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
	
	public void removeAccount(Account account) {		
		this.accounts.remove(account);
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
	
	@Override
	public List<String> getApplicationFutures() {
		List<String> futures = new ArrayList<String>();
		futures.add("http://jabber.org/protocol/games/chess/v1");		
		return futures;
	}
}
