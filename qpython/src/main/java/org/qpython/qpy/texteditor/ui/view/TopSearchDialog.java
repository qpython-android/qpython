//package org.qpython.qpy.texteditor.ui.view;
//
//import android.app.Dialog;
//import android.content.Context;
//import android.databinding.DataBindingUtil;
//import android.graphics.Color;
//import android.graphics.drawable.ColorDrawable;
//import android.text.TextWatcher;
//import android.view.Gravity;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.Window;
//
//import org.qpython.qpy.texteditor.R;
//import org.qpython.qpy.texteditor.databinding.DialogSearchTopBinding;
//
///**
// * Top search dialog
// * Created by Hmei on 2017-05-10.
// */
//
//public class TopSearchDialog {
//
//    private static Dialog searchDialog;
//    private Callback callback;
//    private DialogSearchTopBinding binding;
//
//    public TopSearchDialog(Context context) {
//        searchDialog = new Dialog(context);
//        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_search_top, null, false);
//        initWindow();
//        initListener();
//    }
//
//    private void initWindow() {
//        Window window = searchDialog.getWindow();
//        window.setGravity( Gravity.TOP);
//        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//    }
//
//    private void initListener() {
//        binding.ibClear.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                binding.textSearch.setText("");
//            }
//        });
//        binding.ibClose.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dismiss();
//            }
//        });
//    }
//
//    public TopSearchDialog addTextChangeListener(TextWatcher textWatcher) {
//        binding.textSearch.addTextChangedListener(textWatcher);
//        return this;
//    }
//
//    public TopSearchDialog addCallback(Callback callback) {
//        this.callback = callback;
//        return this;
//    }
//
//    public void show() {
//        searchDialog.show();
//    }
//
//    public void dismiss() {
//        searchDialog.dismiss();
//        if (callback != null) {
//            callback.dismiss();
//        }
//    }
//
//    interface Callback{
//        void dismiss();
//    }
//}
