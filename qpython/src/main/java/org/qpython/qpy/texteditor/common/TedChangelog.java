package org.qpython.qpy.texteditor.common;

import org.qpython.qpy.R;

public class TedChangelog extends org.qpython.qpy.texteditor.androidlib.common.ChangeLog {

	/**
	 * @see fr.xgouchet.androidlib.common.ChangeLog#getTitleResourceForVersion(int)
	 */
	@Override
	public int getTitleResourceForVersion(int version) {
		int res = 0;
		switch (version) {
		case 18:
		default:
			res = R.string.release18;
		}
		return res;
	}

	/**
	 * @see fr.xgouchet.androidlib.common.ChangeLog#getChangeLogResourceForVersion(int)
	 */
	@Override
	public int getChangeLogResourceForVersion(int version) {
		int res = 0;
		switch (version) {
		case 18:
		default:
			res = R.string.release18_log;
		}
		return res;
	}

}
