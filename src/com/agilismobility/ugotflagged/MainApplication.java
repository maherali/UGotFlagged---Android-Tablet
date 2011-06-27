package com.agilismobility.ugotflagged;

import android.app.Application;
import android.content.Context;

public class MainApplication extends Application {

	private static Context mInstance;

	@Override
	public void onCreate() {
		mInstance = this;
		super.onCreate();
	}

	@Override
	public void onLowMemory() {
		// TODO Auto-generated method stub
		super.onLowMemory();
	}

	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
		super.onTerminate();
	}

	public static Context getInstance() {
		return mInstance;
	}

}
