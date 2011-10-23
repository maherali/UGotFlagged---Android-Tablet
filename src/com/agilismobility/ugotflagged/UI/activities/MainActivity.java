package com.agilismobility.ugotflagged.ui.activities;

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
import android.widget.TextView;

import com.agilismobility.LocationAwareness;
import com.agilismobility.LocationAwareness.ILocationResponder;
import com.agilismobility.ugotflagged.MainApplication;
import com.agilismobility.ugotflagged.R;
import com.agilismobility.ugotflagged.dtos.GeocodeDTO;
import com.agilismobility.ugotflagged.dtos.PostDTO;
import com.agilismobility.ugotflagged.dtos.UserDTO;
import com.agilismobility.ugotflagged.services.GeoCodingService;
import com.agilismobility.ugotflagged.services.PostService;
import com.agilismobility.ugotflagged.services.RefreshService;
import com.agilismobility.ugotflagged.services.SessionService;
import com.agilismobility.ugotflagged.utils.XMLHelper;
import com.agilismobility.utils.Constants;

public class MainActivity extends BaseActivity implements TabListener, ILocationResponder {

	private static final String SELECTED_TAB = "selected_tab";

	private String[] ACTIONS = { "Stream", "Favorites", "Followed" };

	LocationAwareness mLocationAwareness;
	RefreshStreamReceiver mRefreshReceiver;
	CommentReceiver addCommentReceiver;
	LikeUnlikeReceiver likeUnlikeReceiver;
	GeoCodingReceiver mGeocodingService;

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
		addInterestingNotificationName(Constants.FINDING_MOST_LIKED_FLAGS);
		addInterestingNotificationName(Constants.FINDING_USER_POSTS);

		((Button) findViewById(R.id.refresh_button)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				refreshCurrentStream();
			}
		});
		if (savedInstanceState != null) {
			mSelectedTabPosition = savedInstanceState.getInt(SELECTED_TAB);
			getActionBar().setSelectedNavigationItem(mSelectedTabPosition);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		mLocationAwareness.hasResumed();
		updateFragmentsContent();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mLocationAwareness.hasPaused();
		((MainApplication) getApplication()).getImageCache().clear();
	}

	@Override
	protected void onDestroy() {
		if (mLocationAwareness != null) {
			mLocationAwareness.hasDestroyed();
		}
		if (mRefreshReceiver != null) {
			unregisterReceiver(mRefreshReceiver);
		}
		if (mGeocodingService != null) {
			unregisterReceiver(mGeocodingService);
		}
		if (addCommentReceiver != null) {
			unregisterReceiver(addCommentReceiver);
		}
		if (likeUnlikeReceiver != null) {
			unregisterReceiver(likeUnlikeReceiver);
		}
		super.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(SELECTED_TAB, getActionBar().getSelectedTab().getPosition());
	}

	private void setupCurrentFragment() {
		if (getActionBar().getTabCount() == 0 || mSelectedTabPosition == getActionBar().getTabAt(0).getPosition()) {
			setupStreamTab();
		} else if (mSelectedTabPosition == getActionBar().getTabAt(1).getPosition()) {
			setupFavoritesTab();
		} else if (mSelectedTabPosition == getActionBar().getTabAt(2).getPosition()) {
			setupFollowedTab();
		}
	}

	private void addOrReplace(final int id, Fragment frag, Fragment newFrag) {
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		if (frag == null) {
			ft.add(id, newFrag);
		} else {
			ft.replace(id, newFrag);
		}
		ft.commit();
	}

	private void setupStreamTab() {
		setContentView(R.layout.main);
		addOrReplace(R.id.left_frag, getFragmentManager().findFragmentById(R.id.left_frag),
				((MainApplication) getApplication()).getStreamFragment());
		addOrReplace(R.id.right_frag, getFragmentManager().findFragmentById(R.id.right_frag),
				((MainApplication) getApplication()).getStreamFlagDetailsFragment());
		TextView streamTitle = (TextView) findViewById(R.id.stream_title);
		UserDTO currUser = MainApplication.GlobalState.getCurrentUser();
		if (currUser != null) {
			streamTitle.setText(currUser.userName + "'s" + " Stream");
		}
	}

	private void setupFavoritesTab() {
		setContentView(R.layout.liked);
		addOrReplace(R.id.left_frag, getFragmentManager().findFragmentById(R.id.left_frag),
				((MainApplication) getApplication()).getLikedFragment());
		addOrReplace(R.id.right_frag, getFragmentManager().findFragmentById(R.id.right_frag),
				((MainApplication) getApplication()).getLikedFlagDetailsFragment());
	}

	private void setupFollowedTab() {
		setContentView(R.layout.followed);
		addOrReplace(R.id.left_frag, getFragmentManager().findFragmentById(R.id.left_frag),
				((MainApplication) getApplication()).getFollowedUsersFragment());
		addOrReplace(R.id.middle_frag, getFragmentManager().findFragmentById(R.id.middle_frag),
				((MainApplication) getApplication()).getFollowedUserPostsFragment());
		addOrReplace(R.id.right_frag, getFragmentManager().findFragmentById(R.id.right_frag),
				((MainApplication) getApplication()).getFollowedFlagDetailsFragment());
	}

	private void showProgress(boolean show) {
		View refreshView = ((Button) getActionBar().getCustomView().findViewById(R.id.refresh_button));
		View progressView = ((ProgressBar) getActionBar().getCustomView().findViewById(R.id.progress));
		if (show) {
			refreshView.setVisibility(View.GONE);
			progressView.setVisibility(View.VISIBLE);
		} else {
			refreshView.setVisibility(View.VISIBLE);
			progressView.setVisibility(View.GONE);
		}
	}

	@Override
	protected void receivedDoingInterestingNotification(String notif) {
		if (Constants.LOGGING_IN.equals(notif) || Constants.PARSING_USER_DATA.equals(notif) || Constants.REFRESHING_STREAM.equals(notif)
				|| Constants.FINDING_MOST_LIKED_FLAGS.equals(notif) || Constants.FINDING_USER_POSTS.equals(notif)) {
			showProgress(true);
		}
	}

	@Override
	protected void receivedFinishedDoingInterestingNotification(String notif) {
		if (Constants.LOGGING_IN.equals(notif) || Constants.PARSING_USER_DATA.equals(notif) || Constants.REFRESHING_STREAM.equals(notif)
				|| Constants.FINDING_MOST_LIKED_FLAGS.equals(notif) || Constants.FINDING_USER_POSTS.equals(notif)) {
			showProgress(false);
		}
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
			int curPos;
			curPos = ((MainApplication) getApplication()).getStreamFragment().getCurrentPosition();
			((MainApplication) getApplication()).createFragments();
			setupCurrentFragment();
			((MainApplication) getApplication()).getStreamFragment().setCurrentPosition(curPos);
			((MainApplication) getApplication()).getStreamFlagDetailsFragment().setCurrentPosition(curPos);
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
			logout();
			return true;
		case R.id.add_flag:
			addFlag();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void addFlag() {
		Intent intent = new Intent();
		intent.setClass(getApplication(), AddFlagActivity.class);
		startActivity(intent);
	}

	private void logout() {
		((MainApplication) getApplication()).clearData();
		LoginActivity.enableRememberMe(false);
		finish();
		Intent intent = new Intent();
		intent.setClass(getApplication(), LoginActivity.class);
		startActivity(intent);
		intent = new Intent(this, SessionService.class);
		intent.putExtra(SessionService.ACTION, SessionService.LOGOUT_ACTION);
		startService(intent);
	}

	@Override
	public void newLocationFound(Location location) {
		((MainApplication) getApplication()).updateCurrentLocation(location);
		Intent intent = new Intent(this, GeoCodingService.class);
		intent.putExtra(GeoCodingService.ACTION, GeoCodingService.GEOCODE_ACTION);
		intent.putExtra(GeoCodingService.GEO_LAT_PARAM, location.getLatitude() + "");
		intent.putExtra(GeoCodingService.GEO_LNG_PARAM, location.getLongitude() + "");
		startService(intent);
		updateFragmentsContent();
	}

	public class GeoCodingReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (GeoCodingService.GEOCODE_ACTION.equals(intent.getStringExtra(GeoCodingService.ACTION))) {
				if (intent.getBooleanExtra(GeoCodingService.SUCCESS_ARG, false)) {
					parseGeocodingAndGo(intent.getStringExtra(GeoCodingService.XML_ARG));
				}
			}
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

	private void refreshCurrentStream() {
		switch (mSelectedTabPosition) {
		case 0:
			refreshStream();
			break;
		case 1:
			refreshFavs();
			break;
		case 2:
			refreshFollowed();
			break;
		default:
			break;
		}

	}

	private void refreshFollowed() {
		((MainApplication) getApplication()).getFollowedUsersFragment().findFollowedUsers();
	}

	private void refreshFavs() {
		((MainApplication) getApplication()).getLikedFragment().findLiked();
	}

	private void registerReceiver() {
		IntentFilter filter = new IntentFilter(RefreshService.REFRESHING_FINISHED_NOTIF);
		mRefreshReceiver = new RefreshStreamReceiver();
		registerReceiver(mRefreshReceiver, filter);

		filter = new IntentFilter(GeoCodingService.GEO_CODE_FINISHED_NOTIF);
		mGeocodingService = new GeoCodingReceiver();
		registerReceiver(mGeocodingService, filter);

		filter = new IntentFilter(PostService.ADD_COMMENT_FINISHED_NOTIF);
		addCommentReceiver = new CommentReceiver();
		registerReceiver(addCommentReceiver, filter);

		filter = new IntentFilter(PostService.LIKE_UNLIKE_FINISHED_NOTIF);
		likeUnlikeReceiver = new LikeUnlikeReceiver();
		registerReceiver(likeUnlikeReceiver, filter);
	}

	public class RefreshStreamReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getBooleanExtra(RefreshService.SUCCESS_ARG, false)) {
				parseRefreshStreamAndGo(intent.getStringExtra(RefreshService.XML_ARG));
			} else {
				showError(intent.getStringExtra(RefreshService.ERROR_ARG));
			}
		}
	}

	public class LikeUnlikeReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (PostService.LIKE_ACTION.equals(intent.getStringExtra(PostService.ACTION))
					|| PostService.UNLIKE_ACTION.equals(intent.getStringExtra(PostService.ACTION))) {
				if (intent.getBooleanExtra(SessionService.SUCCESS_ARG, false)) {
					parsePostAndGo(intent.getStringExtra(SessionService.XML_ARG));
				} else {
					showError(intent.getStringExtra(SessionService.ERROR_ARG));
				}
			}
		}
	}

	public class CommentReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (PostService.ADD_COMMENT_ACTION.equals(intent.getStringExtra(PostService.ACTION))) {
				if (intent.getBooleanExtra(SessionService.SUCCESS_ARG, false)) {
					parsePostAndGo(intent.getStringExtra(SessionService.XML_ARG));
				} else {
					showError(intent.getStringExtra(SessionService.ERROR_ARG));
				}
			}
		}
	}

	private void parseRefreshStreamAndGo(final String xml) {
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
					updateFragmentsContent();
				} else {
					showError(u.errors);
				}
			}
		}.execute();
	}

	private void parseGeocodingAndGo(final String xml) {
		Constants.broadcastDoingSomethingNotification(Constants.PARSING_GEO_DATA);
		new AsyncTask<Void, Void, GeocodeDTO>() {
			@Override
			protected GeocodeDTO doInBackground(Void... params) {
				return new GeocodeDTO(new XMLHelper(xml));
			}

			@Override
			protected void onPostExecute(GeocodeDTO geo) {
				Constants.broadcastFinishedDoingSomethingNotification(Constants.PARSING_GEO_DATA);
				if (geo.errors.size() == 0) {
					MainApplication.GlobalState.setCurrentGeocodedAddress(geo);
				}
			}
		}.execute();
	}

	private void parsePostAndGo(final String xml) {
		Constants.broadcastDoingSomethingNotification(Constants.PARSING_POST_DATA);
		new AsyncTask<Void, Void, PostDTO>() {
			@Override
			protected PostDTO doInBackground(Void... params) {
				return new PostDTO(new XMLHelper(xml));
			}

			@Override
			protected void onPostExecute(PostDTO post) {
				Constants.broadcastFinishedDoingSomethingNotification(Constants.PARSING_POST_DATA);
				if (post.errors.size() == 0) {
					MainApplication.GlobalState.updatePost(post);
					updateFragmentsContent();
				} else {
					showError(post.errors);
				}
			}
		}.execute();
	}

	public void updateFragmentsContent() {
		((MainApplication) getApplication()).getStreamFragment().update();
		((MainApplication) getApplication()).getStreamFlagDetailsFragment().update();
		((MainApplication) getApplication()).getLikedFragment().update();
		((MainApplication) getApplication()).getLikedFlagDetailsFragment().update();
		((MainApplication) getApplication()).getFollowedUsersFragment().update();
		((MainApplication) getApplication()).getFollowedUserPostsFragment().update();
		((MainApplication) getApplication()).getFollowedFlagDetailsFragment().update();
	}
}
