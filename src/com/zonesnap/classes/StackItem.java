package com.zonesnap.classes;

import android.graphics.Bitmap;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.zonesnap.zonesnap_app.R;

public class StackItem {
	
	private Bitmap img_;
	private String text_;
	private Long id_;
	
	public Bitmap getImg(){
		return img_;
	}
	
	public String getText(){
		return text_;
	}
	
	public Long getId(){
		return id_;
	}
	
	public void setImg( Bitmap b){
		this.img_ = b;
	}
	
	public void setText( String t){
		this.text_ = t;
	}
	
	public void setId( Long i){
		this.id_ = i;
	}
	
	public StackItem(Bitmap map, String des, Long id){
		this.img_ = map;
		this.text_ = des;
		this.id_ = id;
		
	}
	public StackItem(Bitmap map, String des){
		this.img_ = map;
		this.text_ = des;
	}
	

}
