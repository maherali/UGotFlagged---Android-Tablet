package com.agilismobility.ugotflagged.proxy;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.agilismobility.ugotflagged.MainApplication;
import com.agilismobility.ugotflagged.utils.PipeStream;
import com.agilismobility.utils.Constants;

public class ServerProxy {

	private static final int CONNECTION_TIMEOUT = 2 * 60 * 1000;
	private static String sessionId;
	public static final String URL = "https://ugotflagged-dev.heroku.com";
	public static final String SERVER_PREF_FILE_NAME = "SERVER_PREF";
	public static final String SP_SESSION = "SESSION";

	private static final int MAX_NUMBER_OF_RETRIES = 5;

	private static final int RESPONSE_CODE_N1 = -1;
	private static final int RESPONSE_CODE_ZERO = 0;
	private static final int RESPONSE_CODE_200 = 200;
	private static final int RESPONSE_CODE_300 = 300;
	private static final int RESPONSE_CODE_307 = 307;
	private static final int RESPONSE_CODE_404 = 404;

	static {
		sessionId = getStoredSession();
	}

	public static String getStoredSession() {
		SharedPreferences settings = MainApplication.getInstance().getSharedPreferences(SERVER_PREF_FILE_NAME, 0);
		return (settings.getString(SP_SESSION, null));
	}

	public static void saveSession() {
		SharedPreferences settings = MainApplication.getInstance().getSharedPreferences(SERVER_PREF_FILE_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(SP_SESSION, sessionId);
		editor.commit();
	}

	public static void resetSession() {
		sessionId = null;
		saveSession();
	}

	public static void syncCookie() {
		CookieSyncManager mgr = CookieSyncManager.createInstance(MainApplication.getInstance());
		CookieManager cookieManager = CookieManager.getInstance();
		cookieManager.setCookie(ServerProxy.URL, ServerProxy.sessionId);
		mgr.sync();
	}

	static public String getCookies() {
		return sessionId;
	}

	public enum HTTP_METHOD {
		Post, Get
	}

	public static String getMethod(HTTP_METHOD method) {
		return method == HTTP_METHOD.Post ? "POST" : method == HTTP_METHOD.Get ? "GET" : null;
	}

	public static void post(String funcName, String path, String data, IServerResponder result) {
		makeAServerCall(funcName, HTTP_METHOD.Post, path, data, result);
	}

	public static void get(String funcName, String path, String data, IServerResponder result) {
		makeAServerCall(funcName, HTTP_METHOD.Get, path, data, result);
	}

	@SuppressWarnings("unchecked")
	public static void makeAServerCall(String funcName, HTTP_METHOD method, String path, String data, IServerResponder result) {
		ArrayList<Object> params = new ArrayList<Object>();
		params.add(method);
		params.add(path);
		params.add(data);
		params.add(result);
		params.add(funcName);
		ServerCallTask task = new ServerCallTask();
		Constants.broadcastDoingSomethingNotification(funcName);
		task.execute(params);
	}

	private static class ServerCallTask extends AsyncTask<ArrayList<Object>, Integer, ArrayList<Object>> {
		HttpURLConnection conn;
		private int tryCount = 0;

		@Override
		@SuppressWarnings("static-access")
		protected ArrayList<Object> doInBackground(ArrayList<Object>... params) {
			tryCount++;

			HTTP_METHOD method = (HTTP_METHOD) params[0].get(0);
			String path = (String) params[0].get(1);
			String data = (String) params[0].get(2);
			URL url = null;
			int respcode = RESPONSE_CODE_ZERO;
			String xml = "";
			String detailedErrorMessage = null;
			try {
				if (method == HTTP_METHOD.Post) {
					url = new URL((path.toLowerCase().startsWith("http") ? "" : URL) + path);
				} else {
					if (data != null && data.length() > 0 && !path.endsWith("?")) {
						path = path + "?";
					}
					if (data != null && data.length() > 0) {
						url = new URL((path.toLowerCase().startsWith("http") ? "" : URL) + path + data);
					} else {
						url = new URL((path.toLowerCase().startsWith("http") ? "" : URL) + path);
					}
				}
				conn = (HttpURLConnection) url.openConnection();
				conn.setRequestProperty("Accept-Encoding", "gzip");
				conn.setRequestProperty("App-Agent", "UGotFlagged/1.4 (Device/Android; OSV/Honeycomb; Locale/us-en; ServerVersion/1.0)");
				conn.setDoInput(true);
				conn.setFollowRedirects(false);
				if (method == HTTP_METHOD.Post) {
					conn.setDoOutput(true);
				}
				conn.setUseCaches(false);
				conn.setRequestMethod(getMethod(method));
				conn.setConnectTimeout(CONNECTION_TIMEOUT);

				if (method == HTTP_METHOD.Post) {
					conn.setRequestProperty("Content-Length", String.format("%d", data.length()));
					conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				}
				if (sessionId != null) {
					conn.setRequestProperty("Cookie", sessionId);
				}
				InputStream in = null;
				try {
					conn.connect();
					if (method == HTTP_METHOD.Post) {
						DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
						dos.writeBytes(data != null ? data : "");
						dos.flush();
						dos.close();
					}
					String cookieVal = conn.getHeaderField("Set-Cookie");
					if (cookieVal != null) {
						sessionId = cookieVal.substring(0, cookieVal.indexOf(";"));
						saveSession();
					}
					respcode = conn.getResponseCode();
					if (respcode == RESPONSE_CODE_N1) {
						if (tryCount < MAX_NUMBER_OF_RETRIES) {
							return doInBackground(params);
						} else {
							respcode = RESPONSE_CODE_ZERO;
						}
					}
					InputStream temp;
					String encoding = conn.getContentEncoding();
					if (encoding != null && encoding.equalsIgnoreCase("gzip")) {
						temp = new GZIPInputStream(conn.getInputStream());
					} else {
						temp = conn.getInputStream();
					}
					if (respcode >= RESPONSE_CODE_300 && respcode <= RESPONSE_CODE_307) {
						String redirUrl = conn.getHeaderField("Location").trim();
						String resXML = "<Response><RedirectUrl>" + URLEncoder.encode(redirUrl) + "</RedirectUrl></Response>";
						temp = new ByteArrayInputStream(resXML.getBytes());
						respcode = RESPONSE_CODE_200;
					}
					if (respcode == RESPONSE_CODE_200) {
						PipeStream pipe = new PipeStream(temp);
						in = pipe.peek();
					} else {
						in = temp;
					}

					BufferedReader r = new BufferedReader(new InputStreamReader(in));
					StringBuilder total = new StringBuilder();
					String line;
					while ((line = r.readLine()) != null) {
						total.append(line);
					}
					xml = total.toString();

				} finally {
					if (in != null)
						in.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
				detailedErrorMessage = e.getMessage();
			}
			ArrayList<Object> arr = new ArrayList<Object>();
			if (MainApplication.isNetworkConnected()) {
				if (respcode == RESPONSE_CODE_200 && !xml.contains("<errors>")) {
					MainApplication.getInstance().setXML(url.toExternalForm(), xml);
				}
				arr.add(xml);
				arr.add(params[0].get(3));
				arr.add(respcode);
				arr.add(detailedErrorMessage);
				arr.add(params[0].get(4));
				return arr;
			} else {
				String x = MainApplication.getInstance().getXML(url.toExternalForm());
				if (x != null) {
					arr.add(x);
					arr.add(params[0].get(3));
					arr.add(RESPONSE_CODE_200);
					arr.add(null);
				} else {
					arr.add("?xml version=\"1.0\" encoding=\"UTF-8\"?><errors><error>Device is offline</error></errors>");
					arr.add(params[0].get(3));
					arr.add(RESPONSE_CODE_404);
					arr.add("Device is offline");
				}
				arr.add(params[0].get(4));
				return arr;
			}
		}

		@Override
		protected void onCancelled() {
			if (conn != null) {
				conn.disconnect();
			}
		}

		@Override
		protected void onPostExecute(ArrayList<Object> result) {
			Constants.broadcastFinishedDoingSomethingNotification((String) result.get(4));
			String xml = (String) result.get(0);
			IServerResponder callBack = (IServerResponder) result.get(1);
			Integer responseCode = (Integer) result.get(2);
			String detailedErrorMessage = (String) result.get(3);
			ServerResponseSummary srs = new ServerResponseSummary(xml, detailedErrorMessage, responseCode);
			if (responseCode == RESPONSE_CODE_200 && detailedErrorMessage == null) {
				callBack.success(srs);
			} else {
				callBack.failure(srs);
			}
		}
	}

	public static class ServerResponseSummary {
		public String xml;
		public String detailedErrorMessage;
		public int responseCode;

		public ServerResponseSummary(String xml, String msg, int rc) {
			this.xml = xml;
			this.detailedErrorMessage = msg;
			this.responseCode = rc;
		}
	}

	public static interface IServerResponder {

		public void success(ServerResponseSummary response);

		public void failure(ServerResponseSummary response);
	}
}