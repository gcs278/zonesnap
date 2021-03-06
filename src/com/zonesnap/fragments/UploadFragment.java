package com.zonesnap.fragments;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zonesnap.classes.ZoneSnap_App;
import com.zonesnap.networking.NetworkPostPicture;
import com.zonesnap.zonesnap_app.R;

// fragment for camera
@SuppressLint("ValidFragment")
public class UploadFragment extends Fragment {
	public static final String ARG_SECTION_NUMBER = "section_number";
	private static final int CAMERA_REQUEST = 1888;

	// GUI Variables
	ImageButton camerabtn;
	Button uploadbtn;
	Button clearbtn;
	ImageView imageView;
	EditText editTitle;

	// Upload Variables
	boolean imgTaken;
	String image64;
	Bitmap image;
	Button notifybtn;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_upload, container,
				false);

		// if an image is taken, set the ui components to display it
		if (imgTaken) {
			imageView.setVisibility(View.VISIBLE);
			imageView.setImageBitmap(image);
			camerabtn.setVisibility(View.GONE);
		}
		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		// Set Fonts
		Typeface zsLogo = Typeface.createFromAsset(getActivity().getAssets(),
				"fonts/capella.ttf");
		Typeface zsFont = Typeface.createFromAsset(getActivity().getAssets(),
				"fonts/Orbitron-Regular.ttf");
		TextView title = (TextView) getView().findViewById(R.id.upload_title);
		title.setTypeface(zsLogo);
		TextView description = (TextView) getView().findViewById(
				R.id.upload_titleLabel);
		description.setTypeface(zsFont);

		imgTaken = false;
		editTitle = (EditText) getView().findViewById(R.id.titleEdit);

		// image view set to invis at first
		imageView = (ImageView) getView().findViewById(R.id.uploadImg);
		imageView.setVisibility(View.GONE);

		// camera button
		camerabtn = (ImageButton) getView().findViewById(R.id.cameraBtn);
		camerabtn.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				Intent cameraIntent = new Intent(
						android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(cameraIntent, CAMERA_REQUEST);
			}
		});

		// clear button
		clearbtn = (Button) getView().findViewById(R.id.clearBtn);
		clearbtn.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				uploadClear();
			}
		});

		// upload button
		uploadbtn = (Button) getView().findViewById(R.id.uploadBtn);
		uploadbtn.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				String title = editTitle.getText().toString();
				
				// make sure that the user has taken an picture and entered a
				// title
				if (imgTaken && title != "") {
					NetworkPostPicture task = new NetworkPostPicture(
							getActivity());
					task.execute(title, image64,
							ZoneSnap_App.user.getUsername());
					uploadClear();
				} else
					Toast.makeText(getActivity(),
							"You must first take a photo and enter a title.",
							Toast.LENGTH_SHORT).show();
			}
		});
		
		editTitle.setText("");
	}

	// resets the fragment
	public void uploadClear() {
		imgTaken = false;
		imageView.setVisibility(View.GONE);
		camerabtn.setVisibility(View.VISIBLE);
		editTitle.setText("");
	}

	// Called when the camera activities respond when finished
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		getActivity();
		// camera
		if (requestCode == CAMERA_REQUEST
				&& resultCode == Activity.RESULT_OK) {
			
			// Get Image and conver to bytes
			image = (Bitmap) data.getExtras().get("data");
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
			byte[] bytes = stream.toByteArray();
			bytes = Base64.encodeBase64(bytes);

			// Convert to base64 string
			try {
				image64 = new String(bytes, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

			// reset variables tracking progress
			Toast.makeText(getActivity(), "photo taken!", Toast.LENGTH_SHORT)
					.show();
			imgTaken = true;
			
			// Set Image
			imageView.setVisibility(View.VISIBLE);
			imageView.setImageBitmap(image);
			camerabtn.setVisibility(View.GONE);

		}

	}
}