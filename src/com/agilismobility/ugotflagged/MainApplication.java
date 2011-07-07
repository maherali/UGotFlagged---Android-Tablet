package com.agilismobility.ugotflagged;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Application;
import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.agilismobility.ugotflagged.UI.fragments.FlagDetailsFragment;
import com.agilismobility.ugotflagged.UI.fragments.FlagsFragment;
import com.agilismobility.ugotflagged.UI.fragments.FollowedUsersFragment;
import com.agilismobility.ugotflagged.UI.fragments.FollowersFragment;
import com.agilismobility.ugotflagged.UI.fragments.UserFlagsFragment;
import com.agilismobility.ugotflagged.dtos.PostDTO;
import com.agilismobility.ugotflagged.dtos.UserDTO;
import com.agilismobility.ugotflagged.dtos.UsersDTO;
import com.agilismobility.ugotflagged.services.ImageResources;

public class MainApplication extends Application {

	private static Context mInstance;
	private final String TAG = "MainApplication";
	private Location mCurrentLocation;

	private FlagsFragment flagsFragment;
	private FlagDetailsFragment flagDetailsFragment;
	private FollowedUsersFragment followedUsersFragment;
	private FollowersFragment followersFragment;
	private UserFlagsFragment followedUserFlags;

	public FlagDetailsFragment getFlagDetailsFragment() {
		return flagDetailsFragment;
	}

	public FlagsFragment getFlagsFragment() {
		return flagsFragment;
	}

	public FollowedUsersFragment getFollowedUsersFragment() {
		return followedUsersFragment;
	}

	public FollowersFragment getFollowersFragment() {
		return followersFragment;
	}

	public UserFlagsFragment getFollowedUserFlagsFragment() {
		return followedUserFlags;
	}

	public void createFragments() {
		flagsFragment = new FlagsFragment();
		flagDetailsFragment = new FlagDetailsFragment();
		followedUsersFragment = new FollowedUsersFragment();
		followersFragment = new FollowersFragment();
		followedUserFlags = new UserFlagsFragment();
	}

	@Override
	public void onCreate() {
		mInstance = this;
		super.onCreate();
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

	public static Context getInstance() {
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

}
