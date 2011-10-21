package com.agilismobility.ugotflagged.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.agilismobility.ugotflagged.proxy.ServerProxy;
import com.agilismobility.ugotflagged.proxy.ServerProxy.IServerResponder;
import com.agilismobility.ugotflagged.proxy.ServerProxy.ServerResponseSummary;
import com.agilismobility.ugotflagged.utils.Utils;

public class GeoCodingService extends Service {
	public static final String ERROR_ARG = "error";
	public static final String XML_ARG = "xml";
	public static final String SUCCESS_ARG = "success";
	public static final String ACTION = "ACTION";
	public static final String GEOCODE_ACTION = "GEOCODE_ACTION";
	public static final String GEO_LAT_PARAM = "lat";
	public static final String GEO_LNG_PARAM = "lng";
	private static String TAG = "GeoCodingService";
	public static final String GEO_CODE_FINISHED_NOTIF = "GEO_CODE_FINISHED_NOTIF";

	public static final String GEOCODING = "GEOCODING";

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, final int startId) {
		String action = intent.getStringExtra(ACTION);
		String lat = intent.getStringExtra(GEO_LAT_PARAM);
		String lng = intent.getStringExtra(GEO_LNG_PARAM);
		if (GEOCODE_ACTION.equals(action)) {
			geocode(lat, lng, startId);
		}
		return START_REDELIVER_INTENT;
	}

	private void geocode(String lat, String lng, final int startId) {
		ServerProxy.get(GEOCODING, "http://ws.geonames.org/findNearestIntersection?lat=" + lat + "&lng=" + lng, Utils.toUrlParams(),
				new IServerResponder() {
					@Override
					public void success(ServerResponseSummary srs) {
						anounceGeoCodingFinished(startId, srs, true);
					}

					@Override
					public void failure(ServerResponseSummary srs) {
						anounceGeoCodingFinished(startId, srs, false);
					}
				});
	}

	private void anounceGeoCodingFinished(int startID, ServerResponseSummary srs, boolean success) {
		Intent newIntent = new Intent(GEO_CODE_FINISHED_NOTIF);
		newIntent.putExtra(ACTION, GEOCODE_ACTION);
		newIntent.putExtra(SUCCESS_ARG, success);
		newIntent.putExtra(XML_ARG, srs.xml);
		newIntent.putExtra(ERROR_ARG, srs.detailedErrorMessage);
		sendBroadcast(newIntent);
		stopSelf(startID);
	}
}