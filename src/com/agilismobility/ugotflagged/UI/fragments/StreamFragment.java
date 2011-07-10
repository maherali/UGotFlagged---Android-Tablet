package com.agilismobility.ugotflagged.UI.fragments;

import com.agilismobility.ugotflagged.MainApplication;
import com.agilismobility.ugotflagged.dtos.PostDTO;

public class StreamFragment extends FlagsFragment {

	@Override
	protected void setupListAdapter() {
		setListAdapter(m_adapter = new SlowAdapter(getActivity()));
	}

	@Override
	protected void showAt(int position) {
		FlagDetailsFragment frag = ((MainApplication) MainApplication.getInstance()).getFlagDetailsFragment();
		if (frag != null && frag.isVisible()) {
			frag.updateContent(position);
		}
		mCurPosition = position;
	}

	@Override
	protected int getPostCount() {
		return MainApplication.GlobalState.getStream().size();
	}

	@Override
	protected PostDTO getPostAtPosition(int position) {
		return MainApplication.GlobalState.getStream().get(position);
	}

}