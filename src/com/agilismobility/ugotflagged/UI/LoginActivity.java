package com.agilismobility.ugotflagged.UI;

import com.agilismobility.ugotflagged.MainApplication;
import com.agilismobility.ugotflagged.R;
import com.agilismobility.ugotflagged.dtos.UserDTO;
import com.agilismobility.ugotflagged.services.LoginService;
import com.agilismobility.ugotflagged.utils.XMLHelper;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class LoginActivity extends BaseActivity {

	public static final String LOGIN_PREF = "LOGIN_PREF";
	MyReceiver receiver;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		final TextView email = (TextView) findViewById(R.id.login_email);
		final TextView password = (TextView) findViewById(R.id.login_password);
		final CheckBox rememberMe = (CheckBox) findViewById(R.id.rememberme);

		SharedPreferences settings = getSharedPreferences(LOGIN_PREF, 0);
		email.setText(settings.getString("login_email", ""));
		password.setText(settings.getString("login_password", ""));
		rememberMe.setChecked(settings.getBoolean("login_remember_me", false));

		((Button) findViewById(R.id.login_submit)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				startLogin();
			}
		});
		registerReceiver();
		if (settings.getBoolean("login_remember_me", false)) {
			startLogin();
		}
	}

	protected void startLogin() {
		final TextView email = (TextView) findViewById(R.id.login_email);
		final TextView password = (TextView) findViewById(R.id.login_password);
		final CheckBox rememberMe = (CheckBox) findViewById(R.id.rememberme);
		SharedPreferences settings = getSharedPreferences(LOGIN_PREF, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("login_email", email.getText().toString());
		editor.putString("login_password", password.getText().toString());
		editor.putBoolean("login_remember_me", rememberMe.isChecked());
		editor.commit();
		Intent intent = new Intent(LoginActivity.this, LoginService.class);
		intent.putExtra("email", email.getText().toString());
		intent.putExtra("password", password.getText().toString());
		startService(intent);
		((Button) findViewById(R.id.login_submit)).setEnabled(false);
	}

	public static void enableRememberMe(boolean enable) {
		SharedPreferences settings = MainApplication.getInstance().getSharedPreferences(LOGIN_PREF, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean("login_remember_me", enable);
		editor.commit();
	}

	private void registerReceiver() {
		IntentFilter filter = new IntentFilter(LoginService.LOGIN_SUCCESS_NOTIF);
		receiver = new MyReceiver();
		registerReceiver(receiver, filter);
	}

	public class MyReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			((Button) findViewById(R.id.login_submit)).setEnabled(true);
			boolean success = intent.getBooleanExtra("success", true);
			String xml = intent.getStringExtra("xml");
			if (success) {
				UserDTO user = new UserDTO(new XMLHelper(xml));
				if (user.errors.size() == 0) {
					MainApplication.GlobalState.setCurrentUser(user);
					Intent newIntent = new Intent();
					newIntent.setClass(getApplication(), FlagsActivity.class);
					startActivity(newIntent);
					finish();
				} else {
					showError(user.errors);
				}
			}
		}
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(receiver);
		super.onDestroy();
	}

}
