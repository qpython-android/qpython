package org.qpython.qpy.main.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;
import org.qpython.qpy.R;
import org.qpython.qpy.main.activity.QWebViewActivity;
import org.qpython.qpy.main.app.App;
import org.qpython.qpy.main.app.CONF;
import org.qpython.qpy.main.receiver.NotificationBean;

import java.util.Map;


public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final int LOG_NOTIFICATION_ID = (int) System.currentTimeMillis();

    private boolean handled = false;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Map<String, String> data = remoteMessage.getData();
        String json = App.getGson().toJson(data);
        NotificationBean bean = App.getGson().fromJson(json, NotificationBean.class);
        if (!bean.isForce()&&!PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getString(R.string.key_hide_push), true)) {
            return;
        }
        Intent intent;
        if (bean.getType().equals("ext")) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(bean.getLink()));
        } else {
            intent = new Intent(App.getContext(), QWebViewActivity.class);
            intent.putExtra(QWebViewActivity.TITLE, bean.getTitle());
            intent.putExtra("url", bean.getLink());
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Notification.Builder builder = new Notification.Builder(this)
                .setSmallIcon(R.drawable.img_home_logo)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(bean.getMsg())
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        Notification notification;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            notification = builder.build();
        } else {
            notification = builder.getNotification();
        }
        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(LOG_NOTIFICATION_ID, notification);
        handled = true;
    }

 /*   @Override
    public void handleIntent(Intent intent) {
        super.handleIntent(intent);
        if (handled) {
            return;
        }
        Bundle bundle = intent.getExtras();
        JSONObject extras = new JSONObject();
        try {
            for (String s : bundle.keySet()) {
                extras.put(s, bundle.get(s));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SharedPreferences.Editor editor = getSharedPreferences(CONF.NOTIFICATION_SP_NAME, MODE_PRIVATE).edit();
        editor.putString(CONF.NOTIFICATION_SP_OBJ, extras.toString());
        editor.apply();
    }*/
}