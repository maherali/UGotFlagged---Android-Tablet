package com.agilismobility.ugotflagged.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Formatter;

import android.location.Location;

public class Utils {

	public static String toUrlParams(String... params) {
		StringBuffer urlParams = new StringBuffer();
		int index = 1;
		for (String param : params) {
			try {
				if (index % 2 != 0) {
					urlParams.append(URLEncoder.encode(param, "UTF-8")).append("=");
				} else {
					urlParams.append(URLEncoder.encode(param, "UTF-8")).append("&");
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			index++;
		}
		return urlParams.toString();
	}

	public static String distanceAway(Location loc, float lat, float lng) {
		if (loc != null) {
			float[] results = new float[1];
			Location.distanceBetween(loc.getLatitude(), loc.getLongitude(), lat, lng, results);
			if (Math.abs(results[0] / 1609.344f) <= 0.05f) {
				return "here";
			} else {
				return new Formatter().format("%.1f", Math.abs(results[0] / 1609.344f)) + " miles away";
			}
		} else {
			return "unknown miles away";
		}
	}
}
