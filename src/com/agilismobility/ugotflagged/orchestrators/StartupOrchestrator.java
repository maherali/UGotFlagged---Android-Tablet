package com.agilismobility.ugotflagged.orchestrators;

import android.app.Activity;
import android.content.Intent;

import com.agilismobility.ugotflagged.UI.FlagsActivity;
import com.agilismobility.ugotflagged.UI.LoginActivity;

public class StartupOrchestrator {
	private Activity mContext;
	private boolean remembeMe = false;

	public StartupOrchestrator(Activity context) {
		mContext = context;
	}

	public void start() {
		if (remembeMe) {
			Intent intent = new Intent();
			intent.setClass(mContext, FlagsActivity.class);
			mContext.startActivity(intent);
		} else {
			Intent intent = new Intent();
			intent.setClass(mContext, LoginActivity.class);
			mContext.startActivity(intent);
		}
		mContext.finish();
		this.mContext = null;
	}
}

// Start
// if remember me is set, ask the service for a cached login
// if there are no cached data the ...
// if there are cached data, start the FlagsActivity
