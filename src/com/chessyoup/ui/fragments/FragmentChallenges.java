package com.chessyoup.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.chessyoup.R;
import com.chessyoup.ui.adapters.ChallengesAdapter;
import com.cyp.game.IChallenge;

public class FragmentChallenges extends Fragment {

	private ChallengesAdapter adapter;

	private Runnable onChallengeSelected;

	private ListView challengesListView;
	
	private IChallenge selectedChallenge;
	
	public FragmentChallenges(ChallengesAdapter adapter) {
		this.adapter = adapter;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.challenges, container, false);
		challengesListView = (ListView) view
				.findViewById(R.id.challengesListView);
		challengesListView.setAdapter(adapter);

		challengesListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				selectedChallenge = (IChallenge)adapter.getItem(position);
				
				if (onChallengeSelected != null) {
					onChallengeSelected.run();					
				}
			}
		});

		return view;
	}

	public IChallenge getSelectedChallenge() {
		return this.selectedChallenge;
	}

	public Runnable getOnChallengeSelected() {
		return onChallengeSelected;
	}

	public void setOnChallengeSelected(Runnable onChallengeSelected) {
		this.onChallengeSelected = onChallengeSelected;
	}
}