package org.qpython.qpy.texteditor.undo;

import android.text.Editable;
import android.util.Log;

import org.qpython.qpy.BuildConfig;

public class TextChangeInsert implements TextChange {

	private StringBuffer mSequence;
	private int          mStart;

	TextChangeInsert(CharSequence seq, int start) {
		mSequence = new StringBuffer();
		mSequence.append(seq);
		mStart = start;
	}

	@Override
	public int getCaret() {
		if (mSequence.toString().contains(" "))
			return -1;
		if (mSequence.toString().contains("\n"))
			return -1;
		return mStart + mSequence.length();
	}

	@Override
	public void append(CharSequence seq) {
		mSequence.append(seq);
	}

	@Override
	public boolean canMergeChangeBefore(CharSequence s, int start, int count, int after) {

		CharSequence sub;
		boolean append, replace;

		if (mSequence.toString().contains(" "))
			return false;
		if (mSequence.toString().contains("\n"))
			return false;

		sub = s.subSequence(start, start + count);
		append = (start == mStart + mSequence.length());
		replace = (start == mStart) && (after >= mSequence.length())
				&& (sub.toString().startsWith(mSequence.toString()));

		if (append) {
			// mSequence.append(sub);
			return true;
		}

		if (replace) {
			// mSequence = new StringBuffer();
			// mSequence.append(sub);
			return true;
		}
		return false;
	}

	@Override
	public boolean canMergeChangeAfter(CharSequence s, int start, int before, int count) {
		CharSequence sub;
		boolean append, replace;

		if (mSequence.toString().contains(" "))
			return false;
		if (mSequence.toString().contains("\n"))
			return false;

		sub = s.subSequence(start, start + count);
		append = (start == mStart + mSequence.length());
		replace = (start == mStart) && (count >= mSequence.length())
				&& (sub.toString().startsWith(mSequence.toString()));

		if (append) {
			mSequence.append(sub);
			return true;
		}

		if (replace) {
			mSequence = new StringBuffer();
			mSequence.append(sub);
			return true;
		}

		return false;
	}

	@Override
	public int undo(Editable s) {
		if (BuildConfig.DEBUG)
			Log.i(TAG, "Undo Insert : deleting " + mStart + " to " + (mStart + mSequence.length()));
		s.replace(mStart, mStart + mSequence.length(), "");
		return mStart;
	}

	@Override
	public int redo(Editable text) {
		text.insert(mStart, mSequence);
		return mStart + mSequence.length();
	}

	@Override
	public String toString() {
		return "+\"" + mSequence.toString().replaceAll("\n", "~") + "\" @" + mStart;
	}

}
