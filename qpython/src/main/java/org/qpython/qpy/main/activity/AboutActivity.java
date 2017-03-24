package org.qpython.qpy.main.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.widget.RelativeLayout;

import com.quseit.common.updater.Updater;
import com.quseit.common.updater.callback.DialogCallback;
import org.qpython.qpy.R;
import org.qpython.qpy.plugin.activity.PluginManagerActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by mathiasluo on 16-5-29.
 */

public class AboutActivity extends BaseActivity {
    private Unbinder mUnbinder;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.setting_about_layout)
    RelativeLayout mAboutLayout;
    @BindView(R.id.setting_feedback_layout)
    RelativeLayout mFeedbackLayout;
    @BindView(R.id.setting_mark_layout)
    RelativeLayout mMarkLayout;
    @BindView(R.id.setting_lib_manager)
    RelativeLayout mLibManagerLayout;
    @BindView(R.id.setting_sl4a_server)
    RelativeLayout mSl4aServerLayout;
    @BindView(R.id.setting_normal_program)
    RelativeLayout mNormalProgramLayout;
    @BindView(R.id.setting_qpypi)
    RelativeLayout mQpypiLayout;
    @BindView(R.id.setting_internal_space)
    RelativeLayout mClearSpaceLayout;
    @BindView(R.id.setting_ftp_server)
    RelativeLayout mFtpServerLayout;


    public static final void startActivity(Context context) {
        Intent intent = new Intent(context, AboutActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        mUnbinder = ButterKnife.bind(this);
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
    }

    private void init() {
        mToolbar.setTitle(getString(R.string.setting_about));
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        mToolbar.setNavigationOnClickListener(view -> AboutActivity.this.finish());
    }

    @OnClick(R.id.setting_update)
    void checkUpdate() {
        Updater.checkUpdate(new DialogCallback(this, false));
    }

    @OnClick(R.id.setting_lib_manager)
    void startPluginManagerActivity() {
        PluginManagerActivity.start(this);
    }
}
