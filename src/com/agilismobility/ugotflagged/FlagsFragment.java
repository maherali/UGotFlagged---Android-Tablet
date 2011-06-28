package com.agilismobility.ugotflagged;

import com.agilismobility.ugotflagged.R;
import com.agilismobility.ugotflagged.dtos.PostDTO;

import android.app.ListFragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class FlagsFragment extends ListFragment {
	private int mCurPosition = 0;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (savedInstanceState != null) {
			mCurPosition = savedInstanceState.getInt("listPosition");
		}
		populateStream();
		ListView lv = getListView();
		lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		lv.setCacheColorHint(Color.TRANSPARENT);
		selectPosition(mCurPosition);
	}

	public void populateStream() {
		setListAdapter(new ArrayAdapter<PostDTO>(getActivity(), R.layout.stream_item, MainApplication.GlobalState.getStream()));
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		showAt(position);
	}

	private void showAt(int position) {
		FlagDetailsFragment frag = (FlagDetailsFragment) getFragmentManager().findFragmentById(R.id.frag_details);
		frag.updateContent(position);
		mCurPosition = position;
	}

	public void selectPosition(int position) {
		ListView lv = getListView();
		lv.setItemChecked(position, true);
		showAt(position);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("listPosition", mCurPosition);
	}
}