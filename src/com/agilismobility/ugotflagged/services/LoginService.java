package com.agilismobility.ugotflagged.services;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

public class LoginService extends Service {

	private static String TAG = "LoginService";
	public static final String LOGIN_SUCCESS_NOTIF = "LOGIN_SUCCESS_NOTIF";

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, final int startId) {
		String email = intent.getStringExtra("email");
		String password = intent.getStringExtra("password");
		new LoginTask().execute(email, password, new Integer(startId).toString());
		return START_REDELIVER_INTENT;
	}

	private class LoginTask extends AsyncTask<String, Integer, Boolean> {
		private String email;
		private String password;
		private int startId;

		protected Boolean doInBackground(String... params) {
			this.email = params[0];
			this.password = params[1];
			this.startId = new Integer(params[2]);
			login();
			return true;
		}

		protected void onProgressUpdate(Integer... progress) {

		}

		protected void onPostExecute(Boolean result) {
			announceLoginSuccess(startId);
		}

		private void login() {
			Log.d(TAG, "log in using " + this.email + " and password " + this.password);
			// URL imgURL = null;
			// try {
			// imgURL = new URL(theUrl);
			// } catch (MalformedURLException e) {
			// e.printStackTrace();
			// }
			// HttpURLConnection conn = null;
			// try {
			// conn = (HttpURLConnection) imgURL.openConnection();
			// conn.setDoInput(true);
			// conn.connect();
			// InputStream is = conn.getInputStream();
			// BitmapFactory.Options o = new BitmapFactory.Options();
			// o.inSampleSize = 2; // 1/2 the size
			// Bitmap bit = BitmapFactory.decodeStream(is, null, o);
			// bmImg = Bitmap.createScaledBitmap(bit, this.desirdWidth,
			// this.desiredHeight, true);
			// bit.recycle();
			// } catch (Exception e) {
			// e.printStackTrace();
			// } finally {
			// if (conn != null) {
			// conn.disconnect();
			// }
			// }
		}
	}

	public void announceLoginSuccess(int startID) {
		Intent newIntent = new Intent(LOGIN_SUCCESS_NOTIF);
		sendBroadcast(newIntent);
		stopSelf(startID);
	}

}