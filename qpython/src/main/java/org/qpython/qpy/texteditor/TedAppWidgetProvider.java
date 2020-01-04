package org.qpython.qpy.texteditor;

import java.io.File;

import org.qpython.qpy.R;
import org.qpython.qpy.texteditor.common.Constants;
import org.qpython.qpy.texteditor.common.WidgetPrefs;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;
import org.qpython.qpy.BuildConfig;

public class TedAppWidgetProvider extends AppWidgetProvider implements Constants {

	public static void updateWidget(Context context, AppWidgetManager manager, int widgetId) {
		Intent intent;
		PendingIntent pendingIntent;
		RemoteViews views;
		WidgetPrefs pref;
		File targetFile;

		pref = new WidgetPrefs();
		pref.load(context, widgetId);

		if (BuildConfig.DEBUG)
			Log.i(TAG, "Updating widgetId " + String.valueOf(widgetId));

		targetFile = new File(pref.mTargetPath);

		intent = new Intent(context, TedFragment.class);
		intent.setAction(ACTION_WIDGET_OPEN);
		intent.setData(Uri.fromFile(targetFile));
		intent.putExtra(EXTRA_FORCE_READ_ONLY, pref.mReadOnly);

		pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

		views = new RemoteViews(context.getPackageName(), R.layout.widget);

		views.setOnClickPendingIntent(R.id.widgetLayout, pendingIntent);
		if (pref.mReadOnly)
			views.setImageViewResource(R.id.icon, R.drawable.ic_editor_file);
		else
			views.setImageViewResource(R.id.icon, R.drawable.ic_editor_unknown);
		views.setTextViewText(R.id.textFileName, targetFile.getName());

		manager.updateAppWidget(widgetId, views);
	}

	/**
	 * @see android.appwidget.AppWidgetProvider#onUpdate(android.content.Context,
	 *      android.appwidget.AppWidgetManager, int[])
	 */
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

		AppWidgetManager manager;
		ComponentName widgetName;

		if (BuildConfig.DEBUG)
			Log.i(TAG, "onUpdate widgets");

		manager = AppWidgetManager.getInstance(context);
		widgetName = new ComponentName(context, TedAppWidgetProvider.class);
		int[] widgetIds = manager.getAppWidgetIds(widgetName);

		for (int widgetId : widgetIds) {
			updateWidget(context, manager, widgetId);
		}
	}

	/**
	 * @see android.appwidget.AppWidgetProvider#onDeleted(android.content.Context,
	 *      int[])
	 */
	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {

		for (int widgetId : appWidgetIds) {
			WidgetPrefs.delete(context, widgetId);
		}
	}
}
