package com.zonesnap.adapters;

import java.util.ArrayList;

import com.zonesnap.classes.ZoneSnap_App;
import com.zonesnap.networking.NetworkPostLike;
import com.zonesnap.zonesnap_app.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

// This is the adapter for the full screen image view
public class FullScreenImageAdapter extends PagerAdapter {

	private Activity _activity;
	private ArrayList<Integer> photoIDs;
	private LayoutInflater inflater;

	// constructor
	public FullScreenImageAdapter(Activity activity, ArrayList<Integer> photoID) {
		this._activity = activity;
		this.photoIDs = photoID;
	}

	@Override
	public int getCount() {
		return this.photoIDs.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == ((RelativeLayout) object);
	}

	@Override
	public Object instantiateItem(ViewGroup container, final int position) {
		ImageView imgDisplay;
		final ImageView imgLiked;
		TextView desc, likesTitle;
		final TextView likes;
		Button btnClose;
		final Button btnLike;

		inflater = (LayoutInflater) _activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View viewLayout = inflater.inflate(R.layout.layout_fullscreen_image,
				container, false);

		// Get the GUI variables
		imgDisplay = (ImageView) viewLayout.findViewById(R.id.imgDisplay);
		btnClose = (Button) viewLayout.findViewById(R.id.btnClose);
		btnLike = (Button) viewLayout.findViewById(R.id.image_btnLike);
		desc = (TextView) viewLayout.findViewById(R.id.image_desc);
		likes = (TextView) viewLayout.findViewById(R.id.image_likesNum);
		likesTitle = (TextView) viewLayout.findViewById(R.id.image_likesTitle);
		imgLiked = (ImageView) viewLayout.findViewById(R.id.image_liked);

		// Set Font of text Views
		Typeface zsFont = Typeface.createFromAsset(_activity.getAssets(),
				"fonts/Orbitron-Regular.ttf");
		desc.setTypeface(zsFont);
		likes.setTypeface(zsFont);
		likesTitle.setTypeface(zsFont);

		// Set the image
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		imgDisplay.setImageBitmap(ZoneSnap_App.imageCache.get(photoIDs
				.get(position)));

		desc.setText(ZoneSnap_App.descCache.get(photoIDs.get(position)));
		likes.setText(String.valueOf(ZoneSnap_App.likeCache.get(photoIDs
				.get(position))));

		// See if photo has been liked
		if (ZoneSnap_App.likedList.contains(photoIDs.get(position))) {
			imgLiked.setVisibility(View.VISIBLE);
			btnLike.setEnabled(false);
			btnLike.setText("Liked");
		}

		// close button click event
		btnClose.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				_activity.finish();
			}
		});

		btnLike.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// Network task to like photo
				NetworkPostLike likeTask = new NetworkPostLike(_activity);
				likeTask.execute(ZoneSnap_App.user.getUsername(),
						String.valueOf(photoIDs.get(position)));

				int prevLikes = ZoneSnap_App.likeCache.get(photoIDs
						.get(position));
				imgLiked.setVisibility(View.VISIBLE);
				btnLike.setEnabled(false);
				btnLike.setText("Liked");
				likes.setText(String.valueOf(prevLikes + 1));
			}
		});

		((ViewPager) container).addView(viewLayout);

		return viewLayout;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		((ViewPager) container).removeView((RelativeLayout) object);

	}
}