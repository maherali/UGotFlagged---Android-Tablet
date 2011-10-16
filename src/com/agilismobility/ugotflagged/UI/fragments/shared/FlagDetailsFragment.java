package com.agilismobility.ugotflagged.ui.fragments.shared;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.agilismobility.ugotflagged.MainApplication;
import com.agilismobility.ugotflagged.R;
import com.agilismobility.ugotflagged.dtos.PostDTO;
import com.agilismobility.ugotflagged.dtos.ReplyDTO;
import com.agilismobility.ugotflagged.services.ImageDownloadingService;
import com.agilismobility.ugotflagged.utils.Utils;

public abstract class FlagDetailsFragment extends Fragment {
	private View mLayout;
	MyReceiver receiver;
	private int mPosition;

	public void setCurrentPosition(int pos) {
		mPosition = pos;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		registerReceiver();
		mLayout = inflater.inflate(R.layout.post_details, null);
		View frame = mLayout.findViewById(R.id.frame);
		frame.setVisibility(View.GONE);
		View commentsImage = mLayout.findViewById(R.id.post_comments_image);
		commentsImage.setVisibility(View.GONE);
		View usersFavsImage = mLayout.findViewById(R.id.post_users_image);
		usersFavsImage.setVisibility(View.GONE);
		return mLayout;
	}

	public void clearContent() {
		View frame = mLayout.findViewById(R.id.frame);
		frame.setVisibility(View.INVISIBLE);
	}

	@Override
	public void onStart() {
		super.onStart();
		mLayout.setVisibility(View.VISIBLE);
		if (isVisible() && getPost(mPosition) != null) {
			updateContent(mPosition);
		}
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

	private Bitmap postTypeImageForPostType(int postType) {
		Bitmap img = null;
		switch (postType) {
		case 0:
			img = Utils.getImageAsset("shame_48.png");
			break;
		case 1:
			img = Utils.getImageAsset("asshole_48.png");
			break;
		case 2:
			img = Utils.getImageAsset("kudos_48.png");
			break;
		case 3:
			img = Utils.getImageAsset("warning_48.png");
			break;
		default:
			break;
		}
		return img;
	}

	public void updateContent(int position) {
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
		ImageView postTypePicture = (ImageView) mLayout.findViewById(R.id.post_type);
		TextView addressText = (TextView) mLayout.findViewById(R.id.address);
		TextView distanceAway = (TextView) mLayout.findViewById(R.id.distance_away);
		TextView timeAgo = (TextView) mLayout.findViewById(R.id.timeago);

		PostDTO post = getPost(position);

		Bitmap bitmap = ((MainApplication) getActivity().getApplication()).getImageCache().getImageForURL(post.authorAvatarURL);
		Bitmap bitmapPicture = ((MainApplication) getActivity().getApplication()).getImageCache().getImageForURL(post.photoMainURL);

		text.setText(post.text);
		userNameText.setText(post.author);
		postTitleText.setText(post.title);
		postCommentsText.setText(post.replies.size() == 1 ? (post.replies.size() + " comment") : (post.replies.size() + " comments"));
		postUserFavs.setText(post.totalLikes == 1 ? (post.totalLikes + " user") : (post.totalLikes + " users"));
		addReplies(post.replies);

		((Button) getActivity().findViewById(R.id.add_comment)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				addComment();
			}
		});

		postTypePicture.setImageBitmap(postTypeImageForPostType(post.postType));
		addressText.setText(postAddress(post));
		distanceAway.setText(Utils.distanceAway(((MainApplication) getActivity().getApplication()).getCurrentLocation(), post.lat, post.lng));
		timeAgo.setText(post.timeAgo);

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
		String issuer = post.plateIssuer != null ? post.plateIssuer.toUpperCase() : "";
		licensePlatePicture.setImageBitmap(Utils.getImageAsset(issuer + ".jpg"));
		plateNoText.setText(post.plateNumber);
		licensePlatePicture.invalidate();
	}

	private String postAddress(PostDTO post) {
		String addr = post.street != null ? (post.street + ", ") : "";
		addr = addr + (post.city != null ? (post.city + ", ") : "");
		addr = addr + (post.state != null ? post.state : "");
		if (post.street == null && post.city == null && post.state == null) {
			if ((post.lat >= -90.0f && post.lat <= 90) && (post.lng >= -180 && post.lng <= 180)) {
				addr = "Latitude/Longitude: (" + post.lat + ", " + post.lng + ")";
			}
		}
		return addr;
	}

	@Override
	public void onDestroy() {
		getActivity().unregisterReceiver(receiver);
		super.onDestroy();
	}

	protected void addComment() {
		LayoutInflater inflater = LayoutInflater.from(this.getActivity());
		View alertDialogView = inflater.inflate(R.layout.add_reply, null);
		EditText myEditText = (EditText) alertDialogView.findViewById(R.id.text_edit_view);
		AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
		builder.setView(alertDialogView);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		}).show();
	}

	abstract protected PostDTO getPost(int pos);
}