package com.zonesnap.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.zonesnap.activities.CurrentImageAdapter;
import com.zonesnap.zonesnap_app.R;

//Fragment for the Historic ZoneSnap View
public class HistoryFragment extends Fragment {
	public static final String ARG_SECTION_NUMBER = "section_number";

	public HistoryFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_history, container,
				false);

		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		GridView grid = (GridView) getView().findViewById(R.id.gridHistory);
		grid.setAdapter(new CurrentImageAdapter(getActivity()));

		grid.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				Toast.makeText(getActivity(), "" + position, Toast.LENGTH_SHORT)
						.show();
			}
		});
		// Set font
		Typeface zsFont = Typeface.createFromAsset(getActivity().getAssets(),
				"fonts/Orbitron-Regular.ttf");
		TextView title = (TextView) getView().findViewById(
				R.id.history_likedTitle);
		title.setTypeface(zsFont);
		TextView title2 = (TextView) getView().findViewById(
				R.id.history_pastTitle);
		title2.setTypeface(zsFont);
	}
}