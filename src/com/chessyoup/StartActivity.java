package com.chessyoup;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.chessyoup.server.Room;
import com.chessyoup.server.RoomListener;
import com.chessyoup.server.RoomsManager;
import com.chessyoup.server.User;

public class StartActivity extends Activity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("RoomActivity", "on create");
		this.initUI();
		this.runInitTask();
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
		Bitmap bmp = BitmapFactory.decodeResource(getResources(),
				R.drawable.light);
		BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), bmp);
		bitmapDrawable.setTileModeXY(Shader.TileMode.REPEAT,
				Shader.TileMode.REPEAT);
		this.findViewById(R.id.startLayout).setBackgroundDrawable(
				bitmapDrawable);
	}

	private void runInitTask() {
		AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				List<Room> rooms = RoomsManager.getManager(
						StartActivity.this.getApplicationContext()).getRooms();
				Log.d("StartActivity", "Rooms:" + rooms.toString());

				RoomsManager.getManager(
						StartActivity.this.getApplicationContext())
						.setRoomListener(new RoomListener() {

							@Override
							public void usersReceived(List<User> users) { 
							}

							@Override
							public void roomLeaved(Room sourceRoom) {
							}

							@Override
							public void roomJoined(final Room sourceRoom,
									boolean status) {
								if (status) {
									StartActivity.this
											.runOnUiThread(new Runnable() {

												@Override
												public void run() {
													Intent intent = new Intent(
															StartActivity.this,
															RoomActivity.class);
													intent.putExtra("roomId",
															sourceRoom.getId());
													startActivity(intent);
													StartActivity.this.finish();
												}
											});
								} else {
									AlertDialog.Builder db = new AlertDialog.Builder(
											StartActivity.this);
									db.setTitle("Error");
									String actions[] = new String[1];
									actions[0] = "OK";
									db.setItems(
											actions,
											new DialogInterface.OnClickListener() {

												@Override
												public void onClick(
														DialogInterface dialog,
														int which) {
													StartActivity.this.finish();
												}
											});

									AlertDialog ad = db.create();
									ad.setCancelable(true);
									ad.setCanceledOnTouchOutside(true);
									ad.show();
								}

							}
						});

				for (Room room : rooms) {
					if (room.getJoinedUsers() < room.getSize()) {
						Log.d("StartActivity", "Join on room :room");
						RoomsManager.getManager(
								StartActivity.this.getApplicationContext())
								.joinRoom(room);
						break;
					}
				}

				return null;
			}
		};

		task.execute();
	}
}
