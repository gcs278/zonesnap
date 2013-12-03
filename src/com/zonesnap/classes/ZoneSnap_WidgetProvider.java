package com.zonesnap.classes;

import java.util.Random;

import com.zonesnap.zonesnap_app.R;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.RemoteViews;
import android.widget.Toast;
import android.widget.ToggleButton;

public class ZoneSnap_WidgetProvider extends AppWidgetProvider{

	@Override
	  public void onUpdate(Context context, AppWidgetManager appWidgetManager,
	      int[] appWidgetIds) {

	    // Get all ids
	    ComponentName thisWidget = new ComponentName(context,
	        ZoneSnap_WidgetProvider.class);
	    int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
	    for (int widgetId : allWidgetIds) {
	      // create some random data
	      int number = (new Random().nextInt(100));

	      final RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
	          R.layout.widget_layout);
	      
	      // Register an onClickListener
	      Intent intent = new Intent(context, ZoneSnap_WidgetProvider.class);
	      intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
	      intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
	      
	      // handler for received Intents for the "my-event" event
		  	BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
		  		@Override
		  		public void onReceive(Context context, Intent intent) {
		  			// Extract data included in the Intent
		  			Bundle b = intent.getExtras();
		  			Location loc = (Location) b.get("Location");
		  			int zone = b.getInt("Zone");
		  			
		  			remoteViews.setTextViewText(R.id.widgetLabel,  "Zone: " + String.valueOf(zone));
		  			
		  		}
		  	};
	      
	      LocalBroadcastManager.getInstance(context.getApplicationContext()).registerReceiver(
					mMessageReceiver, new IntentFilter(LocationService.BROADCAST));

	  
	  	
	      PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
	          0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
	      remoteViews.setOnClickPendingIntent(R.id.widgetLabel, pendingIntent);
	      appWidgetManager.updateAppWidget(widgetId, remoteViews);
	    }
	  }
	 
	} 
