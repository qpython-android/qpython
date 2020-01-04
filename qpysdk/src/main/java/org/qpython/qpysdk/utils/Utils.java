package org.qpython.qpysdk.utils;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ConfigurationInfo;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;

import com.quseit.qpyengine.R;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.sql.Time;

/**
 * Created by yhc on 16/8/21.
 */
public class Utils {
    static final String TAG = "Utils";

    public final int KIVY_REQUEST_CODE = 10103;
    public static final String TITLE = "title";
    public static final String PATH = "path";

    public static void checkRunTimeLog(Context context, String title, String path) {
        final Intent updateIntent = new Intent();
        updateIntent.setClassName(context.getPackageName(), "org.qpython.qpy.main.activity.LogActivity");
        updateIntent.putExtra(TITLE, title);
        updateIntent.putExtra(PATH, path);
        context.startActivity(updateIntent);

    }
    public static String getCode(Context context) {
        String packageName = context.getPackageName();
        String[] xcode = packageName.split("\\.");
        String code = xcode[xcode.length-1];
        return code;
    }

    private static int reverseResourceId(Context ctx,String resourceName,String typeName) {
        if (ctx == null) {
            // FIXME 这里请使用 {@link IllegalArgumentException}
            throw new IllegalArgumentException();
        }
        return ctx.getResources().getIdentifier(resourceName, typeName, ctx.getApplicationContext().getPackageName());
    }
    public static int getDrawableId(Context ctx,String drawableResourceName) {
        return reverseResourceId(ctx,drawableResourceName,"drawable");
    }


    public static Notification getNotification(Context context, String contentTitle, String contentText, PendingIntent intent,
                                               int smallIconId, Bitmap largeIconId, int flags) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            Notification notification = new Notification.Builder(context) //new Notification(icon, tickerText, when);
                    .setTicker(contentTitle)
                    .setContentTitle(contentTitle)
                    .setContentText(contentText)
                    .setSmallIcon(smallIconId)
                    .setLargeIcon(largeIconId)
                    .setAutoCancel(true)
                    .setContentIntent(intent)
                    .build();

            return notification;
        } else if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
            Notification notification = new Notification.Builder(context) //new Notification(icon, tickerText, when);
                    .setTicker(contentTitle)
                    .setContentTitle(contentTitle)
                    .setContentText(contentText)
                    .setSmallIcon(smallIconId)
                    .setSmallIcon(smallIconId)
                    .setLargeIcon(largeIconId)
                    .setAutoCancel(true)
                    .setContentIntent(intent)
                    .getNotification();
            return notification;
        } else {
            Notification notification = new Notification(smallIconId, contentTitle, System.currentTimeMillis());
            notification.tickerText = contentTitle;
            notification.contentIntent = intent;
            notification.flags |= flags;
            return null;
        }
    }


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

    public static boolean socketPing(String url, int timeout) {
        try {
            Thread.sleep(timeout);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            URL hello = new URL(url);
            Log.d(TAG, "socketPing:" + hello.getHost() + "-" + hello.getPort() + "-");
            Socket s = new Socket();
            s.connect(new InetSocketAddress(hello.getHost(), hello.getPort()), timeout);
            s.close();

            return true;
        } catch (ConnectException ex) {
            return false;
        } catch (Exception e) {
            return false;
        }
    }
    public static boolean httpPing(String url, int timeout)  {
        Log.d(TAG, "httpPing:"+url);
        url = url.replaceFirst("https", "http"); // Otherwise an exception may be thrown on invalid SSL certificates.
        try {
//            URL hello = new URL(url);
//            LogUtil.d(TAG, "httpPing:"+hello.getHost()+"-"+hello.getPort()+"-");
//
//            Socket s = new Socket(hello.getHost(), hello.getPort());
//            boolean stat = s.isConnected();
//
//            s.close();
//
//
//            return stat;

            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setConnectTimeout(timeout);
            connection.setReadTimeout(timeout);
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();
            Log.d(TAG, "responseCode:"+responseCode);

            try {
                Thread.sleep(timeout);
            } catch (InterruptedException ex) {

            }

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
            //LogUtil.d("Bean", "MalformedURLException:"+e);
            return false;
        }
    }




}
