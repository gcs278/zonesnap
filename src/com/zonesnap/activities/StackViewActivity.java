/*******************************************************************
 * ZoneSnap
 * Authors: Grant Spence, Eric Owen, Denis Pelevin, Brad Russell
 * Date: 12/3/2013
 * Purpose: This application handles the Stackview activity. This is
 *  an alternative display function to the fullscreen.  
 *******************************************************************/

package com.zonesnap.activities;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.widget.StackView;

import com.zonesnap.adapters.StackAdapter;
import com.zonesnap.classes.StackItem;
import com.zonesnap.classes.ZoneSnap_App;
import com.zonesnap.zonesnap_app.R;

public class StackViewActivity extends Activity {
	
	private StackAdapter sa_;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stack_view);
		int photoID = this.getIntent().getExtras().getInt("position");
		
		ArrayList<Integer> pictureList = this.getIntent().getIntegerArrayListExtra("picture_list");
		
//		Bitmap b = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_launcher);
//		
//		StackItem item = new StackItem(b, "Test Image");
		
		sa_ = new StackAdapter(this, new ArrayList<StackItem>());
		
		StackView stack = (StackView) findViewById(R.id.stackview);
		
		stack.setAdapter(sa_);
		
		for (Integer i : pictureList) {
			StackItem item1 = new StackItem(ZoneSnap_App.imageCache.get(i),ZoneSnap_App.descCache.get(i));
			sa_.add(item1);
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.stack_view, menu);
		return true;
	}

}
