package org.qpython.qpy.main.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.qpython.qpy.R;
import org.qpython.qpy.databinding.ActivitySignInBinding;
import org.qpython.qpy.main.app.App;
import org.qpython.qpy.main.app.CONF;
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

    private LoginControler mLoginControler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_in);
        binding.textView3.setText(Html.fromHtml(getString(R.string.by_signing_in_you_agree_to_out_privacy_policy_term_of_service)));
        initListener();

        mLoginControler = new LoginControler(this);
    }

    private void initListener() {
        binding.textView3.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_user_private)))));
        binding.button2.setOnClickListener(v -> signIn());
        binding.button3.setOnClickListener(v -> finish());
    }

    private void signIn() {
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
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
        Toast.makeText(SignInActivity.this, msg, Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    public void loginSuccess() {
        Log.d("SignInActivity", "loginSuccess");
//        setResult(RESULT_OK);
//        this.finish();

        setResult(RESULT_OK);
        binding.progressBar2.setVisibility(View.GONE);
        showToast("login successfully");
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLoginControler!=null) {

            mLoginControler.onDestroy();
        }
    }
}