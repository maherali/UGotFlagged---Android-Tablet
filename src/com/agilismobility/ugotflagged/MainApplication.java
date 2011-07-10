package com.agilismobility.ugotflagged;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Application;
import android.content.Context;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.agilismobility.ugotflagged.UI.fragments.FlagDetailsFragment;
import com.agilismobility.ugotflagged.UI.fragments.FollowedUsersFragment;
import com.agilismobility.ugotflagged.UI.fragments.FollowersFragment;
import com.agilismobility.ugotflagged.UI.fragments.StreamFragment;
import com.agilismobility.ugotflagged.UI.fragments.UserPostsFragment;
import com.agilismobility.ugotflagged.dtos.PostDTO;
import com.agilismobility.ugotflagged.dtos.UserDTO;
import com.agilismobility.ugotflagged.dtos.UsersDTO;
import com.agilismobility.ugotflagged.services.ImageResources;

public class MainApplication extends Application {

	private static MainApplication mInstance;
	private final String TAG = "MainApplication";
	private Location mCurrentLocation;

	private StreamFragment streamFragment;
	private FlagDetailsFragment flagDetailsFragment;
	private FollowedUsersFragment followedUsersFragment;
	private FollowersFragment followersFragment;
	private UserPostsFragment followedUserFlags;
	private CacheDatabase cacheDB;

	public FlagDetailsFragment getFlagDetailsFragment() {
		return flagDetailsFragment;
	}

	public StreamFragment getStreamFragment() {
		return streamFragment;
	}

	public FollowedUsersFragment getFollowedUsersFragment() {
		return followedUsersFragment;
	}

	public FollowersFragment getFollowersFragment() {
		return followersFragment;
	}

	public UserPostsFragment getFollowedUserPostsFragment() {
		return followedUserFlags;
	}

	public void createFragments() {
		streamFragment = new StreamFragment();
		flagDetailsFragment = new FlagDetailsFragment();
		followedUsersFragment = new FollowedUsersFragment();
		followersFragment = new FollowersFragment();
		followedUserFlags = new UserPostsFragment();
	}

	@Override
	public void onCreate() {
		mInstance = this;
		cacheDB = new CacheDatabase(this);
		super.onCreate();
	}

	public CacheDatabase getCache() {
		return cacheDB;
	}

	public void setXML(String url, String xml) {
		cacheDB.setXML(url, xml, GlobalState.getCurrentUser() != null ? GlobalState.getCurrentUser().userName : null);
	}

	public String getXML(String url) {
		return cacheDB.getXML(url, GlobalState.getCurrentUser() != null ? GlobalState.getCurrentUser().userName : null);
	}

	@Override
	public void onLowMemory() {
		Log.d(TAG, "***********************onLowMemory***********************");
		getImageCache().clear();
		super.onLowMemory();
	}

	public ImageResources getImageCache() {
		return ImageResources.getInstance();
	}

	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
		super.onTerminate();
	}

	public static MainApplication getInstance() {
		return mInstance;
	}

	public static class GlobalState {

		static int currUserID;
		static HashMap<Integer, UserDTO> users = new HashMap<Integer, UserDTO>();
		static UsersDTO followedUsers;

		public static void setCurrentUser(UserDTO user) {
			currUserID = user.identifier;
			users.put(currUserID, user);
		}

		public static ArrayList<PostDTO> getStream() {
			return users.get(currUserID) != null ? users.get(currUserID).posts : new ArrayList<PostDTO>();
		}

		public static UserDTO getCurrentUser() {
			return users.get(currUserID);
		}

		public static void clearData() {
			users.clear();
			followedUsers = null;
		}

		public static UsersDTO getFollowedUsers() {
			return followedUsers;
		}

		public static void setFollowedUsers(UsersDTO us) {
			followedUsers = us;
		}

		public static void removeFollowedUser(UserDTO user) {
			for (int i = 0; i < getFollowedUsers().getUsers().size(); i++) {
				UserDTO aUser = getFollowedUsers().getUsers().get(i);
				if (aUser.userName.equals(user.userName)) {
					getFollowedUsers().getUsers().remove(i);
					break;
				}
			}
		}

	}

	public Location getCurrentLocation() {
		return mCurrentLocation;
	}

	public void updateCurrentLocation(Location location) {
		mCurrentLocation = location;
	}

	public void clearData() {
		GlobalState.clearData();
		getImageCache().clear();
	}

	public static boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) MainApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo network = cm.getActiveNetworkInfo();
		if (network != null) {
			return network.isAvailable();
		}
		return false;
	}
}
