package com.zonesnap.networking;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.zonesnap.activities.MapMenuActivity;
import com.zonesnap.classes.LocationService;
import com.zonesnap.classes.ZoneSnap_App;
import com.zonesnap.zonesnap_app.R;

// This task gets the current zone that user is located
public class NetworkGetZone extends AsyncTask<String, Void, Integer> {
	// Location variables
	Double latitude, longitude;
	Location location;
	
	Context context;
	
	// failed variable
	boolean failed = false;
	

	public NetworkGetZone(Location location, Context context) {
		this.context = context;
		this.location = location;
		
		// Make sure GPS is working
		try {
			this.latitude = location.getLatitude();
			this.longitude = location.getLongitude();
		} catch (NullPointerException e) {
			EnableGPS();
			failed = true;
		}
	}

	// Retrieve data
	@Override
	protected Integer doInBackground(String... params) {
		if (failed == true)
			return -1;
		int returnData = -1;
		try {
			// Set up HTTP GET
			HttpClient httpclient = new DefaultHttpClient();
			HttpParams httpParams = httpclient.getParams();
			ConnManagerParams.setTimeout(httpParams, 4000);
			HttpConnectionParams.setSoTimeout(httpParams, 4000);
			HttpConnectionParams.setConnectionTimeout(httpParams, 4000);
			URI address = new URI("http", null, ZoneSnap_App.URL,
					ZoneSnap_App.PORT, "/zonelookup", "lat=" + latitude
							+ "&long=" + longitude, null);

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
			failed = true;
			return -2;
		} catch (URISyntaxException e) {
			failed = true;
			return -2;
		}
		return returnData;
	}

	@SuppressLint("NewApi")
	@Override
	protected void onPostExecute(Integer result) {
		// check if it didn't fail
		if (failed) {
			Toast.makeText(context, "Failed to get zone", Toast.LENGTH_SHORT)
					.show();
		} else {
			// Notification for new zone
			if (result != ZoneSnap_App.zone && result != 0) {
				// send notification to system that user entered new zone
				final NotificationManager notiMgr = (NotificationManager) context
						.getSystemService(Context.NOTIFICATION_SERVICE);
				Intent notIntent = new Intent(context, MapMenuActivity.class);
				PendingIntent pIntent = PendingIntent.getActivity(context, 0,
						notIntent, 0);
				Notification n = new Notification.Builder(context)
						.setContentTitle("Entered new zone.")
						.setSmallIcon(R.drawable.zonesnap1_launcher)
						.setContentText("Touch to view content of new zone.")
						.setContentIntent(pIntent).setAutoCancel(true).build();
				notiMgr.notify(0, n);
			}
			Intent intent = new Intent(LocationService.BROADCAST);
			intent.putExtra("Location", location);
			intent.putExtra("Zone", result);

			LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
		}

	}

	// Function for enabling GPS
	private void EnableGPS() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(
				context);
		builder.setMessage(
				"Your GPS seems to be disabled, do you want to enable it?")
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(
									@SuppressWarnings("unused") final DialogInterface dialog,
									@SuppressWarnings("unused") final int id) {
								context.startActivity(new Intent(
										android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(final DialogInterface dialog,
							@SuppressWarnings("unused") final int id) {
						dialog.cancel();
					}
				});
		final AlertDialog alert = builder.create();
		alert.show();
	}

}