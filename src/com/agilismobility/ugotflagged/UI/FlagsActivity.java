package com.agilismobility.ugotflagged.UI;

import com.agilismobility.LocationAwareness;
import com.agilismobility.LocationAwareness.ILocationResponder;
import com.agilismobility.ugotflagged.MainApplication;
import com.agilismobility.ugotflagged.R;
import com.agilismobility.ugotflagged.R.id;
import com.agilismobility.ugotflagged.R.layout;
import com.agilismobility.ugotflagged.R.menu;
import com.agilismobility.ugotflagged.UI.fragments.FlagsFragment;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class FlagsActivity extends BaseActivity implements TabListener, ILocationResponder {

	private String[] ACTIONS = { "Stream", "Followers", "Help", "Settings" };

	LocationAwareness mLocationAwareness;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		ActionBar bar = getActionBar();
		for (int i = 0; i < ACTIONS.length; i++) {
			bar.addTab(bar.newTab().setText(ACTIONS[i]).setTabListener(this));
		}
		bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_USE_LOGO);
		bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		bar.setDisplayShowHomeEnabled(true);
		this.mLocationAwareness = new LocationAwareness(this, this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mLocationAwareness.hasResumed();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mLocationAwareness.hasPaused();
	}

	@Override
	protected void onDestroy() {
		mLocationAwareness.hasDestroyed();
		super.onDestroy();
	}

	@Override
	public void onTabReselected(Tab arg0, FragmentTransaction arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.logout:
			LoginActivity.enableRememberMe(false);
			finish();
			Intent intent = new Intent();
			intent.setClass(getApplication(), LoginActivity.class);
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void newLocationFound(Location location) {
		((MainApplication) getApplication()).updateCurrentLocation(location);
		Log.i("****************", "found new location");
		FlagsFragment frag = (FlagsFragment) getFragmentManager().findFragmentById(R.id.frag_flags);
		frag.refresh();
	}

}
