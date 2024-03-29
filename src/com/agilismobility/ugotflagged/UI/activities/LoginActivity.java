package com.agilismobility.ugotflagged.ui.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.agilismobility.ugotflagged.MainApplication;
import com.agilismobility.ugotflagged.R;
import com.agilismobility.ugotflagged.dtos.UserDTO;
import com.agilismobility.ugotflagged.services.SessionService;
import com.agilismobility.ugotflagged.utils.XMLHelper;
import com.agilismobility.utils.Constants;

public class LoginActivity extends BaseActivity {

	private static final String SP_LOGIN_PASSWORD = "login_password";
	private static final String SP_LOGIN_USER_NAME = "login_user_name";
	private static final String SP_LOGIN_REMEMBER_ME = "login_remember_me";
	public static final String LOGIN_PREF_FILE_NAME = "LOGIN_PREF";

	SessionReceiver mSessionReceiver;
	LoginAutoReceiver mLoginAutoReceiver;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		registerReceivers();
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
				showProgress();
			}
		});

		if (settings.getBoolean(SP_LOGIN_REMEMBER_ME, false)) {
			/*
			 * startFlagActivity(); try { Thread.sleep(500); } catch
			 * (InterruptedException e) { e.printStackTrace(); }
			 */
			initiateLogin(userName.getText().toString(), password.getText().toString());
			enableButton(R.id.login_submit, false);
			showProgress();
		}
	}

	protected void initiateLogin(String userName, String password) {
		Intent intent = new Intent(this, SessionService.class);
		intent.putExtra(SessionService.ACTION, SessionService.LOGIN_ACTION);
		intent.putExtra(SessionService.USER_NAME_ARG, userName);
		intent.putExtra(SessionService.PASSWORD_ARG, password);
		startService(intent);
	}

	public static void enableRememberMe(boolean enable) {
		SharedPreferences settings = MainApplication.getInstance().getSharedPreferences(LOGIN_PREF_FILE_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(SP_LOGIN_REMEMBER_ME, enable);
		editor.commit();
	}

	private void registerReceivers() {
		IntentFilter filter = new IntentFilter(SessionService.SESSION_NOTIF);
		mSessionReceiver = new SessionReceiver();
		registerReceiver(mSessionReceiver, filter);

		filter = new IntentFilter(Constants.LOGIN_AUTO_NOTIFICATION);
		mLoginAutoReceiver = new LoginAutoReceiver();
		registerReceiver(mLoginAutoReceiver, filter);
	}

	private void parseLoginAndGo(final String xml) {
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
					startFlagActivity();
					finish();
				} else {
					hideProgress();
					showError(u.errors);
				}
			}
		}.execute();
	}

	public class SessionReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (SessionService.LOGIN_ACTION.equals(intent.getStringExtra(SessionService.ACTION))) {
				if (intent.getBooleanExtra(SessionService.SUCCESS_ARG, false)) {
					parseLoginAndGo(intent.getStringExtra(SessionService.XML_ARG));
				} else {
					hideProgress();
					enableButton(R.id.login_submit, true);
					showError(intent.getStringExtra(SessionService.ERROR_ARG));
				}
			}
		}
	}

	public class LoginAutoReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			TextView userName = (TextView) findViewById(R.id.user_name);
			userName.setText(MainApplication.GlobalState.getCurrentUser().userName);
			TextView password = (TextView) findViewById(R.id.password);
			password.setText(MainApplication.GlobalState.getCurrentUser().password);
			CheckBox rememberMe = (CheckBox) findViewById(R.id.rememberme);
			rememberMe.setChecked(true);
		}
	}

	@Override
	protected void onDestroy() {
		if (mSessionReceiver != null) {
			unregisterReceiver(mSessionReceiver);
		}
		if (mLoginAutoReceiver != null) {
			unregisterReceiver(mLoginAutoReceiver);
		}
		super.onDestroy();
	}

	private void startFlagActivity() {
		Intent newIntent = new Intent();
		newIntent.setClass(getApplication(), MainActivity.class);
		startActivity(newIntent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.login_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.register:
			Intent intent = new Intent();
			intent.setClass(getApplication(), RegisterActivity.class);
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void showProgress() {
		((Button) findViewById(R.id.login_submit)).setVisibility(View.GONE);
		((ProgressBar) findViewById(R.id.progress)).setVisibility(View.VISIBLE);
	}

	private void hideProgress() {
		((Button) findViewById(R.id.login_submit)).setVisibility(View.VISIBLE);
		((ProgressBar) findViewById(R.id.progress)).setVisibility(View.GONE);
	}

}
