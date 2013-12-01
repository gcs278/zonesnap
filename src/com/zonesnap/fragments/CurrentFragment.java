package com.zonesnap.fragments;

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
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.zonesnap.activities.HomeActivity;
import com.zonesnap.activities.ImageAdapter;
import com.zonesnap.activities.ZoneSnap_App;
import com.zonesnap.zonesnap_app.R;

// Fragment for the current zone
public class CurrentFragment extends Fragment {
	public static final String ARG_SECTION_NUMBER = "section_number";

	// Location variables
	double latitude, longitude;
	LocationManager locationManager;

	// Layout Variables
	TextView logo, gridTitle, message;
	Button refresh;
	ProgressBar progressBar;

	public int currentZone = 0;

	public CurrentFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_current, container,
				false);
		getActivity().getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		// Set Font of the Grid Title
		Typeface zsFont = Typeface.createFromAsset(getActivity().getAssets(),
				"fonts/Orbitron-Regular.ttf");
		gridTitle = (TextView) getView().findViewById(R.id.current_title);
		gridTitle.setTypeface(zsFont);

		// Set font of Zonesnap logo
		Typeface zsLogo = Typeface.createFromAsset(getActivity().getAssets(),
				"fonts/capella.ttf");
		logo = (TextView) getView().findViewById(R.id.current_Logo);
		logo.setTypeface(zsLogo);
		message = (TextView) getView().findViewById(R.id.current_message);
		message.setTypeface(zsFont);

		// Register the listener with the Location Manager to receive
		// location updates
		// Acquire a reference to the system Location Manager
		locationManager = (LocationManager) getActivity().getSystemService(
				Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
				3, mLocationListener);

		// Call First update
		updateLocation(locationManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER));

		// Set the refresh button to refresh
		refresh = (Button) getView().findViewById(R.id.refresh);
		refresh.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				LocationManager locationManager = (LocationManager) getActivity()
						.getSystemService(Context.LOCATION_SERVICE);
				// Update Location
				updateLocation(locationManager
						.getLastKnownLocation(LocationManager.GPS_PROVIDER));
			}
		});

		// Get the progress bar
		progressBar = (ProgressBar) getActivity().findViewById(
				R.id.current_progress);
	}

	// Update location and GUI
	public void updateLocation(final Location location) {
		logo.setText("Finding Zone...");

		// Check if GPS is disabled
		if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			new AlertDialog.Builder(getActivity())
					.setMessage("Error: GPS is disabled");
			return;
		}

		// Get location
		latitude = location.getLatitude();
		longitude = location.getLongitude();

		// if (location.getAccuracy() > 40.0) {
		// logo.setText("Waiting for accuracy...");
		// return;
		// }

		// Get the current zone information
		NetworkGetZone zoneTask = new NetworkGetZone(getActivity(), logo,
				latitude, longitude, currentZone);
		zoneTask.execute();

		// Get the current picture list
		NetworkGetCurrentPictureList listTask = new NetworkGetCurrentPictureList(
				getActivity());
		listTask.execute();
		
		
	}

	// Pause the zone searching
	@Override
	public void onPause() {
		locationManager.removeUpdates(mLocationListener);
		super.onPause();
	}
	
	// Resume the zone listening
	@Override
	public void onResume() {
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
				3, mLocationListener);
		super.onResume();
	}
	
	// Listen for location changes
	public final LocationListener mLocationListener = new LocationListener() {
		@Override
		public void onLocationChanged(final Location location) {
			// Update location
			updateLocation(location);
		}

		@Override
		public void onProviderDisabled(String arg0) {
			// Let client know that GPS was disabled
			new AlertDialog.Builder(getActivity())
					.setMessage(
							"It seems your GPS has been disabled. Please enable it for ZoneSnap to work.")
					.show();
		}

		@Override
		public void onProviderEnabled(String arg0) {
		}

		@Override
		public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		}
	};

	// This network activity retrieves the current Zone picture list
	public class NetworkGetCurrentPictureList extends
			AsyncTask<String, Void, String> {
		Context activity;
		boolean fail = true; // Success variable

		// List of photo IDs to retrieve
		public ArrayList<Integer> photoIDs = new ArrayList<Integer>();

		public NetworkGetCurrentPictureList(Context context) {
			activity = context;
		}

		// Retrieve data
		@Override
		protected String doInBackground(String... params) {
			String photoListJSON = "";
			try {
				// Set Timeout
				HttpParams httpParams = new BasicHttpParams();
				HttpConnectionParams.setConnectionTimeout(httpParams, 4000);
				HttpConnectionParams.setSoTimeout(httpParams, 4000);

				// Set up HTTP GET
				HttpClient httpclient = new DefaultHttpClient(httpParams);
				URI address = new URI("http", null, ZoneSnap_App.URL,
						ZoneSnap_App.PORT, "/uploadpic",
						"type=list&order=date&lat=" + latitude + "&long="
								+ longitude, null);
				System.out.println(address.toString());
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
					photoListJSON = out.toString();

				} else {
					// Closes the connection.
					response.getEntity().getContent().close();
					throw new IOException(statusLine.getReasonPhrase());
				}

				try {
					JSONParser j = new JSONParser();
					JSONObject json = (JSONObject) j.parse(photoListJSON);
					JSONArray array = (JSONArray) json.get("photoIDs");

					for (int i = 0; i < array.size(); i++) {
						photoIDs.add(Integer.parseInt(array.get(i).toString()));
					}

				} catch (ParseException e) {
					e.printStackTrace();
				}

			} catch (IOException e) {
				fail = true;
				return e.getMessage();
			} catch (URISyntaxException e) {
				fail = true;
				return e.getMessage();
			}
			fail = false;
			return photoListJSON;
		}

		// Process data, display
		@Override
		protected void onPostExecute(String result) {
			// Check if it didn't fail
			if (!fail) {
				// Call image adapter to retrieve the images
				try {
					GridView grid = (GridView) getView().findViewById(
							R.id.gridCurrent);
					grid.setAdapter(new ImageAdapter(getActivity(),
							ZoneSnap_App.CURRENT, photoIDs, progressBar,
							message));
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
			} else {
				// Display the error if we can't connect
				try {
					new AlertDialog.Builder(activity).setMessage(
							ZoneSnap_App.getErrorMessage() + result).show();
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
			}

		}

	}

	// This task gets the current zone that user is located
	public class NetworkGetZone extends AsyncTask<String, Void, Integer> {
		Context activity;
		ImageView view;
		int photoID, previousZone;
		Double latitude, longitude;
		TextView textView;
		boolean failed = false;

		public NetworkGetZone(Context context, TextView title, Double latitude,
				Double longitude, int previousZone) {
			activity = context;
			this.latitude = latitude;
			this.longitude = longitude;
			textView = title;
			this.previousZone = previousZone;
		}

		// Retrieve data
		@Override
		protected Integer doInBackground(String... params) {
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
				HttpResponse response = httpclient
						.execute(new HttpGet(address));

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

		@Override
		protected void onProgressUpdate(Void... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
		}

		// Process data, display
		@SuppressLint("NewApi")
		@Override
		protected void onPostExecute(Integer result) {
			// check if it didn't fail
			if (failed) {
				try {
					// Show a toast we failed to get zone
					Toast.makeText(activity, "Failed to find zone. Please check connection.",
							Toast.LENGTH_LONG).show();
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
			} else {
				// Notify user of new zone

				if (result != currentZone) {
					try {
						Toast.makeText(getActivity(), "New Zone!",
								Toast.LENGTH_SHORT).show();
					} catch (NullPointerException e) {
						e.printStackTrace();
					}
				}
					// send notification to system that user entered new zone
					final NotificationManager notiMgr = (NotificationManager) getActivity()
							.getSystemService(getActivity().NOTIFICATION_SERVICE);
					Intent notIntent = new Intent(getActivity(), HomeActivity.class);
					PendingIntent pIntent = PendingIntent.getActivity(
							getActivity(), 0, notIntent, 0);
					Notification n = new Notification.Builder(getActivity())
							.setContentTitle("Entered new zone.")
							.setSmallIcon(R.drawable.zonesnap1_launcher)
							.setContentText("Touch to view content of new zone.")
							.setContentIntent(pIntent).setAutoCancel(true).build();
					notiMgr.notify(0, n);
				}
				currentZone = result;

				// Set title
				textView.setText("Zone " + result);

			}

		}
	}

}
