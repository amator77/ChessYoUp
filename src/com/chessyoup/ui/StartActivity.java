package com.chessyoup.ui;

import java.io.IOException;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chessyoup.R;
import com.chessyoup.account.ABasicGTalkAccount;
import com.chessyoup.store.CredentialStore;
import com.chessyoup.store.SharedPreferencesCredentialStore;
import com.cyp.accounts.Account;
import com.cyp.application.Application;
import com.google.api.client.auth.oauth2.draft10.AccessTokenResponse;
import com.google.api.client.googleapis.auth.oauth2.draft10.GoogleAccessTokenRequest.GoogleAuthorizationCodeGrant;
import com.google.api.client.googleapis.auth.oauth2.draft10.GoogleAuthorizationRequestUrl;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;

public class StartActivity extends Activity {

	private TextView accountTextView;

	private ProgressBar progressView;

	private ImageButton gtalkButton;

	private WebView webview;

	private static final String SCOPE = "https://www.googleapis.com/auth/googletalk";

	private static final String CALLBACK_URL = "http://localhost";

	private static final String CLIENT_ID = "824424892358.apps.googleusercontent.com";

	private static final String CLIENT_SECRET = "3ng2GDWbloODjOxs4d1r_Jti";

	private SharedPreferences prefs;

	private AlertDialog alert;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try {
			Application.configure("com.chessyoup.context.AndroidContext",
					this.getApplicationContext());
		} catch (Exception e) {
			e.printStackTrace();
		}

		Log.d("RoomActivity", "on create");
		this.initUI();
		this.installListeners();

		this.prefs = PreferenceManager.getDefaultSharedPreferences(this);

		SharedPreferencesCredentialStore store = new SharedPreferencesCredentialStore(
				PreferenceManager
						.getDefaultSharedPreferences(StartActivity.this));
		AccessTokenResponse account = store.read();		

		if (account.refreshToken != null && account.refreshToken.trim().length() > 0) {
			if( isOnline() ){
				runLoginTask(account);
			}
			else{
				Toast.makeText(this, "No connection!", Toast.LENGTH_LONG).show();
				this.accountTextView.setVisibility(View.INVISIBLE);
				this.progressView.setVisibility(View.INVISIBLE);
				this.gtalkButton.setVisibility(View.VISIBLE);
			}
		} else {
			this.accountTextView.setVisibility(View.INVISIBLE);
			this.progressView.setVisibility(View.INVISIBLE);
			this.gtalkButton.setVisibility(View.VISIBLE);
		}
	}
	
	private void installListeners() {
		this.gtalkButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String authorizationUrl = new GoogleAuthorizationRequestUrl(
						CLIENT_ID, CALLBACK_URL, SCOPE).build();
				System.out.println("authorizationUrl : " + authorizationUrl);
				alert.show();
				webview.loadUrl(authorizationUrl);				
			}
		});
		webview.setFocusable(true);
		webview.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
				case MotionEvent.ACTION_UP:
					v.requestFocus();
					break;
				}
				return false;
			}
		});
	}

	@Override
	public void onStart() {
		super.onStart();
		Log.d("RoomActivity", "on start");
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.d("RoomActivity", "on spause");
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d("RoomActivity", "on resume");
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.d("RoomActivity", "on stop");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d("RoomActivity", "on destroy");
	}

	private void initUI() {
		setContentView(R.layout.start);
		this.webview = new WebView(this);
		this.webview.getSettings().setJavaScriptEnabled(true);
		this.webview.setVisibility(View.VISIBLE);
		this.webview.setWebViewClient(new MyWebViewClient());
		alert = new AlertDialog.Builder(this).create();
		LinearLayout ll = new LinearLayout(this);
		EditText dummy = new EditText(this);
		dummy.setVisibility(View.INVISIBLE);
		ll.addView(dummy, 0, 0);
		ll.addView(this.webview);
		alert.setView(ll);
		this.webview.setFocusable(true);
		this.accountTextView = (TextView) findViewById(R.id.startViewAccountTextView);
		this.progressView = (ProgressBar) findViewById(R.id.startViewProgressBar);
		this.gtalkButton = (ImageButton) findViewById(R.id.startViewGTalkImageButton);

		Bitmap bmp = BitmapFactory.decodeResource(getResources(),
				R.drawable.light);
		BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), bmp);
		bitmapDrawable.setTileModeXY(Shader.TileMode.REPEAT,
				Shader.TileMode.REPEAT);
		this.findViewById(R.id.startLayout).setBackgroundDrawable(
				bitmapDrawable);
	}

	private void runLoginTask(final AccessTokenResponse accessTokenResponse) {
		System.out
				.println("run login task : " + accessTokenResponse.toString());
		this.accountTextView.setVisibility(View.VISIBLE);
		this.progressView.setVisibility(View.VISIBLE);
		this.gtalkButton.setVisibility(View.INVISIBLE);

		AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {

				final ABasicGTalkAccount account = new ABasicGTalkAccount(
						CLIENT_ID, accessTokenResponse.accessToken);

				account.login(new Account.LoginCallback() {

					@Override
					public void onLogginSuccess() {
						Application.getContext().registerAccount(account);

						StartActivity.this.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								Intent intent = new Intent(StartActivity.this,
										MainActivity.class);
								StartActivity.this.startActivity(intent);
								StartActivity.this.finish();
							}
						});
					}

					@Override
					public void onLogginError(String errorMessage) {
						StartActivity.this.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								String authorizationUrl = new GoogleAuthorizationRequestUrl(
										CLIENT_ID, CALLBACK_URL, SCOPE).build();
								alert.show();
								webview.loadUrl(authorizationUrl);
							}
						});
					}
				});

				return null;
			}
		};

		task.execute();

	}

	class MyWebViewClient extends WebViewClient {
		@Override
		public void onPageStarted(WebView view, String url, Bitmap bitmap) {
			System.out.println("onPageStarted : " + url);
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			
			if (url.startsWith(CALLBACK_URL)) {
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
					alert.dismiss();
				}
			}
			else{
				android.accounts.Account[] acs = AccountManager.get(StartActivity.this).getAccounts();
				for(android.accounts.Account ac : acs ){
					if( ac.type.contains("google")){
						webview.loadUrl("javascript:document.getElementById('Email').value='"+ac.name+"';document.getElementById('Passwd').focus()");						
						break;
					}
				}				
			}
		}

		private String extractCodeFromUrl(String url) {
			return url.substring(CALLBACK_URL.length() + 7, url.length());
		}
	}

	private void runGetAccessTokenTask(
			final GoogleAuthorizationCodeGrant grant, final WebView view) {
		AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {

				try {
					final AccessTokenResponse accessTokenResponse = grant
							.execute();
					
					CredentialStore credentialStore = new SharedPreferencesCredentialStore(
							prefs);
					credentialStore.write(accessTokenResponse);

					StartActivity.this.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							view.setVisibility(View.INVISIBLE);
							alert.dismiss();
							runLoginTask(accessTokenResponse);
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
	
	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	   
	    if (cm.getActiveNetworkInfo() != null
	            && cm.getActiveNetworkInfo().isAvailable()
	            && cm.getActiveNetworkInfo().isConnected()) {
	        return true;
	    } else {
	     
	        return false;
	    }
	}
}
