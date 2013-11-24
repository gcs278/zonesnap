package com.zonesnap.activities;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.sax.RootElement;
import android.support.v4.util.LruCache;

public class ZoneSnap_App extends Application {
	// Cache for storing pictures from network
	public static LruCache<String, Bitmap> mMemoryCache;
	public int PORT = 8080;
	public String URL = "www.grantspence.com";
	
	public ZoneSnap_App() {
		// Get max available VM memory, exceeding this amount will throw an
		// OutOfMemory exception. Stored in kilobytes as LruCache takes an
		// int in its constructor.
		final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

		// Use 1/8th of the available memory for this memory cache.
		final int cacheSize = maxMemory / 8;
		System.out.println("Cache Initialized");
		mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				// The cache size will be measured in kilobytes rather than
				// number of items.
				return bitmap.getByteCount() / 1024;
			}
		};
	}

}
