package org.qpython.qpy.main.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

/**
 * Receive QPY broadcast and exec python programs
 * Created by Hmei on 2017-07-25.
 */

public class QPyService extends IntentService {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
//     * @param name Used to name the worker thread, important only for debugging.
     */
    public QPyService() {
        super("QPyService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
    }
}
