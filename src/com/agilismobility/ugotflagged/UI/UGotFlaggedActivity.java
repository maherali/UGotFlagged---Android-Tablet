package com.agilismobility.ugotflagged.UI;

import com.agilismobility.ugotflagged.orchestrators.StartupOrchestrator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class UGotFlaggedActivity extends BaseActivity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		new StartupOrchestrator(this).start();
	}

}