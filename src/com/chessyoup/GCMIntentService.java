package com.chessyoup;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.chessyoup.R;
import com.chessyoup.connector.Message;
import com.chessyoup.connector.gcm.GCMConnectionManager;
import com.google.android.gcm.GCMBaseIntentService;

public class GCMIntentService extends GCMBaseIntentService {
	
	public static final Map<String, String> config = new HashMap<String, String>();
	private static final String TAG = "GCMIntentService";
	
	static{
		GCMIntentService.config.put("sender_id", "824424892358");
		GCMIntentService.config.put("api_key", "AIzaSyB6HkBz-SrHpnMgaX1-Wff3E-WQM2z2VIM");
	}
	
	public GCMIntentService() {
		super(GCMIntentService.config.get("sender_id"));
	}

	@Override
	protected void onRegistered(Context context, String registrationId) {
		Log.i(TAG, "Device registered: regId = " + registrationId);
		GCMConnectionManager.getManager().gcmRegistered(registrationId);		
	}

	@Override
	protected void onUnregistered(Context context, String registrationId) {
		Log.i(TAG, "Device unregistered");	
		GCMConnectionManager.getManager().gcmUnRegistered(registrationId);
	}

	@Override
	protected void onMessage(Context context, Intent intent) {
		Log.i(TAG, "Received message :" + intent.getExtras().toString());		
		GCMConnectionManager.getManager().hadleIncomingMessage(intent.getExtras().getString("source_id"),intent.getExtras().getString("payload") );		
	}

	@Override
	protected void onDeletedMessages(Context context, int total) {
		Log.i(TAG, "Received deleted messages notification");
		String message = getString(R.string.gcm_deleted, total);
	}

	@Override
	public void onError(Context context, String errorId) {		
		Log.i(TAG, "Received error: " + errorId);
	}

	@Override
	protected boolean onRecoverableError(Context context, String errorId) {
		// log message
		Log.i(TAG, "Received recoverable error: " + errorId);
		return super.onRecoverableError(context, errorId);
	}
}
