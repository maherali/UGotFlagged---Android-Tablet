package com.agilismobility.ugotflagged.UI;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.agilismobility.ugotflagged.R;

public class DialogActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.custom_dialog_activity);
		setTitle("You can only perform this function in Online mode.");
		((Button) findViewById(R.id.ok)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

}
