package com.zonesnap.activities;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import com.zonesnap.zonesnap_app.R;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

// This class displays the images for the gridviews
public class ImageAdapter extends BaseAdapter {
	private Context mContext;
	
	// TextView Variables
	ProgressBar progressBar;
	TextView message;
	
	// Picture list to retrieve
	ArrayList<Integer> pictureList = new ArrayList<Integer>();
	
	// Fragment gridview type
	String adapter_type;

	public ImageAdapter(Context c, String adapterType,
			ArrayList<Integer> pictureList, ProgressBar progressBar, TextView message) {
		
		mContext = c;
		
		this.adapter_type = adapterType;
		this.progressBar = progressBar;
		this.message = message;
		
		// Show loading # pictures
		if (pictureList.size() != 0) {
			progressBar.setVisibility(View.VISIBLE);
			message.setVisibility(View.GONE);
		}
		else {
			message.setVisibility(View.VISIBLE);
			if (adapterType == ZoneSnap_App.CURRENT) {
				message.setText("No pictures found here. Claim this zone!");
			} else {
				message.setText("No liked Pictures. Explore more zones!");
			}
			progressBar.setVisibility(View.GONE);
		}
		
		// Copy picture list to local
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
			ZoneSnap_App.imageCache.put(key, bitmap);
		}
	}

	// Retrieves bitmap from cache
	public Bitmap getBitmapFromMemCache(int key) {
		// Try to get the picture from current cache
		return ZoneSnap_App.imageCache.get(key);

	}

	// create a new ImageView for each item referenced by the Adapter
	public View getView(int position, View convertView, ViewGroup parent) {
		// ImageView parameters
		ImageView imageView;
		imageView = new ImageView(mContext);
		imageView.setLayoutParams(new GridView.LayoutParams(250, 250));
		imageView.setScaleType(ImageView.ScaleType.FIT_XY);
		imageView.setPadding(8, 8, 8, 8);
		
		// Get value from cache
		Bitmap cached = getBitmapFromMemCache(pictureList.get(position));

		// Check if value exists in cache, otherwise use network to get it
		if (cached != null) {
			imageView.setImageBitmap(cached);
			this.progressBar.setVisibility(View.GONE);
		} else {
			// Network task, passes imageview, like pass by reference
			NetworkGetPicture task = new NetworkGetPicture(mContext, imageView,
					pictureList.get(position));
			task.execute();
		}
		
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
			// Launch StackView Activity
			Intent i = new Intent(mContext, StackViewActivity.class);
			i.putExtra("position", photoID);
			mContext.startActivity(i);
		}

	}

	// This network activity retrieves and updates a picture
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
				// Set Timeout
				HttpParams httpParams = new BasicHttpParams();
				HttpConnectionParams.setConnectionTimeout(httpParams, 4000);
				HttpConnectionParams.setSoTimeout(httpParams, 4000);
				
				// Set up HTTP GET
				HttpClient httpclient = new DefaultHttpClient(httpParams);
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
			// Check if it didn't fail
			if (result != "connectFail"
					&& !result.trim().equalsIgnoreCase(new String("null"))) {

				JSONParser j = new JSONParser();
				JSONObject json = null;
				try {
					json = (JSONObject) j.parse(result);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				
				// Parse the data coming in
				String imageBase64 = (String) json.get("image");
				String title = (String) json.get("title");
				String likes = String.valueOf(Integer.parseInt(json.get("likes").toString()));
				
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
					ImageAdapter.this.progressBar.setVisibility(View.GONE);
				}

			} else {
				// Show a toast we failed to get image
				Toast.makeText(mContext, "Failed to retrieve image "+photoID, Toast.LENGTH_SHORT).show();
			}

		}
	}

}