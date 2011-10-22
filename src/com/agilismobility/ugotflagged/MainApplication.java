package com.agilismobility.ugotflagged;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.agilismobility.ugotflagged.dtos.GeocodeDTO;
import com.agilismobility.ugotflagged.dtos.PostDTO;
import com.agilismobility.ugotflagged.dtos.UserDTO;
import com.agilismobility.ugotflagged.dtos.UsersDTO;
import com.agilismobility.ugotflagged.services.ImageResources;
import com.agilismobility.ugotflagged.ui.fragments.followed.FollowedFlagDetailsFragment;
import com.agilismobility.ugotflagged.ui.fragments.followed.FollowedUserPostsFragment;
import com.agilismobility.ugotflagged.ui.fragments.followed.FollowedUsersFragment;
import com.agilismobility.ugotflagged.ui.fragments.followers.FollowersFragment;
import com.agilismobility.ugotflagged.ui.fragments.stream.StreamFlagDetailsFragment;
import com.agilismobility.ugotflagged.ui.fragments.stream.StreamFragment;
import com.agilismobility.ugotflagged.utils.PipeStream;
import com.agilismobility.ugotflagged.utils.XMLHelper;

public class MainApplication extends Application {
	private static MainApplication mInstance;
	private final String TAG = "MainApplication";
	private Location mCurrentLocation;
	private StreamFragment streamFragment;
	private StreamFlagDetailsFragment streamFlagDetailsFragment;

	private FollowedUsersFragment followedUsersFragment;
	private FollowedUserPostsFragment followedUserFlags;
	private FollowedFlagDetailsFragment followedFlagDetailsFragment;

	private FollowersFragment followersFragment;
	private CacheDatabase cacheDB;

	private static State[] states;
	private static Vehicle[] vehicles;

	public StreamFlagDetailsFragment getStreamFlagDetailsFragment() {
		return streamFlagDetailsFragment;
	}

	public StreamFragment getStreamFragment() {
		return streamFragment;
	}

	public FollowedUsersFragment getFollowedUsersFragment() {
		return followedUsersFragment;
	}

	public FollowedUserPostsFragment getFollowedUserPostsFragment() {
		return followedUserFlags;
	}

	public FollowedFlagDetailsFragment getFollowedFlagDetailsFragment() {
		return followedFlagDetailsFragment;
	}

	public FollowersFragment getFollowersFragment() {
		return followersFragment;
	}

	public void createFragments() {
		streamFragment = new StreamFragment();
		streamFlagDetailsFragment = new StreamFlagDetailsFragment();
		followedUsersFragment = new FollowedUsersFragment();
		followedUserFlags = new FollowedUserPostsFragment();
		followedFlagDetailsFragment = new FollowedFlagDetailsFragment();
		followersFragment = new FollowersFragment();
	}

	@Override
	public void onCreate() {
		mInstance = this;
		cacheDB = new CacheDatabase(this);
		fillinStates();
		fillinvehicles();
		super.onCreate();
	}

	public static State[] getStates() {
		return states;
	}

	public static Vehicle[] getVehicles() {
		return vehicles;
	}

	private void fillinStates() {
		AssetManager assetManager = getAssets();
		InputStream inputStream = null;
		try {
			inputStream = assetManager.open("states.xml");
		} catch (IOException e) {
		}
		String xml = "";
		PipeStream pipe = new PipeStream(inputStream);
		try {
			pipe.peek();
			xml = pipe.getData();
		} catch (Exception e) {
		}
		if (!"".equals(xml)) {
			states = State.parse(new XMLHelper(xml)).toArray(new State[0]);
		}
	}

	private void fillinvehicles() {
		AssetManager assetManager = getAssets();
		InputStream inputStream = null;
		try {
			inputStream = assetManager.open("vehicles.xml");
		} catch (IOException e) {
		}
		String xml = "";
		PipeStream pipe = new PipeStream(inputStream);
		try {
			pipe.peek();
			xml = pipe.getData();
		} catch (Exception e) {
		}
		if (!"".equals(xml)) {
			vehicles = Vehicle.parse(new XMLHelper(xml)).toArray(new Vehicle[0]);
		}
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
		static HashMap<Integer, ArrayList<PostDTO>> userPosts = new HashMap<Integer, ArrayList<PostDTO>>();
		static GeocodeDTO geocodedAddress;

		public static void setCurrentUser(UserDTO user) {
			currUserID = user.identifier;
			users.put(currUserID, user);
		}

		public static ArrayList<PostDTO> getStream() {
			return getCurrentUser() != null ? getCurrentUser().posts : new ArrayList<PostDTO>();
		}

		public static UserDTO getCurrentUser() {
			return users.get(currUserID);
		}

		public static ArrayList<PostDTO> getUserPosts(int userID) {
			return userPosts.get(userID);
		}

		public static void setUserPosts(int userID, ArrayList<PostDTO> posts) {
			userPosts.put(userID, posts);
		}

		public static void clearData() {
			users.clear();
			userPosts.clear();
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

		private static void updatePost(ArrayList<PostDTO> array, PostDTO post) {
			int foundAt = -1;
			for (int i = 0; i < array.size(); i++) {
				PostDTO p = array.get(i);
				if (p.identifier == post.identifier) {
					foundAt = i;
					break;
				}
			}
			if (foundAt >= 0) {
				array.remove(foundAt);
				array.add(foundAt, post);
			}
		}

		public static void updatePost(PostDTO post) {
			updatePost(getStream(), post);
			for (ArrayList<PostDTO> array : userPosts.values()) {
				updatePost(array, post);
			}
		}

		private static PostDTO findPost(ArrayList<PostDTO> array, int identifier) {
			for (int i = 0; i < array.size(); i++) {
				PostDTO p = array.get(i);
				if (p.identifier == identifier) {
					return p;
				}
			}
			return null;
		}

		public static PostDTO getPost(int identifier) {
			PostDTO returnedPost = null;
			returnedPost = findPost(getStream(), identifier);
			if (returnedPost != null) {
				return returnedPost;
			}
			for (ArrayList<PostDTO> array : userPosts.values()) {
				returnedPost = findPost(array, identifier);
				if (returnedPost != null) {
					return returnedPost;
				}
			}
			return null;
		}

		public static void setCurrentGeocodedAddress(GeocodeDTO geo) {
			geocodedAddress = geo;
		}

		public static GeocodeDTO getCurrentGeocodedAddress() {
			return geocodedAddress;
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
