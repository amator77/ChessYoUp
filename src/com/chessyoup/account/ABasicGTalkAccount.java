package com.chessyoup.account;

import com.chessyoup.R;
import com.cyp.chess.account.BasicGTalkAccount;

public class ABasicGTalkAccount extends BasicGTalkAccount {

	public ABasicGTalkAccount(String id, String credentials) {
		super(id, credentials);
	}
	
	public String getIconTypeResource(){
		return String.valueOf(R.drawable.gtalk);
	}
}
