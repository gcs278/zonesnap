package com.zonesnap.activities;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.widget.StackView;

import com.zonesnap.zonesnap_app.R;

public class StackViewActivity extends Activity {
	
	private StackAdapter sa_;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stack_view);
		int photoID = this.getIntent().getExtras().getInt("position");
		
		Bitmap b = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_launcher);
		
		StackItem item = new StackItem(b, "Test Image");
		
		sa_ = new StackAdapter(this, new ArrayList<StackItem>());
		
		StackView stack = (StackView) findViewById(R.id.stackview);
		
		stack.setAdapter(sa_);
		
		StackItem item1 = new StackItem(ZoneSnap_App.currentImageCache.get(String.valueOf(photoID)),"Lol");
		sa_.add(item1);
		StackItem item2 = new StackItem(ZoneSnap_App.currentImageCache.get(String.valueOf(photoID+1)),"Lol");
		sa_.add(item2);
		StackItem item3 = new StackItem(ZoneSnap_App.currentImageCache.get(String.valueOf(photoID+2)),"Lol");
		sa_.add(item3);
		StackItem item4 = new StackItem(ZoneSnap_App.currentImageCache.get(String.valueOf(photoID+3)),"Lol");
		sa_.add(item4);
//		for(int i = 0; i < 5; i++){
//			sa_.add(item);
//		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.stack_view, menu);
		return true;
	}

}
