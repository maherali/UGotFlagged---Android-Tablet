package com.agilismobility.ugotflagged;

import java.util.ArrayList;
import java.util.HashMap;

import com.agilismobility.ugotflagged.dtos.PostDTO;
import com.agilismobility.ugotflagged.dtos.UserDTO;

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

	public static class GlobalState {

		static int currUserID;
		static HashMap<Integer, UserDTO> users = new HashMap<Integer, UserDTO>();

		public static void setCurrentUser(UserDTO user) {
			currUserID = user.identifier;
			users.put(currUserID, user);
		}

		public static ArrayList<PostDTO> getStream() {
			return users.get(currUserID).posts;
		}
		
	}

}
