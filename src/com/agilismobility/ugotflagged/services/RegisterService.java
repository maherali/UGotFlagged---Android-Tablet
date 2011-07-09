package com.agilismobility.ugotflagged.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.agilismobility.ugotflagged.proxy.ServerProxy;
import com.agilismobility.ugotflagged.proxy.ServerProxy.IServerResponder;
import com.agilismobility.ugotflagged.proxy.ServerProxy.ServerResponseSummary;
import com.agilismobility.ugotflagged.utils.Utils;
import com.agilismobility.utils.Constants;

public class RegisterService extends Service {

	public static final String ERROR_ARG = "error";
	public static final String XML_ARG = "xml";
	public static final String SUCCESS_ARG = "success";

	public static final String USER_NAME_ARG = "new_user[user_name]";
	public static final String FIRST_NAME_ARG = "new_user[first_name]";
	public static final String LAST_NAME_ARG = "new_user[last_name]";
	public static final String PASSWORD_ARG = "new_user[password]";
	public static final String EMAIL_ARG = "new_user[email]";

	private static String TAG = "RegisterService";
	public static final String REGISTER_FINISHED_NOTIF = "REGISTER_FINISHED_NOTIF";

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, final int startId) {
		String firstName = intent.getStringExtra(FIRST_NAME_ARG);
		String lastName = intent.getStringExtra(LAST_NAME_ARG);
		String userName = intent.getStringExtra(USER_NAME_ARG);
		String email = intent.getStringExtra(EMAIL_ARG);
		String password = intent.getStringExtra(PASSWORD_ARG);
		register(firstName, lastName, userName, password, email, startId);
		return START_REDELIVER_INTENT;
	}

	private void register(String firstName, String lastName, String userName, String password, String email, final int startId) {
		ServerProxy.post(Constants.REGISTERING, "/users", Utils.toUrlParams(USER_NAME_ARG, userName, PASSWORD_ARG, password, EMAIL_ARG, email,
				FIRST_NAME_ARG, firstName, LAST_NAME_ARG, lastName), new IServerResponder() {
			@Override
			public void success(ServerResponseSummary srs) {
				anounceRegisterFinished(startId, srs, true);
			}

			@Override
			public void failure(ServerResponseSummary srs) {
				anounceRegisterFinished(startId, srs, false);
			}
		});
	}

	private void anounceRegisterFinished(int startID, ServerResponseSummary srs, boolean success) {
		Intent newIntent = new Intent(REGISTER_FINISHED_NOTIF);
		newIntent.putExtra(SUCCESS_ARG, success);
		newIntent.putExtra(XML_ARG, srs.xml);
		newIntent.putExtra(ERROR_ARG, srs.detailedErrorMessage);
		sendBroadcast(newIntent);
		stopSelf(startID);
	}

}
