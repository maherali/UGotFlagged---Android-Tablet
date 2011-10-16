package com.agilismobility.ugotflagged.ui.fragments.stream;

import android.widget.ListView;

import com.agilismobility.ugotflagged.MainApplication;
import com.agilismobility.ugotflagged.dtos.PostDTO;
import com.agilismobility.ugotflagged.ui.fragments.shared.FlagsFragment;

public class StreamFragment extends FlagsFragment {

	@Override
	protected void setupListAdapter() {
		setListAdapter(m_adapter = new SlowAdapter(getActivity()));
	}

	@Override
	protected void showAt(int position) {
		StreamFlagDetailsFragment frag = ((MainApplication) MainApplication.getInstance()).getStreamFlagDetailsFragment();
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

	@Override
	protected void setupHeaderView(ListView lv) {
	}

}