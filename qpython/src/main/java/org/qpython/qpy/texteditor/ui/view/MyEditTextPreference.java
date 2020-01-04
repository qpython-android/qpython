package org.qpython.qpy.texteditor.ui.view;

import android.support.v7.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Hmei on 2017-06-14.
 */

public class MyEditTextPreference extends EditTextPreference {
    public MyEditTextPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MyEditTextPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyEditTextPreference(Context context) {
        super(context);
    }

    @Override
    protected void showDialog(Bundle state) {

        super.showDialog(state);
    }
}
