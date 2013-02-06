package com.chessyoup.store;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.google.api.client.auth.oauth2.draft10.AccessTokenResponse;
import com.google.api.client.googleapis.auth.oauth2.draft10.GoogleAccessProtectedResource;

public class SharedPreferencesCredentialStore implements CredentialStore {

	public static final String ACCESS_TOKEN = "access_token";
	public static final String EXPIRES_IN = "expires_in";
	public static final String REFRESH_TOKEN = "refresh_token";
	public static final String SCOPE = "scope";
	public static final String USERNAME = "username";
	
	private SharedPreferences prefs;
	
	public SharedPreferencesCredentialStore(SharedPreferences prefs) {
		this.prefs = prefs;
	}
	
	@Override
	public AccessTokenResponse read() {
		AccessTokenResponse accessTokenResponse = new AccessTokenResponse();
			accessTokenResponse.accessToken = prefs.getString(ACCESS_TOKEN, "");
			accessTokenResponse.expiresIn = prefs.getLong(EXPIRES_IN, 0);
			accessTokenResponse.refreshToken = prefs.getString(REFRESH_TOKEN, "");
			accessTokenResponse.scope = prefs.getString(SCOPE, "");
			accessTokenResponse.put("username", prefs.getString(USERNAME, ""));
		return accessTokenResponse;
	}

	@Override
	public void write(AccessTokenResponse accessTokenResponse) {
		Editor editor = prefs.edit();
		editor.putString(ACCESS_TOKEN,accessTokenResponse.accessToken);
		editor.putLong(EXPIRES_IN,accessTokenResponse.expiresIn);
		editor.putString(REFRESH_TOKEN,accessTokenResponse.refreshToken);
		editor.putString(SCOPE,accessTokenResponse.scope);
		editor.putString(USERNAME,accessTokenResponse.get(USERNAME) != null ? accessTokenResponse.get(USERNAME).toString() : "");
		editor.commit();
	}
	
	@Override
	public void write(GoogleAccessProtectedResource accessTokenResource) {
		Editor editor = prefs.edit();
		editor.putString(ACCESS_TOKEN,accessTokenResource.getAccessToken());
		editor.putString(REFRESH_TOKEN,accessTokenResource.getRefreshToken());
		editor.putString(USERNAME,accessTokenResource.getClientId());
		editor.commit();
	}
	
	@Override
	public void clearCredentials() {
		Editor editor = prefs.edit();
		editor.remove(ACCESS_TOKEN);
		editor.remove(EXPIRES_IN);
		editor.remove(REFRESH_TOKEN);
		editor.remove(SCOPE);
		editor.remove(USERNAME);
		editor.commit();
	}
}
