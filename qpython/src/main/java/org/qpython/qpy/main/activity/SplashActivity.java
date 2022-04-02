package org.qpython.qpy.main.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Toast;

import com.huawei.hms.analytics.HiAnalytics;
import com.huawei.hms.analytics.HiAnalyticsInstance;
import com.quseit.util.Log;
import com.xiaomi.mipush.sdk.MiPushMessage;
import com.xiaomi.mipush.sdk.PushMessageHelper;

import org.qpython.qpy.R;
import org.qpython.qpy.databinding.ActivitySplashBinding;
import org.qpython.qpy.main.app.App;
import org.qpython.qpy.main.widget.MyCheckTextView;
import org.qpython.qpy.utils.BrandUtil;
import org.qpython.qpy.utils.JumpToUtils;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends AppCompatActivity implements View.OnClickListener, MyCheckTextView.ClickListener {
    private final static String TAG = "SplashActivity";
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
        binding.tvContent.setText(R.string.user_agreement_text);
    }

    private void judgeAgreementStatus() {
        if (App.getAgreementStatus()){
            delayJumpToMain();
            initHuaweiAnalytics();
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

        if(getIntent() != null) {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                intent.putExtras(bundle);
            }
        }

        startActivity(intent);
        finish();
    }

    /**
     * 初始化华为分析
     */
    private void initHuaweiAnalytics() {
        if(BrandUtil.isBrandHuawei()) {
            //判断华为设备，初始化分析实例
            HiAnalyticsInstance instance = HiAnalytics.getInstance(this);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_positive) {
            if(binding.cbxAgreeContent.isChecked()) {
                App.setAgreementStatus(true);
                App.initLibs(App.appInstance);
                initHuaweiAnalytics();
                jumpToMain();
            } else {
                Toast.makeText(this, R.string.user_aggreement_toast,
                        Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.tv_negative){
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
//        QWebViewActivity.start(this, getString(R.string.privacy_agreement), getString(R.string.url_user_private));
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_user_private)));
        startActivity(browserIntent);
    }

    private void goServiceAgreement() {
//        QWebViewActivity.start(this, getString(R.string.service_agreement), getString(R.string.url_user_agreement));
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_user_agreement)));
        startActivity(browserIntent);
    }
}