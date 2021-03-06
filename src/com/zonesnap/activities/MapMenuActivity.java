/*******************************************************************
 * ZoneSnap
 * Authors: Grant Spence, Eric Owen, Denis Pelevin, Brad Russell
 * Date: 12/3/2013
 * Purpose: This Activity controls the map section of the application.
 *  This is the first activity the user comes to after they login. 
 *  The map is from Google maps, overlayed with the locations of
 *  all of the photos taken. The user can change the type of map
 *  background and use it as they would the Google map application.  
 *******************************************************************/
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
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.facebook.Session;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.zonesnap.classes.Coordinates;
import com.zonesnap.classes.LocationService;
import com.zonesnap.classes.ZoneSnap_App;
import com.zonesnap.zonesnap_app.R;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MapMenuActivity extends FragmentActivity {
	GoogleMap map; // Map Var

	// Marker array of pictures on the map
	List<Coordinates> markers = new ArrayList<Coordinates>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		Intent intent = new Intent(this, LocationService.class);
		startService(intent);

		// Get the map fragment
		SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map);
		map = fm.getMap();

		// Set the current location to the last known location
		map.setMyLocationEnabled(true);
		LocationManager locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);
		Location location = locationManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		LatLng myCoor = null;

		// Verify that GPS is running
		if (location != null) {
			myCoor = new LatLng(location.getLatitude(), location.getLongitude());
		} else {
			EnableGPS();
		}

		try {
			// Move the camera to current location
			// This has been known to throw exception
			map.animateCamera(CameraUpdateFactory.newLatLngZoom(myCoor, 18));
			map.setMapType(ZoneSnap_App.MAP_TYPE);
		} catch (NullPointerException e) {
			// GPS is not enabled
			Toast.makeText(this, "GPS is disabled", Toast.LENGTH_LONG);
		}

		// Get all of the markers for pictures
		NetworkGetPictureLocations task = new NetworkGetPictureLocations();
		task.execute();

		// ////////////////////// BUTTON CODE//////////////////////////////////
		// navigate to upload fragment
		Button toUpload = (Button) findViewById(R.id.toUploadBtn);
		toUpload.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(getBaseContext(),
						MainFragmentActivity.class);
				i.putExtra("position", 0);
				startActivity(i);
			}
		});

		// nvaigate to current fragment
		Button toCurrent = (Button) findViewById(R.id.toCurrentBtn);
		toCurrent.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(getBaseContext(),
						MainFragmentActivity.class);
				i.putExtra("position", 1);
				startActivity(i);
			}
		});

		// navigate to past fragment
		Button toPast = (Button) findViewById(R.id.toPastBtn);
		toPast.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(getBaseContext(),
						MainFragmentActivity.class);
				i.putExtra("position", 2);
				startActivity(i);
			}
		});

		// navigate to profile fragment
		Button toProfile = (Button) findViewById(R.id.toProfileBtn);
		toProfile.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(getBaseContext(),
						MainFragmentActivity.class);
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
		toUpload.setTypeface(zsFont);
		toPast.setTypeface(zsFont);
		toCurrent.setTypeface(zsFont);
		toProfile.setTypeface(zsFont);
	}

	private void EnableGPS() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(
				"Your GPS seems to be disabled, do you want to enable it?")
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(
									@SuppressWarnings("unused") final DialogInterface dialog,
									@SuppressWarnings("unused") final int id) {
								startActivity(new Intent(
										android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(final DialogInterface dialog,
							@SuppressWarnings("unused") final int id) {
						dialog.cancel();
					}
				});
		final AlertDialog alert = builder.create();
		alert.show();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Start Settings acivity
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

					// Destroy the facebook session
					Session session = Session.getActiveSession();
					if (session.isOpened())
						session.closeAndClearTokenInformation();

					// Go back to login
					MapMenuActivity.super.onBackPressed();
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
		NetworkGetPictureLocations task = new NetworkGetPictureLocations();
		task.execute();

		// Reset the map type
		map.setMapType(ZoneSnap_App.MAP_TYPE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home, menu);
		return true;
	}

	// This network activity get the info from all pictures and displays the
	// markers on Map
	public class NetworkGetPictureLocations extends
			AsyncTask<String, Void, String> {
		// Local markers array list
		public ArrayList<Coordinates> currentMarkers = new ArrayList<Coordinates>();

		// Retrieve data
		@Override
		protected String doInBackground(String... params) {
			String jsonData = "";
			try {
				// Set Timeout
				HttpParams httpParams = new BasicHttpParams();
				HttpConnectionParams.setConnectionTimeout(httpParams, 4000);
				HttpConnectionParams.setSoTimeout(httpParams, 4000);

				// Set up HTTP GET
				HttpClient httpclient = new DefaultHttpClient(httpParams);
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
					// Get the data
					jsonData = out.toString();

				} else {
					// Closes the connection.
					response.getEntity().getContent().close();
					throw new IOException(statusLine.getReasonPhrase());
				}

				try {
					// Parse the json data
					JSONParser j = new JSONParser();
					JSONObject json = (JSONObject) j.parse(jsonData);
					JSONArray array = (JSONArray) json.get("pics");

					// Get each pictures information
					for (int i = 0; i < array.size(); i++) {
						// Create a coordinate and add to the photos list
						String data = array.get(i).toString();
						String[] parts = data.split(",");
						Coordinates coor = new Coordinates();
						try {
							coor.photoId = Integer.parseInt(parts[0]);
							coor.latitude = Double.parseDouble(parts[1]);
							coor.longitude = Double.parseDouble(parts[2]);
							coor.title = parts[3];
							coor.username = parts[4];
						} catch (ArrayIndexOutOfBoundsException e) {
							e.printStackTrace();
						}

						currentMarkers.add(coor);
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}

			} catch (IOException e) {
				return "connectFail";
			} catch (URISyntaxException e) {
				return "connectFail";
			}
			return jsonData;
		}

		// Process data, display
		@Override
		protected void onPostExecute(String result) {
			// check if it didn't fail
			if (result != "connectFail") {
				// Go through current markers and add if needed to map
				for (Iterator<Coordinates> i = currentMarkers.iterator(); i
						.hasNext();) {
					Coordinates coor = i.next();

					// If marker isn't on map already, add to map
					if (!markers.contains(coor)) {
						markers.add(coor);
						LatLng point = new LatLng(coor.latitude, coor.longitude);
						map.addMarker(new MarkerOptions()
								.position(point)
								.title("Photo #" + String.valueOf(coor.photoId)
										+ " By: " + coor.username)
								.snippet(coor.title));
					}

				}

			} else {
				try {
					new AlertDialog.Builder(MapMenuActivity.this).setMessage(
							ZoneSnap_App.getErrorMessage() + result).show();
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
			}

		}

	}

}
