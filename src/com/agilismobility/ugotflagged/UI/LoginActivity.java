package com.agilismobility.ugotflagged.UI;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.agilismobility.ugotflagged.MainApplication;
import com.agilismobility.ugotflagged.R;
import com.agilismobility.ugotflagged.dtos.UserDTO;
import com.agilismobility.ugotflagged.services.LoginService;
import com.agilismobility.ugotflagged.utils.XMLHelper;
import com.agilismobility.utils.Constants;

public class LoginActivity extends BaseActivity {

	private static final String SP_LOGIN_PASSWORD = "login_password";
	private static final String SP_LOGIN_USER_NAME = "login_user_name";
	private static final String SP_LOGIN_REMEMBER_ME = "login_remember_me";
	public static final String LOGIN_PREF_FILE_NAME = "LOGIN_PREF";
	
	LoginReceiver mLoginReceiver;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		registerReceiver();
		setContentView(R.layout.login);
		final TextView userName = (TextView) findViewById(R.id.user_name);
		final TextView password = (TextView) findViewById(R.id.password);
		final CheckBox rememberMe = (CheckBox) findViewById(R.id.rememberme);

		SharedPreferences settings = getSharedPreferences(LOGIN_PREF_FILE_NAME, 0);
		userName.setText(settings.getString(SP_LOGIN_USER_NAME, ""));
		password.setText(settings.getString(SP_LOGIN_PASSWORD, ""));
		rememberMe.setChecked(settings.getBoolean(SP_LOGIN_REMEMBER_ME, false));

		((Button) findViewById(R.id.login_submit)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				SharedPreferences settings = getSharedPreferences(LOGIN_PREF_FILE_NAME, 0);
				SharedPreferences.Editor editor = settings.edit();
				editor.putString(SP_LOGIN_USER_NAME, userName.getText().toString());
				editor.putString(SP_LOGIN_PASSWORD, password.getText().toString());
				editor.putBoolean(SP_LOGIN_REMEMBER_ME, rememberMe.isChecked());
				editor.commit();
				initiateLogin(userName.getText().toString(), password.getText().toString());
				enableButton(R.id.login_submit, false);
			}
		});

		if (settings.getBoolean(SP_LOGIN_REMEMBER_ME, false)) {
			Intent newIntent = new Intent();
			newIntent.setClass(getApplication(), FlagsActivity.class);
			startActivity(newIntent);
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			initiateLogin(userName.getText().toString(), password.getText().toString());
			enableButton(R.id.login_submit, false);
		}
	}

	protected void initiateLogin(String userName, String password) {
		Intent intent = new Intent(this, LoginService.class);
		intent.putExtra(LoginService.USER_NAME_ARG, userName);
		intent.putExtra(LoginService.PASSWORD_ARG, password);
		startService(intent);
	}

	public static void enableRememberMe(boolean enable) {
		SharedPreferences settings = MainApplication.getInstance().getSharedPreferences(LOGIN_PREF_FILE_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(SP_LOGIN_REMEMBER_ME, enable);
		editor.commit();
	}

	private void registerReceiver() {
		IntentFilter filter = new IntentFilter(LoginService.LOGIN_FINISHED_NOTIF);
		mLoginReceiver = new LoginReceiver();
		registerReceiver(mLoginReceiver, filter);
	}

	public class LoginReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			boolean success = intent.getBooleanExtra(LoginService.SUCCESS_ARG, true);
			final String xml = intent.getStringExtra(LoginService.XML_ARG);

			if (success) {
				Constants.broadcastDoingSomethingNotification(Constants.PARSING_USER_DATA);
				new AsyncTask<Void, Void, UserDTO>() {
					@Override
					protected UserDTO doInBackground(Void... params) {
						return new UserDTO(new XMLHelper(xml));
					}

					@Override
					protected void onPostExecute(UserDTO u) {
						Constants.broadcastFinishedDoingSomethingNotification(Constants.PARSING_USER_DATA);
						enableButton(R.id.login_submit, true);
						if (u.errors.size() == 0) {
							MainApplication.GlobalState.setCurrentUser(u);
							Intent newIntent = new Intent();
							newIntent.setClass(getApplication(), FlagsActivity.class);
							startActivity(newIntent);
							finish();
						} else {
							showError(u.errors);
						}
					}
				}.execute();
			} else {
				enableButton(R.id.login_submit, true);
				showError(intent.getStringExtra(LoginService.ERROR_ARG));
			}
		}
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(mLoginReceiver);
		super.onDestroy();
	}

}
