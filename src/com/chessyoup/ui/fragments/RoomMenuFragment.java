package com.chessyoup.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.chessyoup.ui.UIActionRegister;

public class RoomMenuFragment extends ListFragment {

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setListAdapter(new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1, new String[] { "amator77", "gigel", "leo", "grasu"," amator77", "gigel", "leo", "grasu"," amator77", "gigel", "leo", "grasu"}));
		getListView().setCacheColorHint(0);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		UIActionRegister.action = l.getAdapter().getItem(position).toString();		
		super.onListItemClick(l, v, position, id);
//		((FragmentMainMenu)getActivity()).getSlideoutHelper().close();
	}
}
