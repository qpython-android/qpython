package org.qpython.qpy.main.widget;

import android.content.Context;
//import android.os.Handler;
//import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.webkit.WebView;

//import com.liulishuo.filedownloader.i.IFileDownloadIPCCallback;

import org.qpython.qpy.utils.SolfKeybroadUtil;

/**
 * 文 件 名: WebviewEX
 * 创 建 人: ZhangRonghua
 * 创建日期: 2018/2/10 15:21
 * 修改时间：
 * 修改备注：
 */

public class WebviewEX extends WebView {
    public WebviewEX(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        if (event.getAction() == MotionEvent.ACTION_UP && isSelected()){
//            closeKeyboard();
//            return true;
//        }
        return super.onTouchEvent(event);
    }

    private void closeKeyboard() {
        SolfKeybroadUtil.showSolfInput(this,false);
    }


}
