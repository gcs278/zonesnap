package com.zonesnap.activities;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.internal.p;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.zonesnap.networking.get.Coordinates;
import com.zonesnap.zonesnap_app.R;
import com.zonesnap.zonesnap_app.R.id;
import com.zonesnap.zonesnap_app.R.layout;
import com.zonesnap.zonesnap_app.R.menu;

import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.service.textservice.SpellCheckerService.Session;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends FragmentActivity {
	GoogleMap map;
	List<Coordinates> markers = new ArrayList<Coordinates>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map);
		map = fm.getMap();
		map.setMyLocationEnabled(true);
		LocationManager locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);
		Location location = locationManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		LatLng myCoor = null;
		if (location != null) {
			System.out.println("Here");
			myCoor = new LatLng(location.getLatitude(), location.getLongitude());
		} else {
			new AlertDialog.Builder(this).setMessage(
					"Location error. Is GPS enabled?").show();
		}

		// Move the camera to current location
		// This has been known to throw exception
		try {
			map.animateCamera(CameraUpdateFactory.newLatLngZoom(myCoor, 13));
		} catch (NullPointerException e) {
			new AlertDialog.Builder(this).setMessage(
					"Whoops. Something when wrong.").show();
		}

		NetworkGetPictureLocations task = new NetworkGetPictureLocations(this);
		task.execute();
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

		// Set the fonts
		Typeface zsLogo = Typeface.createFromAsset(this.getAssets(),
				"fonts/capella.ttf");
		TextView title = (TextView) findViewById(R.id.home_title);
		title.setTypeface(zsLogo);
		Typeface zsFont = Typeface.createFromAsset(this.getAssets(),
				"fonts/Orbitron-Regular.ttf");
		TextView slogan = (TextView) findViewById(R.id.home_slogan);
		slogan.setTypeface(zsFont);

		try {
			// Show a toast to welcome user
			Toast.makeText(
					this,
					"Welcome to ZoneSnap " + ZoneSnap_App.user.getFirstName()
							+ "!", Toast.LENGTH_LONG).show();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent settings = new Intent(this, SettingsActivity.class);
		startActivity(settings);
		return super.onOptionsItemSelected(item);
	}
	
	// Override OnBackPressed to make sure User wants to log out of ZoneSnap
	@Override
	public void onBackPressed() {
		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					finish();
					HomeActivity.super.onBackPressed();
					break;

				case DialogInterface.BUTTON_NEGATIVE:
					// No button clicked
					break;
				}
			}
		};

		// Show dialog of logout
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Logout of ZoneSnap?")
				.setPositiveButton("Yes", dialogClickListener)
				.setNegativeButton("No", dialogClickListener).show();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// Retrieve the picture marks again
		NetworkGetPictureLocations task = new NetworkGetPictureLocations(this);
		task.execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home, menu);
		return true;
	}

	// This network activty retrieves and updates a picture
	public class NetworkGetPictureLocations extends
			AsyncTask<String, Void, String> {
		Context activity;
		public ArrayList<Coordinates> photos = new ArrayList<Coordinates>();

		public NetworkGetPictureLocations(Context context) {
			activity = context;
		}

		// Retrieve data
		@Override
		protected String doInBackground(String... params) {
			String photoListJSON = "";
			try {
				// Set up HTTP GET
				HttpClient httpclient = new DefaultHttpClient();
				URI address = new URI("http", null, ZoneSnap_App.URL,
						ZoneSnap_App.PORT, "/mapdata", "type=pics", null);

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
					JSONArray array = (JSONArray) json.get("pics");

					for (int i = 0; i < array.size(); i++) {
						String data = array.get(i).toString();
						String[] parts = data.split(",");
						Coordinates coor = new Coordinates();
						coor.photoId = Integer.parseInt(parts[0]);
						coor.latitude = Double.parseDouble(parts[1]);
						coor.longitude = Double.parseDouble(parts[2]);

						photos.add(coor);
					}
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
				for (Iterator<Coordinates> i = photos.iterator(); i.hasNext();) {

					Coordinates coor = i.next();

					if (!markers.contains(coor)) {
						markers.add(coor);
						LatLng point = new LatLng(coor.latitude, coor.longitude);
						map.addMarker(new MarkerOptions().position(point)
								.title(String.valueOf(coor.photoId)));
					}

				}

			} else {
				new AlertDialog.Builder(activity).setMessage(
						ZoneSnap_App.getErrorMessage() + result).show();
			}

		}

	}

}
