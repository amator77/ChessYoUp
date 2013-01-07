package com.chessyoup;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

public class StartActivity extends Activity{
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("RoomActivity", "on create");
		this.initUI();
		
		AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {				
					e.printStackTrace();
				}
				
				Intent intent = new Intent(StartActivity.this,
						RoomActivity.class);				
				startActivity(intent);
				
				return null;
			}
		};

		task.execute();
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
	
	private void initUI(){
		setContentView(R.layout.start);		
		Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.light);
	    BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(),bmp);
	    bitmapDrawable.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);	    
	    this.findViewById(R.id.startLayout).setBackgroundDrawable(bitmapDrawable);
	}
}
