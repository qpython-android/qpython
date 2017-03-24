package org.qpython.qpy.plugin.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.widget.FrameLayout;
import android.widget.Toast;

import org.qpython.qpy.R;

import org.qpython.qpy.main.activity.BaseActivity;
import org.qpython.qpy.plugin.LocalPluginManager;
import org.qpython.qpy.plugin.view.FileSelectView;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class LocalPluginInstallActivity extends BaseActivity {
    private static final String sPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
    private static final int REQUEST_READ_EXTERNAL_STORAGE = 0;

    FileSelectView mFileSelectView;
    private Unbinder mUnbinder;

    @BindView(R.id.container)
    FrameLayout mContainer;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_plugin_install);
        mUnbinder = ButterKnife.bind(this);

        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);
        mToolbar.setNavigationOnClickListener(v -> finish());
        mToolbar.setTitle(R.string.title_select_plugin);
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkPermission();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, R.string.toast_read_permission_deny, Toast.LENGTH_SHORT).show();
                finish();
            } else {
                show();
            }
        }
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            int checkPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE);
            } else {
                show();
            }
        } else {
            show();
        }
    }

    private void show() {
        // 初始化文件浏览器
        Map<String, Integer> icon = new HashMap<>();

        icon.put(FileSelectView.PARENT_DIR, R.drawable.ic_file_back);//返回上一层的图标
        icon.put(FileSelectView.FOLDER, R.drawable.ic_file_dir);//文件夹图标
        icon.put("py", R.drawable.ic_plugin_blue);

        mFileSelectView = new FileSelectView(this, file -> {
            if (LocalPluginManager.install(file)) {
                LocalPluginInstallActivity.this.finish();
            }
        }, icon);

        mFileSelectView.setPath(sPath);
        mContainer.addView(mFileSelectView);
    }
}
