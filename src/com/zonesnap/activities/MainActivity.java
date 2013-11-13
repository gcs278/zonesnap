package com.zonesnap.activities;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Locale;

import com.zonesnap.zonesnap_app.R;
import com.zonesnap.zonesnap_app.R.id;
import com.zonesnap.zonesnap_app.R.layout;
import com.zonesnap.zonesnap_app.R.menu;
import com.zonesnap.zonesnap_app.R.string;

import android.content.Intent;
import android.content.ClipData.Item;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.util.Base64;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;
	
	private static final int CAMERA_REQUEST = 1888;

	


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		
				

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
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
				fragment = new CurrentFragment();
				break;
			case 1:
				fragment = new HistoryFragment();
				break;
			case 2:
				fragment = new ProfileFragment();
				break;
			case 3: 
				fragment = new UploadFragment();
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
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			case 2:
				return getString(R.string.title_section3).toUpperCase(l);
			case 3:
				return getString(R.string.title_section4).toUpperCase(l);
			}
			return null;
		}
	}

	// Fragment for the current zone
	public static class CurrentFragment extends Fragment {
		public static final String ARG_SECTION_NUMBER = "section_number";

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
			final ArrayList<String> items = new ArrayList<String>();

			items.add("Current Grid 1 , 11 , 12");
			items.add("Current Grid 2 , 21 , 22");
			grid.setAdapter(new GridAdapter(items));

		}
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

			final GridView grid = (GridView) getView().findViewById(
					R.id.gridHistory);
			final ArrayList<String> items = new ArrayList<String>();

			items.add("History Grid 1 , 11 , 12");
			items.add("History Grid 2 , 21 , 22");
			grid.setAdapter(new GridAdapter(items));
		}
	}

	// Fragment for user's profile
	public static class ProfileFragment extends Fragment {
		public static final String ARG_SECTION_NUMBER = "section_number";

		public ProfileFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_profile,
					container, false);

			return rootView;
		}

		@Override
		public void onViewCreated(View view, Bundle savedInstanceState) {
			super.onViewCreated(view, savedInstanceState);
			// THIS WILL BE WHERE YOU SET UP THE PROFILE DATE
			// i.e. load profile pic
		}
	}
	
	//fragment for camera
	public static class UploadFragment extends Fragment {
		public static final String ARG_SECTION_NUMBER = "section_number";
		
		public UploadFragment() {
			
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_upload,
					container, false);
			return rootView;
		}
		
		@Override
		public void onViewCreated(View view, Bundle savedInstanceState) {
			super.onViewCreated(view, savedInstanceState);
			//setup upload button to call camera intent
			Button uploadbtn = (Button) getView().findViewById(R.id.uploadbtn);
			uploadbtn.setOnClickListener(new OnClickListener() {
				public void onClick(View arg0) {
					Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
					startActivityForResult(cameraIntent, CAMERA_REQUEST);
					
				}
			});
		}
		
		//Called when the camera activities respond when finished
		public void onActivityResult(int requestCode, int resultCode, Intent data) {
			//camera 
			if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
				Bitmap photo = (Bitmap) data.getExtras().get("data");
				//ByteArrayOutputStream stream = new ByteArrayOutputStream();
			    //photo.compress(Bitmap.CompressFormat.JPEG, 100, stream);
			    //byte[] b = stream.toByteArray();
			    Toast.makeText(getActivity(),"photo taken!",Toast.LENGTH_SHORT).show();
			    Intent intent = new Intent(getActivity(), UploadActivity.class);
			    intent.putExtra("image", photo);
			    startActivity(intent);
			    
			}	

		}
	}

}
