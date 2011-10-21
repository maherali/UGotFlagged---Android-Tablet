package com.agilismobility.ugotflagged.ui.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.agilismobility.ugotflagged.MainApplication;
import com.agilismobility.ugotflagged.R;
import com.agilismobility.ugotflagged.dtos.UserDTO;
import com.agilismobility.ugotflagged.services.PostService;
import com.agilismobility.ugotflagged.services.SessionService;
import com.agilismobility.ugotflagged.utils.XMLHelper;
import com.agilismobility.utils.Constants;

public class AddFlagActivity extends BaseActivity {

	AddFlagReceiver mAddFlagReceiver;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		registerReceivers();
		setContentView(R.layout.add_flag);
		final TextView flagTitleText = (TextView) findViewById(R.id.flag_title);
		final TextView plateNumberText = (TextView) findViewById(R.id.tag_number);
		final EditText flagText = (EditText) findViewById(R.id.flag_text);

		((Button) findViewById(R.id.flag_submit)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Location currLocation = ((MainApplication) getApplication()).getCurrentLocation();
				Intent intent = new Intent(AddFlagActivity.this, PostService.class);
				intent.putExtra(PostService.ACTION, PostService.ADD_FLAG_ACTION);
				intent.putExtra(PostService.FLAG_PLATE_ISSUER_PARAM, "NE");
				intent.putExtra(PostService.FLAG_PLATE_TAG_PARAM, plateNumberText.getText().toString());
				intent.putExtra(PostService.FLAG_STREET_PARAM, "1234 Fake Street");
				intent.putExtra(PostService.FLAG_CITY_PARAM, "Lincoln");
				intent.putExtra(PostService.FLAG_STATE_PARAM, "CA");
				intent.putExtra(PostService.FLAG_COUNTRY_PARAM, "US");
				intent.putExtra(PostService.FLAG_LAT_PARAM, currLocation.getLatitude() + "");
				intent.putExtra(PostService.FLAG_LONG_PARAM, currLocation.getLongitude() + "");
				intent.putExtra(PostService.FLAG_TITLE_PARAM, flagTitleText.getText().toString());
				intent.putExtra(PostService.FLAG_TEXT_PARAM, flagText.getText().toString());
				intent.putExtra(PostService.FLAG_VEHICLE_PARAM, "A nice color");
				intent.putExtra(PostService.FLAG_VEHICLE_TYPE_PARAM, "2");
				intent.putExtra(PostService.FLAG_POST_TYPE_PARAM, "2");

				startService(intent);
			}
		});
	}

	@Override
	protected void onDestroy() {
		if (mAddFlagReceiver != null) {
			unregisterReceiver(mAddFlagReceiver);
		}
		super.onDestroy();
	}

	public class AddFlagReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (PostService.ADD_FLAG_ACTION.equals(intent.getStringExtra(PostService.ACTION))) {
				if (intent.getBooleanExtra(SessionService.SUCCESS_ARG, false)) {
					parseUserAndGo(intent.getStringExtra(PostService.XML_ARG));
				} else {
					// hideProgress();
					// enableButton(R.id.login_submit, true);
					showError(intent.getStringExtra(SessionService.ERROR_ARG));
				}
			}
		}
	}

	private void parseUserAndGo(final String xml) {
		Constants.broadcastDoingSomethingNotification(Constants.PARSING_USER_DATA);
		new AsyncTask<Void, Void, UserDTO>() {
			@Override
			protected UserDTO doInBackground(Void... params) {
				return new UserDTO(new XMLHelper(xml));
			}

			@Override
			protected void onPostExecute(UserDTO u) {
				Constants.broadcastFinishedDoingSomethingNotification(Constants.PARSING_USER_DATA);
				if (u.errors.size() == 0) {
					MainApplication.GlobalState.setCurrentUser(u);
					finish();
				} else {
					showError(u.errors);
				}
			}
		}.execute();
	}

	private void registerReceivers() {
		IntentFilter filter = new IntentFilter(PostService.ADD_FLAG_FINISHED_NOTIF);
		mAddFlagReceiver = new AddFlagReceiver();
		registerReceiver(mAddFlagReceiver, filter);
	}

}
