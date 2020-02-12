package org.qpython.qpy.main.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.qpython.qsl4a.QPyScriptService;


public class BootBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "QPyScriptServiceR";

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d(TAG, "BootReceiver.onReceive: " + intent.getAction());
        System.out.println("QPYSL4A程序即将执行");

        context.startService(new Intent(context, QPyScriptService.class));
    }
}
