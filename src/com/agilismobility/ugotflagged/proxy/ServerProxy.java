package com.agilismobility.ugotflagged.proxy;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;

import com.agilismobility.ugotflagged.MainApplication;
import com.agilismobility.ugotflagged.utils.PipeStream;
import android.os.AsyncTask;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import java.net.URL;

public class ServerProxy {

	private static final int CONNECTION_TIMEOUT = 2 * 60 * 1000;
	private static String sessionId;
	public static final String URL = "https://ugotflagged.heroku.com";

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

	public static void post(String path, String data, IServerResponder result) {
		makeAServerCall(HTTP_METHOD.Post, path, data, result);
	}

	public static void get(String path, String data, IServerResponder result) {
		makeAServerCall(HTTP_METHOD.Get, path, data, result);
	}

	@SuppressWarnings("unchecked")
	public static void makeAServerCall(HTTP_METHOD method, String path, String data, IServerResponder result) {
		ArrayList<Object> params = new ArrayList<Object>();
		params.add(method);
		params.add(path);
		params.add(data);
		params.add(result);
		ServerCallTask task = new ServerCallTask();
		task.execute(params);
	}

	private static class ServerCallTask extends AsyncTask<ArrayList<Object>, Integer, ArrayList<Object>> {
		HttpURLConnection conn;
		private int tryCount = 0;

		@SuppressWarnings("static-access")
		protected ArrayList<Object> doInBackground(ArrayList<Object>... params) {
			tryCount++;

			HTTP_METHOD method = (HTTP_METHOD) params[0].get(0);
			String path = (String) params[0].get(1);
			String data = (String) params[0].get(2);
			URL url;
			int respcode = 0;
			String xml = "";
			String detailedErrorMessage = null;
			try {
				if (method == HTTP_METHOD.Post) {
					url = new URL(URL + path);
				} else {
					if (data != null && data.length() > 0 && !path.endsWith("?")) {
						path = path + "?";
					}
					if (data != null && data.length() > 0) {
						url = new URL(URL + path + data);
					} else {
						url = new URL(URL + path);
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
					}
					respcode = conn.getResponseCode();
					if (respcode == -1) {
						if (tryCount < 6) {
							return doInBackground(params);
						} else {
							respcode = 0;
						}
					}
					InputStream temp;
					String encoding = conn.getContentEncoding();
					if (encoding != null && encoding.equalsIgnoreCase("gzip")) {
						temp = new GZIPInputStream(conn.getInputStream());
					} else {
						temp = conn.getInputStream();
					}
					if (respcode >= 300 && respcode <= 307) {
						String redirUrl = conn.getHeaderField("Location").trim();
						String resXML = "<Response><RedirectUrl>" + URLEncoder.encode(redirUrl) + "</RedirectUrl></Response>";
						temp = new ByteArrayInputStream(resXML.getBytes());
						respcode = 200;
					}
					if (respcode == 200) {
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
			arr.add(xml);
			arr.add(params[0].get(3));
			arr.add(respcode);
			arr.add(detailedErrorMessage);

			return arr;
		}

		@Override
		protected void onCancelled() {
			if (conn != null) {
				conn.disconnect();
			}
		}

		protected void onPostExecute(ArrayList<Object> result) {
			String xml = (String) result.get(0);
			IServerResponder callBack = (IServerResponder) result.get(1);
			Integer responseCode = (Integer) result.get(2);
			String detailedErrorMessage = (String) result.get(3);
			ServerResponseSummary srs = new ServerResponseSummary(xml, detailedErrorMessage, responseCode);
			if (responseCode == 200) {
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