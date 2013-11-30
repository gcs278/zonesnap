package com.zonesnap.activities;

import com.zonesnap.networking.post.NetworkPostTracking;

import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.facebook.widget.ProfilePictureView;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.sax.RootElement;
import android.support.v4.util.LruCache;

public class ZoneSnap_App extends Application {
	//////////////////// GLOBAL CACHES ///////////////////////////////
	
	// Cache for storing pictures from network
	public static LruCache<Integer, Bitmap> imageCache; 
	
	/////////////////// SERVER INFORMATION ///////////////////////////
	
	public static int PORT = 8080;
	public static String URL = "www.grantspence.com";
	
	////////////////// CONSTANTS /////////////////////////////////////
	
	public static String CURRENT = "current";
	public static String LIKED = "liked";
	
	///////////////// FACEBOOK VARS //////////////////////////////////
	
	public static ProfilePictureView profilePic;
    public static GraphUser user;
    
    // Construtor
	public ZoneSnap_App() {
		// Get max available VM memory, exceeding this amount will throw an
		// OutOfMemory exception. Stored in kilobytes as LruCache takes an
		// int in its constructor.
		final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

		// Use 1/8th of the available memory for this memory cache.
		final int cacheSize = maxMemory / 8;

		imageCache = new LruCache<Integer, Bitmap>(cacheSize) {
			@Override
			protected int sizeOf(Integer key, Bitmap bitmap) {
				// The cache size will be measured in kilobytes rather than
				// number of items.
				return bitmap.getByteCount() / 1024;
			}

		};
		
	}
}
