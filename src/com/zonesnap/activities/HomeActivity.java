package com.zonesnap.activities;

import java.util.List;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.zonesnap.zonesnap_app.R;
import com.zonesnap.zonesnap_app.R.id;
import com.zonesnap.zonesnap_app.R.layout;
import com.zonesnap.zonesnap_app.R.menu;

import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class HomeActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
		GoogleMap map = fm.getMap();
		map.setMyLocationEnabled(true);
		LocationManager locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);
	    Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
	    LatLng myCoor = null;
	    if (location != null) {
	    	System.out.println("Here");
	    	myCoor = new LatLng(location.getLatitude(), location.getLongitude());
	    }
	    map.animateCamera(CameraUpdateFactory.newLatLngZoom(myCoor,17));
//	    map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
//	    map.addMarker(new MarkerOptions().position(myCoor).title("HEllo"));
//		mapView.onCreate(savedInstanceState);
//		GoogleMap map = mapView.getMap();
//		try {
//			map.getUiSettings().setMyLocationButtonEnabled(false);
//			map.setMyLocationEnabled(true);
//		} catch (NullPointerException e) {
//			e.printStackTrace();
//		}
//
//		try {
//			MapsInitializer.initialize(this.getBaseContext());
//		} catch (GooglePlayServicesNotAvailableException e) {
//			e.printStackTrace();
//		}

		// navigate to upload fragment
		ImageButton toUpload = (ImageButton) findViewById(R.id.toUploadBtn);
		toUpload.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(getBaseContext(), MainActivity.class);
				i.putExtra("position", 0);
				startActivity(i);
			}
		});

		// nvaigate to current fragment
		ImageButton toCurrent = (ImageButton) findViewById(R.id.toCurrentBtn);
		toCurrent.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(getBaseContext(), MainActivity.class);
				i.putExtra("position", 1);
				startActivity(i);
			}
		});

		ImageButton toPast = (ImageButton) findViewById(R.id.toPastBtn);
		toPast.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(getBaseContext(), MainActivity.class);
				i.putExtra("position", 2);
				startActivity(i);
			}
		});

		ImageButton toProfile = (ImageButton) findViewById(R.id.toProfileBtn);
		toProfile.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(getBaseContext(), MainActivity.class);
				i.putExtra("position", 3);
				startActivity(i);

			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home, menu);
		return true;
	}

}
