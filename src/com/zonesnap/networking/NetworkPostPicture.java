package com.zonesnap.networking;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.internal.ac;
import com.zonesnap.classes.ZoneSnap_App;
// This task is for uploading a picture to the database
public class NetworkPostPicture extends AsyncTask<String, Void, String> {
	Context activity;
	// Progress display
	ProgressDialog pd;
	double latitude, longitude;
	public NetworkPostPicture(Context context) {
		activity = context;
	}

	// Let user know its updating
	@Override
	protected void onPreExecute() {
		// Display a loading widget to keep user happy
		pd = new ProgressDialog(activity);
		pd.setTitle("Uploading Picture...");
		pd.setMessage("Please wait.");
		pd.setCancelable(false);
		pd.setIndeterminate(true);
		pd.show();
		
		// Get Current location
		Location location = getBestLocation();
		latitude = location.getLatitude();
		longitude = location.getLongitude();
		
	}

	/**
	 * try to get the 'best' location selected from all providers
	 */
	private Location getBestLocation() {
	    Location gpslocation = getLocationByProvider(LocationManager.GPS_PROVIDER);
	    Location networkLocation =
	            getLocationByProvider(LocationManager.NETWORK_PROVIDER);
	    // if we have only one location available, the choice is easy
	    if (gpslocation == null) {
	        // Log.d(TAG, "No GPS Location available.");
	        return networkLocation;
	    }
	    if (networkLocation == null) {
	        // Log.d(TAG, "No Network Location available");
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
	        // Log.d(TAG, "Returning current GPS Location");
	        return gpslocation;
	    }
	    // gps is old, we can't trust it. use network location
	    if (!networkIsOld) {
	       // Log.d(TAG, "GPS is old, Network is current, returning network");
	        return networkLocation;
	    }
	    // both are old return the newer of those two
	    if (gpslocation.getTime() > networkLocation.getTime()) {
	       // Log.d(TAG, "Both are old, returning gps(newer)");
	        return gpslocation;
	    } else {
	       // Log.d(TAG, "Both are old, returning network(newer)");
	        return networkLocation;
	    }
	}

	/**
	 * get the last known location from a specific provider (network/gps)
	 */
	private Location getLocationByProvider(String provider) {
	    Location location = null;
	    LocationManager locationManager = (LocationManager) activity.getApplicationContext()
	            .getSystemService(Context.LOCATION_SERVICE);
	    try {
	        if (locationManager.isProviderEnabled(provider)) {
	            location = locationManager.getLastKnownLocation(provider);
	        }
	    } catch (IllegalArgumentException e) {
	       //  Log.d(TAG, "Cannot acces Provider " + provider);
	    }
	    return location;
	}
	// Retrieve data
	@Override
	protected String doInBackground(String... params) {
		String verified = "OK";
		try {
			// Set Timeout
			HttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, 4000);
			HttpConnectionParams.setSoTimeout(httpParams, 4000);

			// Create the HTTP Post
			HttpClient client = new DefaultHttpClient(httpParams);
			URI address = new URI("http", null, ZoneSnap_App.URL, ZoneSnap_App.PORT, "/uploadpic", null,
					null);
			HttpPost request = new HttpPost(address);
			
			// Create JSON object for image
			JSONObject json = new JSONObject();
			json.put("image",params[1]);
			json.put("title", params[0]);
			json.put("lat", latitude);
			json.put("long",longitude);
			json.put("username", params[2]);
			
			request.setEntity(new StringEntity(json.toString().replace("\\", "")));			

			ResponseHandler<String> responsehandler = new BasicResponseHandler();
			client.execute(request, responsehandler);

		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return e.getMessage();
		} catch (IOException e) {
			e.printStackTrace();
			return e.getMessage();
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return e.getMessage();
		} catch (JSONException e) {
			e.printStackTrace();
			return e.getMessage();
		}
		return verified;
	}

	// Process data, display
	@Override
	protected void onPostExecute(String result) {
		// Close progress dialog
		pd.dismiss();
		if (result.contains("OK")) {
			new AlertDialog.Builder(activity).setMessage(
					"Picture successfully uploaded to database").show();

		} else {
			new AlertDialog.Builder(activity).setMessage(
					ZoneSnap_App.getErrorMessage() + result).show();
		}

	}
}
