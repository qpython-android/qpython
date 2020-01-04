package org.qpython.qpy.main.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.qpython.qpy.R;
import org.qpython.qpy.databinding.ActivityAddGistBinding;
import org.qpython.qpy.main.app.App;
import org.qpython.qpy.main.app.User;
import org.qpython.qpy.main.server.gist.GistEvent;
import org.qpython.qpy.main.server.gist.createScreen.CreateGistControler;
import org.qpython.qpy.main.server.gist.createScreen.CreateGistView;
import org.qpython.qpy.texteditor.common.TextFileUtils;
import org.qpython.qpy.utils.CodePattern;
import org.qpython.qpy.utils.SolfKeybroadUtil;

import java.io.File;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class GistEditActivity extends AppCompatActivity implements TextWatcher, CreateGistView {

    public static final String GIST_ID = "gist_id";
    public static final String TITLE   = "filename";
    public static final String DESC    = "desc";
    public static final String CODE    = "code";

    public static final String TYPE   = "type";
    public static final String UPDATE = "update";
    public static final String NEW    = "new";

    private static final int    DESCRIPTION_TEXT_MAX_LENGTH = 140;
    private static final String PATH                        = "path";
    private ActivityAddGistBinding binding;
    private String                 path;

    private CreateGistControler mCreateGistControler;
    private String              type;
    private String              filename;

    public static void start(Context context, String path) {
        Intent starter = new Intent(context, GistEditActivity.class);
        starter.putExtra(PATH, path);
        starter.putExtra(TYPE, NEW);
        context.startActivity(starter);
    }

    public static void start(Context context, String gistId, String title, String desc, SpannableString
            code) {
        Intent starter = new Intent(context, GistEditActivity.class);
        starter.putExtra(GIST_ID, gistId);
        starter.putExtra(TITLE, title);
        starter.putExtra(DESC, desc);
        starter.putExtra(CODE, code.toString());
        starter.putExtra(TYPE, UPDATE);

        context.startActivity(starter);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_gist);
        type = getIntent().getStringExtra(TYPE);
        path = getIntent().getStringExtra(PATH);
        filename = getIntent().getStringExtra(TITLE);
        if (TextUtils.isEmpty(filename)) {
            filename = new File(path).getName();
        }

        initView();
        initListener();
        mCreateGistControler = new CreateGistControler(this);
    }

    private void initView() {
        setSupportActionBar(binding.toolbar);
        setTitle(filename);
        binding.toolbar.setNavigationIcon(R.drawable.ic_back);
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        //加载代码数据
        switch (type) {
            case NEW:
                loadCode();
                break;
            case UPDATE:
                String desc = getIntent().getStringExtra(DESC);
                String code = getIntent().getStringExtra(CODE);
                binding.description.setText(desc);
                binding.codeContentTv.setVisibility(View.VISIBLE);
                binding.codeContentTv.setText(CodePattern.formatPyCode(code));
                break;
        }
    }

    /**
     * 通过文件路径获取到文件内容
     */
    private void loadCode() {
//        binding.filename.setText(new File(path).getName());
        binding.progressBar.setVisibility(View.VISIBLE);
        Observable
                .create((Observable.OnSubscribe<String>) subscriber -> {
                    File file = new File(path);
                    String content = TextFileUtils.readTextFile(file);
                    if (content == null) {
                        subscriber.onError(new Throwable("no data"));
                    }
                    subscriber.onNext(content);
                    subscriber.onCompleted();
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnTerminate(() -> binding.progressBar.setVisibility(View.GONE))
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(String s) {
                        binding.codeContentTv.setVisibility(View.VISIBLE);
                        binding.codeContentTv.setText(s);
//                        mCodeReviewDialog.setContent(s);
                    }
                });
    }

    private void initListener() {
        binding.description.addTextChangedListener(this);
//        binding.filename.addTextChangedListener(this);
        binding.sendBtn.setOnClickListener(v -> {
            shareCode();
            SolfKeybroadUtil.showSolfInput(binding.sendBtn, false);
        });

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void afterTextChanged(Editable s) {
        int length = binding.description.length();
        binding.textCount.setText(DESCRIPTION_TEXT_MAX_LENGTH - length + "");
        if (length > DESCRIPTION_TEXT_MAX_LENGTH) {
            binding.textCount.setTextColor(getResources().getColor(R.color.error));
            binding.description.setError(getString(R.string.too_long_hint));
        } else {
            binding.textCount.setTextColor(getResources().getColor(R.color.text_gray));
            binding.description.setError(null);
        }
        if (binding.description.length() > 0
                && binding.description.length() < 140) {
            binding.sendBtn.setClickable(true);
            binding.sendBtn.setTextColor(getResources().getColor(R.color.colorAccent));
        } else {
            binding.sendBtn.setClickable(false);
            binding.sendBtn.setTextColor(getResources().getColor(R.color.text_gray));
        }
    }

    /**
     * 发布代码分享
     */
    private void shareCode() {
        if (loginCheck()) {
            String content = binding.codeContentTv.getText().toString();
            String summary = binding.description.getText().toString();
            boolean isSahre = binding.gistShare.isChecked();
            if (checkData(filename, summary)) {
                switch (type) {
                    case NEW:
                        mCreateGistControler.createGist(
                                filename,
                                summary,
                                content,
                                path.contains(".ipynb") ? "notebook" : "script",
                                isSahre);
                        break;
                    case UPDATE:
                        mCreateGistControler.updateGist(
                                getIntent().getStringExtra(GIST_ID),
                                filename,
                                summary,
                                content);
                        break;
                }
            }
        }
    }

    private boolean checkData(String title, String summary) {
        if (TextUtils.isEmpty(title)) {
            showToast("filename must be not empty!");
            return false;
        }
        if (TextUtils.isEmpty(summary)) {
            showToast("summary must be not empty!");
            return false;
        }
        return true;
    }

    @Override
    public void showLoading() {
        binding.progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        binding.progressBar.setVisibility(View.GONE);
    }

    @Override
    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSuccess(String id) {
        EventBus.getDefault().post(new GistEvent(GistEvent.UPDATE_GIST_SUC));
        //MyGistActivity.startMyShare(this);
        //GistActivity.startCommunity(this);
        finish();
    }

    private boolean loginCheck() {
        User user = App.getUser();
        if (user == null) {
            showToast(getString(R.string.login_first));
            return false;
        } else {
            return true;
        }
    }
}
