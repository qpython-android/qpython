package org.qpython.qpy.texteditor.common;

import java.io.File;

import android.content.Context;

public class FontUtils {

	/**
	 * @param ctx
	 *            the current application context
	 * @return the app folder for fonts
	 */
	public static File getAppFontFolder(Context ctx) {
		return ctx.getDir(Constants.FONT_FOLDER_NAME, Context.MODE_PRIVATE);
	}
}
