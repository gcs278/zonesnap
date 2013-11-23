package com.zonesnap.activities;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.zonesnap.networking.get.NetworkGetPicture;
import com.zonesnap.networking.get.NetworkGetPictureList;
import com.zonesnap.zonesnap_app.R;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.util.LruCache;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
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
		System.out.println("Test: " + pictureList);
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
			ZoneSnap_App.mMemoryCache.put(key, bitmap);
		}
	}

	// Retrieves bitmap from cache
	public Bitmap getBitmapFromMemCache(String key) {
		return ZoneSnap_App.mMemoryCache.get(key);
	}

	// Listener for Network Task
	@Override
	public void onTaskComplete(Bitmap bitmap, int position) {
		addBitmapToMemoryCache(String.valueOf(position), bitmap);
	}

	// create a new ImageView for each item referenced by the Adapter
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageView;
		// if (convertView == null) { // if it's not recycled, initialize some
		imageView = new ImageView(mContext);
		imageView.setLayoutParams(new GridView.LayoutParams(250, 250));
		imageView.setScaleType(ImageView.ScaleType.FIT_XY);
		imageView.setPadding(8, 8, 8, 8);
		// Get value from cache
		Bitmap cached = getBitmapFromMemCache(String.valueOf(pictureList
				.get(position)));

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
		// } else {
		// System.out.println("lkjd");
		// }
		imageView.setOnClickListener(new OnImageClickListener(pictureList.get(position)));
		return imageView;
	}

	class OnImageClickListener implements OnClickListener {

		int photoID;
		// constructor
		public OnImageClickListener(int photoID) {
			this.photoID = photoID;
		}

		@Override
		public void onClick(View v) {
			// on selecting grid view image
			// launch full screen activity
			Intent i = new Intent(mContext, StackViewActivity.class);
			i.putExtra("position", photoID);
			mContext.startActivity(i);
		}

	}

	// This network activty retrieves and updates a picture
	public class NetworkGetPicture extends AsyncTask<String, Void, String> {
		Context activity;
		int port;
		String URL;
		private OnTaskComplete listener;
		ImageView view;
		int photoID;

		public NetworkGetPicture(Context context, OnTaskComplete listener,
				ImageView view, int photoID) {
			activity = context;
			// Get the URL and port
			port = 8080;
			URL = "www.grantspence.com";
			this.listener = listener;
			this.view = view;
			this.photoID = photoID;
		}

		// Retrieve data
		@Override
		protected String doInBackground(String... params) {
			String imageBase64 = "";
			try {
				// Set up HTTP GET
				HttpClient httpclient = new DefaultHttpClient();
				URI address = new URI("http", null, URL, port, "/uploadpic",
						"type=get&photoID=" + photoID, null);

				// Excecute
				HttpResponse response = httpclient
						.execute(new HttpGet(address));

				// Check status
				StatusLine statusLine = response.getStatusLine();
				if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					response.getEntity().writeTo(out);
					out.close();
					// Get the image
					imageBase64 = out.toString();
				} else {
					// Closes the connection.
					response.getEntity().getContent().close();
					throw new IOException(statusLine.getReasonPhrase());
				}
			} catch (IOException e) {
				return "connectFail";
			} catch (URISyntaxException e) {
				return "connectFail";
			}
			return imageBase64;
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
		}

		// Process data, display
		@Override
		protected void onPostExecute(String result) {
			// check if it didn't fail
			if (result != "connectFail"
					&& !result.trim().equalsIgnoreCase(new String("null"))) {
				try {
					// Decode and set image to profile pic
					byte[] decodedString = Base64
							.decode(result, Base64.DEFAULT);
					Bitmap decodedByte = BitmapFactory.decodeByteArray(
							decodedString, 0, decodedString.length);
					// decodedByte = getRoundedCornerBitmap(decodedByte);
					view.setImageBitmap(decodedByte);
					listener.onTaskComplete(decodedByte, photoID);
					view.setAnimation(AnimationUtils.loadAnimation(activity,
							R.anim.zoom_enter));

					// MainActivity.profilePic.setImageBitmap(decodedByte);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				}

			} else {
				System.out.println("Fail");
			}

		}

		public Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
			Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
					bitmap.getHeight(), Config.ARGB_8888);
			Canvas canvas = new Canvas(output);

			final int color = 0xff424242;
			final Paint paint = new Paint();
			final Rect rect = new Rect(0, 0, bitmap.getWidth(),
					bitmap.getHeight());
			final RectF rectF = new RectF(rect);
			final float roundPx = 12;

			paint.setAntiAlias(true);
			canvas.drawARGB(0, 0, 0, 0);
			paint.setColor(color);
			canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

			paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
			canvas.drawBitmap(bitmap, rect, rect, paint);

			return output;
		}
	}

}