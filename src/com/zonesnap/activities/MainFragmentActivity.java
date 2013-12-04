/*******************************************************************
 * ZoneSnap
 * Authors: Grant Spence, Eric Owen, Denis Pelevin, Brad Russell
 * Date: 12/3/2013
 * Purpose: This class manages main portion of the application. Here
 *  the user switches between the fragments that comprise the bulk of
 *  the UI. The fragment implementations can be found in the 
 *  com.zonesnap.fragments package.
 *******************************************************************/

package com.zonesnap.activities;

import java.util.Locale;

import com.zonesnap.fragments.CurrentFragment;
import com.zonesnap.fragments.LikedFragment;
import com.zonesnap.fragments.ProfileFragment;
import com.zonesnap.fragments.UploadFragment;
import com.zonesnap.zonesnap_app.R;

import com.zonesnap.activities.SettingsActivity;
import com.zonesnap.classes.ZoneSnap_App;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

// This is the activity that the fragments run off of
public class MainFragmentActivity extends FragmentActivity implements
		ActionBar.TabListener {
	// Fragment Variables
	SectionsPagerAdapter mSectionsPagerAdapter;
	ViewPager mViewPager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_fragments);

		// get intent extras (postition)
		Bundle extras = getIntent().getExtras();
		int startPosition = extras.getInt("position");

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
		mViewPager.setOffscreenPageLimit(4);
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
		mViewPager.setCurrentItem(startPosition);
		
		// Check that user is logged in
		if (ZoneSnap_App.user == null) {
			Toast.makeText(getApplicationContext(),"Please log back into ZoneSnap", Toast.LENGTH_LONG).show();
			finish();
			return;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	// Settings
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent settings = new Intent(this, SettingsActivity.class);
		startActivity(settings);
		return super.onOptionsItemSelected(item);
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

		// Order of the fragments
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
				fragment = new LikedFragment();
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

		// Get count
		@Override
		public int getCount() {
			// Show 4 total pages.
			return 4;
		}

		// Get Titles
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

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// Nothing

	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// Nothing

	}

}
