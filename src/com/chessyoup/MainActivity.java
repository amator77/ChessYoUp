package com.chessyoup;

import java.io.IOException;
import java.util.Properties;

import android.app.Activity;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.chessyoup.connector.ConnectionManager;
import com.chessyoup.connector.ConnectionManagerListener;
import com.google.android.gcm.GCMRegistrar;

public class MainActivity extends Activity implements ConnectionManagerListener  {
	
	private ConnectionManager connManager;
	
	private Handler handler;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.handler = new Handler();
		Log.d("MainActivity", "on create");
		setContentView(R.layout.main);
		final TextView display = (TextView) findViewById(R.id.display);
		display.append("Device status :"+GCMRegistrar.isRegistered(getApplicationContext()));
		AssetManager manager = this.getAssets();
		Properties p = new Properties();
		
		try {
			p.load(manager.open("chessyoup.properties"));
		} catch (IOException e) {
			Log.e("MainActivity", "Error on reading configuration!");			
			System.exit(1);
		}				
						
		connManager = ConnectionManagerFactory.getFactory().getGCMConnectionManager(new ChessYoUpRemoteService(p.getProperty("chessyoup_url")), this.getApplicationContext());
		connManager.registerListener(this);
		final Button registerButton = (Button) findViewById(R.id.register);
		final Button unRegisterButton = (Button) findViewById(R.id.unregister);
		final Button sendButton = (Button) findViewById(R.id.send);
		
		registerButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.d("MainActivity", "register onClick event");
				
				AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

					@Override
					protected Void doInBackground(Void... params) {
						connManager.initialize();						
						return null;
					}
				};
				
				task.execute();				
			}
			
		});
		
		unRegisterButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.d("MainActivity", "unRegisterButton onClick event");
				
				AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

					@Override
					protected Void doInBackground(Void... params) {
						connManager.dispose();						
						return null;
					}					
				};
				
				task.execute();						
			}
		});
		
		sendButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.d("MainActivity", "sendButton onClick event");				
				
			}
		});
		
		
	}

	@Override
	public void onInitialize(final boolean status) {
		this.handler.post(new Runnable() {
			
			@Override
			public void run() {
				TextView display = (TextView) findViewById(R.id.display);
				display.append("onInitialize :"+status+"\n");
				
				if( status ){
					display.append("Device  :"+connManager.getDevice().toString()+"\n");
				}
			}
		});
	}

	@Override
	public void onDispose(final boolean status) {
		this.handler.post(new Runnable() {
			
			@Override
			public void run() {
				TextView display = (TextView) findViewById(R.id.display);
				display.append("onDispose :"+status+"\n");	
			}
		});			
	}
}
