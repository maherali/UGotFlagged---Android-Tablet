package com.agilismobility.ugotflagged.ui.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.agilismobility.ugotflagged.MainApplication;
import com.agilismobility.ugotflagged.R;
import com.agilismobility.ugotflagged.State;
import com.agilismobility.ugotflagged.Vehicle;
import com.agilismobility.ugotflagged.dtos.GeocodeDTO;
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

		final Spinner issuerSpinner = (Spinner) findViewById(R.id.issuer_spinner);
		ArrayAdapter<State> adapter = new ArrayAdapter<State>(this, android.R.layout.simple_spinner_item, MainApplication.getStates());
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		issuerSpinner.setAdapter(adapter);

		final Spinner vehicleSpinner = (Spinner) findViewById(R.id.vehicle_spinner);
		ArrayAdapter<Vehicle> vehicleAdapter = new ArrayAdapter<Vehicle>(this, android.R.layout.simple_spinner_item,
				MainApplication.getVehicles());
		vehicleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		vehicleSpinner.setAdapter(vehicleAdapter);

		final TextView flagTitleText = (TextView) findViewById(R.id.flag_title);
		final TextView plateNumberText = (TextView) findViewById(R.id.tag_number);
		final EditText flagText = (EditText) findViewById(R.id.flag_text);
		final TextView vehicleDescriptionText = (TextView) findViewById(R.id.vehicle_description);

		((Button) findViewById(R.id.flag_submit)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Location currLocation = ((MainApplication) getApplication()).getCurrentLocation();
				GeocodeDTO geo = MainApplication.GlobalState.getCurrentGeocodedAddress();
				Intent intent = new Intent(AddFlagActivity.this, PostService.class);
				intent.putExtra(PostService.ACTION, PostService.ADD_FLAG_ACTION);

				State selectedState = (State) issuerSpinner.getSelectedItem();
				if (selectedState == null) {
					showError("Please enter select a plate issuer.");
					return;
				}
				intent.putExtra(PostService.FLAG_PLATE_ISSUER_PARAM, selectedState.id);
				if (plateNumberText.getText().toString().trim().equals("")) {
					showError("Please enter a plate number.");
					return;
				}
				if (plateNumberText.getText().toString().length() > 8) {
					showError("Please enter a maximum of 8 characters for plate number.");
					return;
				}
				intent.putExtra(PostService.FLAG_PLATE_TAG_PARAM, plateNumberText.getText().toString());

				String street = "";
				if (geo != null && geo.street1 != null) {
					street = geo.street1;
					if (geo.street2 != null) {
						street += " and " + geo.street2;
					}
				} else {
					street = "Unknown";
				}
				intent.putExtra(PostService.FLAG_STREET_PARAM, street);

				String city = "";
				if (geo != null && geo.city != null) {
					city = geo.city;
				} else {
					city = "Unknown";
				}
				intent.putExtra(PostService.FLAG_CITY_PARAM, city);

				String state = "";
				if (geo != null && geo.state != null) {
					state = geo.state;
				} else {
					state = "Unknown";
				}
				intent.putExtra(PostService.FLAG_STATE_PARAM, state);

				String country = "";
				if (geo != null && geo.countryCode != null) {
					country = geo.countryCode;
				} else {
					country = "Unknown";
				}
				intent.putExtra(PostService.FLAG_COUNTRY_PARAM, country);

				intent.putExtra(PostService.FLAG_LAT_PARAM, currLocation.getLatitude() + "");
				intent.putExtra(PostService.FLAG_LONG_PARAM, currLocation.getLongitude() + "");
				if (flagTitleText.getText().toString().trim().equals("")) {
					showError("Please enter a title for your flag");
					return;
				}
				intent.putExtra(PostService.FLAG_TITLE_PARAM, flagTitleText.getText().toString());
				if (flagText.getText().toString().trim().equals("")) {
					showError("Please enter a description for your flag");
					return;
				}
				intent.putExtra(PostService.FLAG_TEXT_PARAM, flagText.getText().toString());

				if (vehicleDescriptionText.getText().toString().trim().equals("")) {
					showError("Please enter a description for the vehicle");
					return;
				}
				intent.putExtra(PostService.FLAG_VEHICLE_PARAM, vehicleDescriptionText.getText().toString());
				Vehicle selectedVehicle = (Vehicle) vehicleSpinner.getSelectedItem();
				intent.putExtra(PostService.FLAG_VEHICLE_TYPE_PARAM, selectedVehicle.id);
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
