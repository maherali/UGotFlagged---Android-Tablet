package com.agilismobility;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;

import com.agilismobility.utils.Constants;
import com.agilismobility.utils.LocationChangedReceiver;
import com.agilismobility.utils.PassiveLocationChangedReceiver;
import com.agilismobility.utils.PlatformSpecificImplementationFactory;
import com.agilismobility.utils.base.ILastLocationFinder;
import com.agilismobility.utils.base.LocationUpdateRequester;
import com.agilismobility.utils.base.SharedPreferenceSaver;

public class LocationAwareness {
	protected LocationManager locationManager;
	protected SharedPreferences prefs;
	protected Editor prefsEditor;
	protected SharedPreferenceSaver sharedPreferenceSaver;
	protected Criteria criteria;
	protected ILastLocationFinder lastLocationFinder;
	protected LocationUpdateRequester locationUpdateRequester;
	protected PendingIntent locationListenerPendingIntent;
	protected PendingIntent locationListenerPassivePendingIntent;
	protected Activity mActivity;
	protected ILocationResponder mResponder;
	protected Location mLastKnownLocation;

	public Location getLastKnowLocation() {
		return mLastKnownLocation;
	}

	public static interface ILocationResponder {
		public void newLocationFound(Location location);
	}

	public LocationAwareness(Activity activity, ILocationResponder responder) {
		this.mActivity = activity;
		this.mResponder = responder;
		locationManager = (LocationManager) mActivity.getSystemService(Context.LOCATION_SERVICE);
		prefs = mActivity.getSharedPreferences(Constants.SHARED_PREFERENCE_FILE, Context.MODE_PRIVATE);
		prefsEditor = prefs.edit();
		sharedPreferenceSaver = PlatformSpecificImplementationFactory.getSharedPreferenceSaver(mActivity);
		prefsEditor.putBoolean(Constants.SP_KEY_RUN_ONCE, true);
		sharedPreferenceSaver.savePreferences(prefsEditor, false);
		criteria = new Criteria();
		if (Constants.USE_GPS_WHEN_ACTIVITY_VISIBLE) {
			criteria.setAccuracy(Criteria.ACCURACY_FINE);
		} else {
			criteria.setPowerRequirement(Criteria.POWER_LOW);
		}
		Intent activeIntent = new Intent(mActivity, LocationChangedReceiver.class);
		locationListenerPendingIntent = PendingIntent.getBroadcast(mActivity, 0, activeIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		Intent passiveIntent = new Intent(mActivity, PassiveLocationChangedReceiver.class);
		locationListenerPassivePendingIntent = PendingIntent.getBroadcast(mActivity, 0, passiveIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		lastLocationFinder = PlatformSpecificImplementationFactory.getLastLocationFinder(mActivity);
		lastLocationFinder.setChangedLocationListener(oneShotLastLocationUpdateListener);
		locationUpdateRequester = PlatformSpecificImplementationFactory.getLocationUpdateRequester(locationManager);
		initiateFindingLocation();
	}

	public void hasResumed() {
		prefsEditor.putBoolean(Constants.EXTRA_KEY_IN_BACKGROUND, false);
		sharedPreferenceSaver.savePreferences(prefsEditor, false);
		requestLocationUpdates();
	}

	public void hasPaused() {
		prefsEditor.putBoolean(Constants.EXTRA_KEY_IN_BACKGROUND, true);
		sharedPreferenceSaver.savePreferences(prefsEditor, false);
		disableLocationUpdates();
	}

	public void hasDestroyed() {
		this.mActivity = null;
		this.mResponder = null;
	}

	protected void initiateFindingLocation() {
		AsyncTask<Void, Void, Void> findLastLocationTask = new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				Location lastKnownLocation = lastLocationFinder.getLastBestLocation(Constants.MAX_DISTANCE, System.currentTimeMillis()
						- Constants.MAX_TIME);
				foundANewLocation(lastKnownLocation);
				return null;
			}
		};
		findLastLocationTask.execute();
	}

	private void foundANewLocation(final Location location) {
		mActivity.runOnUiThread(new Runnable() {
			public void run() {
				if (location != null) {
					mLastKnownLocation = location;
					mResponder.newLocationFound(location);
				}
			}
		});
	}

	protected void requestLocationUpdates() {
		locationUpdateRequester.requestLocationUpdates(Constants.MAX_TIME, Constants.MAX_DISTANCE, criteria, locationListenerPendingIntent);
		locationUpdateRequester.requestPassiveLocationUpdates(Constants.PASSIVE_MAX_TIME, Constants.PASSIVE_MAX_DISTANCE,
				locationListenerPassivePendingIntent);
		IntentFilter intentFilter = new IntentFilter(Constants.ACTIVE_LOCATION_UPDATE_PROVIDER_DISABLED);
		mActivity.registerReceiver(locProviderDisabledReceiver, intentFilter);
		intentFilter = new IntentFilter(Constants.NEW_LOCATION_FOUND_ACTION);
		mActivity.registerReceiver(newLocationFoundReceiver, intentFilter);
		String bestProvider = locationManager.getBestProvider(criteria, false);
		String bestAvailableProvider = locationManager.getBestProvider(criteria, true);
		if (bestProvider != null && !bestProvider.equals(bestAvailableProvider)) {
			locationManager.requestLocationUpdates(bestProvider, 0, 0, bestInactiveLocationProviderListener, mActivity.getMainLooper());
		}
	}

	protected void disableLocationUpdates() {
		mActivity.unregisterReceiver(locProviderDisabledReceiver);
		mActivity.unregisterReceiver(newLocationFoundReceiver);
		locationManager.removeUpdates(locationListenerPendingIntent);
		locationManager.removeUpdates(bestInactiveLocationProviderListener);
		if (mActivity.isFinishing())
			lastLocationFinder.cancel();
		if (Constants.DISABLE_PASSIVE_LOCATION_WHEN_USER_EXIT && mActivity.isFinishing())
			locationManager.removeUpdates(locationListenerPassivePendingIntent);
	}

	protected LocationListener oneShotLastLocationUpdateListener = new LocationListener() {
		public void onLocationChanged(Location l) {
			foundANewLocation(l);
		}

		public void onProviderDisabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

		public void onProviderEnabled(String provider) {
		}
	};

	protected LocationListener bestInactiveLocationProviderListener = new LocationListener() {
		public void onLocationChanged(Location l) {
		}

		public void onProviderDisabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

		public void onProviderEnabled(String provider) {
			requestLocationUpdates();
		}
	};

	protected BroadcastReceiver locProviderDisabledReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			boolean providerDisabled = !intent.getBooleanExtra(LocationManager.KEY_PROVIDER_ENABLED, false);
			if (providerDisabled) {
				requestLocationUpdates();
			}
		}
	};

	protected BroadcastReceiver newLocationFoundReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String locationKey = Constants.EXTRA_KEY_LOCATION;
			if (intent.hasExtra(locationKey)) {
				Location location = (Location) intent.getExtras().get(locationKey);
				if (location != null) {
					foundANewLocation(location);
				}
			}
		}
	};

}