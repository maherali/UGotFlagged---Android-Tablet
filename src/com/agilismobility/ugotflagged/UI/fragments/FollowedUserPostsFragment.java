package com.agilismobility.ugotflagged.UI.fragments;

import com.agilismobility.ugotflagged.MainApplication;

public class FollowedUserPostsFragment extends UserPostsFragment {

	@Override
	protected void showAt(int position) {
		FollowedFlagDetailsFragment frag = ((MainApplication) MainApplication.getInstance()).getFollowedFlagDetailsFragment();
		if (frag != null && frag.isVisible()) {
			frag.setPost(getPostAtPosition(position));
			frag.updateContent(position);
		}
		mCurPosition = position;
	}

}
