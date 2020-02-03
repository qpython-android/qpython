package org.qpython.qpy.main.app;

import android.content.Context;
import android.util.Log;

import org.qpython.qpy.BuildConfig;
import org.qpython.qpy.codeshare.ShareCodeUtil;
//import com.hipipal.qpyplus.wxapi.WXAPIManager;

/**
 * 文 件 名: AppInit
 * 创 建 人: ZhangRonghua
 * 创建日期: 2018/3/8 15:13
 * 修改时间：
 * 修改备注：
 */

public class AppInit {

    public static void init(Context context){
        //wx
//        WXAPIManager.init(context);
        ShareCodeUtil.getInstance();
    }
}
