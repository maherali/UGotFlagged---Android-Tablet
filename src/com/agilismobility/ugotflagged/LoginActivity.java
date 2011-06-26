package com.agilismobility.ugotflagged;

import com.agilismobility.ugotflagged.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class LoginActivity extends Activity {

	public static final String LOGIN_PREF = "LOGIN_PREF";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		((Button) findViewById(R.id.login_submit))
				.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						TextView email = (TextView) findViewById(R.id.login_email);
						TextView password = (TextView) findViewById(R.id.login_password);

						SharedPreferences settings = getSharedPreferences(
								LOGIN_PREF, 0);
						SharedPreferences.Editor editor = settings.edit();

						editor.putString("login_email", email.getText()
								.toString());
						editor.putString("login_password", password.getText()
								.toString());
						;
						editor.commit();
						
						Intent intent = new Intent();
						intent.setClass(getApplication(), FlagsActivity.class);
						startActivity(intent);
						finish();
					}
				});
	}

}
