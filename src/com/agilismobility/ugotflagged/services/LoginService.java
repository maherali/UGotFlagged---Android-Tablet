package com.agilismobility.ugotflagged.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.agilismobility.ugotflagged.proxy.ServerProxy;
import com.agilismobility.ugotflagged.proxy.ServerProxy.IServerResponder;
import com.agilismobility.ugotflagged.proxy.ServerProxy.ServerResponseSummary;
import com.agilismobility.ugotflagged.utils.Utils;
import com.agilismobility.utils.Constants;

public class LoginService extends Service {

	public static final String ERROR_ARG = "error";
	public static final String XML_ARG = "xml";
	public static final String SUCCESS_ARG = "success";
	public static final String PASSWORD_ARG = "password";
	public static final String USER_NAME_ARG = "user_name";

	private static String TAG = "LoginService";
	public static final String LOGIN_FINISHED_NOTIF = "LOGIN_FINISHED_NOTIF";

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, final int startId) {
		String userName = intent.getStringExtra(USER_NAME_ARG);
		String password = intent.getStringExtra(PASSWORD_ARG);
		login(userName, password, startId);
		return START_REDELIVER_INTENT;
	}

	private void login(String userName, String password, final int startId) {
		Log.d(TAG, "log in using " + userName + " and password " + password);
		ServerProxy.get(Constants.LOGGING_IN, "/sessions?", Utils.toUrlParams(USER_NAME_ARG, userName, PASSWORD_ARG, password),
				new IServerResponder() {
					@Override
					public void success(ServerResponseSummary srs) {
						announceLoginSuccess(startId, srs);
					}

					@Override
					public void failure(ServerResponseSummary srs) {
						announceLoginFailure(startId, srs);
					}
				});
	}

	private void anounceLoginFinished(int startID, ServerResponseSummary srs, boolean success) {
		Intent newIntent = new Intent(LOGIN_FINISHED_NOTIF);
		newIntent.putExtra(SUCCESS_ARG, success);
		newIntent.putExtra(XML_ARG, srs.xml);
		newIntent.putExtra(ERROR_ARG, srs.detailedErrorMessage);
		sendBroadcast(newIntent);
		stopSelf(startID);
	}

	private void announceLoginSuccess(int startID, ServerResponseSummary srs) {
		anounceLoginFinished(startID, srs, true);
	}

	private void announceLoginFailure(int startID, ServerResponseSummary srs) {
		anounceLoginFinished(startID, srs, false);
	}

}