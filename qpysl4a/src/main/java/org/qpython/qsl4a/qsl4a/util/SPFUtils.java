package org.qpython.qsl4a.qsl4a.util;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.UUID;

/**
 * Created by yhc on 16/8/25.
 */
public class SPFUtils {
    public static String getExtConf(Context context) {
        return getSP(context, "config.ext");
    }

    public static String getUserNoId(Context context) {
        String usernoid = getSP(context, "user.usernoid");
        if (usernoid.equals("")) {
            // TODO
            //UUID uuid  =  UUID.randomUUID();
            usernoid = UUID.randomUUID().toString();
            setSP(context, "user.usernoid", usernoid);
        }

        return usernoid;
    }

    public static String getCode(Context context) {
        String packageName = context.getPackageName();
        String[] xcode = packageName.split("\\.");
        String code = xcode[xcode.length-1];
        return code;
    }

    public static String getDateTime(long ts) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String str = df.format(ts);
        return str;
    }

    public static String getSP(Context context, String key)	{
        String val;
        SharedPreferences obj = context.getSharedPreferences("qpyspf",0);
        val = obj.getString(key,"");
        return val;
    }
    public static void setSP(Context context, String key,String val) {
        SharedPreferences obj = context.getSharedPreferences("qpyspf",0);
        SharedPreferences.Editor wobj;
        wobj = obj.edit();
        wobj.putString(key, val);
        wobj.commit();
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

    public static Intent getLinkAsIntent(Context context, String link) {
        //LogUtil.d(TAG, "openRemoteLink:"+link);
        String vlowerFileName = link.toLowerCase();
        if (vlowerFileName.startsWith("lgmarket:")) {
            String[] xx = link.split(":");
            //LogUtil.d(TAG, "lgmarket:"+xx[1]);

            Intent intent = new Intent("com.lge.lgworld.intent.action.VIEW");
            intent.setClassName("com.lge.lgworld", "com.lge.lgworld.LGReceiver");
            intent.putExtra("lgworld.receiver","LGSW_INVOKE_DETAIL");
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

            Intent intent = new Intent( Intent.ACTION_VIEW, uLink );

            return intent;
        }
    }

    public static boolean netCheckin(Context context) {
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

            return false;
        }
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

}
