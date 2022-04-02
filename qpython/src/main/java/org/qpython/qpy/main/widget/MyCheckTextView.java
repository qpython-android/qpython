package org.qpython.qpy.main.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;


public class MyCheckTextView extends ClickableSpan {
    private Context mContext;
    private int mark;
    private int color;
    private ClickListener listener;

    public MyCheckTextView(Context mContext, int mark, int color, ClickListener listener) {
        this.mContext = mContext;
        this.mark = mark;
        this.color = color;
        this.listener = listener;
    }


    @Override
    public void onClick(@NonNull View widget) {
        if (listener != null) {
            listener.click(mark);
        }
    }

    @Override
    public void updateDrawState(@NonNull TextPaint ds) {
        super.updateDrawState(ds);
//        设置文本颜色
        ds.setColor(ContextCompat.getColor(mContext, color));
//         超链接形式的下划线，false 表示不显示下划
        ds.setUnderlineText(false);

    }

    public interface ClickListener {
        void click(int mark);
    }
}
