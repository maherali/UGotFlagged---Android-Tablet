package com.agilismobility;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import com.agilismobility.ugotflagged.MainApplication;
import com.agilismobility.ugotflagged.ui.activities.DialogActivity;

public class InternetButton extends Button {

	protected OnClickInternetListener mOnClickInternetListener;

	public InternetButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (MainApplication.isNetworkConnected()) {
					mOnClickInternetListener.onClick(InternetButton.this);
				} else {
					DialogActivity.showMessage(getContext(), "Error", "You can only perform this function in Online mode.");
				}

			}
		});
	}

	public void setOnClickInternetListener(OnClickInternetListener l) {
		mOnClickInternetListener = l;
	}

	public interface OnClickInternetListener {
		public void onClick(View v);
	}

}
