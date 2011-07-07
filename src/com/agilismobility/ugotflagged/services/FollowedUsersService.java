package com.agilismobility.ugotflagged.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.agilismobility.ugotflagged.proxy.ServerProxy;
import com.agilismobility.ugotflagged.proxy.ServerProxy.IServerResponder;
import com.agilismobility.ugotflagged.proxy.ServerProxy.ServerResponseSummary;
import com.agilismobility.ugotflagged.utils.Utils;
import com.agilismobility.utils.Constants;

public class FollowedUsersService extends Service {

	public static final String ACTION = "ACTION";

	public static final String FIND_FOLLOWED_USERS_ACTION = "FIND_FOLLOWED_USERS_ACTION";
	public static final String UNFOLLOW_USER_ACTION = "UNFOLLOW_USER_ACTION";

	public static final String FOLLOWED_USERS_FINISHED_NOTIF = "FOLLOWED_USERS_FINISHED_NOTIF";

	public static final String XML_ARG = "xml";
	public static final String SUCCESS_ARG = "success";
	public static final String ERROR_ARG = "error";

	public static final String USER_NAME_ARG = "USER_NAME";

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, final int startId) {
		String action = intent.getStringExtra(ACTION);
		if (FIND_FOLLOWED_USERS_ACTION.equals(action)) {
			findFollowedUsers(startId);
		} else if (UNFOLLOW_USER_ACTION.equals(action)) {
			String userName = intent.getStringExtra(USER_NAME_ARG);
			unfollowUser(startId, userName);
		}
		return START_REDELIVER_INTENT;
	}

	private void unfollowUser(final int startId, final String userName) {
		ServerProxy.post(Constants.UNFOLLOWING_FOLLOWED, "/connections", Utils.toUrlParams("user", userName, "_method", "delete"),
				new IServerResponder() {
					@Override
					public void success(ServerResponseSummary srs) {
						anounceUnFollowFinished(startId, srs, true);
					}

					@Override
					public void failure(ServerResponseSummary srs) {
						anounceUnFollowFinished(startId, srs, false);
					}
				});
	}

	private void findFollowedUsers(final int startId) {
		ServerProxy.get(Constants.FINDING_FOLLOWED, "/connections/followed_users?", "", new IServerResponder() {
			@Override
			public void success(ServerResponseSummary srs) {
				anounceFollowedFinished(startId, srs, true);
			}

			@Override
			public void failure(ServerResponseSummary srs) {
				anounceFollowedFinished(startId, srs, false);
			}
		});
	}

	private void anounceFollowedFinished(int startID, ServerResponseSummary srs, boolean success) {
		Intent newIntent = new Intent(FOLLOWED_USERS_FINISHED_NOTIF);
		newIntent.putExtra(ACTION, FIND_FOLLOWED_USERS_ACTION);
		newIntent.putExtra(SUCCESS_ARG, success);
		newIntent.putExtra(XML_ARG, srs.xml);
		newIntent.putExtra(ERROR_ARG, srs.detailedErrorMessage);
		sendBroadcast(newIntent);
		stopSelf(startID);
	}

	private void anounceUnFollowFinished(int startID, ServerResponseSummary srs, boolean success) {
		Intent newIntent = new Intent(FOLLOWED_USERS_FINISHED_NOTIF);
		newIntent.putExtra(ACTION, UNFOLLOW_USER_ACTION);
		newIntent.putExtra(SUCCESS_ARG, success);
		newIntent.putExtra(XML_ARG, srs.xml);
		newIntent.putExtra(ERROR_ARG, srs.detailedErrorMessage);
		sendBroadcast(newIntent);
		stopSelf(startID);
	}

}