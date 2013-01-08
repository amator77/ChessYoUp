package com.chessyoup;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.chessyoup.chat.GCMChatActivity;
import com.chessyoup.connector.Device;
import com.chessyoup.server.Room;
import com.chessyoup.server.RoomListener;
import com.chessyoup.server.RoomsManager;
import com.chessyoup.server.User;

public class RoomActivity extends Activity implements RoomListener {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("RoomActivity", "on create");
		this.initUI();
		this.installListeners();
		this.runReloadUsersTask();
	}

	private void initUI() {
		setContentView(R.layout.room);

		for (Room room : RoomsManager.getManager().getRooms()) {
			if (room.getId()
					.equals(getIntent().getExtras().getString("roomId"))) {
				StringBuffer title = new StringBuffer(room.getName());
				title.append("::").append(
						deviceLabel(RoomsManager.getManager()
								.getConnectionManager().getLocalDevice()));

				this.setTitle(title.toString());
			}
		}
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
		RoomsManager.getManager(this).leaveRoom();
	}

	@Override
	public void roomJoined(final Room sourceRoom, boolean status) {
	}

	@Override
	public void roomLeaved(Room sourceRoom) {
		Log.d("RoomActivity", "roomLeaved :" + sourceRoom.getName());
	}

	@Override
	public void usersReceived(final List<User> users) {
		Log.d("RoomActivity", "usersReceived :" + users);

		this.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				ListView listView = (ListView) findViewById(R.id.room_users);

				ArrayAdapter<User> adapter = new ArrayAdapter<User>(
						RoomActivity.this, android.R.layout.simple_list_item_1,
						users);
				listView.setAdapter(adapter);
			}
		});
	}

	private void runReloadUsersTask() {
		AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				RoomsManager.getManager(
						RoomActivity.this.getApplicationContext()).loadUsers();

				return null;
			}
		};

		task.execute();

	}

	private void installListeners() {
		RoomsManager.getManager().setRoomListener(this);
		final ListView listView = (ListView) findViewById(R.id.room_users);
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {

				final User selectedUser = (User) listView.getAdapter().getItem(
						position);
				Log.d("RoomActivity", selectedUser.toString());
				AlertDialog.Builder db = new AlertDialog.Builder(
						RoomActivity.this);
				db.setTitle("Action");

				db.setItems(R.array.action_array,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								switch (which) {
								case 0:
									launchChessboardActivity(selectedUser);
									break;
								case 1:
									launchChatActivity(selectedUser);
									break;

								default:
									break;
								}
							}
						});

				AlertDialog ad = db.create();
				ad.setCancelable(true);
				ad.setCanceledOnTouchOutside(true);
				ad.show();

				return true;
			}
		});

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				final User selectedUser = (User) listView.getAdapter().getItem(
						position);
				launchChessboardActivity(selectedUser);
			}
		});

		Button reloadButton = (Button) findViewById(R.id.reload_room_users);
		reloadButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				runReloadUsersTask();
			}
		});
	}

	public String deviceLabel(Device device) {
		StringBuffer sb = new StringBuffer();
		if (device.getAccount() != null && !device.getAccount().equals("null")) {
			sb.append(device.getAccount());
		} else if (device.getDevicePhoneNumber() != null
				&& !device.getDevicePhoneNumber().equals("null")) {
			sb.append(device.getDevicePhoneNumber());
		} else {
			sb.append(device.getDeviceIdentifier());
		}

		return sb.toString();
	}

	@Override
	public void chalangeReceived(final User user) {

		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				AlertDialog.Builder db = new AlertDialog.Builder(
						RoomActivity.this);
				db.setTitle("Chalange from :");
				String actions[] = new String[2];
				actions[0] = "OK";
				actions[1] = "Reject";
				db.setItems(actions, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:							
							runAcceptChalangeTask(user);
							break;
						case 1:
							runRejectChalangeTask(user);
							break;
						default:
							runRejectChalangeTask(user);
							break;
						}
					}					
				});

				AlertDialog ad = db.create();
				ad.setCancelable(true);
				ad.setCanceledOnTouchOutside(true);
				ad.show();
			}
		});
	}

	private void launchChatActivity(User selectedUser) {
		Intent intent = new Intent(this, GCMChatActivity.class);
		intent.putExtra("remote_device_id", selectedUser.getDevice()
				.getDeviceIdentifier());
		intent.putExtra("remote_phone_number", selectedUser.getDevice()
				.getDevicePhoneNumber());
		intent.putExtra("remote_gcm_registration_id", selectedUser.getDevice()
				.getRegistrationId());
		intent.putExtra("remote_account", selectedUser.getDevice().getAccount());

		intent.putExtra("owner_account", RoomsManager.getManager(this)
				.getConnectionManager().getLocalDevice().getAccount());
		startActivity(intent);
	}

	private void launchChessboardActivity(User selectedUser,boolean connected) {
		Intent intent = new Intent(this, ChessboardActivity.class);
		intent.putExtra("remote_device_id", selectedUser.getDevice()
				.getDeviceIdentifier());
		intent.putExtra("remote_phone_number", selectedUser.getDevice()
				.getDevicePhoneNumber());
		intent.putExtra("remote_gcm_registration_id", selectedUser.getDevice()
				.getRegistrationId());
		intent.putExtra("remote_account", selectedUser.getDevice().getAccount());

		intent.putExtra("owner_account", RoomsManager.getManager(this)
				.getConnectionManager().getLocalDevice().getAccount());
		
		intent.putExtra("connected", connected ? "true" : "false");
		
		startActivity(intent);
	}
	
	private void launchChessboardActivity(User selectedUser) {
		launchChessboardActivity(selectedUser,false);
	}
	
	private void runRejectChalangeTask(User user) {

		
	}

	private void runAcceptChalangeTask(final User user) {
		
		AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				RoomsManager.getManager().getConnectionManager().acceptConnection(user.getDevice());
				launchChessboardActivity(user,true);
				return null;
			}
		};

		task.execute();						
	}
}
