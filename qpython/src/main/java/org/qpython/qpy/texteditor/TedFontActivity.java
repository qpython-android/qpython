package org.qpython.qpy.texteditor;

import java.io.File;
import java.util.LinkedList;

import org.qpython.qpy.R;
import org.qpython.qpy.texteditor.ui.adapter.FontListAdapter;

import org.qpython.qpy.texteditor.widget.crouton.Crouton;
import org.qpython.qpy.texteditor.widget.crouton.Style;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;

import org.qpython.qpy.texteditor.androidlib.ui.activity.BrowsingActivity;

public class TedFontActivity extends BrowsingActivity implements
        OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_open);
        mExtensionsWhiteList.add("ttf");

        // set default result
        setResult(RESULT_CANCELED, null);

        // buttons
        findViewById(R.id.buttonCancel).setOnClickListener(this);

        mListAdapter = new FontListAdapter(this, new LinkedList<>());
    }

    @Override
    protected void onFileClick(File file) {
        if (setOpenResult(file))
            finish();
    }

    @Override
    protected boolean onFolderClick(File folder) {
        return true;
    }

    @Override
    protected void onFolderViewFilled() {

    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            // navigate to parent folder
            if (!mCurrentFolder.getPath().equals("/storage/emulated/0")) {
                File parent = mCurrentFolder.getParentFile();
                if ((parent != null) && (parent.exists())) {
                    fillFolderView(parent);
                    return true;
                }
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.buttonCancel) {
            setResult(RESULT_CANCELED);
            finish();
        }
    }

    /**
     * @param file the file to return
     * @return if the result was set correctly
     */
    protected boolean setOpenResult(File file) {
        Intent result;

        if (!file.canRead()) {
            Crouton.showText(this, R.string.toast_file_cant_read, Style.ALERT);
            return false;
        }

        result = new Intent();
        result.setData(Uri.fromFile(file));

        setResult(RESULT_OK, result);
        return true;
    }
}
