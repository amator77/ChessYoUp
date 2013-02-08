package com.chessyoup.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ExpandableListView;

import com.chessyoup.R;
import com.chessyoup.ui.adapters.RosterAdapter;
import com.cyp.transport.Contact;

public class FragmentRoster extends Fragment {

	DisplayMetrics metrics;
	
	private RosterAdapter rosterAdapter;
	
	private ExpandableListView rosterView;
	
	private Runnable onChallengeSelected;
	
	private Contact selectedContact;
	
	public FragmentRoster(RosterAdapter adapter){
		this.rosterAdapter = adapter;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.roaster, container, false);
		rosterView = (ExpandableListView) view
				.findViewById(R.id.roasterGroupView);
		rosterView.setAdapter(this.rosterAdapter);
		
		metrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
		int width = metrics.widthPixels;
		rosterView.setIndicatorBounds(width - GetDipsFromPixel(50), width
				- GetDipsFromPixel(10));
		
		rosterView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
			
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				
				selectedContact = (Contact)rosterAdapter.getChild(groupPosition, childPosition);
				
				if (onChallengeSelected != null) {
					onChallengeSelected.run();					
				}
				
				return true;
			}
		});
		
				
		return view;
	}
			
	public Contact getSelectedContact() {
		return selectedContact;
	}

	public Runnable getOnChallengeSelected() {
		return onChallengeSelected;
	}

	public void setOnChallengeSelected(Runnable onChallengeSelected) {
		this.onChallengeSelected = onChallengeSelected;
	}
	
	
	
	public ExpandableListView getRosterView() {
		return rosterView;
	}

	private int GetDipsFromPixel(float pixels) {
		final float scale = getResources().getDisplayMetrics().density;		
		return (int) (pixels * scale + 0.5f);
	}
}