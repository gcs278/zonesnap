package com.zonesnap.networking;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.location.LocationManager;
import android.os.AsyncTask;
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
		LocationManager locationManager = (LocationManager) activity
				.getSystemService(Context.LOCATION_SERVICE);
		latitude = locationManager.getLastKnownLocation(
				LocationManager.GPS_PROVIDER).getLatitude();
		longitude = locationManager.getLastKnownLocation(
				LocationManager.GPS_PROVIDER).getLongitude();
		
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
			
			// Grant new code
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
			// TODO Auto-generated catch block
			e.printStackTrace();
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
