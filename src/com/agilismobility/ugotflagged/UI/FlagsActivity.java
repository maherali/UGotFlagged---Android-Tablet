package com.agilismobility.ugotflagged.UI;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.agilismobility.LocationAwareness;
import com.agilismobility.LocationAwareness.ILocationResponder;
import com.agilismobility.ugotflagged.MainApplication;
import com.agilismobility.ugotflagged.R;
import com.agilismobility.ugotflagged.UI.fragments.FlagDetailsFragment;
import com.agilismobility.ugotflagged.UI.fragments.FlagsFragment;
import com.agilismobility.ugotflagged.UI.fragments.FollowedUsersFragment;
import com.agilismobility.ugotflagged.UI.fragments.FollowersFragment;
import com.agilismobility.ugotflagged.UI.fragments.UserFlagsFragment;
import com.agilismobility.ugotflagged.dtos.UserDTO;
import com.agilismobility.ugotflagged.services.RefreshService;
import com.agilismobility.ugotflagged.utils.XMLHelper;
import com.agilismobility.utils.Constants;

public class FlagsActivity extends BaseActivity implements TabListener, ILocationResponder {

	private static final String SELECTED_TAB = "selected_tab";

	private String[] ACTIONS = { "Stream", "Followed" };

	LocationAwareness mLocationAwareness;
	RefreshReceiver mRefreshReceiver;

	private int mSelectedTabPosition = -1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		((MainApplication) getApplication()).createFragments();
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		registerReceiver();
		setContentView(R.layout.main);
		setupCurrentFragment();
		ActionBar bar = getActionBar();
		for (int i = 0; i < ACTIONS.length; i++) {
			bar.addTab(bar.newTab().setText(ACTIONS[i]).setTabListener(this));
		}
		bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_USE_LOGO);
		bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		bar.setDisplayShowHomeEnabled(true);
		bar.setCustomView(R.layout.refresh);
		this.mLocationAwareness = new LocationAwareness(this, this);
		addInterestingNotificationName(Constants.LOGGING_IN);
		addInterestingNotificationName(Constants.PARSING_USER_DATA);
		addInterestingNotificationName(Constants.REFRESHING_STREAM);
		((Button) findViewById(R.id.refresh_button)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				refreshStream();
			}
		});
		if (savedInstanceState != null) {
			mSelectedTabPosition = savedInstanceState.getInt(SELECTED_TAB);
			getActionBar().setSelectedNavigationItem(mSelectedTabPosition);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(SELECTED_TAB, getActionBar().getSelectedTab().getPosition());
	}

	private void setupCurrentFragment() {
		if (getActionBar().getTabCount() == 0 || mSelectedTabPosition == getActionBar().getTabAt(0).getPosition()) {
			setupTabOneView();
		} else if (mSelectedTabPosition == getActionBar().getTabAt(1).getPosition()) {
			setupTabTwoView();
		}
	}

	private void setupTabTwoView() {
		setContentView(R.layout.followed);
		Fragment leftFrag = getFragmentManager().findFragmentById(R.id.left_frag);
		Fragment middleFrag = getFragmentManager().findFragmentById(R.id.middle_frag);
		Fragment rightFrag = getFragmentManager().findFragmentById(R.id.right_frag);
		if (leftFrag == null) {
			FollowedUsersFragment newFragment = ((MainApplication) getApplication()).getFollowedUsersFragment();
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.add(R.id.left_frag, newFragment).commit();
		} else {
			FollowedUsersFragment newFragment = ((MainApplication) getApplication()).getFollowedUsersFragment();
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.replace(R.id.left_frag, newFragment).commit();
		}
		if (middleFrag == null) {
			UserFlagsFragment newFragment = ((MainApplication) getApplication()).getFollowedUserFlagsFragment();
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.add(R.id.middle_frag, newFragment).commit();
		} else {
			UserFlagsFragment newFragment = ((MainApplication) getApplication()).getFollowedUserFlagsFragment();
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.replace(R.id.middle_frag, newFragment).commit();
		}
		if (rightFrag == null) {
			FollowersFragment newFragment = ((MainApplication) getApplication()).getFollowersFragment();
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.add(R.id.right_frag, newFragment).commit();
		} else {
			FollowersFragment newFragment = ((MainApplication) getApplication()).getFollowersFragment();
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.replace(R.id.right_frag, newFragment).commit();
		}
	}

	private void setupTabOneView() {
		setContentView(R.layout.main);
		Fragment leftFrag = getFragmentManager().findFragmentById(R.id.left_frag);
		Fragment rightFrag = getFragmentManager().findFragmentById(R.id.right_frag);
		if (leftFrag == null) {
			FlagsFragment newFragment = ((MainApplication) getApplication()).getFlagsFragment();
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.add(R.id.left_frag, newFragment).commit();
		} else {
			FlagsFragment newFragment = ((MainApplication) getApplication()).getFlagsFragment();
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.replace(R.id.left_frag, newFragment).commit();
		}
		if (rightFrag == null) {
			FlagDetailsFragment newFragment = ((MainApplication) getApplication()).getFlagDetailsFragment();
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.add(R.id.right_frag, newFragment).commit();
		} else {
			FlagDetailsFragment newFragment = ((MainApplication) getApplication()).getFlagDetailsFragment();
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.replace(R.id.right_frag, newFragment).commit();
		}
	}

	private void showProgress() {
		((Button) getActionBar().getCustomView().findViewById(R.id.refresh_button)).setVisibility(View.GONE);
		((ProgressBar) getActionBar().getCustomView().findViewById(R.id.progress)).setVisibility(View.VISIBLE);
	}

	private void hideProgress() {
		((Button) getActionBar().getCustomView().findViewById(R.id.refresh_button)).setVisibility(View.VISIBLE);
		((ProgressBar) getActionBar().getCustomView().findViewById(R.id.progress)).setVisibility(View.GONE);
	}

	@Override
	protected void receivedDoingInterestingNotification(String notif) {
		if (Constants.LOGGING_IN.equals(notif)) {
			showProgress();
		} else if (Constants.PARSING_USER_DATA.equals(notif)) {
			showProgress();
		} else if (Constants.REFRESHING_STREAM.equals(notif)) {
			showProgress();
		}
	}

	@Override
	protected void receivedFinishedDoingInterestingNotification(String notif) {
		if (Constants.LOGGING_IN.equals(notif)) {
			hideProgress();
		} else if (Constants.PARSING_USER_DATA.equals(notif)) {
			hideProgress();
		} else if (Constants.REFRESHING_STREAM.equals(notif)) {
			hideProgress();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		mLocationAwareness.hasResumed();
		refreshFlags();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mLocationAwareness.hasPaused();
		((MainApplication) getApplication()).getImageCache().clear();
	}

	@Override
	protected void onDestroy() {
		mLocationAwareness.hasDestroyed();
		unregisterReceiver(mRefreshReceiver);
		super.onDestroy();
	}

	@Override
	public void onTabReselected(Tab arg0, FragmentTransaction arg1) {
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		if (mSelectedTabPosition == -1) {
			mSelectedTabPosition = tab.getPosition();
		} else {
			mSelectedTabPosition = tab.getPosition();
			int curPos = ((MainApplication) getApplication()).getFlagsFragment().getCurrentPosition();
			((MainApplication) getApplication()).createFragments();
			setupCurrentFragment();
			((MainApplication) getApplication()).getFlagsFragment().setCurrentPosition(curPos);
			((MainApplication) getApplication()).getFlagDetailsFragment().setCurrentPosition(curPos);
		}
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
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
			((MainApplication) getApplication()).clearData();
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
		refreshFlags();
	}

	private void refreshFlags() {
		Fragment frag = getFragmentManager().findFragmentById(R.id.left_frag);
		if (frag != null && frag instanceof FlagsFragment) {
			((FlagsFragment) frag).refresh();
		}
	}

	private void refreshStream() {
		UserDTO currUser = MainApplication.GlobalState.getCurrentUser();
		if (currUser != null) {
			Intent intent = new Intent(this, RefreshService.class);
			intent.putExtra(RefreshService.USER_NAME_ARG, currUser.userName);
			startService(intent);
		}
	}

	private void registerReceiver() {
		IntentFilter filter = new IntentFilter(RefreshService.REFRESHING_FINISHED_NOTIF);
		mRefreshReceiver = new RefreshReceiver();
		registerReceiver(mRefreshReceiver, filter);
	}

	public class RefreshReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getBooleanExtra(RefreshService.SUCCESS_ARG, false)) {
				parseRefreshAndGo(intent.getStringExtra(RefreshService.XML_ARG));
			} else {
				showError(intent.getStringExtra(RefreshService.ERROR_ARG));
			}
		}
	}

	private void parseRefreshAndGo(final String xml) {

		Constants.broadcastDoingSomethingNotification(Constants.PARSING_USER_DATA);
		new AsyncTask<Void, Void, UserDTO>() {
			@Override
			protected UserDTO doInBackground(Void... params) {
				return new UserDTO(new XMLHelper(xml));
			}

			@Override
			protected void onPostExecute(UserDTO u) {
				Constants.broadcastFinishedDoingSomethingNotification(Constants.PARSING_USER_DATA);
				if (u.errors.size() == 0) {
					MainApplication.GlobalState.setCurrentUser(u);
					refreshFlags();
				} else {
					showError(u.errors);
				}
			}
		}.execute();
	}

}
