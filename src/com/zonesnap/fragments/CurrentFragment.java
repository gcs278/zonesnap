package com.zonesnap.fragments;

import android.content.Context;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import com.zonesnap.activities.CurrentImageAdapter;
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

		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		final GridView grid = (GridView) getView().findViewById(
				R.id.gridCurrent);
		grid.setAdapter(new CurrentImageAdapter(getActivity()));

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
}
