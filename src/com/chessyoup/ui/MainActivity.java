package com.chessyoup.ui;

import java.io.IOException;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import com.chessyoup.R;
import com.chessyoup.account.ABasicGTalkAccount;
import com.chessyoup.game.GameManager;
import com.chessyoup.store.SharedPreferencesCredentialStore;
import com.chessyoup.ui.adapters.ChallengesAdapter;
import com.chessyoup.ui.adapters.MainViewPagerAdapter;
import com.chessyoup.ui.adapters.RosterAdapter;
import com.chessyoup.ui.fragments.FragmentChallenges;
import com.chessyoup.ui.fragments.FragmentMainMenu;
import com.chessyoup.ui.fragments.FragmentRoster;
import com.cyp.accounts.Account;
import com.cyp.application.Application;
import com.cyp.game.IChallenge;
import com.cyp.game.IGameControllerListener;
import com.cyp.transport.Presence;
import com.cyp.transport.RosterListener;
import com.google.api.client.auth.oauth2.draft10.AccessTokenResponse;
import com.google.api.client.googleapis.auth.oauth2.draft10.GoogleAccessProtectedResource;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.korovyansk.android.slideout.SlideoutActivity;

public class MainActivity extends FragmentActivity implements
		IGameControllerListener, RosterListener {

	private RosterAdapter rosterAdapter;

	private ChallengesAdapter chalangesAdapter;

	private FragmentChallenges fragmentChallenges;

	private FragmentRoster fragmentRoster;

	private ABasicGTalkAccount account;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("MainActivity", "on create");
		this.initUI();
		this.installListeners();

		try {
			Application.configure("com.chessyoup.context.AndroidContext",
					this.getApplicationContext());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		this.runLogintask();
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
		if( this.account == null ){
			this.runLogintask();
		}
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
		account.logout();
	}

	@Override
	public void challengeAccepted(IChallenge arg0) {
		Log.d("challengeAccepted", arg0.toString());
		account.getGameController().startGame(arg0);
		Intent startChessActivityIntent = new Intent(this,
				ChessGameActivity.class);
		startChessActivityIntent.putExtra("remoteId", arg0.getRemoteId());
		startChessActivityIntent.putExtra("gameId", arg0.getTime());
		startActivity(startChessActivityIntent);
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
	public void challengeReceived(final IChallenge arg0) {
		Log.d("challengeReceived", arg0.toString());
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				chalangesAdapter.addChallenge(arg0);
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
				AlertDialog.Builder db = new AlertDialog.Builder(
						MainActivity.this);
				db.setTitle("Challange");
				String actions[] = new String[3];
				actions[0] = "Accept";
				actions[1] = "Reject";
				actions[2] = "Cancel";
				db.setItems(actions, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						IChallenge challenge = fragmentChallenges
								.getSelectedChallenge();

						if (challenge != null) {

							switch (which) {
							case 0:
								account.getGameController()
										.startGame(challenge);
								Intent startChessActivityIntent = new Intent(
										MainActivity.this,
										ChessGameActivity.class);
								startChessActivityIntent.putExtra("remoteId",
										challenge.getRemoteId());
								startChessActivityIntent.putExtra("gameId",
										challenge.getTime());
								startActivity(startChessActivityIntent);
								runAcceptChallengeTask(challenge);
								break;
							case 1:
								chalangesAdapter.removeChallenge(challenge);
								runRejectChallengeTask(challenge);
								break;
							case 2:
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
		});
	}
	
	private static final HttpTransport TRANSPORT = new NetHttpTransport();	
	private static final JsonFactory JSON_FACTORY = new JacksonFactory();
	private static final String SCOPE = "https://www.googleapis.com/auth/googletalk";
	private static final String CALLBACK_URL = "http://localhost";
	private static final String CLIENT_ID = "824424892358.apps.googleusercontent.com";
	private static final String CLIENT_SECRET = "3ng2GDWbloODjOxs4d1r_Jti";
	
	private void runLogintask() {
		AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				SharedPreferencesCredentialStore store = new SharedPreferencesCredentialStore( PreferenceManager.getDefaultSharedPreferences(MainActivity.this));
				AccessTokenResponse account = store.read();
				System.out.println("stored account :"+account.toString());
				if( account.refreshToken != null && account.refreshToken.trim().length() > 0  ){
					
					GoogleAccessProtectedResource access = new GoogleAccessProtectedResource(account.accessToken,
					        TRANSPORT, JSON_FACTORY, CLIENT_ID, CLIENT_SECRET, account.refreshToken);
					try {
						access.refreshToken();					
						store.write(access);
						account.accessToken = access.getAccessToken();
						System.out.println("after refresh :"+account.toString());
					} catch (IOException e) {					
						e.printStackTrace();
					}
					
					MainActivity.this.account = new ABasicGTalkAccount(CLIENT_ID, account.accessToken);
					MainActivity.this.account.login(new Account.LoginCallback() {

						@Override
						public void onLogginSuccess() {
							MainActivity.this.account.getGameController().addGameControllerListener(
									MainActivity.this);
							GameManager.getManager().addGameController(
									MainActivity.this.account.getGameController());

							MainActivity.this.account.getRoster().addListener(MainActivity.this);

							MainActivity.this.runOnUiThread(new Runnable() {

								@Override
								public void run() {
									MainActivity.this.rosterAdapter.addAccount(MainActivity.this.account);
								}
							});
						}

						@Override
						public void onLogginError(String errorMessage) {
						}
					});
					
				}
				else{
					MainActivity.this.runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							Intent googleAuhIntent = new Intent(MainActivity.this,GoogleOauth2Activity.class);
							startActivity(googleAuhIntent);														
						}
					});										
				}
			
				return null;
			}
		};

		task.execute();
	}

	private void runAcceptChallengeTask(final IChallenge challenge) {
		AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				try {
					account.getGameController().acceptChallenge(challenge);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
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
				rosterAdapter.notifyDataSetChanged();
				
				for (int i = 0; i < rosterAdapter.getGroupCount(); i++) {
					fragmentRoster.getRosterView().expandGroup(i);
				}
			}
		});
	}
}
