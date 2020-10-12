package org.qpython.qpy.console.shortcuts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.qpython.qsl4a.qsl4a.LogUtil;

public class ShortcutReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtil.e("111111111" + intent);
    }
}
