package com.agilismobility.ugotflagged.UI.fragments;

import java.util.ArrayList;

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
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.agilismobility.ugotflagged.MainApplication;
import com.agilismobility.ugotflagged.R;
import com.agilismobility.ugotflagged.dtos.PostDTO;
import com.agilismobility.ugotflagged.dtos.ReplyDTO;
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
		View frame = mLayout.findViewById(R.id.frame);
		frame.setVisibility(View.GONE);
		View commentsImage = mLayout.findViewById(R.id.post_comments_image);
		commentsImage.setVisibility(View.GONE);
		View usersFavsImage = mLayout.findViewById(R.id.post_users_image);
		usersFavsImage.setVisibility(View.GONE);
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

	/*
	 * LinearLayout listLayout = (LinearLayout)findViewById(R.id.list);
	 * listLayout.removeAllViews(); for(CurrencyItem currency : currencies){
	 * LayoutInflater vi =
	 * (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE
	 * ); View v = vi.inflate(R.layout.currency_item, null);
	 * 
	 * TextView currencyTypeView = (TextView)
	 * v.findViewById(R.id.currency_type_text);
	 * currencyTypeView.setText(currency.getCurrencyTypeText()); TextView
	 * currencyCountryView = (TextView)
	 * v.findViewById(R.id.currency_country_text);
	 * currencyCountryView.setText(currency.getCountryName()); TextView
	 * exchangeAmountView = (TextView)
	 * v.findViewById(R.id.exchange_amount_formatted);
	 * exchangeAmountView.setText(currency.getExchangeAmountFormatted());
	 * listLayout.addView(v); }
	 */

	private void addReplies(ArrayList<ReplyDTO> replies) {
		LinearLayout repliesLayout = (LinearLayout) mLayout.findViewById(R.id.replies);
		repliesLayout.removeAllViews();
		LayoutInflater inflator = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		for (ReplyDTO reply : replies) {
			View v = inflator.inflate(R.layout.reply, null);
			ImageView avatarImageView = (ImageView) v.findViewById(R.id.avatar);
			TextView userNameText = (TextView) v.findViewById(R.id.user_name);
			TextView replyText = (TextView) v.findViewById(R.id.reply_text);
			TextView timeAgoText = (TextView) v.findViewById(R.id.timeago);
			Bitmap bitmap = ((MainApplication) getActivity().getApplication()).getImageCache().getImageForURL(reply.authorAvatarURL);
			if (bitmap == null) {
				if (reply.authorAvatarURL != null) {
					loadAvatar(reply.authorAvatarURL);
				}
				avatarImageView.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.user));
			} else {
				avatarImageView.setImageBitmap(bitmap);
			}
			userNameText.setText(reply.author);
			replyText.setText(reply.text);
			timeAgoText.setText(reply.timeAgo);
			repliesLayout.addView(v);
		}
	}

	void updateContent(int position) {
		mPosition = position;
		View frame = mLayout.findViewById(R.id.frame);
		frame.setVisibility(View.VISIBLE);
		View commentsImage = mLayout.findViewById(R.id.post_comments_image);
		commentsImage.setVisibility(View.VISIBLE);
		View usersFavsImage = mLayout.findViewById(R.id.post_users_image);
		usersFavsImage.setVisibility(View.VISIBLE);
		TextView text = (TextView) mLayout.findViewById(R.id.text1);
		ImageView image = (ImageView) mLayout.findViewById(R.id.icon);
		TextView userNameText = (TextView) mLayout.findViewById(R.id.user_name);
		TextView postTitleText = (TextView) mLayout.findViewById(R.id.post_title);
		ImageView picture = (ImageView) mLayout.findViewById(R.id.picture);
		TextView postCommentsText = (TextView) mLayout.findViewById(R.id.post_comments_count);
		TextView postUserFavs = (TextView) mLayout.findViewById(R.id.post_users_favs);
		ImageView licensePlatePicture = (ImageView) mLayout.findViewById(R.id.license_plate_image);
		TextView plateNoText = (TextView) mLayout.findViewById(R.id.plate_no);

		PostDTO post = MainApplication.GlobalState.getStream().get(position);
		Bitmap bitmap = ((MainApplication) getActivity().getApplication()).getImageCache().getImageForURL(post.authorAvatarURL);
		Bitmap bitmapPicture = ((MainApplication) getActivity().getApplication()).getImageCache().getImageForURL(post.photoMainURL);

		text.setText(post.text);
		userNameText.setText(post.author);
		postTitleText.setText(post.title);
		postCommentsText.setText(post.replies.size() + " comments");
		postUserFavs.setText(post.totalLikes + " users");
		addReplies(post.replies);
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
		licensePlatePicture.setImageBitmap(Utils.getImageAsset(post.plateIssuer.toUpperCase() + ".jpg"));
		plateNoText.setText(post.plateNumber);
		licensePlatePicture.invalidate();
	}

	@Override
	public void onDestroy() {
		getActivity().unregisterReceiver(receiver);
		super.onDestroy();
	}
}