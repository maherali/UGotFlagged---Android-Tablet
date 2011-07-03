package com.agilismobility.ugotflagged.services;

import java.lang.ref.SoftReference;
import java.util.HashMap;

import android.graphics.Bitmap;

public class ImageResources {

	static HashMap<String, ImageResource> map = new HashMap<String, ImageResource>();
	static int total = 0;
	static ImageResources mInstance = new ImageResources();

	public void clear() {
		map.clear();
	}

	public static ImageResources getInstance() {
		return mInstance;
	}

	private ImageResources() {

	}

	public boolean isImageDownloadingForURL(String url) {
		if (map.get(url) == null) {
			return false;
		}
		return map.get(url).downloading;
	}

	public Bitmap getImageForURL(String url) {
		if (map.get(url) == null) {
			return null;
		}
		return map.get(url).image.get();
	}

	public void markDownloadingForURL(String url) {
		map.put(url, new ImageResource(null, true, url));
	}

	public void markNotDownloadingForURL(String url) {
		ImageResource imgRs = map.get(url);
		if (imgRs != null) {
			imgRs.downloading = false;
		} else {
			map.put(url, new ImageResource(null, false, url));
		}
	}

	public void setImageForUrl(Bitmap bitmap, String url) {
		ImageResource imgRs = map.get(url);
		if (imgRs != null) {
			imgRs.downloading = false;
			imgRs.image = new SoftReference<Bitmap>(bitmap);
		} else {
			map.put(url, new ImageResource(bitmap, url));
		}
	}

	public static class ImageResource {
		public SoftReference<Bitmap> image;
		public boolean downloading;
		public String url;

		ImageResource(Bitmap bm, boolean downld, String url) {
			this.image = new SoftReference<Bitmap>(bm);
			this.downloading = downld;
			this.url = url;
		}

		ImageResource(Bitmap bm, String url) {
			this(bm, false, url);
		}
	}

}
