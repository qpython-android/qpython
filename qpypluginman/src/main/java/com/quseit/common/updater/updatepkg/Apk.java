package com.quseit.common.updater.updatepkg;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;

import com.quseit.common.updater.Updater;

import java.io.File;

public class Apk implements UpdatePackage {
    private String name;
    private String version;
    private int versionCode;
    private String description;
    private String url;

    public Apk(String name, String version, int versionCode, String description, String url) {
        this.name = name;
        this.version = version;
        this.versionCode = versionCode;
        this.description = description;
        this.url = url;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getVersion() {
        return String.valueOf(this.version);
    }

    public String getVersionDescription() {
        return this.description;
    }

    @Override
    public String getDownloadUrl() {
        return this.url;
    }

    @Override
    public boolean checkVersion() {
        Context context = Updater.getContext();
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        int localVersion = packageInfo.versionCode;
        return this.versionCode > localVersion;
    }

    @Override
    public void install(File installFile) {
        Uri uri = Uri.fromFile(installFile);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Updater.getContext().startActivity(intent);
    }
}
