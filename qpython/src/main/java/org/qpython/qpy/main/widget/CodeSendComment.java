package org.qpython.qpy.main.widget;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.qpython.qpy.R;
import org.qpython.qpy.codeshare.ShareCodeUtil;
import org.qpython.qpy.databinding.WidgetSendCommentBinding;

public class CodeSendComment extends DialogFragment {
    private static final String GIST_ID = "gist_id", IS_PROJ = "is_proj";
    private ShareCodeUtil.CommentCallback callback;
    private WidgetSendCommentBinding      binding;
    private String                        gistId, to, reComment;
    private boolean isProj;

    public static CodeSendComment newInstance(String gistId, boolean isProj) {
        CodeSendComment f = new CodeSendComment();

        Bundle args = new Bundle();
        args.putString(GIST_ID, gistId);
        args.putBoolean(IS_PROJ, isProj);
        f.setArguments(args);

        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // the content
        final RelativeLayout root = new RelativeLayout(getActivity());
        root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        // creating the fullscreen dialog
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(root);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        gistId = getArguments().getString(GIST_ID);
        isProj = getArguments().getBoolean(IS_PROJ);
        View view = inflater.inflate(R.layout.widget_send_comment, container, false);
        binding = DataBindingUtil.bind(view);
        binding.btnSend.setOnClickListener(v -> {
            String reply = binding.edit.getText().toString();
            if (reply.length() == 0) {
                Toast.makeText(getActivity(), R.string.null_comment_hint, Toast.LENGTH_SHORT).show();
                return;
            }
            if (reply.startsWith(getString(R.string.re_format, to, ""))) {
                reply = reply.replace(getString(R.string.re_format, to, ""), "");
//                ShareCodeUtil.getInstance().sendComment(gist_id, to, reply, reComment, isProj, callback);
//                binding.edit.setText("");
            } else {
//                ShareCodeUtil.getInstance().sendComment(gist_id, reply, isProj, callback);
//                binding.edit.setText("");
            }
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(binding.edit.getWindowToken(), 0);
        });
        getDialog().getWindow().setGravity(Gravity.BOTTOM);
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getView().post(() -> {
            Window dialogWindow = getDialog().getWindow();

            // Make the dialog possible to be outside touch
            dialogWindow.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
            dialogWindow.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

            getView().invalidate();
        });
    }

    public void setCallback(ShareCodeUtil.CommentCallback callback) {
        this.callback = callback;
    }

    public void setReply(String toName, String reCommentContent) {
        to = toName;
        reComment = reCommentContent;

        binding.edit.setText(Html.fromHtml(getString(R.string.re_format, to, "")));
        binding.edit.setSelection(binding.edit.getText().length());
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(binding.edit, InputMethodManager.SHOW_IMPLICIT);

        binding.edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!to.equals("")) {
                    if (!s.toString().startsWith(getString(R.string.re_format, to, ""))) {
                        to = "";
                        binding.edit.setText(s.toString());
                        binding.edit.setSelection(s.length());
                    }
                }
            }
        });
    }
}
