package com.agilismobility.ugotflagged.UI.fragments;

import com.agilismobility.ugotflagged.MainApplication;
import com.agilismobility.ugotflagged.dtos.PostDTO;

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
