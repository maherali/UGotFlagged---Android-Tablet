package com.agilismobility.ugotflagged.ui.fragments.followed;

import com.agilismobility.ugotflagged.dtos.PostDTO;
import com.agilismobility.ugotflagged.ui.fragments.shared.FlagDetailsFragment;

public class FollowedFlagDetailsFragment extends FlagDetailsFragment {

	protected PostDTO post;

	public void setPost(PostDTO post) {
		this.post = post;
	}

	@Override
	protected PostDTO getPost(int pos) {
		return post;
	}

}
