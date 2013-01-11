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

import com.chessyoup.server.RoomsManager;
import com.chessyoup.xmpp.XMPPConnectionManager;

public class StartActivity extends Activity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("RoomActivity", "on create");
		this.initUI();
		this.installListeners();
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
		accountEditText.setText(googleAccount != null ? googleAccount : "");
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
				Log.d("a", result+"");
				pg.dismiss();

				if (result) {
					Intent intent = new Intent(StartActivity.this, RoasterActivity.class);										
					StartActivity.this.startActivity(intent);										
				} else {
					Toast.makeText(StartActivity.this,
							"Invalid username or password!", Toast.LENGTH_SHORT);
				}
			}
		};

		task.execute();
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
