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

import android.content.Context;
import android.location.LocationManager;

import com.agilismobility.utils.base.ILastLocationFinder;
import com.agilismobility.utils.base.IStrictMode;
import com.agilismobility.utils.base.LocationUpdateRequester;
import com.agilismobility.utils.base.SharedPreferenceSaver;

public class PlatformSpecificImplementationFactory {

	public static ILastLocationFinder getLastLocationFinder(Context context) {
		return Constants.SUPPORTS_GINGERBREAD ? new GingerbreadLastLocationFinder(context) : new LegacyLastLocationFinder(context);
	}

	public static IStrictMode getStrictMode() {
		if (Constants.SUPPORTS_HONEYCOMB)
			return new HoneycombStrictMode();
		else if (Constants.SUPPORTS_GINGERBREAD)
			return new LegacyStrictMode();
		else
			return null;
	}

	public static LocationUpdateRequester getLocationUpdateRequester(LocationManager locationManager) {
		return Constants.SUPPORTS_GINGERBREAD ? new GingerbreadLocationUpdateRequester(locationManager) : new FroyoLocationUpdateRequester(locationManager);
	}

	public static SharedPreferenceSaver getSharedPreferenceSaver(Context context) {
		return Constants.SUPPORTS_GINGERBREAD ? new GingerbreadSharedPreferenceSaver(context) : Constants.SUPPORTS_FROYO ? new FroyoSharedPreferenceSaver(
				context) : new LegacySharedPreferenceSaver(context);
	}
}
