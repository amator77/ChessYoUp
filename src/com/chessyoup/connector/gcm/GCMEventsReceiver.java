package com.chessyoup.connector.gcm;

import android.content.Context;

import com.google.android.gcm.GCMBroadcastReceiver;

public class GCMEventsReceiver extends GCMBroadcastReceiver {
	
	/**
     * Gets the class name of the intent service that will handle GCM messages.
     */
    protected String getGCMIntentServiceClassName(Context context) {
        return "com.chessyoup.connector.gcm.GCMIntentService";
    }
}
