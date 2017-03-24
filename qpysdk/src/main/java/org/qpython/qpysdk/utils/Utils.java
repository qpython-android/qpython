package org.qpython.qpysdk.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ConfigurationInfo;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by yhc on 16/8/21.
 */
public class Utils {
    static final String TAG = "Utils";

    public static String getSP(Context context, String key)	{
        String val;
        SharedPreferences obj = context.getSharedPreferences("qpyspf",0);
        val = obj.getString(key,"");
        return val;
    }

    public static boolean isOpenGL2supported(Context context) {

        final ActivityManager activityManager =
                (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo =
                activityManager.getDeviceConfigurationInfo();
        final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;
        return supportsEs2;
    }
    public static boolean httpPing(String url, int timeout) {
        //Log.d(TAG, "httpPing:"+url+"-"+timeout);
        url = url.replaceFirst("https", "http"); // Otherwise an exception may be thrown on invalid SSL certificates.

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setConnectTimeout(timeout);
            connection.setReadTimeout(timeout);
            connection.setRequestMethod("HEAD");
            int responseCode = connection.getResponseCode();
            //Log.d(TAG, "responseCode:"+responseCode);
            return (responseCode>0);
            //return (200 <= responseCode && responseCode <= 399);
        } catch (IOException exception) {
            Log.d(TAG, "exception:"+exception.getLocalizedMessage());

            return false;
        }
    }
    static public boolean isSrvOk(String srv) {
        try {
            URL u = new URL(srv);
            int port = 80;
            if (u.getPort() != -1) {
                port = u.getPort();
            }
            String url = u.getProtocol() + "://" +u.getHost()+":"+port+"/";
            boolean ret =  httpPing(url, 1000);
            return ret;

        } catch (MalformedURLException e) {
            //Log.d("Bean", "MalformedURLException:"+e);
            return false;
        }
    }




}
