package org.qpython.qpy.texteditor.androidlib.common;

import android.content.Context;

/**
 * 
 */
public class UIUtils {

	/**
	 * @param context
	 *            the current application context
	 * @param dp
	 *            the dip value to convert
	 * @return the px value corresponding to the given dip
	 */
	public static int getPxFromDp(Context context, int dp) {
		float scale = context.getResources().getDisplayMetrics().density;

		return ((int) ((dp * scale) + 0.5f));

	}
}
