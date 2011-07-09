package com.agilismobility.ugotflagged.UI;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.Button;

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

	protected class DoingNotificationReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String notifName = intent.getStringExtra(Constants.NOTIFICATION);
			if (notifName != null) {
				for (String aNotif : mNotificationsObserved) {
					if (aNotif.equals(notifName)) {
						receivedDoingInterestingNotification(notifName);
						break;
					}
				}
			}
		}
	}

	protected class FinishedDoingNotificationReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String notifName = intent.getStringExtra(Constants.NOTIFICATION);
			if (notifName != null) {
				for (String aNotif : mNotificationsObserved) {
					if (aNotif.equals(notifName)) {
						receivedFinishedDoingInterestingNotification(notifName);
						break;
					}
				}
			}
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
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		DialogFragment newFragment = MyDialogFragment.newInstance(title, message);
		newFragment.show(ft, "dialog");
	}

	public static class MyDialogFragment extends DialogFragment {

		public static MyDialogFragment newInstance(String title, String message) {
			MyDialogFragment frag = new MyDialogFragment();
			Bundle args = new Bundle();
			args.putString("title", title);
			args.putString("message", message);
			frag.setArguments(args);
			return frag;
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			String title = getArguments().getString("title");
			String message = getArguments().getString("message");
			return new AlertDialog.Builder(getActivity()).setTitle(title).setMessage(message)
					.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int whichButton) {
						}
					}).create();
		}
	}

	protected void enableButton(int id, boolean enabled) {
		((Button) findViewById(id)).setEnabled(enabled);
	}

}
