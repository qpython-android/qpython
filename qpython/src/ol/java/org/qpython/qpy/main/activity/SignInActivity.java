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

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

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

public class SignInActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener ,LoginView{
    private static final int RC_SIGN_IN = 54503;

    private GoogleApiClient mGoogleApiClient;
    private ActivitySignInBinding binding;
    private FirebaseAuth mAuth;

    private LoginControler mLoginControler;

    {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        initUserInfo(currentUser);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_in);
        binding.textView3.setText(Html.fromHtml(getString(R.string.by_signing_in_you_agree_to_out_privacy_policy_term_of_service)));
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(CONF.GOOGLE_ID_TOKEN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addOnConnectionFailedListener(connectionResult -> showToast(getString(R.string.lost_google_hint)))
                .build();
        initListener();

        mLoginControler = new LoginControler(this);
    }

    private void initListener() {
        binding.textView3.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.privacy_html)))));
        binding.button2.setOnClickListener(v -> signIn());
        binding.button3.setOnClickListener(v -> finish());
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                Log.e("LOGIN", result.getStatus().toString());
                try {
                    String msg = result.getStatus().getStatusMessage().trim();
                    showToast(getString(R.string.login_error) + (msg.equals("") ? "" : ": ") + msg);
                    initUserInfo(null);
                } catch (NullPointerException ignore) {
                    showToast(getString(R.string.no_google));
                }
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        showLoading();
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), "45:FD:60:98:01:9A:37:D9:84:03:06:36:02:F6:85:2C:A2:1F:B8:67");
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        initUserInfo(user);
                    } else {
                        showToast(getString(R.string.auth_failed));
                        initUserInfo(null);
                        hideLoading();
                    }
                });
    }

    private void initUserInfo(FirebaseUser currentUser) {
        if (currentUser == null) {
            App.setUser(null);
            return;
        }
        Log.d("SingInActivity", "NICK:"+currentUser.getDisplayName()+"-UN:"+currentUser.getEmail());
        User user = new User();
        user.setUserId(currentUser.getUid());
        user.setAvatarUrl(currentUser.getPhotoUrl() == null ? "" : currentUser.getPhotoUrl().toString());
        user.setEmail(currentUser.getEmail());
        user.setUserName(currentUser.getEmail());
        user.setNick(currentUser.getDisplayName());
        App.setUser(user);
        if (mLoginControler!=null) {
            mLoginControler.login(user);
        } else {
            Toast.makeText(this, R.string.signin_error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        initUserInfo(null);
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