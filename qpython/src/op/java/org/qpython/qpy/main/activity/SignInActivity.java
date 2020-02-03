package org.qpython.qpy.main.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

//import com.hipipal.qpyplus.wxapi.WXAPIManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.qpython.qpy.R;
import org.qpython.qpy.databinding.ActivitySignInBinding;
import org.qpython.qpy.main.app.App;
import org.qpython.qpy.main.app.User;
import org.qpython.qpy.main.server.gist.loginScreen.LoginControler;
import org.qpython.qpy.main.server.gist.loginScreen.LoginView;

/**
 * SignIn
 * Created by Hmei on 2017-08-04.
 */

public class SignInActivity extends AppCompatActivity implements LoginView{
    private static final int RC_SIGN_IN = 54503;

    private ActivitySignInBinding binding;
    private LoginControler        mLoginControler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_in);
        binding.textView3.setText(Html.fromHtml(getString(R.string.by_signing_in_you_agree_to_out_privacy_policy_term_of_service)));
        initListener();
        EventBus.getDefault().register(this);
        mLoginControler = new LoginControler(this);
    }

    private void initListener() {
        binding.textView3.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.privacy_html)))));
        binding.button2.setOnClickListener(v -> signIn());
        binding.button3.setOnClickListener(v -> finish());
    }

    @Subscribe
    public void loginFail(String result) {
        Toast.makeText(this, R.string.login_failed, Toast.LENGTH_SHORT).show();
    }

    @Subscribe
    public void loginSuccess(User user) {
        Log.d("SignInActivity", "loginSuccess");

    }

    private void signIn() {

//        WXAPIManager.wxLogin();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mLoginControler.onDestroy();
    }

    @Override
    public void showLoading() {
        binding.progressBar2.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        binding.progressBar2.setVisibility(View.GONE);
    }

    @Override
    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void loginSuccess() {
        setResult(RESULT_OK);
        finish();
    }
}
