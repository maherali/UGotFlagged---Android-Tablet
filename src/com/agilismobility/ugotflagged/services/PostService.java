package com.agilismobility.ugotflagged.services;

import android.app.Service;
import android.content.Intent;
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
	public static final String COMMENT_TEXT_PARAM = "reply[text]";
	public static final String POST_ID_PARAM = "POST_ID_PARAM";

	private static String TAG = "PostService";
	public static final String ADD_COMMENT_FINISHED_NOTIF = "ADD_COMMENT_FINISHED_NOTIF";

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, final int startId) {
		String action = intent.getStringExtra(ACTION);
		if (ADD_COMMENT_ACTION.equals(action)) {
			String commentText = intent.getStringExtra(COMMENT_TEXT_PARAM);
			String postId = intent.getStringExtra(POST_ID_PARAM);
			addComment(commentText, postId, startId);
		}
		return START_REDELIVER_INTENT;
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

}
