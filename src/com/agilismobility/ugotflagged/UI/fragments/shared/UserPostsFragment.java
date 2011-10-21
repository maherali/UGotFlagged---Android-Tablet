package com.agilismobility.ugotflagged.ui.fragments.shared;

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
import com.agilismobility.ugotflagged.services.SessionService;
import com.agilismobility.ugotflagged.services.UsersService;
import com.agilismobility.ugotflagged.ui.activities.DialogActivity;
import com.agilismobility.ugotflagged.utils.XMLHelper;
import com.agilismobility.utils.Constants;

abstract public class UserPostsFragment extends FlagsFragment {
	protected Integer userID;
	protected String userName;
	protected UsersReceiver mUsersReceiver;

	private ArrayList<PostDTO> userPosts() {
		return userID != null ? MainApplication.GlobalState.getUserPosts(userID) : null;
	}

	public void setUser(UserDTO u) {
		userID = u.identifier;
		userName = u.userName;
		m_adapter.notifyDataSetChanged();
		findUserPosts();
	}

	private void findUserPosts() {
		showHeaderView();
		Intent intent = new Intent(getActivity(), UsersService.class);
		intent.putExtra(UsersService.ACTION, UsersService.USER_POSTS_ACTION);
		intent.putExtra(UsersService.USER_NAME_ARG, userName);
		getActivity().startService(intent);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		registerReceiver();
	}

	@Override
	public void onDestroy() {
		if (mUsersReceiver != null) {
			getActivity().unregisterReceiver(mUsersReceiver);
		}
		super.onDestroy();
	}

	private void registerReceiver() {
		IntentFilter filter = new IntentFilter(UsersService.USERS_NOTIF);
		mUsersReceiver = new UsersReceiver();
		getActivity().registerReceiver(mUsersReceiver, filter);
	}

	public class UsersReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (UsersService.USER_POSTS_ACTION.equals(intent.getStringExtra(UsersService.ACTION))) {
				if (intent.getBooleanExtra(SessionService.SUCCESS_ARG, false)) {
					removeHeaderView();
					parseUserAndGo(intent.getStringExtra(SessionService.XML_ARG));
				} else {
					removeHeaderView();
					DialogActivity.showMessage(getActivity(), "Error", intent.getStringExtra(SessionService.ERROR_ARG));
				}
			}
		}
	}

	private void parseUserAndGo(final String xml) {
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
					MainApplication.GlobalState.setUserPosts(userID, u.posts);
					refresh();
				} else {
					DialogActivity.showMessage(getActivity(), "Error", u.errors.get(0));
				}
			}
		}.execute();
	}

	@Override
	abstract protected void showAt(int position);

	@Override
	protected int getPostCount() {
		return userPosts() != null ? userPosts().size() : 0;
	}

	@Override
	protected PostDTO getPostAtPosition(int position) {
		return userPosts() != null ? userPosts().get(position) : null;
	}

	@Override
	protected void setupListAdapter() {
		setListAdapter(m_adapter = new SlowAdapter(getActivity()));
	}

	@Override
	protected void setupHeaderView(ListView lv) {
	}

}
