package com.agilismobility.ugotflagged.ui.fragments.liked;

import com.agilismobility.ugotflagged.MainApplication;
import com.agilismobility.ugotflagged.dtos.PostDTO;
import com.agilismobility.ugotflagged.ui.fragments.shared.FlagDetailsFragment;

public class LikedFlagDetailsFragment extends FlagDetailsFragment {

	@Override
	protected PostDTO getPost(int pos) {
		if (MainApplication.GlobalState.getMostLiked() != null && pos < MainApplication.GlobalState.getMostLiked().size()) {
			return MainApplication.GlobalState.getMostLiked().get(pos);
		} else {
			return null;
		}
	}
}
