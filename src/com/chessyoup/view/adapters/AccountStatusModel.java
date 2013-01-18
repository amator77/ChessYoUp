package com.chessyoup.view.adapters;

import java.util.ArrayList;
import java.util.List;

import com.chessyoup.transport.Presence;
import com.chessyoup.transport.Presence.MODE;

public class AccountStatusModel {
	
	private String account;
	
	private String accountStatusString;
	
	private List<Presence.MODE> modes;
	
	public AccountStatusModel(String account,String accountStatusString){
		this.account = account;
		this.accountStatusString = accountStatusString;
		this.modes = new ArrayList<Presence.MODE>();
		this.modes.add(MODE.ONLINE);
		this.modes.add(MODE.OFFLINE);
		this.modes.add(MODE.BUSY);
		this.modes.add(MODE.AWAY);
	}
	
	public Presence.MODE getStatus(int position){
		return this.modes.get(position);
	}
	
	public String getAccount(){
		return this.account;
	}

	public int getCount() {
		
		return this.modes.size();
	}

	public String getAccountStatusString() {
		return accountStatusString;
	}

	public void setAccountStatusString(String accountStatusString) {
		this.accountStatusString = accountStatusString;
	}

	public List<Presence.MODE> getModes() {
		return modes;
	}

	public void setModes(List<Presence.MODE> modes) {
		this.modes = modes;
	}

	public void setAccount(String account) {
		this.account = account;
	}
}
