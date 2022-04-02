package org.qpython.qpy.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;

public class ShortcutUtil {

    public static List<String> getAllTheLauncher(Context context) {
        List<String> names = new ArrayList<>();
        List<PackageInfo> packs = context.getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < packs.size(); i++) {
            PackageInfo p = packs.get(i);


            if (p.versionName == null) {
                continue;
            }
            names.add(p.packageName);
//            newInfo.appname = p.applicationInfo.loadLabel(context.getPackageManager()).toString();
//            newInfo.pname = p.packageName;
//            newInfo.classname = p.applicationInfo.className;
//            newInfo.versionCode = p.versionCode;
//            newInfo.icon = p.applicationInfo.loadIcon(context.getPackageManager());
//        List<String> names = null;
//        PackageManager pkgMgt = context.getPackageManager();
//        Intent it = new Intent(Intent.ACTION_MAIN);
//        it.addCategory(Intent.CATEGORY_LAUNCHER);
//        List<ResolveInfo> ra = pkgMgt.queryIntentActivities(it, 0);
//        if (ra.size() != 0) {
//            names = new ArrayList<String>();
//        }
//        for (int i = 0; i < ra.size(); i++) {
//            String packageName = ra.get(i).activityInfo.packageName;
//            names.add(packageName);
//        }
        }
        return names;
    }

    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    public static List<ShortcutInfo> getShortcutInfo(Context context){
        ShortcutManager mShortcutManager = context.getSystemService(ShortcutManager.class);
        return mShortcutManager.getPinnedShortcuts();
    }
}
