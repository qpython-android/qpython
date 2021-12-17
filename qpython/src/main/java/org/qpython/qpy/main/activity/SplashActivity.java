package org.qpython.qpy.main.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;

import org.qpython.qpy.R;
import org.qpython.qpy.databinding.ActivitySplashBinding;
import org.qpython.qpy.main.app.App;
import org.qpython.qpy.main.widget.MyCheckTextView;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends AppCompatActivity implements View.OnClickListener, MyCheckTextView.ClickListener {

    ActivitySplashBinding binding;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash);
//        setContentView(R.layout.activity_splash);
        initClick();
        initData();
    }

    private void initClick() {
        binding.tvPositive.setOnClickListener(this);
        binding.tvNegative.setOnClickListener(this);
    }

    private void initData() {
        setContent();
        setAgreeContent();
        judgeAgreementStatus();
    }

    private void setAgreeContent() {
        String one = getString(R.string.user_agreement_bottom_split1);
        String two = getString(R.string.user_agreement_bottom_split2);
        String three = getString(R.string.user_agreement_bottom_split3);
        String four = getString(R.string.user_agreement_bottom_split4);

        String content = one + two + three + four;
        SpannableString str = new SpannableString(content);
        str.setSpan(new MyCheckTextView(getApplicationContext(), 0, R.color.color_498fdd, this), one.length(), one.length() + two.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        str.setSpan(new MyCheckTextView(getApplicationContext(), 1, R.color.color_498fdd, this), one.length() + two.length() + three.length(), one.length() + two.length() + three.length() + four.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        binding.tvAgreeContent.setText(str);

        //不设置 没有点击事件
        binding.tvAgreeContent.setMovementMethod(LinkMovementMethod.getInstance());
        //设置点击后的颜色为透明
        binding.tvAgreeContent.setHighlightColor(Color.TRANSPARENT);
    }

    private void setContent() {
//        StringBuffer buffer = new StringBuffer();
//        buffer.append("1.为了给您提供发布服务，我们可能会向您申请摄像头权限、麦克风权限、收集存储权限；\n");
//        buffer.append("2.为了向您推荐您附近的村庄，我们会向您申请位置权限；\n");
//        buffer.append("3.为了账号安全，我们会向您申请系统设备权限收集设备信息、日志信息；\n");
//        buffer.append("4.我们会努力采取各种安全技术保护您的个人信息。未经您同意，我们不会从第三方获取、共享或对外提供您的信息；\n");
//        buffer.append("5.您还可以访问、更正、删除您的个人信息，我们为您提供了注销、投诉等多种不同方式。");
        binding.tvContent.setText(R.string.user_agreement_text);
    }

    private void judgeAgreementStatus() {
        if (App.getAgreementStatus()){
            delayJumpToMain();
        }else{
            binding.clAgreeLayout.setVisibility(View.VISIBLE);
        }
    }

    private void delayJumpToMain() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                jumpToMain();
            }
        }, 1000);
    }

    private void jumpToMain(){
        Intent intent = new Intent(SplashActivity.this, HomeMainActivity.class);
        intent.setAction(getIntent().getAction());
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_positive){
            App.setAgreementStatus(true);
            jumpToMain();
            return;
        }
        if (id == R.id.tv_negative){
            finish();
        }
    }

    @Override
    public void click(int mark) {
        if (mark == 0) {
            goServiceAgreement();
        }else{
            goPrivacyAgreement();
        }
    }

    private void goPrivacyAgreement() {
        QWebViewActivity.start(this, getString(R.string.privacy_agreement), "https://www.qpython.org/privacy-cn.html");
    }

    private void goServiceAgreement() {
        QWebViewActivity.start(this, getString(R.string.service_agreement), "https://www.qpython.org/agreement-cn.html");
    }
}