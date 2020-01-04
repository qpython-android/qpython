package org.qpython.qpy.texteditor.common;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import org.qpython.qpy.BuildConfig;

import java.io.File;
import java.util.ArrayList;

/**
 * Storage for a recent files list
 * 
 * TODO code review
 */
public class RecentFiles implements Constants {

	/** the list of paths in the recent list */
	private static ArrayList<String> PATHS;

	/**
	 * loads the recent files from shared preferences
	 *
	 * @param saved
	 *            the previously saved string
	 */
	public static void loadRecentFiles(String saved) {
		PATHS = new ArrayList<>();
		String[] paths = saved.split(File.pathSeparator);
		for (String path : paths) {
			if (path.length() > 0) {
				PATHS.add(path);
			}
			if (PATHS.size() == Settings.MAX_RECENT_FILES) {
				break;
			}
		}
	}

	/**
	 * Saves the preferences when they have been edited
	 *
	 * @param prefs
	 *            the preferences to save to
	 */
	public static void saveRecentList(SharedPreferences prefs) {
		String str = "";
		Editor editor;

		for (String path : PATHS) {
			str += path;
			str += File.pathSeparator;
		}

		editor = prefs.edit();
		editor.putString(PREFERENCE_RECENTS, str);
		editor.commit();
	}

	/**
	 * @return the list of most recent files
	 */
	public static ArrayList<String> getRecentFiles() {
		return PATHS;
	}

	/**
	 * Updates the recent list with a path. If the path is already in the list,
	 * bring it back to top, else add it.
	 *
	 * @param path
	 *            the path to insert
	 */
	public static void updateRecentList(String path) {
		if (PATHS.contains(path)) {
			PATHS.remove(path);
		}

		PATHS.add(0, path);
		while (PATHS.size() > Settings.MAX_RECENT_FILES) {
			PATHS.remove(Settings.MAX_RECENT_FILES);
		}
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "added path to recent files : " + path);
		}
	}

	/**
	 * Removes a path from the recent files list
	 *
	 * @param path
	 *            the path to remove
	 */
	public static void removePath(String path) {
		if (PATHS.contains(path)) {
			PATHS.remove(path);
		}
	}
}
