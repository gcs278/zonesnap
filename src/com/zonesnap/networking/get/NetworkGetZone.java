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
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnManagerParamBean;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.zonesnap.activities.OnTaskComplete;
import com.zonesnap.zonesnap_app.R;

// This network activty retrieves and updates a picture
public class NetworkGetZone extends AsyncTask<String, Void, Integer> {
	Context activity;
	int port;
	String URL;
	ImageView view;
	int photoID;
	Double latitude, longitude;
	TextView textView;

	public NetworkGetZone(Context context, TextView title, Double latitude,
			Double longitude) {
		activity = context;
		// Get the URL and port
		port = 8080;
		URL = "www.grantspence.com";
		this.latitude = latitude;
		this.longitude = longitude;
		textView = title;
	}

	// Retrieve data
	@Override
	protected Integer doInBackground(String... params) {
		int returnData = -1;
		try {
			// Set up HTTP GET
			HttpClient httpclient = new DefaultHttpClient();
			HttpParams httpParams = httpclient.getParams();
			ConnManagerParams.setTimeout(httpParams,4000);
			HttpConnectionParams.setSoTimeout(httpParams, 4000);
			HttpConnectionParams.setConnectionTimeout(httpParams,4000);
			URI address = new URI("http", null, URL, port, "/zonelookup",
					"lat=" + latitude + "&long=" + longitude, null);

			// Excecute
			HttpResponse response = httpclient.execute(new HttpGet(address));

			// Check status
			StatusLine statusLine = response.getStatusLine();
			if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				response.getEntity().writeTo(out);
				out.close();
				// Get the image
				String jsonData = out.toString();
				JSONParser j = new JSONParser();
				JSONObject json;
				try {
					json = (JSONObject) j.parse(jsonData);
				} catch (ParseException e) {
					e.printStackTrace();
					return -2;
				}
				returnData = Integer.parseInt(json.get("zone").toString());

			} else {
				// Closes the connection.
				response.getEntity().getContent().close();
				throw new IOException(statusLine.getReasonPhrase());
			}
		} catch (IOException e) {
			return -2;
		} catch (URISyntaxException e) {
			return -2;
		}
		return returnData;
	}

	@Override
	protected void onProgressUpdate(Void... values) {
		// TODO Auto-generated method stub
		super.onProgressUpdate(values);
	}

	// Process data, display
	@Override
	protected void onPostExecute(Integer result) {
		// check if it didn't fail
		if (result == -2) {

		} else if (result == -1) {
			textView.setText("New Zone");
		} else {
			textView.setText("Zone " + result);
		}

	}
}
