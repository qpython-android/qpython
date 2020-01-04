package org.qpython.qpy.texteditor.undo;

import android.text.Editable;
import android.util.Log;

import org.qpython.qpy.BuildConfig;

public class TextChangeDelete implements TextChange {

    private StringBuffer mSequence;
    private int          mStart;

    /**
     * @param seq   the sequence being deleted
     * @param start the start index
     */
    TextChangeDelete(CharSequence seq, int start) {
        mSequence = new StringBuffer();
        mSequence.append(seq);
        mStart = start;
    }

    @Override
    public int undo(Editable s) {
        s.insert(mStart, mSequence);
        return mStart + mSequence.length();
    }

    @Override
    public int redo(Editable text) {
        text.replace(mStart, mStart + mSequence.length(), "");
        return mStart;
    }

    @Override
    public int getCaret() {
        if (mSequence.toString().contains(" "))
            return -1;
        if (mSequence.toString().contains("\n"))
            return -1;
        return mStart;
    }

    @Override
    public void append(CharSequence seq) {
        mSequence.insert(0, seq);
        if (BuildConfig.DEBUG)
            Log.d(TAG, mSequence.toString());
        mStart -= seq.length();
    }

    @Override
    public boolean canMergeChangeBefore(CharSequence s, int start, int count, int after) {
        CharSequence sub;
        if (mSequence.toString().contains(" "))
            return false;
        if (mSequence.toString().contains("\n"))
            return false;
        if ((count != 1) || (start + count != mStart))
            return false;

        sub = s.subSequence(start, start + count);
        append(sub);
        return true;
    }

    @Override
    public boolean canMergeChangeAfter(CharSequence s, int start, int before, int count) {
        return false;
    }

    @Override
    public String toString() {
        return "-\"" + mSequence.toString().replaceAll("\n", "~") + "\" @" + mStart;
    }

}
