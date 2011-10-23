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
import com.agilismobility.ugotflagged.Vehicle;
import com.agilismobility.ugotflagged.dtos.PostDTO;
import com.agilismobility.ugotflagged.dtos.ReplyDTO;
import com.agilismobility.ugotflagged.services.ImageDownloadingService;
import com.agilismobility.ugotflagged.services.PostService;
import com.agilismobility.ugotflagged.utils.Utils;
import com.agilismobility.util.Util;

public abstract class FlagDetailsFragment extends Fragment {
	private View mLayout;
	ImageReceiver imageReceiver;
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
		registerReceivers();
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

	private void registerReceivers() {
		IntentFilter filter = new IntentFilter(ImageDownloadingService.IMAGE_AVAILABLE_NOTIF);
		imageReceiver = new ImageReceiver();
		getActivity().registerReceiver(imageReceiver, filter);
	}

	public class ImageReceiver extends BroadcastReceiver {
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
		intent.putExtra(ImageDownloadingService.WIDTH_ARG, "300");
		intent.putExtra(ImageDownloadingService.HEIGHT_ARG, "300");
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

	private Bitmap vehcileTypeImage(int vehicleType) {
		Bitmap img = null;
		Vehicle[] vehicles = MainApplication.getVehicles();
		for (int i = 0; i < vehicles.length; i++) {
			Vehicle v = vehicles[i];
			if (new Integer(v.id) == vehicleType) {
				img = Utils.getImageAsset(v.image);
				break;
			}
		}
		return img;
	}

	public void update() {
		if (!isAdded())
			return;
		updateContent(mPosition);
	}

	public void updateContent(int position) {
		mPosition = position;
		View frame = mLayout.findViewById(R.id.frame);
		final PostDTO post = getPost(position);
		if (post == null) {
			frame.setVisibility(View.INVISIBLE);
			return;
		}
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
		TextView vehicleDescription = (TextView) mLayout.findViewById(R.id.vehicle_description);
		ImageView vehicleType = (ImageView) mLayout.findViewById(R.id.vehicle_type);

		TextView timeAgo = (TextView) mLayout.findViewById(R.id.timeago);

		Bitmap bitmap = ((MainApplication) getActivity().getApplication()).getImageCache().getImageForURL(post.authorAvatarURL);
		Bitmap bitmapPicture = ((MainApplication) getActivity().getApplication()).getImageCache().getImageForURL(post.photoMainURL);

		vehicleDescription.setText(post.vehicle);
		vehicleType.setImageBitmap(vehcileTypeImage(post.vehicleType));

		text.setText(post.text);
		userNameText.setText(post.author);
		postTitleText.setText(post.title);
		postCommentsText.setText(Util.pluralize(post.replies.size(), "comment", "comments"));
		postUserFavs.setText(Util.pluralize(post.totalLikes, "user", "users"));
		addReplies(post.replies);

		Button addButton = ((Button) getActivity().findViewById(R.id.add_comment));
		if (addButton == null)
			return;
		addButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				addComment();
			}
		});

		Button likeUnlikeButton = ((Button) getActivity().findViewById(R.id.like_unlike));
		if (post.canLike) {
			likeUnlikeButton.setVisibility(View.VISIBLE);
		} else {
			likeUnlikeButton.setVisibility(View.GONE);
		}
		if (post.liked) {
			likeUnlikeButton.setText("Unlike");
		} else {
			likeUnlikeButton.setText("Like");
		}
		likeUnlikeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				likeUnlike(post);
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

	protected void likeUnlike(PostDTO post) {
		Intent intent = new Intent(getActivity(), PostService.class);
		intent.putExtra(PostService.ACTION, post.liked ? PostService.UNLIKE_ACTION : PostService.LIKE_ACTION);
		intent.putExtra(PostService.POST_ID_PARAM, "" + getPost(mPosition).identifier);
		getActivity().startService(intent);
	}

	private String postAddress(PostDTO post) {
		String addr = post.street != null ? (post.street + ", ") : "";
		addr = addr + (post.city != null ? (post.city + ", ") : "");
		addr = addr + (post.state != null ? post.state : "");
		addr = addr + (post.country != null ? (", " + post.country) : "");
		if (post.street == null && post.city == null && post.state == null) {
			if ((post.lat >= -90.0f && post.lat <= 90) && (post.lng >= -180 && post.lng <= 180)) {
				addr = "Latitude/Longitude: (" + post.lat + ", " + post.lng + ")";
			}
		}
		return addr;
	}

	private void postComment(String commentText) {
		Intent intent = new Intent(getActivity(), PostService.class);
		intent.putExtra(PostService.ACTION, PostService.ADD_COMMENT_ACTION);
		intent.putExtra(PostService.COMMENT_TEXT_PARAM, commentText);
		intent.putExtra(PostService.POST_ID_PARAM, "" + getPost(mPosition).identifier);
		getActivity().startService(intent);
	}

	@Override
	public void onDestroy() {
		if (imageReceiver != null) {
			getActivity().unregisterReceiver(imageReceiver);
		}
		super.onDestroy();
	}

	protected void addComment() {
		LayoutInflater inflater = LayoutInflater.from(this.getActivity());
		View alertDialogView = inflater.inflate(R.layout.add_reply, null);
		final EditText myEditText = (EditText) alertDialogView.findViewById(R.id.text_edit_view);
		AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
		builder.setView(alertDialogView);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				postComment(myEditText.getText().toString());
			}
		}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		}).show();
	}

	abstract protected PostDTO getPost(int pos);
}