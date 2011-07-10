package com.agilismobility.ugotflagged.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.agilismobility.ugotflagged.proxy.ServerProxy;
import com.agilismobility.ugotflagged.proxy.ServerProxy.IServerResponder;
import com.agilismobility.ugotflagged.proxy.ServerProxy.ServerResponseSummary;
import com.agilismobility.ugotflagged.utils.Utils;
import com.agilismobility.utils.Constants;

public class SessionService extends Service {

	private static final String URL_PATH = "/sessions";

	public static final String ACTION = "ACTION";
	public static final String LOGIN_ACTION = "LOGIN_ACTION";
	public static final String LOGOUT_ACTION = "LOGOUT_ACTION";

	public static final String ERROR_ARG = "error";
	public static final String XML_ARG = "xml";
	public static final String SUCCESS_ARG = "success";
	public static final String PASSWORD_ARG = "password";
	public static final String USER_NAME_ARG = "user_name";

	private static String TAG = "SessionService";
	public static final String SESSION_NOTIF = "SESSION_NOTIF";

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, final int startId) {
		String action = intent.getStringExtra(ACTION);
		if (LOGIN_ACTION.equals(action)) {
			String userName = intent.getStringExtra(USER_NAME_ARG);
			String password = intent.getStringExtra(PASSWORD_ARG);
			login(userName, password, startId);
		} else if (LOGOUT_ACTION.equals(action)) {
			logout(startId);
		}
		return START_REDELIVER_INTENT;
	}

	private void login(String userName, String password, final int startId) {
		ServerProxy.resetSession();
		ServerProxy.get(Constants.LOGGING_IN, URL_PATH, Utils.toUrlParams(USER_NAME_ARG, userName, PASSWORD_ARG, password),
				new IServerResponder() {
					@Override
					public void success(ServerResponseSummary srs) {
						anounceLoginFinished(startId, srs, true);
					}

					@Override
					public void failure(ServerResponseSummary srs) {
						anounceLoginFinished(startId, srs, false);
					}
				});
	}

	private void anounceLoginFinished(int startID, ServerResponseSummary srs, boolean success) {
		Intent newIntent = new Intent(SESSION_NOTIF);
		newIntent.putExtra(ACTION, LOGIN_ACTION);
		newIntent.putExtra(SUCCESS_ARG, success);
		newIntent.putExtra(XML_ARG, srs.xml);
		newIntent.putExtra(ERROR_ARG, srs.detailedErrorMessage);
		sendBroadcast(newIntent);
		stopSelf(startID);
	}

	private void logout(final int startId) {
		ServerProxy.post(Constants.LOGGING_OUT, URL_PATH, Utils.toUrlParams("_method", "delete"), new IServerResponder() {

			@Override
			public void success(ServerResponseSummary response) {
				ServerProxy.resetSession();
			}

			@Override
			public void failure(ServerResponseSummary response) {
				ServerProxy.resetSession();
			}

		});
	}

}