package com.chessyoup.ui;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.chessyoup.R;

public class FragmentRoster extends  Fragment {
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
        Bundle savedInstanceState) {   
		
		View view = inflater.inflate(R.layout.roaster, container, false);
	
		
        return view;        
         
    }
}