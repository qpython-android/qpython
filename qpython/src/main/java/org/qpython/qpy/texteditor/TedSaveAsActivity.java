package org.qpython.qpy.texteditor;

import static org.qpython.qpy.texteditor.androidlib.ui.Toaster.showToast;

import java.io.File;

import org.qpython.qpy.R;
import org.qpython.qpy.texteditor.common.Constants;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import org.qpython.qpy.texteditor.androidlib.ui.activity.BrowsingActivity;

public class TedSaveAsActivity extends BrowsingActivity implements Constants, OnClickListener {

	/** the edit text input */
	protected EditText mFileName;
	/** */
	protected Drawable mWriteable;
	/** */
	protected Drawable mLocked;

	/**
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Setup content view
		setContentView(R.layout.layout_save_as);

		// buttons
		findViewById(R.id.buttonCancel).setOnClickListener(this);
		findViewById(R.id.buttonOk).setOnClickListener(this);
		((Button) findViewById(R.id.buttonOk)).setText(R.string.ui_save);

		// widgets
		mFileName = (EditText) findViewById(R.id.editFileName);

		// drawables
		mWriteable = getResources().getDrawable(R.drawable.checked);
		mLocked = getResources().getDrawable(R.drawable.ic_editor_file_little);
	}

	/**
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.buttonCancel) {
			setResult(RESULT_CANCELED);
			finish();
		} else if (v.getId() == R.id.buttonOk) {
			if (setSaveResult())
				finish();
		}
	}

	/**
	 */
	@Override
	protected void onFileClick(File file) {
		if (file.canWrite())
			mFileName.setText(file.getName());
	}

	/**
	 */
	@Override
	protected boolean onFolderClick(File folder) {
		return true;
	}

	/**
	 */
	@Override
	protected void onFolderViewFilled() {

	}

	/**
	 * Sets the result data when the user presses save
	 *
	 * @return if the result is OK (if not, it means the user must change its
	 *         selection / input)
	 */
	@SuppressWarnings("deprecation")
	protected boolean setSaveResult() {
		Intent result;
		String fileName;

		if ((mCurrentFolder == null) || (!mCurrentFolder.exists())) {
			showToast(this, R.string.toast_folder_doesnt_exist, true);
			return false;
		}

		if (!mCurrentFolder.canWrite()) {
			showToast(this, R.string.toast_folder_cant_write, true);
			return false;
		}

		fileName = mFileName.getText().toString();
		if (fileName.length() == 0) {
			showToast(this, R.string.toast_filename_empty, true);
			return false;
		}

		result = new Intent();
		result.putExtra("path", mCurrentFolder.getAbsolutePath() + File.separator + fileName);

		setResult(RESULT_OK, result);
		return true;
	}

}
