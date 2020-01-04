package org.qpython.qpy.texteditor.androidlib.common;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

import org.qpython.qpy.R;


/**
 *  
 */
public abstract class ChangeLog {

	private static final String PREFERENCE_LAST_VERSION = "previous_version";
	private int mCurrentVersion = -1;

	public boolean displayChangeLog(Context context, SharedPreferences prefs) {

		boolean firstLaunch = false;

		if (isFirstLaunch(context, prefs)) {
			firstLaunch = true;

			Builder builder = new Builder(context);

			String message = context.getString(getTitleResource(context))
					+ "\n\n" + context.getString(getChangeLogResource(context));
			builder.setTitle(R.string.ui_whats_new);
			builder.setMessage(message);
			builder.setCancelable(true);
			builder.setPositiveButton(android.R.string.ok,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});

			builder.create().show();

		}

		saveCurrentVersion(context, prefs);

		return firstLaunch;
	}

	/**
	 * @param version
	 *            the version code of the application
	 * @return the string resource for the title to display in the change log
	 */
	public abstract int getTitleResourceForVersion(int version);

	/**
	 * @param context
	 *            the current application context
	 * @return the string resource for the title to display in the change log
	 */
	public int getTitleResource(Context context) {
		int version = getCurrentVersion(context);
		return getTitleResourceForVersion(version);
	}

	/**
	 * @param version
	 *            the version code of the application
	 * @return the string resource for the changelog to display in the change
	 *         log
	 */
	public abstract int getChangeLogResourceForVersion(int version);

	/**
	 * @param context
	 *            the current application context
	 * @return the string resource for the changelog to display in the change
	 *         log
	 */
	public int getChangeLogResource(Context context) {
		int version = getCurrentVersion(context);
		return getChangeLogResourceForVersion(version);
	}

	/**
	 * Warning, this automatically saves the current version, so you only get
	 * one shot. Then again, I guess that's the idea...
	 *
	 * @param context
	 *            the current application context
	 * @param prefs
	 *            the shared preferences to use
	 * @return if this is the first launch since last update
	 */
	public boolean isFirstLaunch(Context context, SharedPreferences prefs) {
		int previous, current;

		previous = getPreviousVersion(context, prefs);
		current = getCurrentVersion(context);

		return (previous < current);
	}

	/**
	 * @param context
	 *            the current application context
	 * @param prefs
	 *            the shared preferences to use
	 * @return the previously opened version of the application
	 */
	protected int getPreviousVersion(Context context, SharedPreferences prefs) {

		int version = prefs.getInt(PREFERENCE_LAST_VERSION, -1);

		return version;
	}

	/**
	 * @param context
	 *            the current application context
	 * @return the current installed application version
	 */
	protected int getCurrentVersion(Context context) {
		int version = mCurrentVersion;

		if (version < 0) {
			try {
				version = context.getPackageManager().getPackageInfo(
						context.getPackageName(), 0).versionCode;
			} catch (NameNotFoundException e) {
				Log.e("Felix", "Unable to get package info for package name "
						+ context.getPackageName());
				version = -1;
			}
		}

		if (version >= 0) {
			mCurrentVersion = version;
		}

		return version;
	}

	/**
	 * Saves the current version for next launch
	 *
	 * @param context
	 *            the current application context
	 * @param prefs
	 *            the shared preferences for this app
	 */
	public void saveCurrentVersion(Context context, SharedPreferences prefs) {
		Editor editor = prefs.edit();

		editor.putInt(PREFERENCE_LAST_VERSION, getCurrentVersion(context));

		editor.commit();
	}
}
