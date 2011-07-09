package com.agilismobility.ugotflagged.UI;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.agilismobility.ugotflagged.MainApplication;
import com.agilismobility.ugotflagged.R;
import com.agilismobility.ugotflagged.dtos.UserDTO;
import com.agilismobility.ugotflagged.services.RegisterService;
import com.agilismobility.ugotflagged.utils.XMLHelper;
import com.agilismobility.utils.Constants;

public class RegisterActivity extends BaseActivity {

	RegisterReceiver mRegisterReceiver;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		registerReceiver();
		((Button) findViewById(R.id.register_submit)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final TextView userName = (TextView) findViewById(R.id.user_name);
				final TextView password = (TextView) findViewById(R.id.password);
				final TextView passwordConf = (TextView) findViewById(R.id.password_conf);
				final TextView email = (TextView) findViewById(R.id.email);
				final TextView firstName = (TextView) findViewById(R.id.first_name);
				final TextView lastName = (TextView) findViewById(R.id.last_name);
				if (password.getText().toString().equals(passwordConf.getText().toString())) {
					enableButton(R.id.register_submit, false);
					initiateRegister(firstName.getText().toString(), lastName.getText().toString(), userName.getText().toString(), password
							.getText().toString(), email.getText().toString());
				} else {
					showError("Password and password confirmation do not match.");
				}
			}
		});
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(mRegisterReceiver);
		super.onDestroy();
	}

	protected void initiateRegister(String firstName, String lastName, String userName, String password, String email) {
		Intent intent = new Intent(this, RegisterService.class);
		intent.putExtra(RegisterService.USER_NAME_ARG, userName);
		intent.putExtra(RegisterService.PASSWORD_ARG, password);
		intent.putExtra(RegisterService.EMAIL_ARG, email);
		intent.putExtra(RegisterService.FIRST_NAME_ARG, firstName);
		intent.putExtra(RegisterService.LAST_NAME_ARG, lastName);
		startService(intent);
	}

	private void registerReceiver() {
		IntentFilter filter = new IntentFilter(RegisterService.REGISTER_FINISHED_NOTIF);
		mRegisterReceiver = new RegisterReceiver();
		registerReceiver(mRegisterReceiver, filter);
	}

	private void parseRegisterAndGo(final String xml) {
		Constants.broadcastDoingSomethingNotification(Constants.PARSING_USER_DATA);
		new AsyncTask<Void, Void, UserDTO>() {
			@Override
			protected UserDTO doInBackground(Void... params) {
				return new UserDTO(new XMLHelper(xml));
			}

			@Override
			protected void onPostExecute(UserDTO u) {
				Constants.broadcastFinishedDoingSomethingNotification(Constants.PARSING_USER_DATA);
				enableButton(R.id.register_submit, true);
				if (u.errors.size() == 0) {
					MainApplication.GlobalState.setCurrentUser(u);
					finish();
				} else {
					showError(u.errors);
				}
			}
		}.execute();
	}

	private void startLoginActivity() {
		Intent newIntent = new Intent();
		newIntent.setClass(getApplication(), LoginActivity.class);
		startActivity(newIntent);
	}

	public class RegisterReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getBooleanExtra(RegisterService.SUCCESS_ARG, false)) {
				parseRegisterAndGo(intent.getStringExtra(RegisterService.XML_ARG));
			} else {
				enableButton(R.id.register_submit, true);
				showError(intent.getStringExtra(RegisterService.ERROR_ARG));
			}
		}
	}

}
