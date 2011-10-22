package com.agilismobility.ugotflagged.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Formatter;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.location.Location;

import com.agilismobility.ugotflagged.MainApplication;

public class Utils {

	public static Bitmap getImageFromFile(String fileName) {
		Bitmap bitmap = null;
		FileInputStream is = null;
		try {
			File imageFile = new File(fileName);
			is = new FileInputStream(imageFile);
			bitmap = BitmapFactory.decodeStream(is);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return bitmap;
	}

	public static void saveImageToFile(Bitmap img, String fileName) {
		try {
			File imageFile = new File(fileName);
			FileOutputStream fos = new FileOutputStream(imageFile);
			img.compress(Bitmap.CompressFormat.JPEG, 100, fos);
			fos.close();
			fos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Bitmap getImageAsset(String fileName) {
		Bitmap bitmap = null;
		InputStream is = null;
		try {
			is = MainApplication.getInstance().getAssets().open(fileName);
			bitmap = BitmapFactory.decodeStream(is);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return bitmap;
	}

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

	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = pixels;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

}
