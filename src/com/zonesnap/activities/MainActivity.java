package com.zonesnap.activities;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Locale;

import org.apache.commons.codec.binary.Base64;

import com.zonesnap.fragments.ProfileFragment;
import com.zonesnap.networking.get.NetworkGetZone;
import com.zonesnap.networking.post.NetworkPostPicture;
import com.zonesnap.zonesnap_app.R;
import com.zonesnap.zonesnap_app.R.id;
import com.zonesnap.zonesnap_app.R.layout;
import com.zonesnap.zonesnap_app.R.menu;
import com.zonesnap.zonesnap_app.R.string;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.ClipData.Item;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements
		ActionBar.TabListener {
	// Fragment Variables
	SectionsPagerAdapter mSectionsPagerAdapter;
	ViewPager mViewPager;

	private static final int CAMERA_REQUEST = 1888;

	Location mCurrentLocation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
		mViewPager.setCurrentItem(1);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			Fragment fragment;
			switch (position) {
			case 0:
				fragment = new UploadFragment();
				break;
			case 1:
				fragment = new CurrentFragment();
				break;
			case 2:
				fragment = new HistoryFragment();
				break;
			case 3:
				fragment = new ProfileFragment();
				break;

			default:
				fragment = new CurrentFragment();
				break;
			}
			return fragment;
		}

		@Override
		public int getCount() {
			// Show 4 total pages.
			return 4;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section0).toUpperCase(l);
			case 1:
				return getString(R.string.title_section1).toUpperCase(l);
			case 2:
				return getString(R.string.title_section2).toUpperCase(l);
			case 3:
				return getString(R.string.title_section3).toUpperCase(l);

			}
			return null;
		}
	}

	// Fragment for the current zone
	public static class CurrentFragment extends Fragment {
		public static final String ARG_SECTION_NUMBER = "section_number";

		TextView logo;
		public CurrentFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_current,
					container, false);

			return rootView;
		}

		@Override
		public void onViewCreated(View view, Bundle savedInstanceState) {
			super.onViewCreated(view, savedInstanceState);
			final GridView grid = (GridView) getView().findViewById(
					R.id.gridCurrent);
			grid.setAdapter(new CurrentImageAdapter(getActivity()));

			// Set font
			Typeface zsFont = Typeface.createFromAsset(getActivity()
					.getAssets(), "fonts/Orbitron-Regular.ttf");
			TextView title = (TextView) getView().findViewById(
					R.id.current_title);
			title.setTypeface(zsFont);
			Typeface zsLogo = Typeface.createFromAsset(getActivity()
					.getAssets(), "fonts/capella.ttf");
			logo = (TextView) getView()
					.findViewById(R.id.current_Logo);
			logo.setTypeface(zsLogo);
			// Acquire a reference to the system Location Manager
			LocationManager locationManager = (LocationManager) getActivity()
					.getSystemService(Context.LOCATION_SERVICE);
			// Register the listener with the Location Manager to receive   
			// location updates
			locationManager.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);
		}

		public final LocationListener mLocationListener = new LocationListener() {

			@Override
			public void onLocationChanged(final Location location) {
				System.out.println("Lat: "+location.getLatitude());
				System.out.println("Long: "+location.getLongitude());
				System.out.println("Accuracy: "+location.getAccuracy());
				
				if ( location.getAccuracy() > 40.0 ) {
				}
				
				NetworkGetZone task = new NetworkGetZone(getActivity(), logo, location.getLatitude(),location.getLongitude());
				task.execute();
			}

			@Override
			public void onProviderDisabled(String arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProviderEnabled(String arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
				// TODO Auto-generated method stub

			}
		};
	}

	// Fragment for the Historic ZoneSnap View
	public static class HistoryFragment extends Fragment {
		public static final String ARG_SECTION_NUMBER = "section_number";

		public HistoryFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_history,
					container, false);

			return rootView;
		}

		@Override
		public void onViewCreated(View view, Bundle savedInstanceState) {
			super.onViewCreated(view, savedInstanceState);
			GridView grid = (GridView) getView().findViewById(R.id.gridHistory);
			grid.setAdapter(new CurrentImageAdapter(getActivity()));

			grid.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View v,
						int position, long id) {
					Toast.makeText(getActivity(), "" + position,
							Toast.LENGTH_SHORT).show();
				}
			});
			// Set font
			Typeface zsFont = Typeface.createFromAsset(getActivity()
					.getAssets(), "fonts/Orbitron-Regular.ttf");
			TextView title = (TextView) getView().findViewById(
					R.id.history_likedTitle);
			title.setTypeface(zsFont);
			TextView title2 = (TextView) getView().findViewById(
					R.id.history_pastTitle);
			title2.setTypeface(zsFont);
		}
	}

	// fragment for camera
	public static class UploadFragment extends Fragment {
		public static final String ARG_SECTION_NUMBER = "section_number";

		ImageButton camerabtn;
		Button uploadbtn;
		Button clearbtn;
		ImageView imageView;
		EditText editTitle;
		boolean imgTaken;
		String image64;
		Bitmap image;
		Button notifybtn;

		public UploadFragment() {

		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_upload,
					container, false);
			if (imgTaken) {
				imageView.setVisibility(View.VISIBLE);
				imageView.setImageBitmap(image);
				camerabtn.setVisibility(View.GONE);
			}
			return rootView;
		}

		@Override
		public void onViewCreated(View view, Bundle savedInstanceState) {
			super.onViewCreated(view, savedInstanceState);
			// Set Fonts
			Typeface zsLogo = Typeface.createFromAsset(getActivity()
					.getAssets(), "fonts/capella.ttf");
			TextView title = (TextView) getView().findViewById(
					R.id.upload_title);
			title.setTypeface(zsLogo);

			imgTaken = false;

			// image view set to invis at first
			imageView = (ImageView) getView().findViewById(R.id.uploadImg);
			imageView.setVisibility(View.GONE);

			// camera button
			camerabtn = (ImageButton) getView().findViewById(R.id.cameraBtn);
			camerabtn.setOnClickListener(new OnClickListener() {
				public void onClick(View arg0) {
					Intent cameraIntent = new Intent(
							android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
					startActivityForResult(cameraIntent, CAMERA_REQUEST);
				}
			});

			// clear button
			clearbtn = (Button) getView().findViewById(R.id.clearBtn);
			clearbtn.setOnClickListener(new OnClickListener() {
				public void onClick(View arg0) {
					uploadClear();
				}
			});

			// upload button
			uploadbtn = (Button) getView().findViewById(R.id.uploadBtn);
			uploadbtn.setOnClickListener(new OnClickListener() {
				public void onClick(View arg0) {
					String title = editTitle.getText().toString();
					NetworkPostPicture task = new NetworkPostPicture(
							getActivity());

					task.execute(title, image64);
					uploadClear();
				}
			});

			editTitle = (EditText) getView().findViewById(R.id.titleEdit);

			final NotificationManager notiMgr = (NotificationManager) getActivity()
					.getSystemService(NOTIFICATION_SERVICE);
			// notify test button
			notifybtn = (Button) getView().findViewById(R.id.notificationTest);
			notifybtn.setOnClickListener(new OnClickListener() {
				@SuppressLint({ "ServiceCast", "NewApi" })
				public void onClick(View arg0) {
					Intent notIntent = new Intent(getActivity(),
							MainActivity.class);
					PendingIntent pIntent = PendingIntent.getActivity(
							getActivity(), 0, notIntent, 0);
					Notification n = new Notification.Builder(getActivity())
							.setContentTitle("Entered new zone.")
							.setSmallIcon(R.drawable.zonesnap1_launcher)
							.setContentText(
									"Touch to view content of new zone.")
							.setContentIntent(pIntent).setAutoCancel(true)
							.build();
					notiMgr.notify(0, n);
				}
			});

		}

		public void uploadClear() {
			imgTaken = false;
			imageView.setVisibility(View.GONE);
			camerabtn.setVisibility(View.VISIBLE);
			editTitle.setText("");
		}

		// Called when the camera activities respond when finished
		public void onActivityResult(int requestCode, int resultCode,
				Intent data) {
			// camera
			if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
				image = (Bitmap) data.getExtras().get("data");
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
				byte[] bytes = stream.toByteArray();
				bytes = Base64.encodeBase64(bytes);

				// Convert to base64 string
				try {
					image64 = new String(bytes, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}

				Toast.makeText(getActivity(), "photo taken!",
						Toast.LENGTH_SHORT).show();
				imgTaken = true;
				imageView.setVisibility(View.VISIBLE);
				imageView.setImageBitmap(image);
				camerabtn.setVisibility(View.GONE);

			}

		}
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub

	}

}
