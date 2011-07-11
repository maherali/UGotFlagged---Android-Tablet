package com.agilismobility.ugotflagged.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.agilismobility.ugotflagged.proxy.ServerProxy;
import com.agilismobility.ugotflagged.proxy.ServerProxy.IServerResponder;
import com.agilismobility.ugotflagged.proxy.ServerProxy.ServerResponseSummary;
import com.agilismobility.utils.Constants;

public class UsersService extends Service {

	private static final String URL_PATH = "/users";

	public static final String ACTION = "ACTION";
	public static final String USER_POSTS_ACTION = "USER_POSTS_ACTION";

	public static final String USER_NAME_ARG = "USER_NAME_ARG";

	public static final String ERROR_ARG = "error";
	public static final String XML_ARG = "xml";
	public static final String SUCCESS_ARG = "success";

	private static String TAG = "UsersService";
	public static final String USERS_NOTIF = "USERS_NOTIF";

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, final int startId) {
		String action = intent.getStringExtra(ACTION);
		if (USER_POSTS_ACTION.equals(action)) {
			String userName = intent.getStringExtra(USER_NAME_ARG);
			findUserPosts(userName, startId);
		}
		return START_REDELIVER_INTENT;
	}

	private void findUserPosts(String userName, final int startId) {
		ServerProxy.get(Constants.FINDING_USER_POSTS, URL_PATH + "/" + userName, "", new IServerResponder() {
			@Override
			public void success(ServerResponseSummary srs) {
				anounceFindingPostsFinished(startId, srs, true);
			}

			@Override
			public void failure(ServerResponseSummary srs) {
				anounceFindingPostsFinished(startId, srs, false);
			}
		});
	}

	private void anounceFindingPostsFinished(int startID, ServerResponseSummary srs, boolean success) {
		Intent newIntent = new Intent(USERS_NOTIF);
		newIntent.putExtra(ACTION, USER_POSTS_ACTION);
		newIntent.putExtra(SUCCESS_ARG, success);
		newIntent.putExtra(XML_ARG, srs.xml);
		newIntent.putExtra(ERROR_ARG, srs.detailedErrorMessage);
		sendBroadcast(newIntent);
		stopSelf(startID);
	}

}