/*******************************************************************
 * ZoneSnap
 * Authors: Grant Spence, Eric Owen, Denis Pelevin, Brad Russell
 * Date: 12/3/2013
 * Purpose: This class manages the image display activity. It is
 *  called when an image is clicked, and is passed the image array
 *  and position of the image clicked on.   
 *******************************************************************/

package com.zonesnap.activities;

import java.util.ArrayList;

import com.zonesnap.adapters.FullScreenImageAdapter;
import com.zonesnap.zonesnap_app.R;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

// This is the activity for viewing a image full screen
public class FullScreenViewActivity extends Activity {
	
	
	private FullScreenImageAdapter adapter;
	private ViewPager viewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fullscreen_view);

		viewPager = (ViewPager) findViewById(R.id.pager);

		ArrayList<Integer> pictureList = getIntent().getIntegerArrayListExtra(
				"picture_list");
		int position = getIntent().getIntExtra("position", 0);

		adapter = new FullScreenImageAdapter(FullScreenViewActivity.this,
				pictureList);

		viewPager.setAdapter(adapter);

		// displaying selected image first
		viewPager.setCurrentItem(pictureList.indexOf(position));
	}
}
