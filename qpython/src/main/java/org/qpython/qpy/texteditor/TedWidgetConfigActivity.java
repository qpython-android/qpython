package org.qpython.qpy.texteditor;

import java.io.File;

import org.qpython.qpy.R;
import org.qpython.qpy.texteditor.common.Constants;
import org.qpython.qpy.texteditor.common.WidgetPrefs;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import org.qpython.qpy.texteditor.androidlib.data.FileUtils;
import org.qpython.qpy.texteditor.androidlib.ui.Toaster;
import org.qpython.qpy.texteditor.androidlib.ui.activity.BrowsingActivity;

@SuppressWarnings("deprecation")
public class TedWidgetConfigActivity extends BrowsingActivity implements Constants, OnClickListener {

	/** the id of the widget being configured */
	protected int mAppWidgetId;
	/** the selected file */
	protected File mSelectedFile;

	/**
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_widget_config);

		setResult(RESULT_CANCELED, null);

		// buttons
		findViewById(R.id.buttonCancel).setOnClickListener(this);

		Toaster.showToast(this, R.string.toast_widget_select, false);
	}

	/**
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();

		// get widget id
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		if (extras != null) {
			mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
					AppWidgetManager.INVALID_APPWIDGET_ID);
		}
	}

	/**
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.buttonCancel) {
			setResult(RESULT_CANCELED);
			finish();
		}
	}

	/**
	 */
	@Override
	protected void onFileClick(File file) {
		if (file.canRead()) {
			mSelectedFile = file;
			setResultComplete();
		}
	}

	/**
	 */
	@Override
	protected boolean onFolderClick(File folder) {
		mSelectedFile = null;
		return true;
	}

	/**
	 */
	@Override
	protected void onFolderViewFilled() {

	}

	/**
	 *
	 */
	protected void setResultComplete() {
		AppWidgetManager appWidgetManager;
		Intent resultValue;
		WidgetPrefs prefs;
		boolean readOnly;

		readOnly = ((CheckBox) findViewById(R.id.readOnly)).isChecked();

		prefs = new WidgetPrefs();
		prefs.mTargetPath = FileUtils.getCanonizePath(mSelectedFile);
		prefs.mReadOnly = readOnly;
		prefs.store(this, mAppWidgetId);

		appWidgetManager = AppWidgetManager.getInstance(this);
		TedAppWidgetProvider.updateWidget(this, appWidgetManager, mAppWidgetId);

		//
		resultValue = new Intent();
		resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
		setResult(RESULT_OK, resultValue);
		finish();
	}
}
