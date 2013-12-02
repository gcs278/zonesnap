package com.zonesnap.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.zonesnap.classes.StackItem;
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
			holder.like = (Button) row.findViewById(R.id.likeButton);
			
			Typeface zsFont = Typeface.createFromAsset(context.getAssets(),
					"fonts/Orbitron-Regular.ttf");
			holder.text.setTypeface(zsFont);


			row.setTag(holder);
		} else {
			holder = (StackHolder) row.getTag();
		}

		StackItem item = data.get(position);

		holder.img.setImageBitmap(item.getImg());
		holder.text.setText(item.getText());
		holder.text.setAlpha(0);
		
		holder.like.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				
				
				Button l = (Button) v.findViewById(R.id.likeButton);
				l.setText("Liked");
				l.setClickable(false);
				
				
			}
			
		});
		
		row.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				
				if(arg1.getDownTime() >= 1000){
					for (int i = 0; i <= 100; i++) {
						arg0.findViewById(R.id.stackText).setAlpha(i);
					}
				}
				else if(arg1.getActionMasked() == MotionEvent.ACTION_UP){
					for (int i = 0; i <= 100; i++) {
						arg0.findViewById(R.id.stackText).setAlpha(100 - i);
					}
				}
				else{
					arg0.findViewById(R.id.stackText).setAlpha(0);
				}
				
				
				return false;
			}
			
		});

		return row;
	}
	
	private class StackHolder {
		ImageView img;
		TextView text;
		Button like;
	
	}
}
