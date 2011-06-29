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
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

public class LocationChangedReceiver extends BroadcastReceiver {

	protected static String TAG = "LocationChangedReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		String locationKey = LocationManager.KEY_LOCATION_CHANGED;
		String providerEnabledKey = LocationManager.KEY_PROVIDER_ENABLED;
		if (intent.hasExtra(providerEnabledKey)) {
			if (!intent.getBooleanExtra(providerEnabledKey, true)) {
				Intent providerDisabledIntent = new Intent(Constants.ACTIVE_LOCATION_UPDATE_PROVIDER_DISABLED);
				context.sendBroadcast(providerDisabledIntent);
			}
		}
		if (intent.hasExtra(locationKey)) {
			Location location = (Location) intent.getExtras().get(locationKey);
			Log.d(TAG, "Actively Updating place list");
			Intent updateServiceIntent = new Intent(Constants.NEW_LOCATION_FOUND_ACTION);
			updateServiceIntent.putExtra(Constants.EXTRA_KEY_LOCATION, location);
			updateServiceIntent.putExtra(Constants.EXTRA_KEY_RADIUS, Constants.DEFAULT_RADIUS);
			updateServiceIntent.putExtra(Constants.EXTRA_KEY_FORCEREFRESH, true);
			context.sendBroadcast(updateServiceIntent);
		}
	}
}