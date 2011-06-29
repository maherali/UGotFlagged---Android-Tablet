/*
 * Copyright 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.agilismobility.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

public class PassiveLocationChangedReceiver extends BroadcastReceiver {

	protected static String TAG = "PassiveLocationChangedReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		String key = LocationManager.KEY_LOCATION_CHANGED;
		Location location = null;
		if (intent.hasExtra(key)) {
			location = (Location) intent.getExtras().get(key);
		} else {
			LegacyLastLocationFinder lastLocationFinder = new LegacyLastLocationFinder(context);
			location = lastLocationFinder.getLastBestLocation(Constants.MAX_DISTANCE, System.currentTimeMillis() - Constants.MAX_TIME);
			SharedPreferences prefs = context.getSharedPreferences(Constants.SHARED_PREFERENCE_FILE, Context.MODE_PRIVATE);
			long lastTime = prefs.getLong(Constants.SP_KEY_LAST_LIST_UPDATE_TIME, Long.MIN_VALUE);
			long lastLat = prefs.getLong(Constants.SP_KEY_LAST_LIST_UPDATE_LAT, Long.MIN_VALUE);
			long lastLng = prefs.getLong(Constants.SP_KEY_LAST_LIST_UPDATE_LNG, Long.MIN_VALUE);
			Location lastLocation = new Location(Constants.CONSTRUCTED_LOCATION_PROVIDER);
			lastLocation.setLatitude(lastLat);
			lastLocation.setLongitude(lastLng);
			if ((lastTime > System.currentTimeMillis() - Constants.MAX_TIME) || (lastLocation.distanceTo(location) < Constants.MAX_DISTANCE)) {
				location = null;
			}
		}
		if (location != null) {
			Log.d(TAG, "Passivly updating place list.");
			Intent updateServiceIntent = new Intent(Constants.NEW_LOCATION_FOUND_ACTION);
			updateServiceIntent.putExtra(Constants.EXTRA_KEY_LOCATION, location);
			updateServiceIntent.putExtra(Constants.EXTRA_KEY_RADIUS, Constants.DEFAULT_RADIUS);
			updateServiceIntent.putExtra(Constants.EXTRA_KEY_FORCEREFRESH, false);
			context.sendBroadcast(updateServiceIntent);
		}
	}
}