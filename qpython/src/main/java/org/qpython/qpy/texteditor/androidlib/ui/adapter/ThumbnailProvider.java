package org.qpython.qpy.texteditor.androidlib.ui.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;

import java.io.File;

public interface ThumbnailProvider {

	public Drawable getThumbnailForFile(Context context, File file);
}
