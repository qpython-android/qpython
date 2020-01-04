package org.qpython.qpy.main.widget;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import org.qpython.qpy.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.qpython.qpy.utils.CodePattern.COLOR_BUILTIN;
import static org.qpython.qpy.utils.CodePattern.COLOR_COMMENT;
import static org.qpython.qpy.utils.CodePattern.COLOR_KEYWORD;
import static org.qpython.qpy.utils.CodePattern.PATTERN_PY_BUILD_IN;
import static org.qpython.qpy.utils.CodePattern.PATTERN_PY_COMMENT;
import static org.qpython.qpy.utils.CodePattern.PATTERN_PY_KEYWORD;

/**
 * 文 件 名: CodeReviewDialog
 * 创 建 人: ZhangRonghua
 * 创建日期: 2017/12/28 09:30
 * 邮   箱: qq798435167@gmail.com
 * 修改时间：
 * 修改备注：
 */

public class CodeReviewDialog extends Dialog {
    private TextView mTextView;

    public CodeReviewDialog(@NonNull Context context) {
        super(context, R.style.Dialog);
        setContentView(R.layout.dialog_code_review);
        mTextView = findViewById(R.id.code_content_tv);
        findViewById(R.id.root_layout).setOnClickListener(v -> dismiss());

    }

    public void setContent(SpannableString content) {
        mTextView.setText(content);
    }
}
