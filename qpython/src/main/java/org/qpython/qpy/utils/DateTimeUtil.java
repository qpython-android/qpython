package org.qpython.qpy.utils;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * 文 件 名: DateTimeUtil
 * 创 建 人: ZhangRonghua
 * 创建日期: 2017/12/28 10:15
 * 邮   箱: qq798435167@gmail.com
 * 修改时间：
 * 修改备注：
 */

public class DateTimeUtil {

    public static String format(Date remoteDate) {
        Date date = remoteDate;
        String result = null;
        long creatTime = date.getTime();
        long currentTime = System.currentTimeMillis();
        //间隔时间秒
        long detalTimeS = (currentTime - creatTime) / 1000;
        long detalTimeM = detalTimeS / 60;
        long detalTimeH = detalTimeM / 60;
        long detalTimeDay = detalTimeH / 24;
        if (detalTimeS < 60) {
            result = "just moment";
        } else if (detalTimeS >= 60 && detalTimeM < 60) {
            result = detalTimeM + " minutes ago";
        } else if (detalTimeM >= 60 && detalTimeH <= 10) {
            result = detalTimeH + " hours ago";
        } else if (detalTimeH > 10) {
            result = dateFormat("M-d HH:mm", date);
        } else if (detalTimeDay >= 1 && detalTimeDay < 365) {
            result = dateFormat("M-d", date);
        } else {
            result = dateFormat("yyyy-M-d", date);
        }
        return result;
    }

    @SuppressLint("SimpleDateFormat")
    public static String dateFormat(String reg, Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(reg);
        return simpleDateFormat.format(date);
    }
}
