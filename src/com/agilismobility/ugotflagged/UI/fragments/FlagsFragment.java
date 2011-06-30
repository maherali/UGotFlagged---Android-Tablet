package com.agilismobility.ugotflagged.UI.fragments;

import com.agilismobility.ugotflagged.R;
import com.agilismobility.ugotflagged.MainApplication;
import com.agilismobility.ugotflagged.dtos.PostDTO;
import com.agilismobility.ugotflagged.services.ImageDownloadingService;
import com.agilismobility.ugotflagged.utils.Utils;

import android.app.ListFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
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

public class FlagsFragment extends ListFragment implements ListView.OnScrollListener {
	private int mCurPosition = 0;
	private boolean mBusy;
	private SlowAdapter m_adapter;
	MyReceiver receiver;

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
		selectPosition(mCurPosition);
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
			Log.d("", "found an image!!!!!!!!!!!!!!!!!!!!!!");
			refresh();
		}
	}

	private void load(String url) {
		Intent intent = new Intent(getActivity(), ImageDownloadingService.class);
		intent.putExtra("com.agilismobility.architecture.url", url);
		intent.putExtra("com.agilismobility.architecture.width", "48");
		intent.putExtra("com.agilismobility.architecture.height", "48");
		intent.putExtra("com.agilismobility.architecture.corners", "4");
		getActivity().startService(intent);
	}

	private class SlowAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public SlowAdapter(Context context) {
			mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		public int getCount() {
			return MainApplication.GlobalState.getStream().size();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

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

			PostDTO post = MainApplication.GlobalState.getStream().get(position);
			Bitmap bitmap = ((MainApplication) getActivity().getApplication()).getImageCache().getImageForURL(post.authorAvatarURL);
			if (bitmap != null) {
				image.setImageBitmap(bitmap);
			} else {
				image.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.user));
			}
			Bitmap bitmapPicture = ((MainApplication) getActivity().getApplication()).getImageCache().getImageForURL(post.photoiPhoneURL);
			if (bitmapPicture != null) {
				picture.setVisibility(View.VISIBLE);
				picture.setImageBitmap(bitmapPicture);
			} else {
				if (post.photoiPhoneURL != null) {
					picture.setVisibility(View.VISIBLE);
					picture.setImageBitmap(null);
				} else {
					picture.setVisibility(View.GONE);
				}
			}
			if (!mBusy) {
				text.setText(post.text);
				userNameText.setText(post.author);
				postTitleText.setText(post.title);
				postCommentsText.setText(post.replies.size() + " comments");
				postUserFavs.setText(post.totalLikes + " users");
				Location currLoc = ((MainApplication) getActivity().getApplication()).getCurrentLocation();
				distanceAway.setText(Utils.distanceAway(currLoc, post.lat, post.lng));
				text.setTag(null);
				if (bitmap == null) {
					load(post.authorAvatarURL);
					image.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.user));
				}
				if (bitmapPicture == null) {
					if (post.photoiPhoneURL != null) {
						picture.setVisibility(View.VISIBLE);
						load(post.photoiPhoneURL);
					} else {
						picture.setVisibility(View.GONE);
					}
				}
			} else {
				text.setText(post.text);
				userNameText.setText(post.author);
				postTitleText.setText(post.title);
				postCommentsText.setText(post.replies.size() + " comments");
				postUserFavs.setText(post.totalLikes + " users");
				Location currLoc = ((MainApplication) getActivity().getApplication()).getCurrentLocation();
				distanceAway.setText(Utils.distanceAway(currLoc, post.lat, post.lng));
				text.setTag(this);
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
				TextView text = (TextView) layout.findViewById(R.id.text1);
				ImageView image = (ImageView) layout.findViewById(R.id.icon);
				TextView userNameText = (TextView) layout.findViewById(R.id.user_name);
				TextView postTitleText = (TextView) layout.findViewById(R.id.post_title);
				TextView postCommentsText = (TextView) layout.findViewById(R.id.post_comments_count);
				TextView postUserFavs = (TextView) layout.findViewById(R.id.post_users_favs);
				ImageView picture = (ImageView) layout.findViewById(R.id.picture);
				TextView distanceAway = (TextView) layout.findViewById(R.id.distance_away);

				if (text.getTag() != null) {
					PostDTO post = MainApplication.GlobalState.getStream().get(first + i);
					text.setText(post.text);
					userNameText.setText(post.author);
					postTitleText.setText(post.title);
					postCommentsText.setText(post.replies.size() + " comments");
					postUserFavs.setText(post.totalLikes + " users");
					Location currLoc = ((MainApplication) getActivity().getApplication()).getCurrentLocation();
					distanceAway.setText(Utils.distanceAway(currLoc, post.lat, post.lng));
					text.setTag(null);
					Bitmap bitmap = ((MainApplication) getActivity().getApplication()).getImageCache().getImageForURL(post.authorAvatarURL);
					if (bitmap != null) {
						image.setImageBitmap(bitmap);
					} else {
						image.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.user));
						load(post.authorAvatarURL);
					}
					Bitmap bitmapPicture = ((MainApplication) getActivity().getApplication()).getImageCache().getImageForURL(
							post.photoiPhoneURL);
					if (bitmapPicture != null) {
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
		FlagDetailsFragment frag = (FlagDetailsFragment) getFragmentManager().findFragmentById(R.id.frag_details);
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