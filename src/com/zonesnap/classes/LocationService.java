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
import android.util.Log;
import android.widget.Toast;

public class LocationService extends Service {
	public static final String BROADCAST = "com.zonesnap.zonesnap_app.LOCATION";
	public LocationManager locationManager;
	public MyLocationListener listener;
	public Location previousBestLocation = null;

	Intent intent;
	int currentZone = 0;

	@Override
	public void onCreate() {
		super.onCreate();
		intent = new Intent(BROADCAST);
	}

	@Override
	public void onStart(Intent intent, int startId) {
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		listener = new MyLocationListener();
		
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				30000, ZoneSnap_App.GPS_MIN_DISTANCE, listener);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		// handler.removeCallbacks(sendUpdatesToUI);
		super.onDestroy();
		Log.v("STOP_SERVICE", "DONE");
		locationManager.removeUpdates(listener);
	}

	public static Thread performOnBackgroundThread(final Runnable runnable) {
		final Thread t = new Thread() {
			@Override
			public void run() {
				try {
					runnable.run();
				} finally {

				}
			}
		};
		t.start();
		return t;
	}

	public class MyLocationListener implements LocationListener {
		@SuppressLint("NewApi")
		@Override
		public void onLocationChanged(final Location loc) {
			Log.i("**************************************", "Location changed");

			// Get the Zone
			NetworkGetZone zoneTask = new NetworkGetZone(loc, LocationService.this, currentZone);
			zoneTask.execute();
		}

		@Override
		public void onProviderDisabled(String provider) {
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