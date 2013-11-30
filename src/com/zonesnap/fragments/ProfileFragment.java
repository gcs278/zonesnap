package com.zonesnap.fragments;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.widget.ProfilePictureView;
import com.zonesnap.activities.ZoneSnap_App;
import com.zonesnap.zonesnap_app.R;

//Fragment for user's profile
public class ProfileFragment extends Fragment {

	private ProfilePictureView profilePictureView;// = (ProfilePictureView)
													// getView().findViewById(R.id.profilePicture);

	public static final String ARG_SECTION_NUMBER = "section_number";

	public ProfileFragment() {
	}

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
		// THIS WILL BE WHERE YOU SET UP THE PROFILE DATE
		// i.e. load profile pic
		// Set Fonts
		Typeface zsLogo = Typeface.createFromAsset(getActivity().getAssets(),
				"fonts/capella.ttf");
		Typeface zsFont = Typeface.createFromAsset(getActivity().getAssets(),
				"fonts/Orbitron-Regular.ttf");
		TextView title = (TextView) getView().findViewById(R.id.profile_title);
		title.setTypeface(zsLogo);
		TextView userId = (TextView) getView()
				.findViewById(R.id.profile_userId);
		userId.setTypeface(zsFont);
		userId.setText(ZoneSnap_App.user.getUsername());
		profilePictureView = (ProfilePictureView) getActivity().findViewById(
				R.id.profilePicture);
		profilePictureView.setProfileId(ZoneSnap_App.user.getId());

	}

	// This network task retrieves profile information and displays
	public class NetworkGetProfileInfo extends AsyncTask<String, Void, String> {
		Context activity;

		public NetworkGetProfileInfo(Context context) {
			activity = context;
		}

		// Retrieve data
		@Override
		protected String doInBackground(String... params) {
			String JSON = "";
			try {
				// Set up HTTP GET
				HttpClient httpclient = new DefaultHttpClient();
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
				String total_likes = (String) json.get("total_likes");
				String zones_crossed = (String) json.get("zones_crossed");

			} else {
				System.out.println("Fail");
			}

		}
	}

}