package com.chessyoup;

import java.util.Collection;

import org.jivesoftware.smack.packet.Message;

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
import android.widget.ImageButton;
import android.widget.Spinner;

import com.chessyoup.transport.Presence.MODE;
import com.chessyoup.transport.xmpp.UIListener;
import com.chessyoup.transport.xmpp.XMPPConnectionManager;
import com.chessyoup.transport.xmpp.XMPPContact;
import com.chessyoup.transport.xmpp.XMPPGameController;
import com.chessyoup.transport.xmpp.XMPPListener;

public class RoasterActivity extends Activity implements XMPPListener,
		UIListener {
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

	private void initUI() {
//		setContentView(R.layout.roaster);
//		this.setTitle(XMPPConnectionManager.getInstance().getLoggedUser());
//
//		ListView listView = (ListView) findViewById(R.id.roasterUsers);
//		List<XMPPContact> users = new ArrayList<XMPPContact>();
//		Roster roster = XMPPConnectionManager.getInstance().getRoster();
//
//		for (RosterEntry entry : roster.getEntries()) {
//			users.add(new XMPPContact(entry.getUser(), entry.getName()));
//		}
//
//		RosterModel rosterModel = new RosterModel();
//		rosterModel.setContactsList(users);
//		RosterAdapter rosterAdapter = new RosterAdapter(this, rosterModel);
//		listView.setAdapter(rosterAdapter);
//
//		Spinner spinner = (Spinner) findViewById(R.id.accountStatus);
//		AccountStatusAdapter adapter = new AccountStatusAdapter(this,
//				new AccountStatusModel(XMPPConnectionManager.getInstance()
//						.getLoggedUser(), "online"));
//		spinner.setAdapter(adapter);
	}

	private void installListeners() {
//		final ListView listView = (ListView) findViewById(R.id.roasterUsers);
//
//		listView.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view,
//					int position, long id) {
//				final XMPPContact user = (XMPPContact) listView.getAdapter()
//						.getItem(position);
//
//				if (true) {
//
//					Intent startXMPPChessboard = new Intent(
//							RoasterActivity.this, XMPPChessBoardActivity.class);
//					startXMPPChessboard
//							.putExtra("ownerJID", XMPPConnectionManager
//									.getInstance().getLoggedUser());
//					startXMPPChessboard.putExtra("remoteJID", user.getId());
//					startXMPPChessboard.putExtra("color", "white");
//					startActivity(startXMPPChessboard);
//
//				} else {
//					AlertDialog.Builder db = new AlertDialog.Builder(
//							RoasterActivity.this);
//					db.setTitle("Not an ChessYoUp client!");
//					String actions[] = new String[2];
//					actions[0] = "Send invite?";
//					actions[1] = "Cancel";
//					db.setItems(actions, new DialogInterface.OnClickListener() {
//
//						@Override
//						public void onClick(DialogInterface dialog, int which) {
//							switch (which) {
//							case 0:
//								runSendInviteask(user);
//								break;
//							case 1:
//								break;
//							default:
//
//								break;
//							}
//						}
//					});
//
//					AlertDialog ad = db.create();
//					ad.setCancelable(true);
//					ad.setCanceledOnTouchOutside(false);
//					ad.show();
//				}
//			}
//		});

		final Spinner spinner = (Spinner) findViewById(R.id.accountStatus);
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parentView,
					View selectedItemView, int position, long id) {
				MODE status = (MODE) spinner.getItemAtPosition(position);
				// XMPPConnectionManager.getInstance().setPresence(status);
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

	private void runSendInviteask(final XMPPContact user) {

		AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				StringBuilder is = new StringBuilder(
						"Hello!\n Please join ChessYoUp by downloading android apk from  http://chessyoup.com/chessyoup.apk \n Thanks. ");
				Message m = new Message();
				m.setTo(user.getId());
				m.setBody(is.toString());
				XMPPConnectionManager.getInstance().getXmppConnection()
						.sendPacket(m);

				return null;
			}
		};

		task.execute();

	}

	@Override
	public void gameStartRequest(final String from, final String whitePlayer,
			final String blackPlayer) {

		this.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Log.d("RoasterActivity", "Start chessboard activity.");
				Intent startXMPPChessboard = new Intent(RoasterActivity.this,
						XMPPChessBoardActivity.class);
				startXMPPChessboard.putExtra("ownerJID", XMPPConnectionManager
						.getInstance().getLoggedUser());
				startXMPPChessboard.putExtra("remoteJID", from);
				startXMPPChessboard.putExtra("color",
						from.equals(whitePlayer.toString()) ? "white" : "black");
				startXMPPChessboard.putExtra("autostart", "true");
				startActivity(startXMPPChessboard);
			}
		});
	}
}
