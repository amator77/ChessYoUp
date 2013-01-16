package com.chessyoup;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.chessyoup.xmpp.XMPPConnectionManager;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.widget.LoginButton;

public class StartActivity extends Activity {
	SessionStatusCallback statusCallback = new SessionStatusCallback();
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("RoomActivity", "on create");
		this.initUI();
		this.installListeners();
//		
//		Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
//
//		
//		
//        Session session = Session.getActiveSession();
//        if (session == null) {
//            if (savedInstanceState != null) {
//                session = Session.restoreSession(this, null, statusCallback, savedInstanceState);
//            }
//            if (session == null) {
//                session = new Session(this);                
//            }
//            Session.setActiveSession(session);
//            if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
//                session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));
//            }
//        }        
//
//        updateView();
	}

	private void updateView() {
		
		Session ssession = Session.getActiveSession();
    	
    	if (ssession.isOpened()) {
    		try {
				this.runFacebookLoginTask(ssession.getApplicationId(),ssession.getAccessToken());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		
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
		
		LoginButton authButton = (LoginButton)findViewById(R.id.facebookLoginButton);
		List<String> permisions = new ArrayList<String>();
		permisions.add("xmpp_login");
		permisions.add("publish_strea");			
		authButton.setReadPermissions(permisions);
		
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
		
		Button gtalkLoginButton = (Button) findViewById(R.id.gtalkLoginButton);
		
		gtalkLoginButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {				
				AccountManager am = AccountManager.get( StartActivity.this);
				Account[] accounts =  am.getAccounts();
				Account googleAccount = null;
				for(Account ac:accounts ){
					if(ac.type.equals("com.google")){
						googleAccount = ac;
						Log.d("acc", ac.name +" , "+ac.type+" , ");
						break;
					}					
				}
				
				if( googleAccount != null ){
					
					am.getAuthToken (googleAccount, "https://www.googleapis.com/auth/googletalk", new Bundle(),StartActivity.this, new OnTokenAcquired(),new Handler(new Handler.Callback() {
						
						@Override
						public boolean handleMessage(Message msg) {
							Log.d("eerro on auth",msg.toString());
							return false;
						}
					}));															
				}
			}			
		});
	}
	
	private class OnTokenAcquired implements AccountManagerCallback<Bundle> {
	    @Override
	    public void run(AccountManagerFuture<Bundle> result) {
	        // Get the result of the operation from the AccountManagerFuture.
	    	
	        Bundle bundle;
			try {
				bundle = result.getResult(); 
		        Log.d("google token ", bundle.getString(AccountManager.KEY_AUTHTOKEN));
			} catch (OperationCanceledException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (AuthenticatorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	}
	
	private void runFacebookLoginTask(final String apiId ,final String token) throws IOException {
		final ProgressDialog pg = ProgressDialog.show(this, "Login","Processing...");		
		
		AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(Void... params) {
				AssetManager manager = StartActivity.this.getAssets();				
				String path = "";
				try {
					InputStream fis =  manager.open("cacerts.bks");
					File f = File.createTempFile("cacerts", "bks");
					byte[] buffer = new byte[1024];
					FileOutputStream fos = new FileOutputStream(f);
					int read = 0;
					
					while( (read = fis.read(buffer)) != -1 ){
						fos.write(buffer, 0, read);
					}
					
					fis.close();
					fos.close();
					path = f.getPath();
				} catch (IOException e) {
					e.printStackTrace();
				}					
				
				return XMPPConnectionManager.getInstance().facebookLogin(apiId, "AAAGOpd0PegYBAMw4C6Am0XUYkx6RJ3jf5cPqXYLwNiEnjgpa2d3xurZAasbmSVd5mkZBTWBaMSJFuhKbnrNYf3zRvJdYKZCP00MIkynacxRfVpaiPbD",path);
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
