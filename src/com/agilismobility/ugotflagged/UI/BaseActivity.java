package com.agilismobility.ugotflagged.UI;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;

public abstract class BaseActivity extends Activity {

	void showError(String error) {
		showDialog("Error", error);
	}

	void showError(ArrayList<String> errors) {
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
						public void onClick(DialogInterface dialog, int whichButton) {
						}
					}).create();
		}
	}
}
