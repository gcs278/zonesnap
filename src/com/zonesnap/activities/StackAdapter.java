package com.zonesnap.activities;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zonesnap.zonesnap_app.R;

public class StackAdapter extends ArrayAdapter<StackItem> {

	Context context;
	final static int layoutResourceId = R.layout.stack_item;
	ArrayList<StackItem> data = null;

	public StackAdapter(Context c, ArrayList<StackItem> data) {
		super(c, layoutResourceId, data);
		this.context = c;
		this.data = data;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		StackHolder holder = null;

		if (row == null) {
			LayoutInflater inflater = (LayoutInflater) (context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
			row = inflater.inflate(layoutResourceId, parent, false);

			holder = new StackHolder();
			holder.img = (ImageView) row.findViewById(R.id.stackImage);
			holder.text = (TextView) row.findViewById(R.id.stackText);

			row.setTag(holder);
		} else {
			holder = (StackHolder) row.getTag();
		}

		StackItem item = data.get(position);

		holder.img.setImageBitmap(item.getImg());
		holder.text.setText(item.getText());

		return row;
	}

	private class StackHolder {
		ImageView img;
		TextView text;
	}
}