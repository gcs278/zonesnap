package com.zonesnap.activities;

import java.util.ArrayList;


import android.R;
import android.content.Context;
import android.widget.ArrayAdapter;

public class StackAdapter extends ArrayAdapter<StackItem> {
	
	Context context; 
    final static int layoutResourceId = R.layout.stack_item;    
    ArrayList<StackItem> data = null;
	
	public StackAdapter(Context c, ArrayList<StackItem> data){
		super(c, layoutResourceId, data);
		this.context = c;
		this.data = data;
		
	}
}
