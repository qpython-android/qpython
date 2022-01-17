package org.qpython.qpy.service;

import android.util.Log;

/**
 * @ProjectName: qpython
 * @Package: org.qpython.qpy.service
 * @ClassName: HmsMessageService
 * @Description: 华为Hms消息服务
 * @Author: wjx
 * @CreateDate: 2022/1/17 14:31
 * @Version: 1.0
 */
public class HmsMessageService extends com.huawei.hms.push.HmsMessageService {
    private static final String TAG = "HmsMessageService";

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);

        // 获取token
        Log.i(TAG, "have received refresh token " + token);
    }
}
