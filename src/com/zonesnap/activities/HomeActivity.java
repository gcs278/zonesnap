package com.zonesnap.activities;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.zonesnap.zonesnap_app.R;
import com.zonesnap.zonesnap_app.R.id;
import com.zonesnap.zonesnap_app.R.layout;
import com.zonesnap.zonesnap_app.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class HomeActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		
		MapView mapView = (MapView) findViewById(R.id.mapview);
		mapView.onCreate(savedInstanceState);
		GoogleMap map = mapView.getMap();
		map.getUiSettings().setMyLocationButtonEnabled(false);
		map.setMyLocationEnabled(true);
		
		try {
			MapsInitializer.initialize(this.getBaseContext());
		} catch (GooglePlayServicesNotAvailableException e) {
			e.printStackTrace();
		}
		
		// navigate to upload fragment
		ImageButton toUpload = (ImageButton) findViewById(R.id.toUploadBtn);
		toUpload.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(getBaseContext(), MainActivity.class);
				i.putExtra("position", 0);
				startActivity(i);
				finish();				
			}
		});
		
		// nvaigate to current fragment
		ImageButton toCurrent = (ImageButton) findViewById(R.id.toCurrentBtn);
		toCurrent.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(getBaseContext(), MainActivity.class);
				i.putExtra("position", 1);
				startActivity(i);
				finish();				
			}
		});
		
		
		ImageButton toPast = (ImageButton) findViewById(R.id.toPastBtn);
		toPast.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(getBaseContext(), MainActivity.class);
				i.putExtra("position", 2);
				startActivity(i);
				finish();				
			}
		});
		
		
		ImageButton toProfile = (ImageButton) findViewById(R.id.toProfileBtn);
		toProfile.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(getBaseContext(), MainActivity.class);
				i.putExtra("position", 3);
				startActivity(i);
				finish();				
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
