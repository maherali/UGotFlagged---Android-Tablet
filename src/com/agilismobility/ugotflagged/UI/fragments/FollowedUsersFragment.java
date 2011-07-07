package com.agilismobility.ugotflagged.UI.fragments;

import android.app.ListFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.agilismobility.ugotflagged.MainApplication;
import com.agilismobility.ugotflagged.R;
import com.agilismobility.ugotflagged.UI.BaseActivity;
import com.agilismobility.ugotflagged.dtos.UserDTO;
import com.agilismobility.ugotflagged.dtos.UsersDTO;
import com.agilismobility.ugotflagged.services.FollowedUsersService;
import com.agilismobility.ugotflagged.services.ImageDownloadingService;
import com.agilismobility.ugotflagged.utils.XMLHelper;
import com.agilismobility.utils.Constants;

public class FollowedUsersFragment extends ListFragment implements ListView.OnScrollListener {
	private int mCurPosition;
	private boolean mBusy;
	private SlowAdapter m_adapter;
	ImageDownloadedReceiver imageDownloadedReceiver;
	FollowedUsersReceiver mFollowedUsersReceiver;

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
		registerReceivers();
		if (savedInstanceState != null) {
			mCurPosition = savedInstanceState.getInt("listPosition");
		}
		findFollowedUsers();
		ListView lv = getListView();
		lv.setItemsCanFocus(true);
		lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		lv.setCacheColorHint(Color.TRANSPARENT);
		if (mCurPosition >= 0) {
			selectPosition(mCurPosition);
		}
		setListAdapter(m_adapter = new SlowAdapter(getActivity()));
		getListView().setOnScrollListener(this);
	}

	@Override
	public void onDestroy() {
		getActivity().unregisterReceiver(imageDownloadedReceiver);
		getActivity().unregisterReceiver(mFollowedUsersReceiver);
		super.onDestroy();
	}

	private void registerReceivers() {
		getActivity().registerReceiver(imageDownloadedReceiver = new ImageDownloadedReceiver(),
				new IntentFilter(ImageDownloadingService.IMAGE_AVAILABLE_NOTIF));
		getActivity().registerReceiver(mFollowedUsersReceiver = new FollowedUsersReceiver(),
				new IntentFilter(FollowedUsersService.FOLLOWED_USERS_FINISHED_NOTIF));
	}

	public void followUnFollow(UserDTO user) {
		Log.d("", user.userName);
	}

	public void refresh() {
		m_adapter.notifyDataSetChanged();
	}

	public class ImageDownloadedReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			refresh();
		}
	}

	private void parseFollowed(final String xml) {
		Constants.broadcastDoingSomethingNotification(Constants.PARSING_USER_DATA);
		new AsyncTask<Void, Void, UsersDTO>() {
			@Override
			protected UsersDTO doInBackground(Void... params) {
				return new UsersDTO(new XMLHelper(xml));
			}

			@Override
			protected void onPostExecute(UsersDTO u) {
				Constants.broadcastFinishedDoingSomethingNotification(Constants.PARSING_USER_DATA);
				if (u.errors.size() == 0) {
					MainApplication.GlobalState.setFollowedUsers(u);
					refresh();
				} else {
					((BaseActivity) getActivity()).showError(u.errors);
				}
			}
		}.execute();
	}

	public class FollowedUsersReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getBooleanExtra(FollowedUsersService.SUCCESS_ARG, false)) {
				parseFollowed(intent.getStringExtra(FollowedUsersService.XML_ARG));
			} else {
				((BaseActivity) getActivity()).showError(intent.getStringExtra(FollowedUsersService.ERROR_ARG));
			}
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
			if (MainApplication.GlobalState.getFollowedUsers() != null) {
				return MainApplication.GlobalState.getFollowedUsers().getUsers().size();
			} else {
				return 0;
			}
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
				layout = (LinearLayout) mInflater.inflate(R.layout.followed_user, parent, false);
			} else {
				layout = (LinearLayout) convertView;
			}

			ImageView avatarImage = (ImageView) layout.findViewById(R.id.avatar);
			TextView userNameText = (TextView) layout.findViewById(R.id.user_name);
			Button followButton = (Button) layout.findViewById(R.id.follow);

			UserDTO user = MainApplication.GlobalState.getFollowedUsers().getUsers().get(position);
			userNameText.setText(user.userName);
			followButton.setText("UnFollow");
			followButton.setTag(user);
			followButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					UserDTO user = (UserDTO) v.getTag();
					followUnFollow(user);
				}
			});

			Bitmap bitmap = ((MainApplication) getActivity().getApplication()).getImageCache().getImageForURL(user.avatarMainURL);
			if (bitmap != null) {
				avatarImage.setTag(null);
				avatarImage.setImageBitmap(bitmap);
			} else {
				if (user.avatarMainURL != null) {
					avatarImage.setTag(this);
					if (!mBusy) {
						load(user.avatarMainURL);
					}
				}
				avatarImage.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.user));
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
				UserDTO user = MainApplication.GlobalState.getFollowedUsers().getUsers().get(first + i);
				ImageView image = (ImageView) layout.findViewById(R.id.avatar);
				if (image.getTag() != null) {
					Bitmap bitmap = ((MainApplication) getActivity().getApplication()).getImageCache().getImageForURL(user.avatarMainURL);
					if (bitmap != null) {
						image.setTag(null);
						image.setImageBitmap(bitmap);
					} else {
						image.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.user));
						if (user.avatarMainURL != null) {
							load(user.avatarMainURL);
						} else {
							image.setTag(null);
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

	public void findFollowedUsers() {
		getActivity().startService(new Intent(getActivity(), FollowedUsersService.class));
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		showAt(position);
	}

	private void showAt(int position) {
		// FlagDetailsFragment frag = ((MainApplication)
		// MainApplication.getInstance()).getFlagDetailsFragment();
		// if (frag != null && frag.isVisible()) {
		// frag.updateContent(position);
		// }
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
