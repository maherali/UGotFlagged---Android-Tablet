package com.agilismobility.util;

public class Util {

	public static String pluralize(int v, String sing, String plu) {
		if (v == 1) {
			return "" + v + " " + plu;
		} else {
			return "" + v + " " + sing;
		}
	}

}
