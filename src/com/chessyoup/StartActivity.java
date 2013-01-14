package com.chessyoup;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.chessyoup.xmpp.XMPPConnectionManager;
import com.facebook.LoggingBehavior;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;

public class StartActivity extends Activity {
	SessionStatusCallback statusCallback = new SessionStatusCallback();
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("RoomActivity", "on create");
		this.initUI();
		this.installListeners();
		
		Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);

		
		
        Session session = Session.getActiveSession();
        if (session == null) {
            if (savedInstanceState != null) {
                session = Session.restoreSession(this, null, statusCallback, savedInstanceState);
            }
            if (session == null) {
                session = new Session(this);
            }
            Session.setActiveSession(session);
            if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
                session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));
            }
        }        

        updateView();
	}

	private void updateView() {
		
		Session ssession = Session.getActiveSession();
    	
    	if (ssession.isOpened()) {
    		this.runFacebookLoginTask(ssession.getApplicationId(),ssession.getAccessToken());
    		
    		Log.d("Facebook acces token :", ssession.getAccessToken());        		
    		Log.d("Facebook app id :", ssession.getApplicationId());
    		Log.d("Facebook exp date :", ssession.getExpirationDate().toString());
    		Log.d("Facebook state :", ssession.getState().toString()); 
    		Log.d("Facebook permisions:", ssession.getPermissions() != null ? ssession.getPermissions().toString() : "no permision");
    		
    		
    	}
    	else{
    		Log.d("nasol", "nu am sesiune");
    	}
	}

	@Override
	public void onStart() {
		super.onStart();
		Log.d("RoomActivity", "on start");
		EditText accountEditText = (EditText) findViewById(R.id.loginEditTextAccountPassword);
		accountEditText.requestFocus();
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
		AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				XMPPConnectionManager.getInstance().logout();
				return null;
			}
		};

		task.execute();
		Log.d("RoomActivity", "on destroy");
	}
	
	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
    }
	
	private void initUI() {
		setContentView(R.layout.start);
		Bitmap bmp = BitmapFactory.decodeResource(getResources(),
				R.drawable.light);
		BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), bmp);
		bitmapDrawable.setTileModeXY(Shader.TileMode.REPEAT,
				Shader.TileMode.REPEAT);
		this.findViewById(R.id.startLayout).setBackgroundDrawable(
				bitmapDrawable);

		EditText accountEditText = (EditText) findViewById(R.id.loginEditTextAccount);
		String googleAccount = getGoogleAccount();
		accountEditText.setText(googleAccount != null ? googleAccount
				: "amator77@gmail.com");
		EditText passwordEditText = (EditText) findViewById(R.id.loginEditTextAccountPassword);
	}

	private void installListeners() {
		Button accountEditText = (Button) findViewById(R.id.loginButton);
		accountEditText.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				EditText accountEditText = (EditText) findViewById(R.id.loginEditTextAccount);
				EditText passwordEditText = (EditText) findViewById(R.id.loginEditTextAccountPassword);
				String username = accountEditText.getEditableText().toString();
				String password = passwordEditText.getEditableText().toString();

				if (username != null && username.trim().length() > 0
						&& password != null && password.trim().length() > 0) {
					runLoginTask(username, password);
				} else {
					Toast.makeText(StartActivity.this,
							"Please provide username or password!",
							Toast.LENGTH_SHORT);
				}
			}
		});
		
		Button facebookLoginButton = (Button) findViewById(R.id.facebookLoginButton);
		facebookLoginButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {				
				Session session = Session.getActiveSession();
		        if (!session.isOpened() && !session.isClosed()) {
		            session.openForRead(new Session.OpenRequest(StartActivity.this).setCallback(statusCallback));
		        } else {
		            Session.openActiveSession(StartActivity.this, true, statusCallback);
		        }				
			}			
		});
	}
	
	private void runFacebookLoginTask(final String apiId ,final String token) {
		final ProgressDialog pg = ProgressDialog.show(this, "Login","Processing...");
		
		AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(Void... params) {
				return XMPPConnectionManager.getInstance().facebookLogin(apiId, token);
			}

			protected void onPostExecute(Boolean result) {
				Log.d("a", result + "");
				pg.dismiss();

				if (result) {
					Intent intent = new Intent(StartActivity.this,
							RoasterActivity.class);
					StartActivity.this.startActivity(intent);
				} else {
					Toast.makeText(StartActivity.this,
							"Invalid username or password!", Toast.LENGTH_SHORT);
				}
			}
		};

		task.execute();
	}
	
	private void runLoginTask(final String username, final String password) {
		final ProgressDialog pg = ProgressDialog.show(this, "Login",
				"Signin as :" + username);

		AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(Void... params) {
				return XMPPConnectionManager.getInstance().login(username,
						password);
			}

			protected void onPostExecute(Boolean result) {
				Log.d("a", result + "");
				pg.dismiss();

				if (result) {
					Intent intent = new Intent(StartActivity.this,
							RoasterActivity.class);
					StartActivity.this.startActivity(intent);
				} else {
					Toast.makeText(StartActivity.this,
							"Invalid username or password!", Toast.LENGTH_SHORT);
				}
			}
		};

		task.execute();
	}
	
	private class SessionStatusCallback implements Session.StatusCallback {
		
        @Override
        public void call(Session session, SessionState state, Exception exception) {
        	
        	updateView();        	        	
        }
    }
	
	private String getGoogleAccount() {
		String googleAccount = null;

		Account[] accounts = AccountManager.get(getApplicationContext())
				.getAccounts();

		for (Account ac : accounts) {
			if (ac.type.equals("com.google")) {
				googleAccount = ac.name;
				Log.d("GCMConnectionManager", "Google account :"
						+ googleAccount);
			}

		}

		return googleAccount;
	}
}
