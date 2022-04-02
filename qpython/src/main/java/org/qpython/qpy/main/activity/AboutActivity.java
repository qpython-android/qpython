package org.qpython.qpy.main.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.qpython.qpy.R;
import org.qpython.qpy.databinding.ActivityAboutBinding;
import org.qpython.qpy.main.app.App;
import org.qpython.qpy.main.server.MySubscriber;
import org.qpython.qpy.main.server.model.UpdateModel;
import org.qpython.qpy.texteditor.androidlib.common.MiscUtils;


/**
 * About page
 * Created by Hmei on 2017-05-27.
 */

public class AboutActivity extends BaseActivity {
    ActivityAboutBinding binding;
    ProgressDialog       progressDialog;

    public static void start(Context context) {
        Intent starter = new Intent(context, AboutActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_about);
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            binding.version.setText(getString(R.string.v_version, version));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.checking_update));
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        setTitle(R.string.about);
        myToolbar.setNavigationIcon(R.drawable.ic_back);
        myToolbar.setNavigationOnClickListener(view -> finish());

        initListener();
        checkUpdate(true);
    }

    private void initListener() {
        binding.tvStar.setOnClickListener(v -> {
            Intent viewIntent = new Intent("android.intent.action.VIEW",
                    Uri.parse("market://details?id=" + getPackageName()));
            startActivity(viewIntent);
        });

        binding.tvPrivacy.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_user_private)));
            startActivity(browserIntent);
        });

        binding.tvFeedback.setOnClickListener(v -> onFeedback(""));

        binding.tvUpdate.setOnClickListener(v -> {
            checkUpdate(false);
            progressDialog.show();
        });

        binding.tvThanks.setOnClickListener(v ->
            startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(getString(R.string.thanks_link))))
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.about_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_mail:
//                MiscUtils.sendEMailTo(getString(R.string.ui_mail), getPackageManager().getApplicationLabel(getApplicationInfo()).toString());
                onFeedback("");
                break;
            case R.id.menu_share:
                MiscUtils.share(this, getString(R.string.share_text));
                break;
        }
        return true;
    }

    private void checkUpdate(boolean open) {
        App.getService().checkUpdate(new MySubscriber<UpdateModel>() {
            @Override
            public void onCompleted() {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onNext(UpdateModel updateModel) {
                PackageInfo pInfo;
                try {
                    pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                    int verCode = pInfo.versionCode;
                    String verName = pInfo.versionName;
                    if (verCode < updateModel.getApp().getVer()) {
                        binding.tvNewHint.setText("");
                        binding.tvNewHint.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_about_new, 0);
                        if (open) {
                            return;// 打开界面无需显示dialog
                        }
                        AlertDialog alertDialog = new AlertDialog.Builder(AboutActivity.this, R.style.MyDialog)
                                .setTitle(R.string.download_apk)
                                .setMessage(R.string.download_apk_hint)
                                .setPositiveButton(R.string.confirm,
                                        ((dialog, which) ->
                                                App.getService().downloadFile(
                                                        updateModel.getApp().getLink(),
                                                        getString(R.string.app_name),
                                                        getString(R.string.downloading_new_apk))))
                                .setNegativeButton(R.string.cancel, null)
                                .create();
                        alertDialog.show();
                    } else {
                        binding.tvNewHint.setText(R.string.already_newest);
                        if (open) {
                            return;
                        }
                        Toast.makeText(AboutActivity.this, getString(R.string.already_newest_current_version) + " " + verName, Toast.LENGTH_LONG).show();
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
