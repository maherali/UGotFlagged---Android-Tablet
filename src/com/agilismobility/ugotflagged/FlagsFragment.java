package com.agilismobility.ugotflagged;

import com.agilismobility.ugotflagged.R;
import com.agilismobility.ugotflagged.dtos.PostDTO;
import com.agilismobility.ugotflagged.services.ImageDownloadingService;

import android.app.ListFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
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

	public class MyReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d("", "found an image!!!!!!!!!!!!!!!!!!!!!!");
			m_adapter.notifyDataSetChanged();
		}
	}

	private void load(String url) {
		Intent intent = new Intent(getActivity(), ImageDownloadingService.class);
		intent.putExtra("com.agilismobility.architecture.url", url);
		intent.putExtra("com.agilismobility.architecture.width", "100");
		intent.putExtra("com.agilismobility.architecture.height", "100");
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

			PostDTO post = MainApplication.GlobalState.getStream().get(position);
			Bitmap bitmap = ((MainApplication) getActivity().getApplication()).getImageCache().getImageForURL(post.photoiPhoneURL);
			if (bitmap != null) {
				image.setImageBitmap(bitmap);
			} else {
				image.setImageBitmap(null);
			}
			if (!mBusy) {
				text.setText(post.text);
				text.setTag(null);
				if (bitmap == null) {
					load(post.photoiPhoneURL);
				}
			} else {
				text.setText(post.text);
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
				if (text.getTag() != null) {
					PostDTO post = MainApplication.GlobalState.getStream().get(first + i);
					text.setText(post.text);
					text.setTag(null);
					Bitmap bitmap = ((MainApplication) getActivity().getApplication()).getImageCache().getImageForURL(post.photoiPhoneURL);
					if (bitmap != null) {
						image.setImageBitmap(bitmap);
					} else {
						image.setImageBitmap(null);
						load(post.photoiPhoneURL);
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