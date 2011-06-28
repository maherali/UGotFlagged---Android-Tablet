package com.agilismobility.ugotflagged.services;

import com.agilismobility.ugotflagged.proxy.ServerProxy;
import com.agilismobility.ugotflagged.proxy.ServerProxy.IServerResponder;
import com.agilismobility.ugotflagged.proxy.ServerResponse;
import com.agilismobility.ugotflagged.utils.Utils;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

public class LoginService extends Service {

	private static String TAG = "LoginService";
	public static final String LOGIN_SUCCESS_NOTIF = "LOGIN_SUCCESS_NOTIF";

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
		ServerProxy.get("/sessions?", Utils.toUrlParams("user_name", email, "password", password), new IServerResponder() {
			@Override
			public void success(ServerResponse response) {
				announceLoginSuccess(startId, response);
			}

			@Override
			public void failure(ServerResponse response) {
				announceLoginFailure(startId, response);
			}

		});
	}

	private void announceLoginSuccess(int startID, ServerResponse response) {
		Intent newIntent = new Intent(LOGIN_SUCCESS_NOTIF);
		newIntent.putExtra("success", true);
		newIntent.putExtra("xml", response.getXML());
		sendBroadcast(newIntent);
		stopSelf(startID);
	}

	private void announceLoginFailure(int startID, ServerResponse response) {
		Intent newIntent = new Intent(LOGIN_SUCCESS_NOTIF);
		newIntent.putExtra("success", false);
		newIntent.putExtra("xml", response.getXML());
		sendBroadcast(newIntent);
		stopSelf(startID);
	}

}