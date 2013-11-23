package com.zonesnap.activities;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.zonesnap.networking.get.NetworkGetPicture;
import com.zonesnap.networking.get.NetworkGetPictureList;
import com.zonesnap.zonesnap_app.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter implements OnTaskComplete {
	private Context mContext;
	// Cache for storing pictures from network
	private LruCache<String, Bitmap> mMemoryCache;
	RotateAnimation anim;
	ArrayList<Integer> pictureList = new ArrayList<Integer>();
	int picIndex = 0;
	
	public ImageAdapter(Context c) {
		mContext = c;

		anim = new RotateAnimation(0.0f, 360, RotateAnimation.RELATIVE_TO_SELF,
				0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		// anim.setInterpolator(new LinearInterpolator());
		anim.setRepeatCount(Animation.INFINITE);
		anim.setDuration(1000);
		anim.setFillAfter(true);

		// Get max available VM memory, exceeding this amount will throw an
		// OutOfMemory exception. Stored in kilobytes as LruCache takes an
		// int in its constructor.
		final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

		// Use 1/8th of the available memory for this memory cache.
		final int cacheSize = maxMemory / 8;

		mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				// The cache size will be measured in kilobytes rather than
				// number of items.
				return bitmap.getByteCount() / 1024;
			}
		};
		
		NetworkGetPictureList task = new NetworkGetPictureList(mContext);
		try {
			task.execute().get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		pictureList = (ArrayList<Integer>) task.photoIDs.clone();
		System.out.println("Test: "+pictureList);
	}

	// How many pictures displaying
	public int getCount() {
		return (pictureList.size());
	}

	// Not used, but must override
	@Override
	public Object getItem(int arg0) {
		return null;
	}

	// Gets id
	@Override
	public long getItemId(int position) {
		return 0;
	}

	// Adds a bitmap to our cache
	public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
		if (getBitmapFromMemCache(key) == null) {
			mMemoryCache.put(key, bitmap);
		}
	}

	// Retrieves bitmap from cache
	public Bitmap getBitmapFromMemCache(String key) {
		return mMemoryCache.get(key);
	}

	// Listener for Network Task
	@Override
	public void onTaskComplete(Bitmap bitmap, int position) {
		addBitmapToMemoryCache(String.valueOf(position), bitmap);
	}

	// create a new ImageView for each item referenced by the Adapter
	public View getView(int position, View convertView, ViewGroup parent) {
		System.out.println(position);
		ImageView imageView;
		// if (convertView == null) { // if it's not recycled, initialize some
			System.out.println("here1");				// attributes
			imageView = new ImageView(mContext);
			imageView.setLayoutParams(new GridView.LayoutParams(250, 250));
			imageView.setScaleType(ImageView.ScaleType.FIT_XY);
			imageView.setPadding(8, 8, 8, 8);
			// Get value from cache
			Bitmap cached = getBitmapFromMemCache(String.valueOf(position));

			// Check if value exists in cache, otherwise use network to get it
			if (cached != null) {
				imageView.setImageBitmap(cached);
				// imageView.setAnimation(null);
			} else {

				// Place holder picture while loading
				// imageView.setImageResource(R.drawable.placeholder);
				
				// Network task, passes imageview, like pass by reference
				NetworkGetPicture task = new NetworkGetPicture(mContext, this,
						imageView, pictureList.get(position));
				task.execute("");
			}
//		} else {
//			System.out.println("here");
//			imageView = (ImageView) convertView;
//		}
		return imageView;
	}

}