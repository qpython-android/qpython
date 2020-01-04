package org.qpython.qpy.main.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import org.qpython.qpy.main.activity.QWebViewActivity;
import org.qpython.qpysdk.utils.FileHelper;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by mathiasluo on 16-6-1.
 */

public class Utils extends org.qpython.qpysdk.utils.Utils {
    static final String TAG = "Utils";

    public static void startWebActivityWithUrl(Context context, String title, String url) {
        Utils.startWebActivityWithUrl(context, title, url, "", false, false);
    }
    public static void startWebActivityWithUrl(Context context, String title, String url, String script, boolean isNoHead, boolean isDrawer) {
        Intent intent = new Intent(context, QWebViewActivity.class);
        if (script.equals("")) {
            Uri u = Uri.parse(url);
            intent.setData(u);
        }

        intent.putExtra(QWebViewActivity.ACT, "main");
        intent.putExtra(QWebViewActivity.TITLE, title);
        intent.putExtra(QWebViewActivity.SRC, url);
        intent.putExtra(QWebViewActivity.LOG_PATH, script);
        intent.putExtra(QWebViewActivity.IS_NO_HEADER, isNoHead ? "1" : "0");
        intent.putExtra(QWebViewActivity.IS_DRAWER, isDrawer ? "drawer" : "");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(intent);

    }

    public static void createDirectoryOnExternalStorage(String path) {
        try {
            if (Environment.getExternalStorageState().equalsIgnoreCase("mounted")) {
                File file = new File(Environment.getExternalStorageDirectory(), path);
                if (!file.exists()) {
                    try {
                        file.mkdirs();

                        Log.d(TAG, "createDirectoryOnExternalStorage created " + Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + path);
                    } catch (Exception e) {
                        Log.e(TAG, "createDirectoryOnExternalStorage error: ", e);
                    }
                }
            } else {
                Log.e(TAG, "createDirectoryOnExternalStorage error: " + "External storage is not mounted");
            }
        } catch (Exception e) {
            Log.e(TAG, "createDirectoryOnExternalStorage error: " + e);
        }

    }


    public static boolean netOk(Context context) {
        try {
            ConnectivityManager nInfo = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            nInfo.getActiveNetworkInfo().isConnectedOrConnecting();


            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                return true;
            } else {
                return false;
            }

        } catch (Exception e) {
            Log.d(TAG, "Network available:false");

            return false;
        }
    }

    public static String getLang() {
        return Locale.getDefault().getLanguage();
    }

    public static int getVersinoCode(Context context) {
        int intVersioinCode = 0;
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            intVersioinCode = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return intVersioinCode;
    }

    public static Intent openRemoteLink(Context context, String link) {
        //LogUtil.d(TAG, "openRemoteLink:"+link);
        String vlowerFileName = link.toLowerCase();
        if (vlowerFileName.startsWith("lgmarket:")) {
            String[] xx = link.split(":");
            //LogUtil.d(TAG, "lgmarket:"+xx[1]);

            Intent intent = new Intent("com.lge.lgworld.intent.action.VIEW");
            intent.setClassName("com.lge.lgworld", "com.lge.lgworld.LGReceiver");
            intent.putExtra("lgworld.receiver", "LGSW_INVOKE_DETAIL");
            intent.putExtra("APP_PID", xx[1]);

			/*Intent intent = new Intent();
            intent.setClassName("com.lg.apps.cubeapp", "com.lg.apps.cubeapp.PreIntroActivity");
			intent.putExtra("type", "APP_DETAIL ");
			intent.putExtra("codeValue", ""); // value is not needed when moving to Detail page
			intent.putExtra("content_id", xx[1]);   */

            context.sendBroadcast(intent);

            return null;

        } else {
            Uri uLink = Uri.parse(link);

            Intent intent = new Intent(Intent.ACTION_VIEW, uLink);

            return intent;
        }
    }

    public static String getCode(Context context) {
        String packageName = context.getPackageName();
        String[] xcode = packageName.split("\\.");
        String code = xcode[xcode.length - 1];
        return code;
    }

    public static boolean isQPy3(Context context) {
        String code = Utils.getCode(context);
        if (code.contains("qpy3")) {
            return true;
        } else {
            return false;
        }
    }

    public static String getSrv(String script) {
        //LogUtil.d(TAG, "getSrv:"+script);

        String content = FileHelper.getFileContents(script);

        String srv = "http://localhost";
        Pattern srvPattern = Pattern.compile("#qpy://(.+)[\\s]+", Pattern.CASE_INSENSITIVE);
        Matcher matcher1 = srvPattern.matcher(content);

        if (matcher1.find()) {
            srv = "http://" + matcher1.group(1);

            //LogUtil.d(TAG, "URL2:"+srv);

        }
        try {
            URL n = new URL(srv);
            srv = n.getProtocol() + "://" + n.getHost() + ":" + (n.getPort() > 0 ? n.getPort() : 80);
            //LogUtil.d(TAG, "getsrv:"+srv);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return srv;
    }

    public static boolean isZn() {
        String local = Locale.getDefault().getDisplayLanguage();
        return "中文".equals(local);
    }
}
