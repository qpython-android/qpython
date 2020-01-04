package org.qpython.qpy.texteditor.undo;

import android.text.Editable;
import android.util.Log;

import org.qpython.qpy.BuildConfig;
import org.qpython.qpy.texteditor.common.Constants;
import org.qpython.qpy.texteditor.common.Settings;

import java.util.Stack;

public class TextChangeWatcher implements Constants {

    private final Stack<TextChange> history;
    private final Stack<TextChange> future;
    private boolean isRedo = false;

    private TextChange mCurrentChange;

    /**
     *
     */
    public TextChangeWatcher() {
        history = new Stack<>();
        future = new Stack<>();
    }

    /**
     * Undo the last operation
     *
     * @param text the text to undo on
     * @return the caret position
     */
    public int undo(Editable text) {
        pushCurrentChange();

        if (history.size() == 0) {
            if (BuildConfig.DEBUG)
                Log.i(TAG, "Nothing to undo");
            return -1;
        }

        TextChange change = history.pop();
        future.push(change);
        if (change != null)
            return change.undo(text);
        else if (BuildConfig.DEBUG)
            Log.w(TAG, "Null change ?!");

        return -1;
    }

    public int redo(Editable text) {
        isRedo = true;
        if (future.size() == 0) return -1;

        TextChange change = future.pop();
        if (change != null) {
            return change.redo(text);
        } else {
            return -1;
        }
    }

    /**
     * A change to the text {@param s} will be made, where the
     * {@param count} characters starting at {@param start} will be
     * replaced by {@param after} characters
     *
     * @param s     the sequence being changed
     * @param start the start index
     * @param count the number of characters that will change
     * @param after the number of characters that will replace the old ones
     */
    public void beforeChange(CharSequence s, int start, int count, int after) {
        if ((mCurrentChange != null)
                && (mCurrentChange.canMergeChangeBefore(s, start, count, after))) {
        } else {
            if (count == 0) {
                // no existing character changed
                // ignore, will be processed after
            } else if (after == 0) {
                // existing character replaced by none => delete
                processDelete(s, start, count);
            } else {
                // n chars replaced by m other chars => replace
                // replace is a delete AND an insert...
                processDelete(s, start, count);
            }
        }
    }

    /**
     * A change to the text {@param s} has been made, where the
     * {@param count} characters starting at {@param start} have
     * replaced the substring of length {@param before}
     *
     * @param s      the sequence being changed
     * @param start  the start index
     * @param before the number of character that were replaced
     * @param count  the number of characters that will change
     */
    public void afterChange(CharSequence s, int start, int before, int count) {
        if ((mCurrentChange != null)
                && (mCurrentChange.canMergeChangeAfter(s, start, before, count))) {

        } else {
            if (before == 0) {
                // 0 charactes replaced by count => insert
                processInsert(s, start, count);
            } else if (count == 0) {
                // existing character replaced by none => delete, already done
                // before
            } else {
                // n chars replaced by m other chars => replace
                // replace is a delete AND an insert...
                processInsert(s, start, count);
            }
        }
        if (!isRedo) {
            future.clear();
        } else {
            isRedo = false;
        }
        // printStack();
    }

    /**
     * @param s     the sequence being modified
     * @param start the first character index
     * @param count the number of inserted text
     */
    public void processInsert(CharSequence s, int start, int count) {
        CharSequence sub = s.subSequence(start, start + count);

        if (mCurrentChange != null)
            pushCurrentChange();

        mCurrentChange = new TextChangeInsert(sub, start);
    }

    /**
     * @param s     the sequence being modified
     * @param start the first character index
     * @param count the number of inserted text
     */
    public void processDelete(CharSequence s, int start, int count) {
        CharSequence sub = s.subSequence(start, start + count);

        if (mCurrentChange != null)
            pushCurrentChange();

        mCurrentChange = new TextChangeDelete(sub, start);
    }

    /**
     * Pushes the current change on top of the stack
     */
    protected void pushCurrentChange() {
        if (mCurrentChange == null)
            return;

        history.push(mCurrentChange);
        while (history.size() > Settings.UNDO_MAX_STACK) {
            history.remove(0);
        }
        mCurrentChange = null;
    }

    /**
     * Prints the current stack
     */
    public void printStack() {
        if (!BuildConfig.DEBUG)
            return;
        Log.i(TAG, "STACK");
        for (TextChange change : history) {
            Log.d(TAG, change.toString());
        }
        Log.d(TAG, "Current change : " + mCurrentChange.toString());
    }

}
