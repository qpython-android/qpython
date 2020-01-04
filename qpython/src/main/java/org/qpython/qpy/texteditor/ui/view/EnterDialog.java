package org.qpython.qpy.texteditor.ui.view;

import android.support.v7.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import org.qpython.qpy.R;
import org.qpython.qpy.databinding.DialogEnterBinding;

/**
 * A Dialog Contain EditText
 * Created by Hmei on 2017-05-18.
 */

public class EnterDialog {
    private DialogEnterBinding binding;

    private AlertDialog dialog;

    public EnterDialog(Context context) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_enter, null, false);

        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyDialog);
        builder.setView(binding.getRoot());
        dialog = builder.create();

        Window window = dialog.getWindow();
        if (window != null) {
            window.setGravity(Gravity.CENTER);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }

        binding.tvNegative.setOnClickListener(v -> dialog.dismiss());
    }

    public EnterDialog setTitle(String title) {
        binding.tvTitle.setText(title);
        return this;
    }

    public EnterDialog setText(String text) {
        binding.etEnter.setText(text);
        binding.etEnter.setSelection(text.length());
        return this;
    }

    public EnterDialog setExt(String ext) {
        binding.etEnter.setText(ext);
        return this;
    }

    public EnterDialog setHint(String hint) {
        binding.etEnter.setHint(hint);
        return this;
    }

    public EnterDialog setConfirmListener(final ClickListener listener) {
        binding.tvPositive.setOnClickListener(v -> {
            if (listener.OnClickListener(binding.etEnter.getText().toString())) {
                dialog.dismiss();
            }
        });
        return this;
    }

    public EnterDialog setEnterType(int type) {
        binding.etEnter.setInputType(type);
        return this;
    }

    public void show() {
        dialog.show();
    }

    public interface ClickListener {
        /**
         * @param name Project/script name
         * @return true if the dialog should be dismissed
         */
        boolean OnClickListener(String name);
    }

}
