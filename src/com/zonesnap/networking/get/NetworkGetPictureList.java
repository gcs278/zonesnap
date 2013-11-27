package com.zonesnap.networking.get;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.zonesnap.activities.OnTaskComplete;
import com.zonesnap.activities.ZoneSnap_App;
import com.zonesnap.zonesnap_app.R;

// This network activty retrieves and updates a picture
public class NetworkGetPictureList extends AsyncTask<String, Void, String> {
	Context activity;
	public ArrayList<Integer> photoIDs = new ArrayList<Integer>();

	public NetworkGetPictureList(Context context) {
		activity = context;
	}

	// Retrieve data
	@Override
	protected String doInBackground(String... params) {
		String photoListJSON = "";
		try {
			// Set up HTTP GET
			HttpClient httpclient = new DefaultHttpClient();
			URI address = new URI("http", null, ZoneSnap_App.URL, ZoneSnap_App.PORT, "/uploadpic",
					"type=list&order=date&lat=0&long=0", null);

			// Excecute
			HttpResponse response = httpclient.execute(new HttpGet(address));

			// Check status
			StatusLine statusLine = response.getStatusLine();
			if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				response.getEntity().writeTo(out);
				out.close();
				// Get the image
				photoListJSON = out.toString();

			} else {
				// Closes the connection.
				response.getEntity().getContent().close();
				throw new IOException(statusLine.getReasonPhrase());
			}
			System.out.println(photoListJSON);
			
			try {
				JSONParser j = new JSONParser();
				JSONObject json = (JSONObject) j.parse(photoListJSON);
				JSONArray array = (JSONArray) json.get("photoIDs");

				for (int i = 0; i < array.size(); i++) {
					photoIDs.add(Integer.parseInt(array.get(i).toString()));
				}
				System.out.println("LOL:" + photoIDs);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
		} catch (IOException e) {
			return "connectFail";
		} catch (URISyntaxException e) {
			return "connectFail";
		}
		return photoListJSON;
	}

	// Process data, display
	@Override
	protected void onPostExecute(String result) {
		// check if it didn't fail
		if (result != "connectFail") {

		} else {
			System.out.println("FailGetPictureList");
		}

	}

}
