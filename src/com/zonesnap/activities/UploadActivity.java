package com.zonesnap.activities;

import java.io.ByteArrayOutputStream;

import com.zonesnap.networking.post.NetworkPostPicture;
import com.zonesnap.zonesnap_app.R;
import com.zonesnap.zonesnap_app.R.layout;
import com.zonesnap.zonesnap_app.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Base64;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class UploadActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_upload);
		
		//get extras passed though intent
		Bundle extras = getIntent().getExtras();
		Bitmap image = (Bitmap) getIntent().getParcelableExtra("image");
		
		//set up imageview and textedit
		ImageView imageView = (ImageView)findViewById(R.id.imageView1);
		final EditText editText = (EditText)findViewById(R.id.editText1);
		
		imageView.setImageBitmap(image);
			
		//convert image to base64
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
	    image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
	    byte[] b = stream.toByteArray();
	    final String image64 = Base64.encodeToString(b, Base64.DEFAULT);
		
		//set up button and button listener
		Button uploadbtn = (Button) findViewById(R.id.button1);
		uploadbtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String title = editText.getText().toString();
				NetworkPostPicture task = new NetworkPostPicture(getBaseContext());
				task.execute(image64);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.upload, menu);
		return true;
	}

}
