package org.qpython.qpy.texteditor.common;

import android.content.Context;
import android.util.Log;

import org.qpython.qpy.BuildConfig;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Misc file utilities
 * 
 * TODO code review
 * 
 */
public class TextFileUtils implements Constants {

	/**
	 * @param path
	 *            the absolute path to the file to save
	 * @param text
	 *            the text to write
	 * @return if the file was saved successfully
	 */
	public static boolean writeTextFile(String path, String text) {
		File file = new File(path);
		OutputStreamWriter writer;
		BufferedWriter out;
		String eol_text = text;
		try {
			if (Settings.END_OF_LINE != EOL_LINUX) {
				eol_text = eol_text.replaceAll("\n", Settings.getEndOfLine());
			}
			writer = new OutputStreamWriter(new FileOutputStream(file),
					Settings.ENCODING);
			out = new BufferedWriter(writer);
			out.write(eol_text);
			out.close();
		} catch (OutOfMemoryError e) {
			Log.w(TAG, "Out of memory error", e);
			return false;
		} catch (IOException e) {
			Log.w(TAG, "Can't write to file " + path, e);
			return false;
		}
		return true;
	}

	/**
	 * @param file
	 *            the file to read
	 * @return the content of the file as text
	 */
	public static String readTextFile(File file) {
		InputStreamReader reader;
		BufferedReader in;
		StringBuffer text = new StringBuffer();
		int c;
		try {
			reader = new InputStreamReader(new FileInputStream(file),
					Settings.ENCODING);
			in = new BufferedReader(reader);
			do {
				c = in.read();
				if (c != -1) {
					text.append((char) c);
				}
			} while (c != -1);
			in.close();
		} catch (IOException e) {
			Log.w(TAG, "Can't read file " + file.getName(), e);
			return null;
		} catch (OutOfMemoryError e) {
			Log.w(TAG, "File is to big to read", e);
			return null;
		}

		// detect end of lines
		String content = text.toString();
		int windows = content.indexOf("\r\n");
		int macos = content.indexOf("\r");

		if (windows != -1) {
			Settings.END_OF_LINE = EOL_WINDOWS;
			content = content.replaceAll("\r\n", "\n");
		} else {
			if (macos != -1) {
				Settings.END_OF_LINE = EOL_MAC;
				content = content.replaceAll("\r", "\n");
			} else {
				Settings.END_OF_LINE = EOL_LINUX;
			}
		}

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Using End of Line : " + Settings.END_OF_LINE);
		}
		return content;
	}

	/**
	 * @param context
	 *            the current context
	 * @param text
	 *            the text to write
	 * @return if the file was saved successfully
	 */
	public static boolean writeInternal(Context context, String text) {
		FileOutputStream fos;
		try {
			fos = context
					.openFileOutput(BACKUP_FILE_NAME, Context.MODE_PRIVATE);
			fos.write(text.getBytes());
			fos.close();
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Saved to file " + context.getFilesDir().getPath()
						+ File.separator + BACKUP_FILE_NAME);
			}
		} catch (FileNotFoundException e) {
			Log.w(TAG, "Couldn't write to internal storage ", e);
			return false;
		} catch (IOException e) {
			Log.w(TAG, "Error occured while writing to internal storage ", e);
			return false;
		}
		return true;
	}

	/**
	 * @param context
	 *            the current context
	 * @return the content of the file as text
	 */
	public static String readInternal(Context context) {
		FileInputStream fis;
		StringBuffer text = new StringBuffer();

		try {
			fis = context.openFileInput(BACKUP_FILE_NAME);
			while (fis.available() > 0) {
				text.append((char) fis.read());
			}
		} catch (FileNotFoundException e) {
			Log.w(TAG, "No backup file available", e);
			return null;
		} catch (IOException e) {
			Log.w(TAG, "Can't read backup file ", e);
			return null;
		}
		return text.toString();
	}

	/**
	 * @param context
	 *            the current context
	 */
	public static void clearInternal(Context context) {
		writeInternal(context, "");
	}
}
