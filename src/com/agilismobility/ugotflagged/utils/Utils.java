package com.agilismobility.ugotflagged.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

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

}
