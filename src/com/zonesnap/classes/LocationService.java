package com.zonesnap.classes;

import com.zonesnap.networking.NetworkGetZone;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

public class LocationService extends Service {
	public static final String BROADCAST = "com.zonesnap.zonesnap_app.LOCATION";
	public LocationManager locationManager;
	public MyLocationListener listener;

	@Override
	public void onStart(Intent intent, int startId) {
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		listener = new MyLocationListener();
		
		// Register listener
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				30000, ZoneSnap_App.GPS_MIN_DISTANCE, listener);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		
		// Remove Updates
		locationManager.removeUpdates(listener);
	}

	public class MyLocationListener implements LocationListener {
		@SuppressLint("NewApi")
		@Override
		public void onLocationChanged(final Location loc) {

			// Get the Zone
			NetworkGetZone zoneTask = new NetworkGetZone(loc, getApplicationContext());
			zoneTask.execute();
		}

		@Override
		public void onProviderDisabled(String provider) {
			// Display GPS disabled
			Toast.makeText(getApplicationContext(), "Gps Disabled",
					Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onProviderEnabled(String provider) {
			Toast.makeText(getApplicationContext(), "Gps Enabled",
					Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onStatusChanged(String arg0, int arg1, Bundle arg2) {

		}

	}
}