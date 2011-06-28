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

public class ImageDownloadingService extends Service {

	private static String TAG = "ImageDownloadingService";
	public static final String IMAGE_AVAILABLE_NOTIF = "IMAGE_AVAILABLE_NOTIF";

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, final int startId) {
		String url = intent.getStringExtra("com.agilismobility.architecture.url");
		String widthStr = intent.getStringExtra("com.agilismobility.architecture.width");
		String heightStr = intent.getStringExtra("com.agilismobility.architecture.height");

		if (((MainApplication) getApplication()).getImageCache().getImageForURL(url) != null) {
			announceFound(url, startId, true);
		} else {
			if (((MainApplication) getApplication()).getImageCache().isImageDownloadingForURL(url)) {

			} else {
				((MainApplication) getApplication()).getImageCache().markDownloadingForURL(url);
				Integer f = startId;
				new ImageDownloadingTask().execute(url, f.toString(), widthStr, heightStr);
			}
		}
		return START_REDELIVER_INTENT;
	}

	private class ImageDownloadingTask extends AsyncTask<String, Integer, Boolean> {
		private String theUrl;
		private int startId;
		private Bitmap bmImg;
		private int desirdWidth, desiredHeight;

		protected Boolean doInBackground(String... params) {
			this.theUrl = params[0];
			this.startId = new Integer(params[1]);
			this.desirdWidth = new Integer(params[2]);
			this.desiredHeight = new Integer(params[3]);
			downloadImage();
			if (bmImg != null) {
				((MainApplication) getApplication()).getImageCache().setImageForUrl(bmImg, theUrl);
			}
			return bmImg != null;
		}

		protected void onProgressUpdate(Integer... progress) {

		}

		protected void onPostExecute(Boolean result) {
			announceFound(theUrl, startId, result);
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
				bmImg = Bitmap.createScaledBitmap(bit, this.desirdWidth, this.desiredHeight, true);
				bit.recycle();
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
		newIntent.putExtra("com.agilismobility.architecture.url", url);
		newIntent.putExtra("com.agilismobility.architecture.success", success);
		sendBroadcast(newIntent);
		stopSelf(startID);
	}

}
