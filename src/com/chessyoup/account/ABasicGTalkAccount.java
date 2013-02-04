package com.chessyoup.account;

import com.chessyoup.R;
import com.cyp.chess.account.GTalkAccount;

public class ABasicGTalkAccount extends GTalkAccount {

	public ABasicGTalkAccount(String id, String credentials) {
		super(id, credentials,true);
	}
	
	public String getIconTypeResource(){
		return String.valueOf(R.drawable.gtalk);
	}
}
