package org.qpython.qpy.texteditor.ui.adapter.bean;

import android.view.View;
import android.widget.TextView;

/**
 * A bean class that describe popup item
 * Created by Hmei on 2017-05-31.
 */

public class PopupItemBean {
    private boolean isDisable = false;
    private String title1, title2;
    private View.OnClickListener clickListener;

    public PopupItemBean(String title, View.OnClickListener clickListener) {
        this.title1 = title;
        this.clickListener = clickListener;
    }


    public View.OnClickListener getClickListener() {
        return v -> {
            clickListener.onClick(v);
            if (title2 != null) {
                TextView tv = (TextView) v;
                isDisable = !isDisable;
                if (isDisable) {
                    tv.setText(title2);
                } else {
                    tv.setText(title1);
                }
            }

        };
    }

    @Override
    public String toString() {
        return isDisable ? title2 : title1;
    }

    public PopupItemBean setTitle2(String title2) {
        this.title2 = title2;
        return this;
    }
}
