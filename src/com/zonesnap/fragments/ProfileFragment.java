package com.zonesnap.fragments;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.widget.ProfilePictureView;
import com.zonesnap.activities.ZoneSnap_App;
import com.zonesnap.zonesnap_app.R;

//Fragment for user's profile
public class ProfileFragment extends Fragment {
	
	private ProfilePictureView profilePictureView;// = (ProfilePictureView) getView().findViewById(R.id.profilePicture);
	
	public static final String ARG_SECTION_NUMBER = "section_number";

	public ProfileFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_profile, container,
				false);

		return rootView;
	}


	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		// THIS WILL BE WHERE YOU SET UP THE PROFILE DATE
		// i.e. load profile pic
		// Set Fonts
		Typeface zsLogo = Typeface.createFromAsset(getActivity().getAssets(),
				"fonts/capella.ttf");
		Typeface zsFont = Typeface.createFromAsset(getActivity().getAssets(),
				"fonts/Orbitron-Regular.ttf");
		TextView title = (TextView) getView().findViewById(R.id.profile_title);
		title.setTypeface(zsLogo);
		TextView userId = (TextView) getView().findViewById(R.id.profile_userId);
		userId.setTypeface(zsFont);
		userId.setText(ZoneSnap_App.user.getUsername());
		profilePictureView = (ProfilePictureView) getActivity().findViewById(R.id.profilePicture);
		profilePictureView.setProfileId(ZoneSnap_App.user.getId());

	}
}