package com.agilismobility.ugotflagged.UI.fragments;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.agilismobility.ugotflagged.MainApplication;
import com.agilismobility.ugotflagged.R;
import com.agilismobility.ugotflagged.dtos.PostDTO;
import com.agilismobility.ugotflagged.services.ImageDownloadingService;
import com.agilismobility.ugotflagged.utils.Utils;

public class FlagDetailsFragment extends Fragment {
	private ScrollView mLayout;
	MyReceiver receiver;
	private int mPosition;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		registerReceiver();
		mLayout = (ScrollView) inflater.inflate(R.layout.post_details, container);
		return mLayout;
	}

	private void registerReceiver() {
		IntentFilter filter = new IntentFilter(ImageDownloadingService.IMAGE_AVAILABLE_NOTIF);
		receiver = new MyReceiver();
		getActivity().registerReceiver(receiver, filter);
	}

	public class MyReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			updateContent(mPosition);
		}
	}

	private void loadAvatar(String url) {
		Intent intent = new Intent(getActivity(), ImageDownloadingService.class);
		intent.putExtra(ImageDownloadingService.URL_ARG, url);
		intent.putExtra(ImageDownloadingService.WIDTH_ARG, "48");
		intent.putExtra(ImageDownloadingService.HEIGHT_ARG, "48");
		intent.putExtra(ImageDownloadingService.CORNERS_ARG, "4");
		getActivity().startService(intent);
	}

	private void loadPhoto(String url) {
		Intent intent = new Intent(getActivity(), ImageDownloadingService.class);
		intent.putExtra(ImageDownloadingService.URL_ARG, url);
		intent.putExtra(ImageDownloadingService.WIDTH_ARG, "400");
		intent.putExtra(ImageDownloadingService.HEIGHT_ARG, "400");
		getActivity().startService(intent);
	}

	void updateContent(int position) {
		mPosition = position;
		TextView text = (TextView) mLayout.findViewById(R.id.text1);
		ImageView image = (ImageView) mLayout.findViewById(R.id.icon);
		TextView userNameText = (TextView) mLayout.findViewById(R.id.user_name);
		TextView postTitleText = (TextView) mLayout.findViewById(R.id.post_title);
		ImageView picture = (ImageView) mLayout.findViewById(R.id.picture);
		TextView postCommentsText = (TextView) mLayout.findViewById(R.id.post_comments_count);
		TextView postUserFavs = (TextView) mLayout.findViewById(R.id.post_users_favs);

		PostDTO post = MainApplication.GlobalState.getStream().get(position);
		Bitmap bitmap = ((MainApplication) getActivity().getApplication()).getImageCache().getImageForURL(post.authorAvatarURL);
		Bitmap bitmapPicture = ((MainApplication) getActivity().getApplication()).getImageCache().getImageForURL(post.photoMainURL);

		text.setText(post.text);
		userNameText.setText(post.author);
		postTitleText.setText(post.title);
		postCommentsText.setText(post.replies.size() + " comments");
		postUserFavs.setText(post.totalLikes + " users");

		if (bitmap == null) {
			if (post.authorAvatarURL != null) {
				loadAvatar(post.authorAvatarURL);
			}
			image.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.user));
		} else {
			image.setImageBitmap(bitmap);
		}
		if (bitmapPicture == null) {
			if (post.photoMainURL != null) {
				picture.setVisibility(View.VISIBLE);
				loadPhoto(post.photoMainURL);
			} else {
				picture.setVisibility(View.GONE);
			}
		} else {
			picture.setVisibility(View.VISIBLE);
			picture.setImageBitmap(Utils.getRoundedCornerBitmap(bitmapPicture, 9));
		}
	}

	@Override
	public void onDestroy() {
		getActivity().unregisterReceiver(receiver);
		super.onDestroy();
	}
}