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
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.chessyoup.chat.GCMChatActivity;
import com.chessyoup.server.Room;
import com.chessyoup.server.RoomListener;
import com.chessyoup.server.RoomsManager;
import com.chessyoup.server.User;

public class RoomActivity extends Activity implements RoomListener {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("RoomActivity", "on create");
		setContentView(R.layout.room);
		this.installListeners();
		this.runLoadRoomsTask();
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
		Log.d("RoomActivity", "Joined with sattus :" + status);
		this.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				TextView view = (TextView) findViewById(R.id.devices_label);
				view.setText("Users (" + sourceRoom.getName() + ")");
				Button joinButton = (Button) findViewById(R.id.join_room);
				joinButton.setText("Leave");
			}
		});
	}

	@Override
	public void roomLeaved(Room sourceRoom) {
		Log.d("RoomActivity", "roomLeaved :" + sourceRoom.getName());
		this.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				TextView view = (TextView) findViewById(R.id.devices_label);
				view.setText("Users (Not joined)");
				Button joinButton = (Button) findViewById(R.id.join_room);
				joinButton.setText("Join");
				ListView listView = (ListView) findViewById(R.id.room_users);
				ArrayAdapter<User> adapter = (ArrayAdapter<User>) listView
						.getAdapter();
				adapter.clear();
				adapter.notifyDataSetChanged();
			}
		});
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

	private void runLoadRoomsTask() {
		AsyncTask<Void, Void, List<Room>> task = new AsyncTask<Void, Void, List<Room>>() {

			@Override
			protected List<Room> doInBackground(Void... params) {
				return RoomsManager.getManager(
						RoomActivity.this.getApplicationContext()).getRooms();
			}

			protected void onPostExecute(final List<Room> result) {
				if (result != null) {
					Spinner spinner = (Spinner) findViewById(R.id.rooms_spinner);
					ArrayAdapter<Room> adapter = new ArrayAdapter<Room>(
							RoomActivity.this,
							android.R.layout.simple_spinner_item, result);
					adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					spinner.setAdapter(adapter);
				}
			}
		};

		task.execute();

	}
	
	private void runJoinRoomTask(final Room room,final RoomListener listener) {
		AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				RoomsManager.getManager(
						RoomActivity.this.getApplicationContext())
						.joinRoom(room,listener);
				
				return null;
			}
		};

		task.execute();

	}
	
	private void runReloadUsersTask() {
		AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				RoomsManager.getManager(
						RoomActivity.this.getApplicationContext())
						.loadUsers();
				
				return null;
			}
		};

		task.execute();

	}
	
	private void runLeaveRoomTask() {
		AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				RoomsManager.getManager(
						RoomActivity.this.getApplicationContext())
						.leaveRoom();
				
				return null;
			}
		};

		task.execute();

	}
	
	private void installListeners() {

		final Button joinButton = (Button) findViewById(R.id.join_room);

		joinButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (RoomsManager.getManager(
						RoomActivity.this.getApplicationContext())
						.getJoinedRoom() == null) {

					Spinner spinner = (Spinner) findViewById(R.id.rooms_spinner);

					if (spinner.getSelectedItem() != null) {
						
						runJoinRoomTask((Room) spinner.getSelectedItem(),
								RoomActivity.this);												
					}
				} else {
					runLeaveRoomTask();					
				}
			}
		});

		final ListView listView = (ListView) findViewById(R.id.room_users);
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				final User selectedUser =  (User)listView.getAdapter().getItem(position);
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
									Log.d("RoomActivity", "Not implemnetd");
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
		
		Button reloadButton = (Button)findViewById(R.id.reload_room_users);
		reloadButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				runReloadUsersTask();				
			}
		});
	}
	
	private void launchChatActivity(User selectedUser) {
		Intent intent = new Intent(this,
				GCMChatActivity.class);
		intent.putExtra("remote_device_id",
				selectedUser.getDevice().getDeviceIdentifier());
		intent.putExtra("remote_phone_number",
				selectedUser.getDevice().getDevicePhoneNumber());
		intent.putExtra("remote_gcm_registration_id",
				selectedUser.getDevice().getRegistrationId());
		intent.putExtra("remote_account", selectedUser.getDevice().getAccount());

		intent.putExtra("owner_account", RoomsManager.getManager(this).getConnectionManager().getLocalDevice().getAccount());
		startActivity(intent);
	}
}
