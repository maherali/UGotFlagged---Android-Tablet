package com.agilismobility.ugotflagged.ui.fragments.followed;

import com.agilismobility.ugotflagged.MainApplication;
import com.agilismobility.ugotflagged.ui.fragments.shared.UserPostsFragment;

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
