package org.qpython.qpy.texteditor.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class WidgetPrefs {

	public final static String WIDGET_PREFERENCES = "fr.xgouchet.texteditor.widget";

	public final static String WIDGET_TARGET_PATH = "target_path_";

	public final static String WIDGET_READ_ONLY = "read_only_";

	public final static String WIDGET_PRESENT_KEY = "widget_present_";

	public String mTargetPath;
	public boolean mReadOnly;

	public WidgetPrefs() {
		mTargetPath = "";
	}

	/**
	 * Delete the data associated with a widget ID
	 *
	 * @param context
	 *            the current context
	 * @param widgetId
	 *            the id to delete
	 */
	public static void delete(Context context, int widgetId) {
		String key;
		SharedPreferences prefs;
		Editor edit;

		prefs = context.getSharedPreferences(WIDGET_PREFERENCES,
				Context.MODE_PRIVATE);
		if (prefs != null) {
			edit = prefs.edit();

			if (edit != null) {
				key = WIDGET_TARGET_PATH + String.valueOf(widgetId);
				edit.remove(key);

				key = WIDGET_READ_ONLY + String.valueOf(widgetId);
				edit.remove(key);

				key = WIDGET_PRESENT_KEY + String.valueOf(widgetId);
				edit.remove(key);

				edit.commit();
			}
		}
	}

	/**
	 * Loads the widget prefs from the shared preferences
	 *
	 * @param context
	 *            the current context
	 * @param widgetId
	 *            this widget id
	 * @return if the widget is still present
	 */
	public boolean load(Context context, int widgetId) {
		String key;
		SharedPreferences prefs;

		prefs = context.getSharedPreferences(WIDGET_PREFERENCES,
				Context.MODE_PRIVATE);

		if (prefs != null) {
			key = WIDGET_TARGET_PATH + String.valueOf(widgetId);
			mTargetPath = prefs.getString(key, "");

			key = WIDGET_READ_ONLY + String.valueOf(widgetId);
			mReadOnly = prefs.getBoolean(key, false);

			key = WIDGET_PRESENT_KEY + String.valueOf(widgetId);
			return prefs.getBoolean(key, false);
		}

		return false;
	}

	/**
	 * Store this widget prefs in the shared preferences
	 *
	 * @param context
	 *            the current context
	 * @param widgetId
	 *            this widget id
	 */
	public void store(Context context, int widgetId) {
		String key;
		SharedPreferences prefs;
		Editor edit;

		prefs = context.getSharedPreferences(WIDGET_PREFERENCES,
				Context.MODE_PRIVATE);

		if (prefs != null) {
			edit = prefs.edit();

			if (edit != null) {
				key = WIDGET_TARGET_PATH + String.valueOf(widgetId);
				edit.putString(key, mTargetPath);

				key = WIDGET_READ_ONLY + String.valueOf(widgetId);
				edit.putBoolean(key, mReadOnly);

				key = WIDGET_PRESENT_KEY + String.valueOf(widgetId);
				edit.putBoolean(key, true);

				edit.commit();
			}
		}
	}
}
