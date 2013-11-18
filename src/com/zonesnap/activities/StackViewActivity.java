package com.zonesnap.activities;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

import com.zonesnap.zonesnap_app.R;

public class StackViewActivity extends Activity {
	
	private StackAdapter sa_;
	private ArrayList<StackItem> items;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stack_view);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.stack_view, menu);
		return true;
	}

}
