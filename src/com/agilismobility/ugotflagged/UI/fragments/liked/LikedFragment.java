package com.agilismobility.ugotflagged.ui.fragments.liked;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;

import com.agilismobility.ugotflagged.MainApplication;
import com.agilismobility.ugotflagged.dtos.PostDTO;
import com.agilismobility.ugotflagged.dtos.UserDTO;
import com.agilismobility.ugotflagged.services.PostService;
import com.agilismobility.ugotflagged.ui.activities.BaseActivity;
import com.agilismobility.ugotflagged.ui.fragments.shared.FlagsFragment;
import com.agilismobility.ugotflagged.utils.XMLHelper;
import com.agilismobility.utils.Constants;

public class LikedFragment extends FlagsFragment {

	LikedReceiver mLikedReceiver;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		IntentFilter filter = new IntentFilter(PostService.MOST_LIKED_FLAGS_FINISHED_NOTIF);
		mLikedReceiver = new LikedReceiver();
		getActivity().registerReceiver(mLikedReceiver, filter);
		findLiked();
	}

	private void findLiked() {
		Intent intent = new Intent(this.getActivity(), PostService.class);
		intent.putExtra(PostService.ACTION, PostService.MOST_LIKED_FLAGS_ACTION);
		getActivity().startService(intent);
	}

	@Override
	public void onDestroy() {
		if (mLikedReceiver != null) {
			getActivity().unregisterReceiver(mLikedReceiver);
		}
		super.onDestroy();
	}

	public class LikedReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getBooleanExtra(PostService.SUCCESS_ARG, false)) {
				if (PostService.MOST_LIKED_FLAGS_ACTION.equals(intent.getStringExtra(PostService.ACTION))) {
					parsLiked(intent.getStringExtra(PostService.XML_ARG));
				}
			} else {
				((BaseActivity) getActivity()).showError(intent.getStringExtra(PostService.ERROR_ARG));
			}
		}
	}

	@Override
	protected void setupListAdapter() {
		setListAdapter(m_adapter = new SlowAdapter(getActivity()));
	}

	public void parsLiked(final String xml) {
		Constants.broadcastDoingSomethingNotification(Constants.PARSING_USER_DATA);
		new AsyncTask<Void, Void, UserDTO>() {
			@Override
			protected UserDTO doInBackground(Void... params) {
				return new UserDTO(new XMLHelper(xml));
			}

			@Override
			protected void onPostExecute(UserDTO u) {
				Constants.broadcastFinishedDoingSomethingNotification(Constants.PARSING_USER_DATA);
				if (u.errors.size() == 0) {
					MainApplication.GlobalState.setMostLiked(u.posts);
					refresh();
				} else {
					((BaseActivity) getActivity()).showError(u.errors);
				}
			}
		}.execute();

	}

	@Override
	protected void showAt(int position) {
		LikedFlagDetailsFragment frag = MainApplication.getInstance().getLikedFlagDetailsFragment();
		if (frag != null && frag.isVisible()) {
			frag.updateContent(position);
		}
		mCurPosition = position;
	}

	@Override
	protected int getPostCount() {
		ArrayList<PostDTO> posts = MainApplication.GlobalState.getMostLiked();
		if (posts != null)
			return posts.size();
		else
			return 0;
	}

	@Override
	protected PostDTO getPostAtPosition(int position) {
		ArrayList<PostDTO> posts = MainApplication.GlobalState.getMostLiked();
		if (posts != null)
			return posts.get(position);
		else
			return null;
	}

	@Override
	protected void setupHeaderView(ListView lv) {
	}

}