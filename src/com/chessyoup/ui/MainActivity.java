package com.chessyoup.ui;

import java.io.IOException;

import org.jivesoftware.smackx.packet.VCard;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.chessyoup.R;
import com.chessyoup.game.GameManager;
import com.chessyoup.ui.adapters.ChallengesAdapter;
import com.chessyoup.ui.adapters.MainViewPagerAdapter;
import com.chessyoup.ui.adapters.RosterAdapter;
import com.chessyoup.ui.fragments.FragmentChallenges;
import com.chessyoup.ui.fragments.FragmentMainMenu;
import com.chessyoup.ui.fragments.FragmentRoster;
import com.cyp.accounts.Account;
import com.cyp.application.Application;
import com.cyp.chess.game.ChessGameController;
import com.cyp.game.IChallenge;
import com.cyp.game.IGameControllerListener;
import com.cyp.transport.Contact;
import com.cyp.transport.Presence;
import com.cyp.transport.RosterListener;
import com.korovyansk.android.slideout.SlideoutActivity;

public class MainActivity extends FragmentActivity implements
		IGameControllerListener, RosterListener {

	private RosterAdapter rosterAdapter;

	private ChallengesAdapter chalangesAdapter;

	private FragmentChallenges fragmentChallenges;

	private FragmentRoster fragmentRoster;

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

		if (UIActionRegister.action.contains("Accounts")) {
			WebView webview = new WebView(this);
			PopupWindow popup = new PopupWindow(webview, 300, 400);
			popup.showAtLocation(findViewById(R.id.contactsButton),
					Gravity.CENTER, 0, 0);
			webview.loadUrl("http://chessbase.com");
			UIActionRegister.action = "";
		}
		
		final ViewPager viewPager = (ViewPager) MainActivity.this.findViewById(R.id.mainViewPager);
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
		
		if( this.account != null ){
			this.account.logout();
		}
	}

	@Override
	public void challengeAccepted(final IChallenge arg0) {
		Log.d("challengeAccepted", arg0.toString());
		account.getGameController().startGame(arg0);
		
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				chalangesAdapter.removeChallenge(arg0);
				Intent startChessActivityIntent = new Intent(MainActivity.this,ChessGameActivity.class);
				startChessActivityIntent.putExtra("remoteId", arg0.getRemoteContact().getId());
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
				final ViewPager viewPager = (ViewPager) MainActivity.this.findViewById(R.id.mainViewPager);
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
		this.rosterAdapter = new RosterAdapter(getApplicationContext());
		this.chalangesAdapter = new ChallengesAdapter(getApplicationContext());
		this.fragmentChallenges = new FragmentChallenges(this.chalangesAdapter);
		this.fragmentRoster = new FragmentRoster(this.rosterAdapter);
		ViewPager viewPager = (ViewPager) this.findViewById(R.id.mainViewPager);
		viewPager.setAdapter(new MainViewPagerAdapter(
				getSupportFragmentManager(), this.fragmentRoster,
				this.fragmentChallenges));
	}

	private void installListeners() {

		findViewById(R.id.menuButton).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						int width = getMenuWidth();
						SlideoutActivity.prepare(MainActivity.this,
								R.id.inner_content, width);
						startActivity(new Intent(MainActivity.this,
								FragmentMainMenu.class));
						overridePendingTransition(0, 0);
					}
				});

		final ViewPager viewPager = (ViewPager) this
				.findViewById(R.id.mainViewPager);

		findViewById(R.id.contactsButton).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						viewPager.setCurrentItem(0);
					}
				});

		findViewById(R.id.chalangesButton).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						viewPager.setCurrentItem(1);
					}
				});

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
				
				if( contact.isCompatible() ){
					showSendChallengeDialog(contact);
				}
				else{
					showSendInviteDialog(contact);
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
							MainActivity.this,
							ChessGameActivity.class);
					startChessActivityIntent.putExtra("remoteId",
							challenge.getRemoteContact().getId());
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
					final IChallenge newChallenge =  account.getGameController().sendChallenge(contact, null);
					
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							chalangesAdapter.addChallenge(newChallenge);
							final ViewPager viewPager = (ViewPager) MainActivity.this.findViewById(R.id.mainViewPager);
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
//				try {					
//					account.sendInvitation(contact);
//				} catch (IOException e) {
//					handleCommunicationError(e);					
//				}
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

	@Override
	public void contactUpdated(Contact arg0) {
		Log.d("MainActivity", "contactUpdated :" + arg0.toString());

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

	@Override
	public void contactDisconected(Contact arg0) {
		Log.d("MainActivity", "contactDisconected :" + arg0.toString());
	}
	
	private void showChallengeDialog(final IChallenge challenge) {
		
		if( challenge.isReceived() )
		{
			AlertDialog.Builder db = new AlertDialog.Builder(
					MainActivity.this);
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
		}
		else{
			AlertDialog.Builder db = new AlertDialog.Builder(
					MainActivity.this);
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
		AlertDialog.Builder db = new AlertDialog.Builder(
				MainActivity.this);
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
		AlertDialog.Builder db = new AlertDialog.Builder(
				MainActivity.this);
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
