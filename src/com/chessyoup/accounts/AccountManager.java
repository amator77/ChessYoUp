package com.chessyoup.accounts;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.chessyoup.accounts.impl.GoogleChessAccount;

public class AccountManager {

	private static AccountManager manager;

	private Context applicationContext;

	private List<Account> accounts;

	private AccountManager(Context applicationContext) {
		this.applicationContext = applicationContext;
		GoogleChessAccount account = new GoogleChessAccount("amator77@gmail.com",
				"leo@1977",applicationContext);
		this.accounts = new ArrayList<Account>();
		this.accounts.add(account);
	}

	public static synchronized void initialize(Context applicationContext) {
		AccountManager.manager = new AccountManager(applicationContext);
	}

	public static AccountManager getManager() {

		if (AccountManager.manager == null) {
			throw new RuntimeException("Manager is not initialized!");
		}

		return AccountManager.manager;
	}

	public List<Account> listAccounts() {
		return this.accounts;
	}
}
