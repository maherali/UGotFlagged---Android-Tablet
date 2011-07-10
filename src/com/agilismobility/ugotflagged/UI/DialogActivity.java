package com.agilismobility.ugotflagged.UI;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

public class DialogActivity extends Activity {

	public static final String TITLE_ARG = "TITLE_ARG";
	public static final String MESSAGE_ARG = "MESSAGE_ARG";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		showDialog(intent.getStringExtra(TITLE_ARG), intent.getStringExtra(MESSAGE_ARG), this);
	}

	public static void showMessage(Context context, String title, String message) {
		Intent intent = new Intent(context, DialogActivity.class);
		intent.putExtra(TITLE_ARG, title);
		intent.putExtra(MESSAGE_ARG, message);
		context.startActivity(intent);
	}

	void showDialog(String title, String message, final Activity a) {
		new AlertDialog.Builder(a, AlertDialog.THEME_HOLO_DARK).setIconAttribute(android.R.attr.alertDialogIcon).setTitle(title)
				.setMessage(message).setPositiveButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int whichButton) {
						a.finish();
					}
				}).setCancelable(false).create().show();
	}

}
