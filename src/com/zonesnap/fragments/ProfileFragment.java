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
	
	//private ProfilePictureView profilePictureView;// = (ProfilePictureView) getView().findViewById(R.id.profilePicture);
	
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
		TextView title = (TextView) getView().findViewById(R.id.profile_title);
		title.setTypeface(zsLogo);
		
		TextView userId = (TextView) getView().findViewById(R.id.userId);
		userId.setText(ZoneSnap_App.user.getName());
		
		//ZoneSnap_App.profilePic.setProfileId(ZoneSnap_App.user.getId());
		
		//profilePictureView = ZoneSnap_App.profilePic;
		
		
	}
}