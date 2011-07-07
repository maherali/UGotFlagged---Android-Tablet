package com.agilismobility.ugotflagged.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.agilismobility.ugotflagged.proxy.ServerProxy;
import com.agilismobility.ugotflagged.proxy.ServerProxy.IServerResponder;
import com.agilismobility.ugotflagged.proxy.ServerProxy.ServerResponseSummary;
import com.agilismobility.utils.Constants;

public class FollowedUsersService extends Service {
	public static final String FOLLOWED_USERS_FINISHED_NOTIF = "FOLLOWED_USERS_FINISHED_NOTIF";
	public static final String XML_ARG = "xml";
	public static final String SUCCESS_ARG = "success";
	public static final String ERROR_ARG = "error";

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, final int startId) {
		findFollowedUsers(startId);
		return START_REDELIVER_INTENT;
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
		newIntent.putExtra(SUCCESS_ARG, success);
		newIntent.putExtra(XML_ARG, srs.xml);
		newIntent.putExtra(ERROR_ARG, srs.detailedErrorMessage);
		sendBroadcast(newIntent);
		stopSelf(startID);
	}

}