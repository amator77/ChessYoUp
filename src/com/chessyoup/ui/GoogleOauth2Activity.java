package com.chessyoup.ui;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.chessyoup.store.CredentialStore;
import com.chessyoup.store.SharedPreferencesCredentialStore;
import com.google.api.client.auth.oauth2.draft10.AccessTokenResponse;
import com.google.api.client.googleapis.auth.oauth2.draft10.GoogleAccessTokenRequest.GoogleAuthorizationCodeGrant;
import com.google.api.client.googleapis.auth.oauth2.draft10.GoogleAuthorizationRequestUrl;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;

public class GoogleOauth2Activity extends Activity {

	final String TAG = getClass().getName();

	private SharedPreferences prefs;

	private static final String SCOPE = "https://www.googleapis.com/auth/googletalk";
	private static final String CALLBACK_URL = "http://localhost";
	private static final String CLIENT_ID = "824424892358.apps.googleusercontent.com";
	private static final String CLIENT_SECRET = "3ng2GDWbloODjOxs4d1r_Jti";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "Starting task to retrieve request token.");
		this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		final WebView webview = new WebView(this);
		webview.getSettings().setJavaScriptEnabled(true);
		webview.setVisibility(View.VISIBLE);
		setContentView(webview);
		String authorizationUrl = new GoogleAuthorizationRequestUrl(CLIENT_ID,
				CALLBACK_URL, SCOPE).build();
		System.out.println("authorizationUrl : " + authorizationUrl);
		/* WebViewClient must be set BEFORE calling loadUrl! */
		webview.setWebViewClient(new WebViewClient() {

			@Override
			public void onPageStarted(WebView view, String url, Bitmap bitmap) {
				System.out.println("onPageStarted : " + url);
			}

			@Override
			public void onPageFinished(WebView view, String url) {

				if (url.startsWith(CALLBACK_URL)) {
					System.out.println("after  CALLBACK_URL: ");

					if (url.indexOf("code=") != -1) {

						String code = extractCodeFromUrl(url);
						runGetAccessTokenTask(new GoogleAuthorizationCodeGrant(
								new NetHttpTransport(), new JacksonFactory(),
								CLIENT_ID, CLIENT_SECRET, code, CALLBACK_URL),
								webview);

					} else if (url.indexOf("error=") != -1) {
						view.setVisibility(View.INVISIBLE);
						new SharedPreferencesCredentialStore(prefs)
								.clearCredentials();
						startActivity(new Intent(GoogleOauth2Activity.this,
								MainActivity.class));
					}

				}

				System.out.println("onPageFinished : " + url);
			}

			private String extractCodeFromUrl(String url) {
				return url.substring(CALLBACK_URL.length() + 7, url.length());
			}
		});

		webview.loadUrl(authorizationUrl);
	}

	private void runGetAccessTokenTask(
			final GoogleAuthorizationCodeGrant grant, final WebView view) {
		AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {

				try {					
					AccessTokenResponse accessTokenResponse = grant.execute();
					System.out.println("accestoken : "
							+ accessTokenResponse.accessToken);

					CredentialStore credentialStore = new SharedPreferencesCredentialStore(
							prefs);
					credentialStore.write(accessTokenResponse);

					GoogleOauth2Activity.this.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							view.setVisibility(View.INVISIBLE);
							GoogleOauth2Activity.this.startActivity(new Intent(
									GoogleOauth2Activity.this,
									MainActivity.class));
							GoogleOauth2Activity.this.finish();

						}
					});
				} catch (IOException e) {
					e.printStackTrace();
				}

				return null;
			}
		};

		task.execute();
	}
}
