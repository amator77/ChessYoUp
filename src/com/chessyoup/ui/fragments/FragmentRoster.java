package com.chessyoup.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.chessyoup.R;
import com.chessyoup.ui.adapters.RosterAdapter;

public class FragmentRoster extends Fragment {

	DisplayMetrics metrics;
	
	private RosterAdapter rosterAdapter;
	
	public FragmentRoster(RosterAdapter adapter){
		this.rosterAdapter = adapter;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.roaster, container, false);
		ExpandableListView rosterView = (ExpandableListView) view
				.findViewById(R.id.roasterGroupView);
		rosterView.setAdapter(this.rosterAdapter);

		metrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
		int width = metrics.widthPixels;
		rosterView.setIndicatorBounds(width - GetDipsFromPixel(50), width
				- GetDipsFromPixel(10));
	
		return view;
	}

	private int GetDipsFromPixel(float pixels) {
		final float scale = getResources().getDisplayMetrics().density;		
		return (int) (pixels * scale + 0.5f);
	}
}