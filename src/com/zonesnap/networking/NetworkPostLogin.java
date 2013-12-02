package com.zonesnap.networking;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

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
import com.zonesnap.activities.MapMenuActivity;
import com.zonesnap.activities.MainFragmentActivity;
import com.zonesnap.classes.ZoneSnap_App;
// This task is for uploading a picture to the database
public class NetworkPostLogin extends AsyncTask<String, Void, String> {
	Context activity;
	ProgressBar progressBar;
	public NetworkPostLogin(Context context, ProgressBar pb) {
		activity = context;
		this.progressBar = pb;
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
			// Set Timeout
			HttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, 4000);
			HttpConnectionParams.setSoTimeout(httpParams, 4000);

			// Create the HTTP Post
			HttpClient client = new DefaultHttpClient(httpParams);
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
		progressBar.setVisibility(View.GONE);
		if (result.contains("OK")) {
			Intent login = new Intent(activity, MapMenuActivity.class);
			activity.startActivity(login);
		} else {
			new AlertDialog.Builder(activity).setMessage(
					ZoneSnap_App.getErrorMessage() + result).show();
		}

	}
}
