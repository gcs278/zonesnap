package com.zonesnap.activities;

import com.zonesnap.zonesnap_app.R;
import com.zonesnap.zonesnap_app.R.id;
import com.zonesnap.zonesnap_app.R.layout;
import com.zonesnap.zonesnap_app.R.menu;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ImageViewActivity extends Activity {

	private Bundle recBun;
	private String[] loc = null;
	private String[] des = null;

	private Button like_;
	private ImageView imageview_;
	private int sensitivity = 50;
	private TextView description_;

	private int current = 0;
	private int last;
	private Uri image_uri;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_view);

		// receive Information from HTTP classes
		recBun = getIntent().getExtras();
		// Locations should be in URI form of where each image is stored on the
		// phone
		loc = recBun.getStringArray("locations");
		// descriptions should be the Description of the photos
		des = recBun.getStringArray("descriptions");

		last = loc.length;
		image_uri = Uri.parse(loc[current]);

		description_ = (TextView) findViewById(R.id.description);
		description_.setText(des[current]);

		like_ = (Button) findViewById(R.id.likeButton);

		like_.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Add call to send like to server

			}

		});
		imageview_ = (ImageView) findViewById(R.id.imageDisplay);
		imageview_.setImageURI(image_uri);

		SimpleOnGestureListener gl = new SimpleOnGestureListener() {

			@Override
			public boolean onFling(MotionEvent start, MotionEvent finish,
					float velocityX, float velocity) {

				// swipe to the right
				if ((finish.getX() - start.getX()) >= sensitivity) {
					// TODO Call next image
					current++;
					if (current < last) {
						description_.setText(des[current]);
						image_uri = Uri.parse(loc[current]);
						imageview_.setImageURI(image_uri);

						return true;

					} else {
						Toast.makeText(ImageViewActivity.this,
								"This is the last image", Toast.LENGTH_SHORT)
								.show();
						return false;
					}

				}
				// swipe to the left
				else if ((start.getX() - finish.getX()) >= sensitivity) {
					// TODO Call next image
					current--;
					if (current < last) {
						description_.setText(des[current]);
						image_uri = Uri.parse(loc[current]);
						imageview_.setImageURI(image_uri);

						return true;

					} else {
						Toast.makeText(ImageViewActivity.this,
								"This is the first image", Toast.LENGTH_SHORT)
								.show();
						return false;
					}

				} else {
					return false;

				}

			}

		};
		imageview_.setOnTouchListener((OnTouchListener) gl);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.image_view, menu);
		return true;
	}

}
