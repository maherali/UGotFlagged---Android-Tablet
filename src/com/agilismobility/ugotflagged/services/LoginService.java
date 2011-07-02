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

	private static String TAG = "LoginService";
	public static final String LOGIN_FINISHED_NOTIF = "LOGIN_FINISHED_NOTIF";

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, final int startId) {
		String email = intent.getStringExtra("email");
		String password = intent.getStringExtra("password");
		login(email, password, startId);
		return START_REDELIVER_INTENT;
	}

	private void login(String email, String password, final int startId) {
		Log.d(TAG, "log in using " + email + " and password " + password);
		ServerProxy.get(Constants.LOGGING_IN, "/sessions?", Utils.toUrlParams("user_name", email, "password", password),
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

	private void announceLoginSuccess(int startID, ServerResponseSummary srs) {
		Intent newIntent = new Intent(LOGIN_FINISHED_NOTIF);
		newIntent.putExtra("success", true);
		newIntent.putExtra("xml", srs.xml);
		newIntent.putExtra("error", srs.detailedErrorMessage);
		sendBroadcast(newIntent);
		stopSelf(startID);
	}

	private void announceLoginFailure(int startID, ServerResponseSummary srs) {
		Intent newIntent = new Intent(LOGIN_FINISHED_NOTIF);
		newIntent.putExtra("success", false);
		newIntent.putExtra("xml", srs.xml);
		newIntent.putExtra("error", srs.detailedErrorMessage);
		sendBroadcast(newIntent);
		stopSelf(startID);
	}

}