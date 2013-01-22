package com.chessyoup.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import com.chessyoup.R;
import com.chessyoup.ui.adapters.MainViewPagerAdapter;
import com.chessyoup.ui.adapters.RosterAdapter;
import com.chessyoup.ui.fragments.FragmenChat;
import com.chessyoup.ui.fragments.FragmentMainMenu;
import com.chessyoup.ui.fragments.FragmentRoster;
import com.gamelib.accounts.Account;
import com.gamelib.accounts.impl.GoogleChessAccount;
import com.gamelib.application.Application;
import com.korovyansk.android.slideout.SlideoutActivity;

public class MainActivity extends FragmentActivity {

	private RosterAdapter rosterAdapter;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("MainActivity", "on create");
		this.initUI();
		this.installListeners();
		try {
			Application.configure("com.chessyoup.context.AndroidContext",
					this.getApplicationContext());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				GoogleChessAccount account = new GoogleChessAccount(
						"florea.leonard@gmail.com", "mirela76");
				account.login(new Account.LoginCallback() {

					@Override
					public void onLogginSuccess() {
						// TODO Auto-generated method stub

					}

					@Override
					public void onLogginError(String errorMessage) {
						// TODO Auto-generated method stub

					}
				});
				return null;
			}
		};

		task.execute();

	}

	private void initUI() {
		this.setContentView(R.layout.main);
		this.rosterAdapter = new RosterAdapter(getApplicationContext());
		ViewPager viewPager = (ViewPager) this.findViewById(R.id.mainViewPager);
		viewPager.setAdapter(new MainViewPagerAdapter(
				getSupportFragmentManager(), new FragmentRoster(
						this.rosterAdapter), new FragmenChat()));
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
}
