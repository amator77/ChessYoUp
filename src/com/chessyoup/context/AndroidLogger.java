package com.chessyoup.context;

import android.util.Log;

import com.cyp.application.Logger;

public class AndroidLogger implements Logger {

	@Override
	public void debug(String component, String message) {
		Log.d(component, message);
	}

	@Override
	public void info(String component, String message) {
		Log.i(component, message);
	}

	@Override
	public void error(String component, String message, Throwable ex) {
		Log.e(component, message, ex);
	}
}
