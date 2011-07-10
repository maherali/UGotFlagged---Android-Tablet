package com.agilismobility.ugotflagged.UI;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.agilismobility.ugotflagged.R;

public class DialogActivity extends Activity {

	public static final String TITLE_ARG = "TITLE_ARG";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		String title = intent.getStringExtra(TITLE_ARG);
		setContentView(R.layout.custom_dialog_activity);
		setTitle(title);
		((Button) findViewById(R.id.ok)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	public static void showMessage(Context context, String title) {
		Intent intent = new Intent(context, DialogActivity.class);
		intent.putExtra(TITLE_ARG, title);
		context.startActivity(intent);
	}

}
