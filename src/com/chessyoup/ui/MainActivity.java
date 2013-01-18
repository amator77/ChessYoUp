package com.chessyoup.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import com.chessyoup.R;
import com.korovyansk.android.slideout.SlideoutActivity;

public class MainActivity extends FragmentActivity {
	
	
	public static class MyAdapter extends FragmentStatePagerAdapter {
		
		FragmentRoster f1 = new FragmentRoster();
		FragmenChat f2 = new FragmenChat();
		
        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Fragment getItem(int position) {
        	if( position == 0){
        		return f1;
        	}
        	else{
        		return f2;
        	}        	
        }
    }
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("RoomActivity", "on create");

		this.setContentView(R.layout.main);
		
		this.setTitle(null);
		
		// if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
		// getActionBar().hide();
		// }

		findViewById(R.id.menuButton).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						int width = (int) TypedValue.applyDimension(
								TypedValue.COMPLEX_UNIT_DIP, 40, getResources()
										.getDisplayMetrics());
						SlideoutActivity.prepare(MainActivity.this,
								R.id.inner_content, width);
						startActivity(new Intent(MainActivity.this,
								MenuActivity.class));
						overridePendingTransition(0, 0);
					}
				});
		
		ViewPager viewPager = (ViewPager)this.findViewById(R.id.mainViewPager);
		viewPager.setAdapter(new MyAdapter(getSupportFragmentManager()));
		
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
}
