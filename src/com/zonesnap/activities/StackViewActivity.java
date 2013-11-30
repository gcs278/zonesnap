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
		
		sa_ = new StackAdapter(this, new ArrayList<StackItem>());
		
		StackView stack = (StackView) findViewById(R.id.stackview);
		
		stack.setAdapter(sa_);
		
		Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
		
		StackItem item = new StackItem(b,"Test Description");
		
		sa_.add(item);
		
		try {
			for(int i = 0; i < ZoneSnap_App.currentImageCache.size(); i++){
				
				if(ZoneSnap_App.currentImageCache.get(i) != null){
					sa_.add(new StackItem(ZoneSnap_App.currentImageCache.get(i), "Test Description"));
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		stack.setDisplayedChild(photoID);

		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.stack_view, menu);
		return true;
	}

}
