package com.chessyoup.ui.fragments;


import com.chessyoup.ui.UIActionRegister;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MenuFragment extends ListFragment {

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setListAdapter(new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1, new String[] { " Accounts", " Settings", " Exit", " About"}));
		getListView().setCacheColorHint(0);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		UIActionRegister.action = l.getAdapter().getItem(position).toString();		
		super.onListItemClick(l, v, position, id);
		((FragmentMainMenu)getActivity()).getSlideoutHelper().close();
	}
}
