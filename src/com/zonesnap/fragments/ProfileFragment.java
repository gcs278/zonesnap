package com.zonesnap.fragments;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

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

import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.widget.ProfilePictureView;
import com.zonesnap.activities.ImageAdapter;
import com.zonesnap.activities.ZoneSnap_App;
import com.zonesnap.zonesnap_app.R;

//Fragment for user's profile
public class ProfileFragment extends Fragment {

	private ProfilePictureView profilePictureView;
	ProgressBar progressBar;
	TextView message, acquired, ranking;

	public static final String ARG_SECTION_NUMBER = "section_number";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_profile, container,
				false);

		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		// Get Fonts
		Typeface zsLogo = Typeface.createFromAsset(getActivity().getAssets(),
				"fonts/capella.ttf");
		Typeface zsFont = Typeface.createFromAsset(getActivity().getAssets(),
				"fonts/Orbitron-Regular.ttf");

		// /////////////// Set all of the textview font's ////////////////////
		TextView title = (TextView) getView().findViewById(R.id.profile_title);
		title.setTypeface(zsLogo);
		TextView userId = (TextView) getView()
				.findViewById(R.id.profile_userId);
		userId.setTypeface(zsFont);
		userId.setText(ZoneSnap_App.user.getUsername());
		TextView picsTitle = (TextView) getView().findViewById(
				R.id.profile_picsTitle);
		picsTitle.setTypeface(zsFont);
		TextView aquiredTitle = (TextView) getView().findViewById(
				R.id.profile_aquired);
		aquiredTitle.setTypeface(zsFont);
		TextView rankingTitle = (TextView) getView().findViewById(
				R.id.profile_ranking);
		rankingTitle.setTypeface(zsFont);
		acquired = (TextView) getView().findViewById(R.id.profile_aquiredNum);
		acquired.setTypeface(zsFont);
		ranking = (TextView) getView().findViewById(R.id.profile_rankingNum);
		ranking.setTypeface(zsFont);
		profilePictureView = (ProfilePictureView) getActivity().findViewById(
				R.id.profilePicture);
		profilePictureView.setProfileId(ZoneSnap_App.user.getId());
		message = (TextView) getView().findViewById(R.id.profile_message);
		message.setTypeface(zsFont);
		// //////////////////////////////////////////////////////////////

		// Get the progress bar
		progressBar = (ProgressBar) getActivity().findViewById(
				R.id.profile_progress);

		// Get personal pictures
		NetworkGetPersonalPictures picsTask = new NetworkGetPersonalPictures();
		picsTask.execute();

		// Get profile data
		NetworkGetProfileInfo profileTask = new NetworkGetProfileInfo();
		profileTask.execute();

	}

	// This network task retrieves profile information and displays
	public class NetworkGetProfileInfo extends AsyncTask<String, Void, String> {

		// Retrieve data
		@Override
		protected String doInBackground(String... params) {
			String JSON = "";
			try {
				// Set Timeout
				HttpParams httpParams = new BasicHttpParams();
				HttpConnectionParams.setConnectionTimeout(httpParams, 4000);
				HttpConnectionParams.setSoTimeout(httpParams, 4000);

				// Set up HTTP GET
				HttpClient httpclient = new DefaultHttpClient(httpParams);
				URI address = new URI("http", null, ZoneSnap_App.URL,
						ZoneSnap_App.PORT, "/profile", "type=profile&user="
								+ ZoneSnap_App.user.getUsername(), null);

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
					JSON = out.toString();
				} else {
					// Closes the connection.
					response.getEntity().getContent().close();
					throw new IOException(statusLine.getReasonPhrase());
				}
			} catch (IOException e) {
				return "connectFail";
			} catch (URISyntaxException e) {
				return "connectFail";
			}
			return JSON;
		}

		// Process data, display
		@Override
		protected void onPostExecute(String result) {
			// check if it didn't fail
			if (result != "connectFail"
					&& !result.trim().equalsIgnoreCase(new String("null"))) {

				// Parse the data
				JSONParser j = new JSONParser();
				JSONObject json = null;
				try {
					json = (JSONObject) j.parse(result);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				// Get likes, and zones crossed
				acquired.setText(json.get("total_likes").toString());
				ranking.setText(json.get("ranking").toString());

			} else {
				try {
					// Show a toast we failed to get zone
					Toast.makeText(getActivity(),
							"Failed to get profile data.", Toast.LENGTH_LONG)
							.show();
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
			}

		}
	}

	// This network activty retrieves and updates a picture
	public class NetworkGetPersonalPictures extends
			AsyncTask<String, Void, String> {
		public ArrayList<Integer> photoIDs = new ArrayList<Integer>();

		// Retrieve data
		@Override
		protected String doInBackground(String... params) {
			String photoListJSON = "";
			try {
				// Set Timeout
				HttpParams httpParams = new BasicHttpParams();
				HttpConnectionParams.setConnectionTimeout(httpParams, 4000);
				HttpConnectionParams.setSoTimeout(httpParams, 4000);

				// Set up HTTP GET
				HttpClient httpclient = new DefaultHttpClient(httpParams);
				URI address = new URI("http", null, ZoneSnap_App.URL,
						ZoneSnap_App.PORT, "/profile", "type=pics&user="
								+ ZoneSnap_App.user.getUsername(), null);

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

				try {
					JSONParser j = new JSONParser();
					JSONObject json = (JSONObject) j.parse(photoListJSON);
					JSONArray array = (JSONArray) json.get("photoIDs");

					for (int i = 0; i < array.size(); i++) {
						photoIDs.add(Integer.parseInt(array.get(i).toString()));
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}

			} catch (IOException e) {
				e.printStackTrace();
				return "connectFail";
			} catch (URISyntaxException e) {
				e.printStackTrace();
				return "connectFail";
			}
			return photoListJSON;
		}

		// Process data, display
		@Override
		protected void onPostExecute(String result) {
			// check if it didn't fail
			if (result != "connectFail") {
				try {
					// Get each picture for the grid view
					GridView grid = (GridView) getView().findViewById(
							R.id.gridProfile);
					grid.setAdapter(new ImageAdapter(getActivity(),
							ZoneSnap_App.LIKED, photoIDs, progressBar, message));
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
			}
		}

	}

}