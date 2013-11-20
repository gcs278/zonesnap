package com.zonesnap.networking.get;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.util.Base64;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.zonesnap.activities.OnTaskComplete;
import com.zonesnap.zonesnap_app.R;

// This network activty retrieves and updates a picture
public class NetworkGetPicture extends AsyncTask<String, Void, String> {
	Context activity;
	int port;
	String URL;
	private OnTaskComplete listener;
	ImageView view;
	int position;
	public NetworkGetPicture(Context context,OnTaskComplete listener,ImageView view,int position) {
		activity = context;
		// Get the URL and port
		port = 8080;
		URL = "www.grantspence.com";
		this.listener = listener;
		this.view = view;
		this.position = position;
	}

	// Retrieve data
	@Override
	protected String doInBackground(String... params) {
		String imageBase64 = "";
		try {
			// Set up HTTP GET
			HttpClient httpclient = new DefaultHttpClient();
			URI address = new URI("http", null, URL, port, "/uploadpic",
				"type=get&photoID=0", null);
			
			// Excecute
			HttpResponse response = httpclient.execute(new HttpGet(address));
			
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

	// Process data, display
	@Override
	protected void onPostExecute(String result) {
		// check if it didn't fail
		if (result != "connectFail") {

			try {
				// Decode and set image to profile pic
				byte[] decodedString = Base64.decode(result, Base64.DEFAULT);
				Bitmap decodedByte = BitmapFactory.decodeByteArray(
						decodedString, 0, decodedString.length);
				decodedByte = getRoundedCornerBitmap(decodedByte);
				view.setImageBitmap(decodedByte);
				listener.onTaskComplete(decodedByte, position);
				view.setAnimation(AnimationUtils.loadAnimation(activity, R.anim.zoom_enter));
				// MainActivity.profilePic.setImageBitmap(decodedByte);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
			
			
		} else {
			System.out.println("Fail");
		}

	}
	
	  public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
		    Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
		        bitmap.getHeight(), Config.ARGB_8888);
		    Canvas canvas = new Canvas(output);
		 
		    final int color = 0xff424242;
		    final Paint paint = new Paint();
		    final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
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
