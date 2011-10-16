package com.agilismobility.ugotflagged.ui.fragments.stream;

import com.agilismobility.ugotflagged.MainApplication;
import com.agilismobility.ugotflagged.dtos.PostDTO;
import com.agilismobility.ugotflagged.ui.fragments.shared.FlagDetailsFragment;

public class StreamFlagDetailsFragment extends FlagDetailsFragment {

	@Override
	protected PostDTO getPost(int pos) {
		if (pos < MainApplication.GlobalState.getStream().size()) {
			return MainApplication.GlobalState.getStream().get(pos);
		} else {
			return null;
		}
	}
}
