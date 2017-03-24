package org.qpython.qsl4a.qsl4a.util;

import android.content.Context;
import android.content.SharedPreferences;

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

}
