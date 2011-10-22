package com.agilismobility.ugotflagged.services;

import java.util.HashMap;
import java.util.Map;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.IBinder;

import com.agilismobility.ugotflagged.proxy.ServerProxy;
import com.agilismobility.ugotflagged.proxy.ServerProxy.IServerResponder;
import com.agilismobility.ugotflagged.proxy.ServerProxy.ServerResponseSummary;
import com.agilismobility.ugotflagged.utils.Utils;
import com.agilismobility.utils.Constants;

public class PostService extends Service {
	public static final String ERROR_ARG = "error";
	public static final String XML_ARG = "xml";
	public static final String SUCCESS_ARG = "success";

	public static final String ACTION = "ACTION";
	public static final String ADD_COMMENT_ACTION = "ADD_COMMENT_ACTION";
	public static final String UNLIKE_ACTION = "UNLIKE_ACTION";
	public static final String LIKE_ACTION = "LIKE_ACTION";
	public static final String ADD_FLAG_ACTION = "ADD_FLAG_ACTION";

	public static final String COMMENT_TEXT_PARAM = "reply[text]";
	public static final String POST_ID_PARAM = "POST_ID_PARAM";

	public static final String FLAG_TITLE_PARAM = "post[title]";
	public static final String FLAG_TEXT_PARAM = "post[text]";
	public static final String FLAG_VEHICLE_PARAM = "post[vehicle]";
	public static final String FLAG_VEHICLE_TYPE_PARAM = "post[vehicle_type]";
	public static final String FLAG_POST_TYPE_PARAM = "post[post_type]";
	public static final String FLAG_CITY_PARAM = "post[city]";
	public static final String FLAG_STATE_PARAM = "post[state]";
	public static final String FLAG_COUNTRY_PARAM = "post[country]";
	public static final String FLAG_STREET_PARAM = "post[street]";
	public static final String FLAG_PLATE_ISSUER_PARAM = "post[plate_issuer]";
	public static final String FLAG_PLATE_TAG_PARAM = "post[plate_tag]";
	public static final String FLAG_LAT_PARAM = "post[lat]";
	public static final String FLAG_LONG_PARAM = "post[long]";
	public static final String FLAG_PICTURE_PARAM = "photo[uploaded_data]";

	private static String TAG = "PostService";
	public static final String ADD_COMMENT_FINISHED_NOTIF = "ADD_COMMENT_FINISHED_NOTIF";
	public static final String LIKE_UNLIKE_FINISHED_NOTIF = "LIKE_UNLIKE_FINISHED_NOTIF";
	public static final String ADD_FLAG_FINISHED_NOTIF = "ADD_FLAG_FINISHED_NOTIF";

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, final int startId) {
		String action = intent.getStringExtra(ACTION);
		String postId = intent.getStringExtra(POST_ID_PARAM);
		if (ADD_COMMENT_ACTION.equals(action)) {
			String commentText = intent.getStringExtra(COMMENT_TEXT_PARAM);
			addComment(commentText, postId, startId);
		} else if (UNLIKE_ACTION.equals(action)) {
			like(false, postId, startId);
		} else if (LIKE_ACTION.equals(action)) {
			like(true, postId, startId);
		} else if (ADD_FLAG_ACTION.equals(action)) {
			String title = intent.getStringExtra(FLAG_TITLE_PARAM);
			String text = intent.getStringExtra(FLAG_TEXT_PARAM);
			String vehicle = intent.getStringExtra(FLAG_VEHICLE_PARAM);
			String vehicleType = intent.getStringExtra(FLAG_VEHICLE_TYPE_PARAM);
			String postType = intent.getStringExtra(FLAG_POST_TYPE_PARAM);
			String city = intent.getStringExtra(FLAG_CITY_PARAM);
			String state = intent.getStringExtra(FLAG_STATE_PARAM);
			String country = intent.getStringExtra(FLAG_COUNTRY_PARAM);
			String street = intent.getStringExtra(FLAG_STREET_PARAM);
			String plateIssuer = intent.getStringExtra(FLAG_PLATE_ISSUER_PARAM);
			String plateTag = intent.getStringExtra(FLAG_PLATE_TAG_PARAM);
			String lat = intent.getStringExtra(FLAG_LAT_PARAM);
			String lng = intent.getStringExtra(FLAG_LONG_PARAM);
			String picPath = intent.getStringExtra(FLAG_PICTURE_PARAM);
			if (picPath != null) {
				Bitmap b = Utils.getScalledImageFromFile(picPath, 300, 300);
				HashMap<String, String> kv = new HashMap<String, String>();
				kv.put(FLAG_TITLE_PARAM, title);
				kv.put(FLAG_TEXT_PARAM, text);
				kv.put(FLAG_VEHICLE_PARAM, vehicle);
				kv.put(FLAG_VEHICLE_TYPE_PARAM, vehicleType);
				kv.put(FLAG_POST_TYPE_PARAM, postType);
				kv.put(FLAG_STREET_PARAM, street);
				kv.put(FLAG_CITY_PARAM, city);
				kv.put(FLAG_STATE_PARAM, state);
				kv.put(FLAG_COUNTRY_PARAM, country);
				kv.put(FLAG_PLATE_ISSUER_PARAM, plateIssuer);
				kv.put(FLAG_PLATE_TAG_PARAM, plateTag);
				kv.put(FLAG_LAT_PARAM, lat);
				kv.put(FLAG_LONG_PARAM, lng);
				addFlagWithImageAndKV(b, kv, startId);
			} else {
				addFlag(title, text, vehicle, vehicleType, postType, city, state, country, street, plateIssuer, plateTag, lat, lng, startId);
			}
		}
		return START_REDELIVER_INTENT;
	}

	private void addFlagWithImageAndKV(Bitmap b, Map<String, String> kv, final int startId) {
		ServerProxy.postWithBitmapAndKV(b, kv, ADD_FLAG_ACTION, "/posts", null, new IServerResponder() {
			@Override
			public void success(ServerResponseSummary srs) {
				anounceAddFlagFinished(startId, srs, true);
			}

			@Override
			public void failure(ServerResponseSummary srs) {
				anounceAddFlagFinished(startId, srs, false);
			}
		});
	}

	private void addFlag(String title, String text, String vehicle, String vehicleType, String postType, String city, String state,
			String country, String street, String plateIssuer, String plateTag, String lat, String lng, final int startId) {
		ServerProxy.post(ADD_FLAG_ACTION, "/posts", Utils.toUrlParams(FLAG_TITLE_PARAM, title, FLAG_TEXT_PARAM, text, FLAG_VEHICLE_PARAM,
				vehicle, FLAG_VEHICLE_TYPE_PARAM, vehicleType, FLAG_POST_TYPE_PARAM, postType, FLAG_CITY_PARAM, city, FLAG_STATE_PARAM, state,
				FLAG_COUNTRY_PARAM, country, FLAG_STREET_PARAM, street, FLAG_PLATE_ISSUER_PARAM, plateIssuer, FLAG_PLATE_TAG_PARAM, plateTag,
				FLAG_LAT_PARAM, lat, FLAG_LONG_PARAM, lng), new IServerResponder() {
			@Override
			public void success(ServerResponseSummary srs) {
				anounceAddFlagFinished(startId, srs, true);
			}

			@Override
			public void failure(ServerResponseSummary srs) {
				anounceAddFlagFinished(startId, srs, false);
			}
		});
	}

	private void like(final boolean doLike, final String postID, final int startId) {
		ServerProxy.post(doLike ? LIKE_ACTION : UNLIKE_ACTION, "/posts/" + postID + (doLike ? "/like" : "/unlike"), Utils.toUrlParams(),
				new IServerResponder() {
					@Override
					public void success(ServerResponseSummary srs) {
						anounceLikeUnLikeFinished(doLike, postID, startId, srs, true);
					}

					@Override
					public void failure(ServerResponseSummary srs) {
						anounceLikeUnLikeFinished(doLike, postID, startId, srs, false);
					}
				});
	}

	private void addComment(String commentText, final String postID, final int startId) {
		ServerProxy.post(Constants.ADDING_COMMENT, "/posts/" + postID + "/replies", Utils.toUrlParams(COMMENT_TEXT_PARAM, commentText),
				new IServerResponder() {
					@Override
					public void success(ServerResponseSummary srs) {
						anounceAddCommentFinished(postID, startId, srs, true);
					}

					@Override
					public void failure(ServerResponseSummary srs) {
						anounceAddCommentFinished(postID, startId, srs, false);
					}
				});
	}

	private void anounceAddCommentFinished(String postID, int startID, ServerResponseSummary srs, boolean success) {
		Intent newIntent = new Intent(ADD_COMMENT_FINISHED_NOTIF);
		newIntent.putExtra(ACTION, ADD_COMMENT_ACTION);
		newIntent.putExtra(SUCCESS_ARG, success);
		newIntent.putExtra(XML_ARG, srs.xml);
		newIntent.putExtra(ERROR_ARG, srs.detailedErrorMessage);
		newIntent.putExtra(POST_ID_PARAM, postID);
		sendBroadcast(newIntent);
		stopSelf(startID);
	}

	private void anounceLikeUnLikeFinished(boolean doLike, String postID, int startID, ServerResponseSummary srs, boolean success) {
		Intent newIntent = new Intent(LIKE_UNLIKE_FINISHED_NOTIF);
		newIntent.putExtra(ACTION, (doLike ? LIKE_ACTION : UNLIKE_ACTION));
		newIntent.putExtra(SUCCESS_ARG, success);
		newIntent.putExtra(XML_ARG, srs.xml);
		newIntent.putExtra(ERROR_ARG, srs.detailedErrorMessage);
		newIntent.putExtra(POST_ID_PARAM, postID);
		sendBroadcast(newIntent);
		stopSelf(startID);
	}

	private void anounceAddFlagFinished(int startID, ServerResponseSummary srs, boolean success) {
		Intent newIntent = new Intent(ADD_FLAG_FINISHED_NOTIF);
		newIntent.putExtra(ACTION, ADD_FLAG_ACTION);
		newIntent.putExtra(SUCCESS_ARG, success);
		newIntent.putExtra(XML_ARG, srs.xml);
		newIntent.putExtra(ERROR_ARG, srs.detailedErrorMessage);
		sendBroadcast(newIntent);
		stopSelf(startID);
	}

}
