package com.agilismobility.ugotflagged.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.agilismobility.ugotflagged.proxy.ServerProxy;
import com.agilismobility.ugotflagged.proxy.ServerProxy.IServerResponder;
import com.agilismobility.ugotflagged.proxy.ServerProxy.ServerResponseSummary;
import com.agilismobility.ugotflagged.utils.Utils;
import com.agilismobility.utils.Constants;

public class RefreshService extends Service {

	private static String TAG = "RefreshService";
	public static final String REFRESHING_FINISHED_NOTIF = "REFRESHING_FINISHED_NOTIF";
	public static final String XML_ARG = "xml";
	public static final String SUCCESS_ARG = "success";
	public static final String ERROR_ARG = "error";

	public static final String USER_NAME_ARG = "USER_NAME_ARG";

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, final int startId) {
		String userName = intent.getStringExtra(USER_NAME_ARG);
		refresh(userName, startId);
		return START_REDELIVER_INTENT;
	}

	private void refresh(String userName, final int startId) {
		ServerProxy.get(Constants.REFRESHING_STREAM, "/users/" + userName, Utils.toUrlParams("stream", ""), new IServerResponder() {
			@Override
			public void success(ServerResponseSummary srs) {
				announceRefreshSuccess(startId, srs);
			}

			@Override
			public void failure(ServerResponseSummary srs) {
				announceRefreshFailure(startId, srs);
			}
		});
	}

	private void anounceRefreshFinished(int startID, ServerResponseSummary srs, boolean success) {
		Intent newIntent = new Intent(REFRESHING_FINISHED_NOTIF);
		newIntent.putExtra(SUCCESS_ARG, success);
		newIntent.putExtra(XML_ARG, srs.xml);
		newIntent.putExtra(ERROR_ARG, srs.detailedErrorMessage);
		sendBroadcast(newIntent);
		stopSelf(startID);
	}

	private void announceRefreshSuccess(int startID, ServerResponseSummary srs) {
		anounceRefreshFinished(startID, srs, true);
	}

	private void announceRefreshFailure(int startID, ServerResponseSummary srs) {
		anounceRefreshFinished(startID, srs, false);
	}

}
