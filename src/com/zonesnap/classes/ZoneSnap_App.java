package com.zonesnap.classes;

import java.util.ArrayList;

import android.app.Application;
import com.facebook.model.GraphUser;
import com.google.android.gms.maps.GoogleMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

public class ZoneSnap_App extends Application {
	private static Context s_instance;
	
	//////////////////// GLOBAL CACHES ///////////////////////////////
	
	// Cache for storing pictures from network
	public static LruCache<Integer, Bitmap> imageCache; 
	
	// Description cache for picture's descriptions
	public static LruCache<Integer, String> descCache;
	
	// How many likes a picture has
	public static LruCache<Integer, Integer> likeCache;
	
	// User responsible for the picture
	public static LruCache<Integer, String> userCache;
	
	/////////////////// SERVER INFORMATION ///////////////////////////
	
	public static int PORT = 8080;
	public static String URL = "www.grantspence.com";
	
	////////////////// CONSTANTS /////////////////////////////////////
	
	public static String CURRENT = "current";
	public static String LIKED = "liked";
	public static int MAP_TYPE = GoogleMap.MAP_TYPE_HYBRID;
	public static int GPS_MIN_DISTANCE = 3;
	public static int zone = 0;
	
	///////////////// FACEBOOK VARS //////////////////////////////////
	
    public static GraphUser user;

    public static ArrayList<Integer> likedList;
	
    // Constructor
	public ZoneSnap_App() {
		s_instance = this;
		// Get max available VM memory, exceeding this amount will throw an
		// OutOfMemory exception. Stored in kilobytes as LruCache takes an
		// int in its constructor.
		final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

		// Use 1/8th of the available memory for this memory cache.
		final int cacheSize = maxMemory / 8;

		// Initialize the caches
		imageCache = new LruCache<Integer, Bitmap>(cacheSize) {
			@Override
			protected int sizeOf(Integer key, Bitmap bitmap) {
				// The cache size will be measured in kilobytes rather than
				// number of items.
				return bitmap.getByteCount() / 1024;
			}

		};
		likeCache = new LruCache<Integer, Integer>(cacheSize);
		descCache = new LruCache<Integer, String>(cacheSize);
		userCache = new LruCache<Integer, String>(cacheSize);
		imageCache.evictAll();
		descCache.evictAll();
		likeCache.evictAll();
		
	}
	
	// Port Getter
	public static int getPort() {
		return PORT;
	}
	
	// Port Setter
	public static void setPort(int value) {
		PORT = value;
	}
	
	// Url Getter
	public static String getURL() {
		return URL;
	}
	
	// URL setter
	public static void setURL(String value) {
		URL = value;
	}
	
	// The following is code for getting a resource string
	public static Context getContext(){
        return s_instance;
    }
	
	// To get the string associated with a resource
	public static String getResourceString(int resId){
        return getContext().getString(resId);       
    }
	
	// Unify error message
	public static String getErrorMessage() {
		return "Error: Could not connect. Please check the URL and port settings. Message: ";
	}
}
