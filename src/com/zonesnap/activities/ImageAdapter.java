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
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
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
import android.widget.TextView;

public class ImageAdapter extends BaseAdapter {
	private Context mContext;
	TextView title;
	String previousTitle;
	ArrayList<Integer> pictureList = new ArrayList<Integer>();
	int picIndex = 0;
	String type;

	public ImageAdapter(Context c, String adapterType,
			ArrayList<Integer> pictureList, TextView title) {
		previousTitle = (String) title.getText();
		mContext = c;
		this.type = adapterType;
		this.title = title;
		if (pictureList.size() != 0)
			title.setText("Loading " + pictureList.size() + " pictures...");
		this.pictureList = (ArrayList<Integer>) pictureList.clone();
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
	public void addBitmapToMemoryCache(int key, Bitmap bitmap) {
		if (getBitmapFromMemCache(key) == null) {
			if (type == ZoneSnap_App.CURRENT) {
				ZoneSnap_App.currentImageCache.put(key, bitmap);
			} else if (type == ZoneSnap_App.LIKED) {
				ZoneSnap_App.likedImageCache.put(key, bitmap);
			}
		}
	}

	// Retrieves bitmap from cache
	public Bitmap getBitmapFromMemCache(int key) {
		Bitmap returnPic = ZoneSnap_App.currentImageCache.get(key);

		if (returnPic != null) {
			return returnPic;
		} else {
			return ZoneSnap_App.likedImageCache.get(key);
		}
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
		Bitmap cached = getBitmapFromMemCache(pictureList.get(position));

		// Check if value exists in cache, otherwise use network to get it
		if (cached != null) {
			imageView.setImageBitmap(cached);
			ImageAdapter.this.title.setText(previousTitle);
			// imageView.setAnimation(null);
		} else {

			// Place holder picture while loading
			// imageView.setImageResource(R.drawable.placeholder);
			// Network task, passes imageview, like pass by reference
			NetworkGetPicture task = new NetworkGetPicture(mContext, imageView,
					pictureList.get(position));
			task.execute();
		}
		// } else {
		// System.out.println("lkjd");
		// }
		imageView.setOnClickListener(new OnImageClickListener(pictureList
				.get(position)));
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
		ImageView view;
		int photoID;

		public NetworkGetPicture(Context context, ImageView view, int photoID) {
			activity = context;
			this.view = view;
			this.photoID = photoID;
		}

		// Retrieve data
		@Override
		protected String doInBackground(String... params) {
			String JSON = "";
			try {
				// Set up HTTP GET
				HttpClient httpclient = new DefaultHttpClient();
				URI address = new URI("http", null, ZoneSnap_App.URL,
						ZoneSnap_App.PORT, "/uploadpic", "type=get&photoID="
								+ photoID, null);

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
					JSON = out.toString();
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
			return JSON;
		}

		// Process data, display
		@Override
		protected void onPostExecute(String result) {
			// check if it didn't fail
			if (result != "connectFail"
					&& !result.trim().equalsIgnoreCase(new String("null"))) {

				JSONParser j = new JSONParser();
				JSONObject json = null;
				try {
					json = (JSONObject) j.parse(result);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				String imageBase64 = (String) json.get("image");
				String title = (String) json.get("title");

				try {
					// Decode and set image to profile pic
					byte[] decodedString = Base64.decode(imageBase64,
							Base64.DEFAULT);
					Bitmap decodedByte = BitmapFactory.decodeByteArray(
							decodedString, 0, decodedString.length);

					view.setImageBitmap(decodedByte);
					addBitmapToMemoryCache(photoID, decodedByte);
					view.setAnimation(AnimationUtils.loadAnimation(activity,
							R.anim.zoom_enter));

				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				}

				// If we are at the last picture, change back to previous title
				if (pictureList.get(pictureList.size() - 1) == photoID) {
					ImageAdapter.this.title.setText(previousTitle);
				}

			} else {
				System.out.println("Fail");
			}

		}
	}

}