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
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;
import com.zonesnap.activities.ImageAdapter;
import com.zonesnap.activities.ZoneSnap_App;
import com.zonesnap.zonesnap_app.R;

//Fragment for the Historic ZoneSnap View
public class HistoryFragment extends Fragment {
	public static final String ARG_SECTION_NUMBER = "section_number";

	public HistoryFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_history, container,
				false);

		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		NetworkGetLikedList likeTask = new NetworkGetLikedList(getActivity());
		likeTask.execute();
		NetworkGetPastList pastTask = new NetworkGetPastList(getActivity());
		pastTask.execute();

		// Set font
		Typeface zsFont = Typeface.createFromAsset(getActivity().getAssets(),
				"fonts/Orbitron-Regular.ttf");
		TextView title = (TextView) getView().findViewById(
				R.id.history_likedTitle);
		title.setTypeface(zsFont);
		TextView title2 = (TextView) getView().findViewById(
				R.id.history_pastTitle);
		title2.setTypeface(zsFont);
	}

	// This network activty retrieves and updates a picture
	public class NetworkGetLikedList extends AsyncTask<String, Void, String> {
		Context activity;
		public ArrayList<Integer> photoIDs = new ArrayList<Integer>();

		public NetworkGetLikedList(Context context) {
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
						ZoneSnap_App.PORT, "/like", "user=grantspence", null);

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
					JSONArray array = (JSONArray) json.get("photoIDs");

					for (int i = 0; i < array.size(); i++) {
						photoIDs.add(Integer.parseInt(array.get(i).toString()));
					}
					System.out.println("LOL:" + photoIDs);
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
			System.out.println(result);
			// check if it didn't fail
			if (result != "connectFail") {
				try {
					GridView grid = (GridView) getView().findViewById(
							R.id.gridLiked);
					grid.setAdapter(new ImageAdapter(getActivity(),
							ZoneSnap_App.LIKED, photoIDs));
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
			} else {
				System.out.println("FailGetPictureList");
			}

		}

	}

	// This network activty retrieves and updates a picture
	public class NetworkGetPastList extends AsyncTask<String, Void, String> {
		Context activity;
		public ArrayList<Integer> photoIDs = new ArrayList<Integer>();

		public NetworkGetPastList(Context context) {
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
						ZoneSnap_App.PORT, "/tracking", "user=grantspence",
						null);

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
					JSONArray array = (JSONArray) json.get("photoIDs");

					for (int i = 0; i < array.size(); i++) {
						photoIDs.add(Integer.parseInt(array.get(i).toString()));
					}
					System.out.println("LOL:" + photoIDs);
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
			System.out.println(result);
			// check if it didn't fail
			if (result != "connectFail") {
				try {
					GridView grid = (GridView) getView().findViewById(
							R.id.gridHistory);
					grid.setAdapter(new ImageAdapter(getActivity(),
							ZoneSnap_App.LIKED, photoIDs));
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
			} else {
				System.out.println("FailGetPictureList");
			}

		}

	}

}