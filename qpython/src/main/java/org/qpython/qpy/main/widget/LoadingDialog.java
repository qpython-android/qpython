package org.qpython.qpy.main.widget;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;

import org.qpython.qpy.R;

/**
 * 文 件 名: LoadingDialog
 * 创 建 人: ZhangRonghua
 * 创建日期: 2018/1/10 10:07
 * 邮   箱: qq798435167@gmail.com
 * 修改时间：
 * 修改备注：
 */

public class LoadingDialog extends Dialog {
    public LoadingDialog(@NonNull Context context) {
        super(context, R.style.BaseDialog);
        setContentView(R.layout.dialog_loading);
    }
}
