package org.qpython.qpy.main.model;

import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;

import java.io.Serializable;

/**
 * @credit http://developer.android.com/reference/android/content/AsyncTaskLoader.html
 */
public abstract class AppModel implements Serializable{
    String   mAppLabel;
    Drawable mIcon;

    public abstract Drawable getIcon();
    public abstract String getLabel();

    public abstract @DrawableRes int getIconRes();
}
