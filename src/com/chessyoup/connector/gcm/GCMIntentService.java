package com.chessyoup.connector.gcm;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;

public class GCMIntentService extends GCMBaseIntentService {
	
	
	public GCMIntentService(){
		super();
	}
	
	protected String[] getSenderIds(Context context) {
		String ids[] = new String[1];
		ids[0] = GCMConnectionManager.getCurrentManager().getSenderId();
		return ids;
	}

	@Override
	protected void onRegistered(Context context, String registrationId) {
		Log.i("GCMIntentService", "Device registered: regId = " + registrationId);

		if (GCMConnectionManager.getCurrentManager() != null) {
			GCMConnectionManager.getCurrentManager().gcmRegistered(registrationId);
		}
	}

	@Override
	protected void onUnregistered(Context context, String registrationId) {
		Log.i("GCMIntentService", "Device unregistered :"+registrationId);
		if (GCMConnectionManager.getCurrentManager() != null) {
			GCMConnectionManager.getCurrentManager().gcmUnRegistered(registrationId);
		}
	}

	@Override
	protected void onMessage(Context context, Intent intent) {
		Log.i("GCMIntentService", "New GCM message :" + intent.getExtras().toString());
		if (GCMConnectionManager.getCurrentManager() != null) {
			GCMConnectionManager.getCurrentManager().hadleIncomingMessage(context, intent);
		}
	}

	@Override
	protected void onDeletedMessages(Context context, int total) {
		Log.i("GCMIntentService", "Received deleted messages notification");
	}

	@Override
	public void onError(Context context, String errorId) {
		Log.i("GCMIntentService", "Received error: " + errorId);
	}

	@Override
	protected boolean onRecoverableError(Context context, String errorId) {
		Log.i("GCMIntentService", "Received recoverable error: " + errorId);
		return super.onRecoverableError(context, errorId);
	}
}
