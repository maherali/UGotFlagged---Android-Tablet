package com.agilismobility.ugotflagged;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class UGotFlaggedActivity extends Activity {

	private boolean remembeMe = false;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (remembeMe) {
			Intent intent = new Intent();
			intent.setClass(getApplication(), FlagsActivity.class);
			startActivity(intent);
		} else {
			Intent intent = new Intent();
			intent.setClass(getApplication(), LoginActivity.class);
			startActivity(intent);
		}
		finish();
	}

}