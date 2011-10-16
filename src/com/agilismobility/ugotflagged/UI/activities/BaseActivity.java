package com.agilismobility.ugotflagged.ui.activities;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

import com.agilismobility.ugotflagged.R;
import com.agilismobility.utils.Constants;

public abstract class BaseActivity extends Activity {

	protected ArrayList<String> mNotificationsObserved;
	protected DoingNotificationReceiver doingSomethingNotificationReceiver;
	protected FinishedDoingNotificationReceiver finishedDoingSomethingNotificationReceiver;

	protected void addInterestingNotificationName(String notif) {
		mNotificationsObserved.add(notif);
	}

	protected void receivedDoingInterestingNotification(String notif) {
	}

	protected void receivedFinishedDoingInterestingNotification(String notif) {
	}

	private void findAndInvoke(String notifName, boolean doing) {
		if (notifName != null) {
			for (String aNotif : mNotificationsObserved) {
				if (aNotif.equals(notifName)) {
					if (doing) {
						receivedDoingInterestingNotification(notifName);
					} else {
						receivedFinishedDoingInterestingNotification(notifName);
					}
					break;
				}
			}
		}
	}

	protected class DoingNotificationReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			findAndInvoke(intent.getStringExtra(Constants.NOTIFICATION), true);
		}
	}

	protected class FinishedDoingNotificationReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			findAndInvoke(intent.getStringExtra(Constants.NOTIFICATION), false);
		}
	}

	protected void setupNotificationSystem() {
		mNotificationsObserved = new ArrayList<String>();
		IntentFilter filter = new IntentFilter(Constants.CURRENTLY_DOING_SOMETHING_NOTIF);
		doingSomethingNotificationReceiver = new DoingNotificationReceiver();
		registerReceiver(doingSomethingNotificationReceiver, filter);

		filter = new IntentFilter(Constants.FINISHED_DOING_SOMETHING_NOTIF);
		finishedDoingSomethingNotificationReceiver = new FinishedDoingNotificationReceiver();
		registerReceiver(finishedDoingSomethingNotificationReceiver, filter);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setupNotificationSystem();
		setTitle("");
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(doingSomethingNotificationReceiver);
		unregisterReceiver(finishedDoingSomethingNotificationReceiver);
		super.onDestroy();
	}

	public void showError(String error) {
		showDialog("Error", error);
	}

	public void showError(ArrayList<String> errors) {
		if (errors.size() > 0) {
			showError(errors.get(0));
		}
	}

	void showDialog(String title, String message) {
		DialogActivity.showMessage(this, title, message);
	}

	protected void enableButton(int id, boolean enabled) {
		((Button) findViewById(id)).setEnabled(enabled);
	}

	protected void showWebDialog(String url) {
		LayoutInflater inflater = LayoutInflater.from(this);
		View alertDialogView = inflater.inflate(R.layout.web_dialog, null);
		WebView myWebView = (WebView) alertDialogView.findViewById(R.id.DialogWebView);
		myWebView.loadUrl(url);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setView(alertDialogView);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		}).show();
	}
}
