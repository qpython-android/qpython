package org.qpython.qpy.main.receiver;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.qpython.qpy.R;
import org.qpython.qpy.main.app.App;
import org.qpython.qpy.main.fragment.LibProjectFragment;

/**
 *
 * Created by Hmei on 2017-06-09.
 */

public class DownloadReceiver extends BroadcastReceiver {
    private long enqueue;

    public DownloadReceiver() {
    }

    public void setEnqueue(long enqueue) {
        this.enqueue = enqueue;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(enqueue);
            Cursor c = ((DownloadManager) App.getContext().getSystemService(Context.DOWNLOAD_SERVICE)).query(query);
            if (c.moveToFirst()) {
                int columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
                if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(columnIndex)) {
                    String title = c.getString(c.getColumnIndex(DownloadManager.COLUMN_TITLE));

                    LibProjectFragment.CreateLibFinishEvent event = new LibProjectFragment.CreateLibFinishEvent();
                    event.fileName = title;
                    EventBus.getDefault().post(event);
                } else {
                    Toast.makeText(context, R.string.download_fail, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
