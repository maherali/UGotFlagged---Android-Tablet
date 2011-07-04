package com.agilismobility.ugotflagged.UI.fragments;

import android.app.ListFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.agilismobility.ugotflagged.MainApplication;
import com.agilismobility.ugotflagged.R;
import com.agilismobility.ugotflagged.dtos.PostDTO;
import com.agilismobility.ugotflagged.services.ImageDownloadingService;
import com.agilismobility.ugotflagged.utils.Utils;

public class FlagsFragment extends ListFragment implements ListView.OnScrollListener {
	private int mCurPosition = -1;
	private boolean mBusy;
	private SlowAdapter m_adapter;
	MyReceiver receiver;

	public int getCurrentPosition() {
		return mCurPosition;
	}

	public void setCurrentPosition(int pos) {
		mCurPosition = pos;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (savedInstanceState != null) {
			mCurPosition = savedInstanceState.getInt("listPosition");
		}
		populateStream();
		ListView lv = getListView();
		lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		lv.setCacheColorHint(Color.TRANSPARENT);
		if (mCurPosition >= 0) {
			selectPosition(mCurPosition);
		}
		setListAdapter(m_adapter = new SlowAdapter(getActivity()));
		getListView().setOnScrollListener(this);
		registerReceiver();
	}

	@Override
	public void onDestroy() {
		getActivity().unregisterReceiver(receiver);
		super.onDestroy();
	}

	private void registerReceiver() {
		IntentFilter filter = new IntentFilter(ImageDownloadingService.IMAGE_AVAILABLE_NOTIF);
		receiver = new MyReceiver();
		getActivity().registerReceiver(receiver, filter);
	}

	public void refresh() {
		m_adapter.notifyDataSetChanged();
	}

	public class MyReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			refresh();
		}
	}

	private void load(String url) {
		Intent intent = new Intent(getActivity(), ImageDownloadingService.class);
		intent.putExtra(ImageDownloadingService.URL_ARG, url);
		intent.putExtra(ImageDownloadingService.WIDTH_ARG, "48");
		intent.putExtra(ImageDownloadingService.HEIGHT_ARG, "48");
		intent.putExtra(ImageDownloadingService.CORNERS_ARG, "4");
		getActivity().startService(intent);
	}

	private class SlowAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public SlowAdapter(Context context) {
			mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return MainApplication.GlobalState.getStream().size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LinearLayout layout;
			if (convertView == null) {
				layout = (LinearLayout) mInflater.inflate(R.layout.stream_item, parent, false);
			} else {
				layout = (LinearLayout) convertView;
			}
			TextView text = (TextView) layout.findViewById(R.id.text1);
			ImageView image = (ImageView) layout.findViewById(R.id.icon);
			TextView userNameText = (TextView) layout.findViewById(R.id.user_name);
			TextView postTitleText = (TextView) layout.findViewById(R.id.post_title);
			ImageView picture = (ImageView) layout.findViewById(R.id.picture);
			TextView postCommentsText = (TextView) layout.findViewById(R.id.post_comments_count);
			TextView postUserFavs = (TextView) layout.findViewById(R.id.post_users_favs);
			TextView distanceAway = (TextView) layout.findViewById(R.id.distance_away);
			TextView timeAgo = (TextView) layout.findViewById(R.id.timeago);

			PostDTO post = MainApplication.GlobalState.getStream().get(position);

			text.setText(post.text);
			userNameText.setText(post.author);
			postTitleText.setText(post.title);
			postCommentsText.setText(post.replies.size() == 1 ? (post.replies.size() + " comment") : (post.replies.size() + " comments"));
			postUserFavs.setText(post.totalLikes == 1 ? (post.totalLikes + " user") : (post.totalLikes + " users"));
			Location currLoc = ((MainApplication) getActivity().getApplication()).getCurrentLocation();
			distanceAway.setText(Utils.distanceAway(currLoc, post.lat, post.lng));
			timeAgo.setText(post.timeAgo);

			Bitmap bitmap = ((MainApplication) getActivity().getApplication()).getImageCache().getImageForURL(post.authorAvatarURL);
			if (bitmap != null) {
				image.setTag(null);
				image.setImageBitmap(bitmap);
			} else {
				if (post.authorAvatarURL != null) {
					image.setTag(this);
					if (!mBusy) {
						load(post.authorAvatarURL);
					}
				}
				image.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.user));
			}

			Bitmap bitmapPicture = ((MainApplication) getActivity().getApplication()).getImageCache().getImageForURL(post.photoiPhoneURL);
			if (bitmapPicture != null) {
				picture.setVisibility(View.VISIBLE);
				picture.setImageBitmap(bitmapPicture);
				picture.setTag(null);
			} else {
				if (post.photoiPhoneURL != null) {
					picture.setTag(this);
					picture.setVisibility(View.VISIBLE);
					picture.setImageBitmap(null);
					if (!mBusy) {
						load(post.photoiPhoneURL);
					}
				} else {
					picture.setVisibility(View.GONE);
				}
			}

			return layout;
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		switch (scrollState) {
		case OnScrollListener.SCROLL_STATE_IDLE:
			mBusy = false;
			int first = view.getFirstVisiblePosition();
			int count = view.getChildCount();
			for (int i = 0; i < count; i++) {
				LinearLayout layout = (LinearLayout) view.getChildAt(i);
				PostDTO post = MainApplication.GlobalState.getStream().get(first + i);

				ImageView image = (ImageView) layout.findViewById(R.id.icon);
				if (image.getTag() != null) {
					Bitmap bitmap = ((MainApplication) getActivity().getApplication()).getImageCache().getImageForURL(post.authorAvatarURL);
					if (bitmap != null) {
						image.setTag(null);
						image.setImageBitmap(bitmap);
					} else {
						image.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.user));
						if (post.authorAvatarURL != null) {
							load(post.authorAvatarURL);
						} else {
							image.setTag(null);
						}
					}
				}

				ImageView picture = (ImageView) layout.findViewById(R.id.picture);
				if (picture.getTag() != null) {
					Bitmap bitmapPicture = ((MainApplication) getActivity().getApplication()).getImageCache().getImageForURL(
							post.photoiPhoneURL);
					if (bitmapPicture != null) {
						picture.setTag(null);
						picture.setVisibility(View.VISIBLE);
						picture.setImageBitmap(bitmapPicture);
					} else {
						if (post.photoiPhoneURL != null) {
							picture.setVisibility(View.VISIBLE);
							load(post.photoiPhoneURL);
						} else {
							picture.setVisibility(View.GONE);
						}
					}
				}
			}
			break;
		case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
			mBusy = true;
			break;
		case OnScrollListener.SCROLL_STATE_FLING:
			mBusy = true;
			break;
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
	}

	public void populateStream() {
		setListAdapter(new ArrayAdapter<PostDTO>(getActivity(), R.layout.stream_item, MainApplication.GlobalState.getStream()));
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		showAt(position);
	}

	private void showAt(int position) {
		FlagDetailsFragment frag = ((MainApplication) MainApplication.getInstance()).getFlagDetailsFragment();
		frag.updateContent(position);
		mCurPosition = position;
	}

	public void selectPosition(int position) {
		ListView lv = getListView();
		lv.setItemChecked(position, true);
		showAt(position);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("listPosition", mCurPosition);
	}
}