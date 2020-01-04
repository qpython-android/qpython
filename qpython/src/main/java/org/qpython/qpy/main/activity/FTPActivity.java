package org.qpython.qpy.main.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.DatabaseUtils;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;

import org.qpython.qpy.R;
import org.qpython.qpy.databinding.ActivityFtpBinding;

/**
 * FTP service setting page
 * Created by Hmei on 2017-05-27.
 */

public class FTPActivity extends Activity {
    ActivityFtpBinding binding;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_ftp);
    }

    public static void start(Context context) {
        Intent starter = new Intent(context, FTPActivity.class);
        context.startActivity(starter);
    }
}
