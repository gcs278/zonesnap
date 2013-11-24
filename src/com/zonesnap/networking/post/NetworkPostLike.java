package com.zonesnap.networking.post;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.zonesnap.activities.MainActivity;
import com.zonesnap.activities.UploadActivity;
// This task is for uploading a picture to the database
public class NetworkPostLike extends AsyncTask<String, Void, String> {
	Context activity;
	// Progress display
	ProgressDialog pd;
	int port;
	String URL;
	
	public NetworkPostLike(Context context) {
		activity = context;
		port = 8080;
		URL = "www.grantspence.com";
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
	}

	// Retrieve data
	@Override
	protected String doInBackground(String... params) {
		String verified = "OK";
		try {

			// Create the HTTP Post
			HttpClient client = new DefaultHttpClient();
			URI address = new URI("http", null, URL, port, "/like", null,
					null);
			HttpPost request = new HttpPost(address);
			
			// Grant new code
			// Create JSON object for image
			JSONObject json = new JSONObject();
			
			json.put("username",params[0]);
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

		}

	}
}
