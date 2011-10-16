package com.agilismobility.ugotflagged.UI.fragments;

import com.agilismobility.ugotflagged.dtos.PostDTO;

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
