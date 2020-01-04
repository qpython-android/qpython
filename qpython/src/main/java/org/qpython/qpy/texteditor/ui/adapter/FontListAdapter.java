package org.qpython.qpy.texteditor.ui.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.qpython.qpy.R;
import org.qpython.qpy.texteditor.androidlib.data.FileUtils;
import org.qpython.qpy.texteditor.androidlib.ui.adapter.FileListAdapter;

import java.io.File;
import java.util.List;

public class FontListAdapter extends FileListAdapter {

	/**
	 * Constructor
	 * 
	 * @param context
	 *            The current context
	 * @param objects
	 *            The objects to represent in the ListView.
	 * @param folder
	 *            the parent folder of the items presented, or null if the top
	 *            folder should not be displayed as up
	 */
	public FontListAdapter(Context context, List<File> objects) {
		super(context, objects, null);
	}

	/**
	 * @see fr.xgouchet.androidlib.ui.adapter.FileListAdapter#getView(int,
	 *      android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = super.getView(position, convertView, parent);

		File f = getItem(position);

		if (FileUtils.getFileExtension(f).equalsIgnoreCase("ttf")) {
			TextView t = (TextView) v.findViewById(R.id.textFileName);
			Typeface font = Typeface.DEFAULT;
			try {
				font = Typeface.createFromFile(f);
				t.setTypeface(font);
			} catch (RuntimeException e) {
				Log.w("TED", "Unable to create a font from " + f.getName());
			}
		}

		return v;
	}
}
