package com.agilismobility.ugotflagged.ui.activities;

import android.content.Intent;
import android.os.Bundle;

public class UGotFlaggedActivity extends BaseActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = new Intent();
		intent.setClass(this, LoginActivity.class);
		startActivity(intent);
		finish();
	}

}