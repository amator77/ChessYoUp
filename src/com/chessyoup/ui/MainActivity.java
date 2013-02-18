package com.chessyoup.ui;

import java.io.IOException;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.chessyoup.R;
import com.chessyoup.game.GameManager;
import com.chessyoup.ui.adapters.ChallengesAdapter;
import com.chessyoup.ui.adapters.MainViewPagerAdapter;
import com.chessyoup.ui.adapters.RosterAdapter;
import com.chessyoup.ui.fragments.FragmentChallenges;
import com.chessyoup.ui.fragments.FragmentRoom;
import com.chessyoup.ui.fragments.FragmentRoster;
import com.chessyoup.ui.fragments.MenuFragment;
import com.chessyoup.ui.fragments.RoomMenuFragment;
import com.cyp.accounts.Account;
import com.cyp.application.Application;
import com.cyp.chess.game.ChessGameController;
import com.cyp.game.IChallenge;
import com.cyp.game.IGameControllerListener;
import com.cyp.transport.Contact;
import com.cyp.transport.Presence;
import com.cyp.transport.RosterListener;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingFragmentActivity;

public class MainActivity extends SlidingFragmentActivity implements
		IGameControllerListener, RosterListener {

	private RosterAdapter rosterAdapter;

	private ChallengesAdapter chalangesAdapter;

	private FragmentChallenges fragmentChallenges;

	private FragmentRoster fragmentRoster;
	
	private FragmentRoom fragmentRoom;
	
	private ViewPager viewPager;

	private Account account;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("MainActivity", "on create");
		this.initUI();
		this.installListeners();
		this.account = Application.getContext().listAccounts().get(0);
		this.account.getGameController().addGameControllerListener(this);
		GameManager.getManager().addGameController(
				(ChessGameController) account.getGameController());
		this.account.getRoster().addListener(this);
		this.rosterAdapter.addAccount(this.account);
	}

	@Override
	public void onStart() {
		super.onStart();
		Log.d("MainActivity", "on start");
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.d("MainActivity", "on spause");
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d("MainActivity", "on resume");
		
		final ViewPager viewPager = (ViewPager) MainActivity.this
				.findViewById(R.id.mainViewPager);
		viewPager.setCurrentItem(0);
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.d("MainActivity", "on stop");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d("MainActivity", "on destroy");

		if (this.account != null) {
			this.account.logout();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			toggle();
			return true;
		case R.id.challangesMenuItem:
			viewPager.setCurrentItem(1);
			this.setTitle("Challanges");
			return true;
		case R.id.contactsMenuItem:
			viewPager.setCurrentItem(0);
			this.setTitle("Contacts");
			return true;
		case R.id.roomMenuItem:
			viewPager.setCurrentItem(2);
			this.setTitle("MainRoom");
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void challengeAccepted(final IChallenge arg0) {
		Log.d("challengeAccepted", arg0.toString());
		account.getGameController().startGame(arg0);

		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				chalangesAdapter.removeChallenge(arg0);
				Intent startChessActivityIntent = new Intent(MainActivity.this,
						ChessGameActivity.class);
				startChessActivityIntent.putExtra("remoteId", arg0
						.getRemoteContact().getId());
				startChessActivityIntent.putExtra("gameId", arg0.getTime());
				startActivity(startChessActivityIntent);
			}
		});
	}

	@Override
	public void challengeCanceled(final IChallenge arg0) {
		Log.d("challengeCanceled", arg0.toString());
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				chalangesAdapter.removeChallenge(arg0);
			}
		});
	}

	@Override
	public void challengeReceived(final IChallenge challenge) {
		Log.d("challengeReceived", challenge.toString());
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				chalangesAdapter.addChallenge(challenge);
				final ViewPager viewPager = (ViewPager) MainActivity.this
						.findViewById(R.id.mainViewPager);
				viewPager.setCurrentItem(1);
			}
		});
	}

	@Override
	public void challengeRejected(final IChallenge arg0) {
		Log.d("challengeRejected", arg0.toString());
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				chalangesAdapter.removeChallenge(arg0);
			}
		});
	}

	private void initUI() {
		this.setContentView(R.layout.main);
		this.setTitle("Contacts");
		setBehindContentView(R.layout.menu_frame);
		FragmentTransaction t = this.getSupportFragmentManager()
				.beginTransaction();
		MenuFragment mf = new MenuFragment();
		t.replace(R.id.menu_frame, mf);
		t.commit();

		// customize the SlidingMenu
		SlidingMenu sm = getSlidingMenu();
		sm.setMode(SlidingMenu.LEFT_RIGHT);
		sm.setShadowWidthRes(R.dimen.shadow_width);
		sm.setShadowDrawable(R.drawable.shadow);
		sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		sm.setFadeDegree(0.35f);
		setSlidingActionBarEnabled(false);		
		sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		
		sm.setSecondaryMenu(R.layout.room_menu_frame);
		sm.setSecondaryShadowDrawable(R.drawable.shadowright);
		getSupportFragmentManager().beginTransaction().replace(R.id.room_menu_frame, new RoomMenuFragment()).commit();
		
		this.rosterAdapter = new RosterAdapter(getApplicationContext());
		this.fragmentRoom = new FragmentRoom();
		this.chalangesAdapter = new ChallengesAdapter(getApplicationContext());
		this.fragmentChallenges = new FragmentChallenges(this.chalangesAdapter);
		this.fragmentRoster = new FragmentRoster(this.rosterAdapter);
		this.viewPager = (ViewPager) this.findViewById(R.id.mainViewPager);
		MainViewPagerAdapter fAdapter = new MainViewPagerAdapter(getSupportFragmentManager());
		fAdapter.addFragment(this.fragmentRoster);
		fAdapter.addFragment(this.fragmentChallenges);
		fAdapter.addFragment(this.fragmentRoom);
		viewPager.setAdapter(fAdapter);
				
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
	}

	private void installListeners() {		

		this.fragmentChallenges.setOnChallengeSelected(new Runnable() {

			@Override
			public void run() {
				showChallengeDialog(fragmentChallenges.getSelectedChallenge());
			}
		});

		this.fragmentRoster.setOnChallengeSelected(new Runnable() {

			@Override
			public void run() {
				Contact contact = fragmentRoster.getSelectedContact();

				if (contact.isCompatible()) {
					showSendChallengeDialog(contact);
				} else {
					showSendInviteDialog(contact);
				}
			}
		});

		viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageScrollStateChanged(int arg0) {
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageSelected(int position) {
				switch (position) {
				case 0:
					setTitle("Contacts");					
					break;
				case 1:
					setTitle("Challanges");
					break;
				case 2:
					setTitle("Main Room");
					break;
				default:					
					setTitle("Contacts");
					break;
				}
			}
		});
	}

	private void runAcceptChallengeTask(final IChallenge challenge) {
		AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				try {
					account.getGameController().acceptChallenge(challenge);
					account.getGameController().startGame(challenge);
					Intent startChessActivityIntent = new Intent(
							MainActivity.this, ChessGameActivity.class);
					startChessActivityIntent.putExtra("remoteId", challenge
							.getRemoteContact().getId());
					startChessActivityIntent.putExtra("gameId",
							challenge.getTime());
					startActivity(startChessActivityIntent);
				} catch (IOException e) {
					handleCommunicationError(e);
				}
				return null;
			}
		};

		task.execute();
	}

	private void runRejectChallengeTask(final IChallenge challenge) {
		AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				try {
					account.getGameController().rejectChallenge(challenge);
				} catch (IOException e) {
					handleCommunicationError(e);
				}
				return null;
			}
		};

		task.execute();
	}

	private void runAbortChallengeTask(final IChallenge challenge) {
		AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				try {
					account.getGameController().abortChallenge(challenge);
				} catch (IOException e) {
					handleCommunicationError(e);
				}
				return null;
			}
		};

		task.execute();
	}

	private void runSendChallengeTask(final Contact contact) {
		AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				try {
					final IChallenge newChallenge = account.getGameController()
							.sendChallenge(contact, null);

					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							chalangesAdapter.addChallenge(newChallenge);
							final ViewPager viewPager = (ViewPager) MainActivity.this
									.findViewById(R.id.mainViewPager);
							viewPager.setCurrentItem(1);
						}
					});

				} catch (IOException e) {
					handleCommunicationError(e);
				}
				return null;
			}
		};

		task.execute();
	}

	private void runSendInvitationTask(final Contact contact) {
		AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				// try {
				// account.sendInvitation(contact);
				// } catch (IOException e) {
				// handleCommunicationError(e);
				// }
				return null;
			}
		};

		task.execute();
	}

	private int getMenuWidth() {
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		float width = displaymetrics.widthPixels;
		Configuration config = getResources().getConfiguration();

		if (config.orientation == Configuration.ORIENTATION_PORTRAIT) {
			return (int) (width * 0.50);
		} else {
			return (int) (width * 0.75);
		}
	}

	@Override
	public void presenceChanged(Presence arg0) {
		Log.d("MainActivity", arg0.getContactId());

		if (rosterAdapter != null && fragmentRoster != null
				&& fragmentRoster.getRosterView() != null) {

			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					rosterAdapter.refresh();

					for (int i = 0; i < rosterAdapter.getGroupCount(); i++) {
						fragmentRoster.getRosterView().expandGroup(i);
					}
				}
			});
		}
	}

	@Override
	public void contactUpdated(Contact arg0) {
		Log.d("MainActivity", "contactUpdated :" + arg0.toString());

		if (rosterAdapter != null && fragmentRoster != null
				&& fragmentRoster.getRosterView() != null) {

			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					rosterAdapter.refresh();
					for (int i = 0; i < rosterAdapter.getGroupCount(); i++) {
						fragmentRoster.getRosterView().expandGroup(i);
					}
				}
			});
		}
	}

	@Override
	public void contactDisconected(Contact arg0) {
		Log.d("MainActivity", "contactDisconected :" + arg0.toString());
	}

	private void showChallengeDialog(final IChallenge challenge) {

		if (challenge.isReceived()) {
			AlertDialog.Builder db = new AlertDialog.Builder(MainActivity.this);
			db.setTitle("Challange");
			String actions[] = new String[3];
			actions[0] = "Accept";
			actions[1] = "Reject";
			actions[2] = "Dismiss";
			db.setItems(actions, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {

					if (challenge != null) {

						switch (which) {
						case 0:
							chalangesAdapter.removeChallenge(challenge);
							runAcceptChallengeTask(challenge);
							break;
						case 1:
							chalangesAdapter.removeChallenge(challenge);
							runRejectChallengeTask(challenge);
							break;
						case 2:
							dialog.dismiss();
							break;
						default:

							break;
						}
					}
				}
			});

			AlertDialog ad = db.create();
			ad.setCancelable(true);
			ad.setCanceledOnTouchOutside(false);
			ad.show();
		} else {
			AlertDialog.Builder db = new AlertDialog.Builder(MainActivity.this);
			db.setTitle("Challange");
			String actions[] = new String[2];
			actions[0] = "Abort";
			actions[1] = "Dismiss";
			db.setItems(actions, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {

					if (challenge != null) {

						switch (which) {
						case 0:
							chalangesAdapter.removeChallenge(challenge);
							runAbortChallengeTask(challenge);
							break;
						case 1:
							dialog.dismiss();
							break;
						default:

							break;
						}
					}
				}
			});

			AlertDialog ad = db.create();
			ad.setCancelable(true);
			ad.setCanceledOnTouchOutside(false);
			ad.show();
		}
	}

	private void showSendChallengeDialog(final Contact contact) {
		AlertDialog.Builder db = new AlertDialog.Builder(MainActivity.this);
		db.setTitle("Challange");
		String actions[] = new String[2];
		actions[0] = "Send Challenge";
		actions[1] = "Dismiss";
		db.setItems(actions, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case 0:
					runSendChallengeTask(contact);
					break;
				case 1:
					dialog.dismiss();
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
	}

	private void showSendInviteDialog(final Contact contact) {
		AlertDialog.Builder db = new AlertDialog.Builder(MainActivity.this);
		db.setTitle("Challange");
		String actions[] = new String[2];
		actions[0] = "Send Invitation";
		actions[1] = "Dismiss";
		db.setItems(actions, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case 0:
					runSendInvitationTask(contact);
					break;
				case 1:
					dialog.dismiss();
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
	}

	private void handleCommunicationError(IOException e) {
		Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
		e.printStackTrace();
	}
}
