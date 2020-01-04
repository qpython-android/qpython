package org.qpython.qpy.utils;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * 文 件 名: SolfKeybroadUtil
 * 创 建 人: ZhangRonghua
 * 创建日期: 2017/12/28 11:24
 * 邮   箱: qq798435167@gmail.com
 * 修改时间：
 * 修改备注：
 */

public class SolfKeybroadUtil {
    public static void showSolfInput(View view , boolean show) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (show) {
            imm.showSoftInput(view, 0);
        } else {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
