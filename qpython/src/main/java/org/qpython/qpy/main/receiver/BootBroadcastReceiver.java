package org.qpython.qpy.main.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.os.Build;

import org.qpython.qsl4a.QPyScriptService;


public class BootBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "QPyScriptServiceR";

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d(TAG, "BootReceiver.onReceive: " + intent.getAction());
        System.out.println("QPYSL4A程序即将执行");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

        } else {

            try {
                context.startService(new Intent(context, QPyScriptService.class));
            } catch (RuntimeException e) {

            }
        }
    }
}
