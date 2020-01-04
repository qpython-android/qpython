package org.qpython.qpy.main.model;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;

import java.io.File;


public class LocalAppModel extends AppModel {

    private final Context         mContext;
    private final ApplicationInfo mInfo;
    private final File            mApkFile;
    private       boolean         mMounted;

    public LocalAppModel(Context context, ApplicationInfo info) {
        mContext = context;
        mInfo = info;
        mApkFile = new File(info.sourceDir);
    }

    private ApplicationInfo getAppInfo() {
        return mInfo;
    }

    public String getApplicationPackageName() {
        return getAppInfo().packageName;
    }

    @Override
    public String getLabel() {
        return mAppLabel;
    }

    @Override
    public int getIconRes() {
        return 0;
    }

    @Override
    public Drawable getIcon() {
        if (mIcon == null) {
            if (mApkFile.exists()) {
                mIcon = mInfo.loadIcon(mContext.getPackageManager());
                return mIcon;
            } else {
                mMounted = false;
            }
        } else if (!mMounted) {
            // If the app wasn't mounted but is now mounted, reload
            // its icon.
            if (mApkFile.exists()) {
                mMounted = true;
                mIcon = mInfo.loadIcon(mContext.getPackageManager());
                return mIcon;
            }
        } else {
            return mIcon;
        }

        return mContext.getResources().getDrawable(android.R.drawable.sym_def_app_icon);
    }


    public void loadLabel(Context context) {
        if (mAppLabel == null || !mMounted) {
            if (!mApkFile.exists()) {
                mMounted = false;
                mAppLabel = mInfo.packageName;
            } else {
                mMounted = true;
                CharSequence label = mInfo.loadLabel(context.getPackageManager());
                mAppLabel = label != null ? label.toString() : mInfo.packageName;
            }
        }
    }
}
