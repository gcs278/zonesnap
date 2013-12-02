package com.zonesnap.networking;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
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

import com.zonesnap.classes.ZoneSnap_App;

// This task is for liking a picture
public class NetworkPostLike extends AsyncTask<String, Void, String> {
	Context activity;

	public NetworkPostLike(Context context) {
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
			URI address = new URI("http", null, ZoneSnap_App.URL,
					ZoneSnap_App.PORT, "/like", null, null);
			HttpPost request = new HttpPost(address);

			// Create JSON object for like
			JSONObject json = new JSONObject();

			json.put("username", params[0]);
			json.put("photoID", params[1]);

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
		if (!result.contains("SUCCESS")) {
			// Put error code here
		} else {
			new AlertDialog.Builder(activity).setMessage(
					ZoneSnap_App.getErrorMessage() + result).show();
		}

	}
}
