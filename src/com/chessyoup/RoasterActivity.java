package com.chessyoup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import com.chessyoup.server.RoomsManager;
import com.chessyoup.server.User;
import com.chessyoup.xmpp.XMPPConnectionManager;
import com.chessyoup.xmpp.XMPPListener;
import com.chessyoup.xmpp.XMPPMessage;
import com.chessyoup.xmpp.XMPPUser;

public class RoasterActivity extends Activity implements XMPPListener{
	ProgressDialog pg;
	
	AlertDialog chalangeDialog;
	
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
		XMPPConnectionManager.getInstance().logout();
	}

	@Override
	public void newEntriesAdded(Collection<String> jabberIds) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void entriesDeleted(Collection<String> jabberIds) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void entriesUpdated(Collection<String> jabberIds) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void presenceChanged(final String jabberId,final String status) {
		this.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				ListView listView = (ListView) findViewById(R.id.roasterUsers);
				for(int i = 0 ; i < listView.getCount(); i++){
					final XMPPUser user = (XMPPUser) listView.getAdapter().getItem(i);
					String parts[] = jabberId.split("@");
					
					if( user.getUsername().equals(parts[0]) ){					
						user.setJabberId(jabberId);
						user.setStatus(status);
						((ArrayAdapter<XMPPUser>)listView.getAdapter()).notifyDataSetChanged();
						break;
					}
				}				
			}
		});								
	}

	@Override
	public void messageReceived(String jabberId, XMPPMessage message) {
		// TODO Auto-generated method stub
		
	}
		
	private void initUI() {
		setContentView(R.layout.roaster);

		this.setTitle(XMPPConnectionManager.getInstance().getLoggedUser());
		
		ListView listView = (ListView) findViewById(R.id.roasterUsers);
		List<XMPPUser> users = new ArrayList<XMPPUser>();
		Roster roster = XMPPConnectionManager.getInstance().getRoster();
		
		for( RosterEntry entry : roster.getEntries() ){			
			users.add(new XMPPUser(entry.getUser(), entry.getStatus() != null ? entry.getStatus().toString() : "offline"));
		}
		
		ArrayAdapter<XMPPUser> adapter = new ArrayAdapter<XMPPUser>( RoasterActivity.this, android.R.layout.simple_list_item_1, users);
		listView.setAdapter(adapter);
	}
	
	private void installListeners() {		
		final ListView listView = (ListView) findViewById(R.id.roasterUsers);
	
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
//				final User selectedUser = (User) listView.getAdapter().getItem(
//						position);
//				
//				runSendChalangeTask(selectedUser);
			}			
		});

		ImageButton reloadButton = (ImageButton) findViewById(R.id.logoutButton);
		reloadButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				XMPPConnectionManager.getInstance().logout();
				RoasterActivity.this.finish();
			}
		});
		
		XMPPConnectionManager.getInstance().addXMPPListener(this);
	}


	private void launchChessboardActivity(User selectedUser,boolean isLocalWhite) {
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
		
		intent.putExtra("color", isLocalWhite ? "white" : "black");
		
		startActivity(intent);
	}
	
	
	private void runRejectChalangeTask(final User user) {
		AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
									
				return null;
			}
		};

		task.execute();				
	}
	
	private void runSendChalangeTask(final User selectedUser) {
		pg = ProgressDialog.show(this, "Action", "Chalange :"+selectedUser.getUsername(), true);
		pg.setCancelable(true);	
		
		AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				
				return null;
			}
		};

		task.execute();		
	}
	
	private void runAcceptChalangeTask(final User user) {
		
		AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {				
				launchChessboardActivity(user,false);
				return null;
			}
		};

		task.execute();						
	}
}
