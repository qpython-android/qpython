package org.qpython.qpy.main.widget;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import org.qpython.qpy.R;

/**
 * 文 件 名: NotebookDialog
 * 创 建 人: ZhangRonghua
 * 创建日期: 2018/2/7 18:25
 * 修改时间：
 * 修改备注：
 */

public class NotebookDialog extends Dialog {

    private CheckBox app, libs;

    public NotebookDialog(@NonNull Context context) {
        super(context, R.style.BaseDialog);
        setContentView(R.layout.dialog__setting_notebook);
        app = findViewById(R.id.notebook_app);
        libs = findViewById(R.id.notebook_lib);
    }

    public void show(boolean installApp, boolean installLib, InstallCallback callback) {
        if (installApp) {
            app.setChecked(true);
            app.setEnabled(false);
            if (installLib) {
                libs.setChecked(true);
                libs.setEnabled(false);
            } else {
                libs.setEnabled(true);
                libs.setOnClickListener(v -> {
                    dismiss();
                    callback.installLibs();
                });
            }
        } else {
            libs.setChecked(false);
            libs.setEnabled(false);
            app.setEnabled(true);
            app.setOnClickListener(v -> {
                dismiss();
                callback.installApp();
            });
        }

        show();
    }

    public interface InstallCallback {
        void installApp();

        void installLibs();
    }
}
