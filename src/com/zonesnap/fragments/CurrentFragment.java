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
import android.content.Context;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.TextView;

import com.zonesnap.activities.ImageAdapter;
import com.zonesnap.activities.MainActivity;
import com.zonesnap.activities.ZoneSnap_App;
import com.zonesnap.networking.get.NetworkGetZone;
import com.zonesnap.zonesnap_app.R;

// Fragment for the current zone
public class CurrentFragment extends Fragment {
	public static final String ARG_SECTION_NUMBER = "section_number";
	double latitude;
	double longitude;
	Context m_classContext;
	TextView logo;
	TextView gridTitle;

	public CurrentFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_current, container,
				false);
		getActivity().getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		m_classContext = getActivity();
		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		// Set font
		Typeface zsFont = Typeface.createFromAsset(getActivity().getAssets(),
				"fonts/Orbitron-Regular.ttf");
		gridTitle = (TextView) getView().findViewById(R.id.current_title);
		gridTitle.setTypeface(zsFont);
		Typeface zsLogo = Typeface.createFromAsset(getActivity().getAssets(),
				"fonts/capella.ttf");
		logo = (TextView) getView().findViewById(R.id.current_Logo);
		logo.setTypeface(zsLogo);

		// Register the listener with the Location Manager to receive
		// location updates
		// Acquire a reference to the system Location Manager
		LocationManager locationManager = (LocationManager) getActivity()
				.getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
				3, mLocationListener);

		updateLocation(locationManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER));
	}

	public void updateLocation(final Location location) {
		logo.setText("Polling GPS...");
		System.out.println("Lat: " + location.getLatitude());
		System.out.println("Long: " + location.getLongitude());
		System.out.println("Accuracy: " + location.getAccuracy());
		latitude = location.getLatitude();
		longitude = location.getLongitude();
		// if (location.getAccuracy() > 40.0) {
		// logo.setText("Waiting for accuracy...");
		// return;
		// }

		NetworkGetZone task = new NetworkGetZone(getActivity(), logo, latitude,
				longitude);
		task.execute();
		NetworkGetCurrentPictureList listTask = new NetworkGetCurrentPictureList(
				getActivity());
		listTask.execute();
	}

	public final LocationListener mLocationListener = new LocationListener() {

		@Override
		public void onLocationChanged(final Location location) {
			updateLocation(location);
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
	public class NetworkGetCurrentPictureList extends
			AsyncTask<String, Void, String> {
		final Context activity;
		boolean fail = true;

		public ArrayList<Integer> photoIDs = new ArrayList<Integer>();

		public NetworkGetCurrentPictureList(Context context) {
			activity = context;
		}

		// Retrieve data
		@Override
		protected String doInBackground(String... params) {
			String photoListJSON = "";
			try {
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
			// check if it didn't fail
			if (!fail) {
				try {
					GridView grid = (GridView) getView().findViewById(
							R.id.gridCurrent);
					grid.setAdapter(new ImageAdapter(getActivity(),
							ZoneSnap_App.CURRENT, photoIDs,gridTitle));
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
			} else {
				try {
					new AlertDialog.Builder(getActivity()).setMessage("Error: "
							+ result).show();
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
			}

		}

	}

}
