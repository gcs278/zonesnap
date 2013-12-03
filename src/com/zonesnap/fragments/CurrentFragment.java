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
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.zonesnap.adapters.ImageAdapter;
import com.zonesnap.classes.LocationService;
import com.zonesnap.classes.ZoneSnap_App;
import com.zonesnap.networking.NetworkGetZone;
import com.zonesnap.zonesnap_app.R;

// Fragment for the current zone
public class CurrentFragment extends Fragment {
	public static final String ARG_SECTION_NUMBER = "section_number";

	// Layout Variables
	TextView logo, gridTitle, message;
	Button refresh;
	ProgressBar progressBar;

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


		// Set the refresh button to refresh
		refresh = (Button) getView().findViewById(R.id.refresh);
		refresh.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				// Call NetworkGetZone which broadcasts a intent
				logo.setText("Locating Zone...");
				NetworkGetZone task = new NetworkGetZone(getBestLocation(),getActivity());
				task.execute();
			}
		});

		// Get the progress bar
		progressBar = (ProgressBar) getActivity().findViewById(
				R.id.current_progress);
	}

	// Pause the zone searching
	@Override
	public void onPause() {
		// Disable broadcast receiver
		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(
				mMessageReceiver);
		super.onPause();
	}

	// Resume the zone listening
	@Override
	public void onResume() {
		
		// Register broadcast receiver for location
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
				mMessageReceiver, new IntentFilter(LocationService.BROADCAST));
		super.onResume();
	}

	// handler for received Intents for the "my-event" event
	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// Extract data included in the Intent
			Bundle b = intent.getExtras();
			Location loc = (Location) b.get("Location");
			int zone = b.getInt("Zone");

			// If it is a new zone
			if (ZoneSnap_App.zone != zone  && ZoneSnap_App.zone != 0) {
				try {
					Toast.makeText(getActivity(), "New Zone!",
							Toast.LENGTH_SHORT).show();
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
			}
			
			// Set current Zone
			ZoneSnap_App.zone = zone;

			if (ZoneSnap_App.zone == -2) {
				logo.setText("Failed");
			} else {
				// Set title
				logo.setText("Zone " + ZoneSnap_App.zone);
			}
			
			// Get the pictures associated with this zone
			NetworkGetCurrentPictureList listTask = new NetworkGetCurrentPictureList(loc);
			listTask.execute();
		}
	};

	/**
	 * try to get the 'best' location selected from all providers
	 */
	private Location getBestLocation() {
		Location gpslocation = getLocationByProvider(LocationManager.GPS_PROVIDER);
		Location networkLocation = getLocationByProvider(LocationManager.NETWORK_PROVIDER);
		// if we have only one location available, the choice is easy
		if (gpslocation == null) {
			return networkLocation;
		}
		if (networkLocation == null) {
			return gpslocation;
		}
		// a locationupdate is considered 'old' if its older than the configured
		// update interval. this means, we didn't get a
		// update from this provider since the last check
		long old = System.currentTimeMillis() - 5000;
		boolean gpsIsOld = (gpslocation.getTime() < old);
		boolean networkIsOld = (networkLocation.getTime() < old);
		// gps is current and available, gps is better than network
		if (!gpsIsOld) {
			return gpslocation;
		}
		// gps is old, we can't trust it. use network location
		if (!networkIsOld) {
			return networkLocation;
		}
		// both are old return the newer of those two
		if (gpslocation.getTime() > networkLocation.getTime()) {
			return gpslocation;
		} else {
			return networkLocation;
		}
	}

	/**
	 * get the last known location from a specific provider (network/gps)
	 */
	private Location getLocationByProvider(String provider) {
		Location location = null;
		LocationManager locationManager = (LocationManager) getActivity()
				.getApplicationContext().getSystemService(
						Context.LOCATION_SERVICE);
		try {
			if (locationManager.isProviderEnabled(provider)) {
				location = locationManager.getLastKnownLocation(provider);
			}
		} catch (IllegalArgumentException e) {
		
		}
		return location;
	}

	// This network activity retrieves the current Zone picture list
	public class NetworkGetCurrentPictureList extends
			AsyncTask<String, Void, String> {
		boolean fail = true; // Success variable
		Double latitude, longitude;
		// List of photo IDs to retrieve
		public ArrayList<Integer> photoIDs = new ArrayList<Integer>();
		
		public NetworkGetCurrentPictureList(Location loc) {
			latitude = loc.getLatitude();
			longitude = loc.getLongitude();
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
					// Parse the data
					JSONParser j = new JSONParser();
					JSONObject json = (JSONObject) j.parse(photoListJSON);
					JSONArray array = (JSONArray) json.get("photoIDs");

					// Add JSON list to local list
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
					new AlertDialog.Builder(getActivity()).setMessage(
							ZoneSnap_App.getErrorMessage() + result).show();
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
			}

		}

	}

}
