package com.chessyoup.store;

import com.google.api.client.auth.oauth2.draft10.AccessTokenResponse;
import com.google.api.client.googleapis.auth.oauth2.draft10.GoogleAccessProtectedResource;

public interface CredentialStore {

  AccessTokenResponse read();
  void write(AccessTokenResponse response);
  void write(GoogleAccessProtectedResource accessTokenResource);
  void clearCredentials();
}
