package com.agilismobility;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import com.agilismobility.ugotflagged.MainApplication;
import com.agilismobility.ugotflagged.UI.DialogActivity;

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
					getContext().startActivity(new Intent(getContext(), DialogActivity.class));
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
