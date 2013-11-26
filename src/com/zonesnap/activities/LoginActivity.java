package com.zonesnap.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.zonesnap.zonesnap_app.R;


public class LoginActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		Button toStack = (Button) findViewById(R.id.toStack);
		
		toStack.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				Intent i = new Intent(LoginActivity.this, StackViewActivity.class);
				startActivity(i);
				finish();
				
			}
		});
		Typeface zsFont = Typeface.createFromAsset(getAssets(), "fonts/capella.ttf");
		TextView title = (TextView)findViewById(R.id.login_title);
		title.setTypeface(zsFont);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	public void login(View view) {
		Intent login = new Intent(this, HomeActivity.class);
		startActivity(login);
	}
}

