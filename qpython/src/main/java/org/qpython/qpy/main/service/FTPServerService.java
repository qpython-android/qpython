package org.qpython.qpy.main.service;

import org.qpython.qpy.main.activity.SettingActivity;

/**
 * Created by Hmei on 2017-06-13.
 */

public class FTPServerService extends org.swiftp.FTPServerService {
    @Override
    protected Class<?> getSettingClass() {
        return SettingActivity.class;
    }
}
