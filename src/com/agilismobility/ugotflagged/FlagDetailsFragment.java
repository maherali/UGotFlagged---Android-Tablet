package com.agilismobility.ugotflagged;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FlagDetailsFragment extends Fragment {
	private TextView mContentView;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContentView = new TextView(getActivity());
		return mContentView;
	}

	void updateContent(int position) {
		mContentView.setText(new Integer(position).toString());
	}
}