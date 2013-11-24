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
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import android.content.Context;
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
import android.widget.GridView;
import android.widget.TextView;

import com.zonesnap.activities.ImageAdapter;
import com.zonesnap.activities.ZoneSnap_App;
import com.zonesnap.networking.get.NetworkGetZone;
import com.zonesnap.zonesnap_app.R;


// Fragment for the current zone
public class CurrentFragment extends Fragment {
	public static final String ARG_SECTION_NUMBER = "section_number";

	TextView logo;

	public CurrentFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_current,
				container, false);
		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		NetworkGetCurrentPictureList listTask = new NetworkGetCurrentPictureList(getActivity());
		listTask.execute();
		
		// Set font
		Typeface zsFont = Typeface.createFromAsset(getActivity()
				.getAssets(), "fonts/Orbitron-Regular.ttf");
		TextView title = (TextView) getView().findViewById(
				R.id.current_title);
		title.setTypeface(zsFont);
		Typeface zsLogo = Typeface.createFromAsset(getActivity()
				.getAssets(), "fonts/capella.ttf");
		logo = (TextView) getView().findViewById(R.id.current_Logo);
		logo.setTypeface(zsLogo);
		// Acquire a reference to the system Location Manager
		LocationManager locationManager = (LocationManager) getActivity()
				.getSystemService(Context.LOCATION_SERVICE);
		// Register the listener with the Location Manager to receive
		// location updates
		locationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);
	}

	public final LocationListener mLocationListener = new LocationListener() {

		@Override
		public void onLocationChanged(final Location location) {
			System.out.println("Lat: " + location.getLatitude());
			System.out.println("Long: " + location.getLongitude());
			System.out.println("Accuracy: " + location.getAccuracy());

			if (location.getAccuracy() > 40.0) {
			}

			NetworkGetZone task = new NetworkGetZone(getActivity(), logo,
					location.getLatitude(), location.getLongitude());
			task.execute();
		}

		@Override
		public void onProviderDisabled(String arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderEnabled(String arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
			// TODO Auto-generated method stub

		}
	};
	
	// This network activty retrieves and updates a picture
	public class NetworkGetCurrentPictureList extends AsyncTask<String, Void, String> {
		Context activity;
		int port;
		String URL;
		public ArrayList<Integer> photoIDs = new ArrayList<Integer>();

		public NetworkGetCurrentPictureList(Context context) {
			activity = context;
			// Get the URL and port
			port = 8080;
			URL = "www.grantspence.com";
		}

		// Retrieve data
		@Override
		protected String doInBackground(String... params) {
			String photoListJSON = "";
			try {
				// Set up HTTP GET
				HttpClient httpclient = new DefaultHttpClient();
				URI address = new URI("http", null, URL, port, "/uploadpic",
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
				final GridView grid = (GridView) getView().findViewById(
						R.id.gridCurrent);
				grid.setAdapter(new ImageAdapter(getActivity(),ZoneSnap_App.CURRENT,photoIDs));
			} else {
				System.out.println("FailGetPictureList");
			}

		}

	}

}
