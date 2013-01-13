package com.chessyoup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.packet.Message;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;

import com.chessyoup.view.adapters.AccountStatusAdapter;
import com.chessyoup.view.adapters.AccountStatusModel;
import com.chessyoup.view.adapters.RosterAdapter;
import com.chessyoup.view.adapters.RosterModel;
import com.chessyoup.xmpp.UIListener;
import com.chessyoup.xmpp.XMPPConnectionManager;
import com.chessyoup.xmpp.XMPPGameController;
import com.chessyoup.xmpp.XMPPListener;
import com.chessyoup.xmpp.XMPPStatus;
import com.chessyoup.xmpp.XMPPStatus.MODE;
import com.chessyoup.xmpp.XMPPUser;

public class RoasterActivity extends Activity implements XMPPListener,UIListener {
	ProgressDialog pg;

	AlertDialog chalangeDialog;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("RoomActivity", "on create");
		this.initUI();
		this.installListeners();
		XMPPGameController.getController().setUiListener(this);
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
	public void presenceChanged(final String jabberId, final XMPPStatus status) {
		this.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				ListView listView = (ListView) findViewById(R.id.roasterUsers);
				for (int i = 0; i < listView.getCount(); i++) {
					final XMPPUser user = (XMPPUser) listView.getAdapter()
							.getItem(i);
					String parts[] = jabberId.split("@");

					if (user.getUsername().equals(parts[0])) {
						user.setJabberId(jabberId);
						user.setStatus(status);
						Log.d("RoasterActivity",
								"Status changed for :" + user.toString());
						((RosterAdapter) listView.getAdapter())
								.notifyDataSetChanged();
						break;
					}
				}
			}
		});
	}

	private void initUI() {
		setContentView(R.layout.roaster);
		this.setTitle(XMPPConnectionManager.getInstance().getLoggedUser());

		ListView listView = (ListView) findViewById(R.id.roasterUsers);
		List<XMPPUser> users = new ArrayList<XMPPUser>();
		Roster roster = XMPPConnectionManager.getInstance().getRoster();

		for (RosterEntry entry : roster.getEntries()) {
			users.add(new XMPPUser(entry.getUser(), new XMPPStatus()));
		}

		RosterModel rosterModel = new RosterModel();
		rosterModel.setContactsList(users);
		RosterAdapter rosterAdapter = new RosterAdapter(this, rosterModel);
		listView.setAdapter(rosterAdapter);

		Spinner spinner = (Spinner) findViewById(R.id.accountStatus);
		AccountStatusAdapter adapter = new AccountStatusAdapter(this,
				new AccountStatusModel(XMPPConnectionManager.getInstance()
						.getLoggedUser(), "online"));
		spinner.setAdapter(adapter);
	}

	private void installListeners() {
		final ListView listView = (ListView) findViewById(R.id.roasterUsers);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				final XMPPUser user = (XMPPUser) listView.getAdapter().getItem(
						position);

				if (user.isChessYoUpUser()) {

					Intent startXMPPChessboard = new Intent(
							RoasterActivity.this, XMPPChessBoardActivity.class);
					startXMPPChessboard
							.putExtra("ownerJID", XMPPConnectionManager
									.getInstance().getLoggedUser());
					startXMPPChessboard.putExtra("remoteJID",
							user.getJabberId());
					startXMPPChessboard.putExtra("color", "white");
					startActivity(startXMPPChessboard);

				} else {
					AlertDialog.Builder db = new AlertDialog.Builder(
							RoasterActivity.this);
					db.setTitle("Not an ChessYoUp client!");
					String actions[] = new String[2];
					actions[0] = "Send invite?";
					actions[1] = "Cancel";
					db.setItems(actions, new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							switch (which) {
							case 0:
								runSendInviteask(user);
								break;
							case 1:
								break;
							default:

								break;
							}
						}
					});

					AlertDialog ad = db.create();
					ad.setCancelable(true);
					ad.setCanceledOnTouchOutside(false);
					ad.show();
				}
			}
		});

		final Spinner spinner = (Spinner) findViewById(R.id.accountStatus);
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parentView,
					View selectedItemView, int position, long id) {
				MODE status = (MODE) spinner.getItemAtPosition(position);
				XMPPConnectionManager.getInstance().setPresence(status);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

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

	private void runSendInviteask(final XMPPUser user) {

		AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				StringBuilder is = new StringBuilder(
						"Hello!\n Please join ChessYoUp by downloading android apk from  http://chessyoup.com/chessyoup.apk \n Thanks. ");
				Message m = new Message();
				m.setTo(user.getJabberId());
				m.setBody(is.toString());
				XMPPConnectionManager.getInstance().getXmppConnection()
						.sendPacket(m);

				return null;
			}
		};

		task.execute();

	}

	@Override
	public void gameStartRequest(final String from,final  String whitePlayer,
			final String blackPlayer) {
		
		this.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				Log.d("RoasterActivity", "Start chessboard activity.");
				Intent startXMPPChessboard = new Intent(RoasterActivity.this,
						XMPPChessBoardActivity.class);
				startXMPPChessboard
						.putExtra("ownerJID", XMPPConnectionManager
								.getInstance().getLoggedUser());
				startXMPPChessboard.putExtra("remoteJID", from);
				startXMPPChessboard.putExtra("color", from.equals(whitePlayer.toString()) ? "white" : "black");
				startXMPPChessboard.putExtra("autostart", "true");
				startActivity(startXMPPChessboard);
			}
		});		
	}
}
