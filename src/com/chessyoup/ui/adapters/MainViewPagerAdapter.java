package com.chessyoup.ui.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class MainViewPagerAdapter  extends FragmentStatePagerAdapter {

	private Fragment f1 , f2 ;

    public MainViewPagerAdapter(FragmentManager fm , Fragment f1 , Fragment f2 ) {
        super(fm);
        this.f1 = f1;
        this.f2 = f2;
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
