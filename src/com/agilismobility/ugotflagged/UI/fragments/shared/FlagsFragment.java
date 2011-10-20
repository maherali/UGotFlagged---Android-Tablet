package com.agilismobility.ugotflagged.ui.fragments.shared;

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
import com.agilismobility.util.Util;

public abstract class FlagsFragment extends ListFragment implements ListView.OnScrollListener {
	protected int mCurPosition;
	protected boolean mBusy;
	protected SlowAdapter m_adapter;
	protected ImageAvailableReceiver receiver;
	protected View mListViewHeader;

	public int getCurrentPosition() {
		return mCurPosition;
	}

	public void setCurrentPosition(int pos) {
		mCurPosition = pos;
	}

	@Override
	public void onStart() {
		super.onStart();
		selectPosition(mCurPosition);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (savedInstanceState != null) {
			mCurPosition = savedInstanceState.getInt("listPosition");
		}
		ListView lv = getListView();
		setupHeaderView(lv);
		lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		lv.setCacheColorHint(Color.CYAN);
		if (mCurPosition >= 0) {
			selectPosition(mCurPosition);
		}

		setupListAdapter();
		getListView().setOnScrollListener(this);
		registerReceiver();
	}

	@Override
	public void onDestroy() {
		if (receiver != null) {
			getActivity().unregisterReceiver(receiver);
		}
		super.onDestroy();
	}

	private void registerReceiver() {
		IntentFilter filter = new IntentFilter(ImageDownloadingService.IMAGE_AVAILABLE_NOTIF);
		receiver = new ImageAvailableReceiver();
		getActivity().registerReceiver(receiver, filter);
	}

	public void refresh() {
		if (getPostCount() == 0) {
			setEmptyText("User has no posts.");
		}
		m_adapter.notifyDataSetChanged();
	}

	public class ImageAvailableReceiver extends BroadcastReceiver {
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

	public class SlowAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public SlowAdapter(Context context) {
			mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return getPostCount();
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

			PostDTO post = getPostAtPosition(position);

			text.setText(post.text);
			userNameText.setText(post.author);
			postTitleText.setText(post.title);
			postCommentsText.setText(Util.pluralize(post.replies.size(), "comment", "comments"));
			postUserFavs.setText(Util.pluralize(post.totalLikes, "user", "users"));
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
				PostDTO post = getPostAtPosition(first + i);
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

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		showAt(position);
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

	protected void showHeaderView() {
		if (mListViewHeader != null) {
			mListViewHeader.setVisibility(View.VISIBLE);
		}
	}

	protected void removeHeaderView() {
		if (mListViewHeader != null) {
			mListViewHeader.setVisibility(View.GONE);
		}
	}

	abstract protected void setupListAdapter();

	abstract protected void showAt(int position);

	abstract protected int getPostCount();

	abstract protected PostDTO getPostAtPosition(int position);

	abstract protected void setupHeaderView(ListView lv);

}
