package com.zonesnap.networking.post;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.internal.ac;
import com.zonesnap.activities.HomeActivity;
import com.zonesnap.activities.MainActivity;
import com.zonesnap.activities.ZoneSnap_App;
// This task is for uploading a picture to the database
public class NetworkPostLogin extends AsyncTask<String, Void, String> {
	Context activity;
	
	public NetworkPostLogin(Context context) {
		activity = context;
	}

	// Let user know its updating
	@Override
	protected void onPreExecute() {

	}

	// Retrieve data
	@Override
	protected String doInBackground(String... params) {
		String verified = "OK";
		try {

			// Create the HTTP Post
			HttpClient client = new DefaultHttpClient();
			URI address = new URI("http", null, ZoneSnap_App.URL, ZoneSnap_App.PORT, "/login", null,
					null);
			HttpPost request = new HttpPost(address);
			
			// Grant new code
			// Create JSON object for image
			JSONObject json = new JSONObject();
			
			json.put("username",params[0]);
			
			request.setEntity(new StringEntity(json.toString()));
				

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
		if (result.contains("OK")) {
			Intent login = new Intent(activity, HomeActivity.class);
			activity.startActivity(login);
		} else {
			new AlertDialog.Builder(activity).setMessage("Unable to login: "
					+ result).show();
		}

	}
}