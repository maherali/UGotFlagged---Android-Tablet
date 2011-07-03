package com.agilismobility.ugotflagged;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Application;
import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.agilismobility.ugotflagged.dtos.PostDTO;
import com.agilismobility.ugotflagged.dtos.UserDTO;
import com.agilismobility.ugotflagged.services.ImageResources;

public class MainApplication extends Application {

	private static Context mInstance;
	private final String TAG = "MainApplication";
	private Location mCurrentLocation;

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
