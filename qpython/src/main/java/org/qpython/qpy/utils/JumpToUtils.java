package org.qpython.qpy.utils;

import android.content.Context;

import org.qpython.qpy.R;
import org.qpython.qpy.main.activity.QWebViewActivity;

/**
 * @ProjectName: qpython
 * @Package: org.qpython.qpy.utils
 * @ClassName: JumpToUtils
 * @Description: 通知逻辑跳转相关
 * @Author: wjx
 * @CreateDate: 2022/1/18 12:03
 * @Version: 1.0
 */
public class JumpToUtils {
    public final static String EXTRA_ACTION = "action";
    public final static String EXTRA_VALUE = "value";

    public final static String EXTRA_TOPIC = "QPython";

    private final static String JUMP_ACTION_WEB_PAGE = "jump_web_page";

    public static void jumpTo(Context context, String action, String value) {
        if(JUMP_ACTION_WEB_PAGE.equals(action)) {
            QWebViewActivity.start(context,
                    context.getString(R.string.text_noti), value);
        }
    }
}
