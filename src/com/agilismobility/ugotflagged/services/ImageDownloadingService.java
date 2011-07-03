package com.agilismobility.ugotflagged.services;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.IBinder;

import com.agilismobility.ugotflagged.MainApplication;
import com.agilismobility.ugotflagged.utils.Utils;

public class ImageDownloadingService extends Service {

	public static final String CORNERS_ARG = "CORNERS_ARG";
	public static final String HEIGHT_ARG = "HEIGHT_ARG";
	public static final String WIDTH_ARG = "WIDTH_ARG";
	public static final String URL_ARG = "URL_ARG";
	private static String TAG = "ImageDownloadingService";

	public static final String IMAGE_AVAILABLE_NOTIF = "IMAGE_AVAILABLE_NOTIF";

	private String fileNameOfCachedImage(String url) {
		String afterCleaning = url.replace(":", "");
		afterCleaning = afterCleaning.replace("/", "");
		afterCleaning = afterCleaning.replace(".", "");
		afterCleaning = afterCleaning + ".jpg";
		return getCacheDir() + afterCleaning;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, final int startId) {
		String url = intent.getStringExtra(URL_ARG);
		String widthStr = intent.getStringExtra(WIDTH_ARG);
		String heightStr = intent.getStringExtra(HEIGHT_ARG);
		String cornersStr = intent.getStringExtra(CORNERS_ARG);
		if (cornersStr == null) {
			cornersStr = "0";
		}
		if (((MainApplication) getApplication()).getImageCache().getImageForURL(url) != null) {
			announceFound(url, startId, true);
		} else {
			Bitmap img = Utils.getImageFromFile(fileNameOfCachedImage(url));
			if (img != null) {
				((MainApplication) getApplication()).getImageCache().setImageForUrl(img, url);
				announceFound(url, startId, true);
			} else if (((MainApplication) getApplication()).getImageCache().isImageDownloadingForURL(url)) {

			} else {
				((MainApplication) getApplication()).getImageCache().markDownloadingForURL(url);
				Integer f = startId;
				new ImageDownloadingTask().execute(url, f.toString(), widthStr, heightStr, cornersStr);
			}
		}
		return START_REDELIVER_INTENT;
	}

	private class ImageDownloadingTask extends AsyncTask<String, Integer, Boolean> {
		private String theUrl;
		private int startId;
		private Bitmap bmImg;
		private int desirdWidth, desiredHeight, corners;

		@Override
		protected Boolean doInBackground(String... params) {
			this.theUrl = params[0];
			this.startId = new Integer(params[1]);
			this.desirdWidth = new Integer(params[2]);
			this.desiredHeight = new Integer(params[3]);
			this.corners = new Integer(params[4]);
			downloadImage();
			return bmImg != null;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (bmImg != null) {
				Utils.saveImageToFile(bmImg, fileNameOfCachedImage(theUrl));
				((MainApplication) getApplication()).getImageCache().setImageForUrl(bmImg, theUrl);
			} else {
				((MainApplication) getApplication()).getImageCache().markNotDownloadingForURL(theUrl);
			}
			if (result) {
				announceFound(theUrl, startId, result);
			} else {
				stopSelf(startId);
			}
		}

		private void downloadImage() {
			URL imgURL = null;
			try {
				imgURL = new URL(theUrl);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			HttpURLConnection conn = null;
			try {
				conn = (HttpURLConnection) imgURL.openConnection();
				conn.setDoInput(true);
				conn.connect();
				InputStream is = conn.getInputStream();
				BitmapFactory.Options o = new BitmapFactory.Options();
				o.inSampleSize = 1;
				Bitmap bit = BitmapFactory.decodeStream(is, null, o);
				Bitmap bm = Bitmap.createScaledBitmap(bit, this.desirdWidth, this.desiredHeight, true);
				bit.recycle();
				if (corners > 0) {
					bmImg = Utils.getRoundedCornerBitmap(bm, corners);
					bm.recycle();
				} else {
					bmImg = bm;
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (conn != null) {
					conn.disconnect();
				}
			}
		}
	}

	public void announceFound(String url, int startID, Boolean success) {
		Intent newIntent = new Intent(IMAGE_AVAILABLE_NOTIF);
		newIntent.putExtra(URL_ARG, url);
		sendBroadcast(newIntent);
		stopSelf(startID);
	}

}
