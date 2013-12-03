package com.zonesnap.classes;

import java.util.Random;

import com.zonesnap.fragments.CurrentFragment.NetworkGetCurrentPictureList;
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

public class ZoneSnap_WidgetProvider extends AppWidgetProvider {

	@Override
	public void onReceive(Context context, Intent intent) {
		System.out.println("Widget Update");

		Bundle b = intent.getExtras();
		try {
			Location loc = (Location) b.get("Location");
			int zone = b.getInt("Zone");
			System.out.println(zone);
			RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
					R.layout.widget_layout);
			remoteViews.setTextViewText(R.id.widgetLabel,
					"Zone: " + String.valueOf(zone));

			remoteViews.setTextViewText(R.id.widgetLabel,
					"Zone: " + String.valueOf(zone));
		} catch (NullPointerException e) {
			//
		}

		super.onReceive(context, intent);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {

		// Get all ids
		ComponentName thisWidget = new ComponentName(context,
				ZoneSnap_WidgetProvider.class);
		int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
		for (int widgetId : allWidgetIds) {
			// create some random data
			System.out.println("Widget Update");

			// Register an onClickListener
			Intent intent = new Intent(context, ZoneSnap_WidgetProvider.class);
			intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
			intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);

			Bundle b = intent.getExtras();
			Location loc = (Location) b.get("Location");
			int zone = b.getInt("Zone");
			System.out.println(zone);

			RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
					R.layout.widget_layout);
			remoteViews.setTextViewText(R.id.widgetLabel,
					"Zone: " + String.valueOf(zone));

			remoteViews.setTextViewText(R.id.widgetLabel,
					"Zone: " + String.valueOf(zone));

		}
	}

}
