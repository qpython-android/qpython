package org.qpython.qpy.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * 文 件 名: OpenWebUtil
 * 创 建 人: ZhangRonghua
 * 创建日期: 2017/12/29 12:18
 * 邮   箱: qq798435167@gmail.com
 * 修改时间：
 * 修改备注：
 */

public class OpenWebUtil {
    public static void open(Context context,String url){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri content_url = Uri.parse(url);
        intent.setData(content_url);
        context.startActivity(Intent.createChooser(intent, "Please select your browser"));
    }
}
