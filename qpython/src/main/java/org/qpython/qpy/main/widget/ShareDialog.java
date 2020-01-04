package org.qpython.qpy.main.widget;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import org.qpython.qpy.R;

/**
 * 文 件 名: ShareDialog
 * 创 建 人: ZhangRonghua
 * 创建日期: 2017/12/26 12:05
 * 邮   箱: qq798435167@gmail.com
 * 修改时间：
 * 修改备注：
 */

public class ShareDialog extends Dialog{

    public static final int FACEBOOK = 0X001;
    public static final int TWITTER = 0X002;
    public static final int COPY_LINK = 0X099;

    public ShareDialog(@NonNull Context context) {
        super(context, R.style.Dialog);
        setContentView(R.layout.dialog_share);
    }

    public void setOnClickListener(OnClickListener listener) {
        findViewById(R.id.facebook_iv).setOnClickListener(v -> {
            dismiss();
            if (listener!=null){
                listener.onClick(FACEBOOK);
            }
        });
        findViewById(R.id.twitter_iv).setOnClickListener(v -> {
            dismiss();
            if (listener!=null){
                listener.onClick(TWITTER);
            }
        });
        findViewById(R.id.copy_link_iv).setOnClickListener(v -> {
            dismiss();
            if (listener!=null){
                listener.onClick(COPY_LINK);
            }
        });
    }

    public interface OnClickListener{
        void onClick(int index);
    }

    @Override
    public void show() {
        super.show();
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.BOTTOM;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        window.setWindowAnimations(R.style.BottomAnim);
    }
}
