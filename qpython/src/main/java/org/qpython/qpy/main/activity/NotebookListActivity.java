package org.qpython.qpy.main.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;

import org.qpython.qpy.R;
import org.qpython.qpy.databinding.ActivityNotebooklistBinding;
import org.qpython.qpy.main.adapter.NotebookAdapter;
import org.qpython.qpysdk.QPyConstants;

import java.io.File;

/**
 * 文 件 名: NotebookListActivity
 * 创 建 人: ZhangRonghua
 * 创建日期: 2018/2/8 15:38
 * 修改时间：
 * 修改备注：
 */

public class NotebookListActivity extends BaseActivity {

    private ActivityNotebooklistBinding mBinding;
    private NotebookAdapter mNotebookAdapter;
    private File mRootFile;
    private static final String sdcard = Environment.getExternalStorageDirectory().getAbsolutePath();

    public static void start(Context context){
        Intent intent = new Intent(context,NotebookListActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_notebooklist);
        initRootDir();
        initToolbar();
        initRV();
        setParentPath(mRootFile.getAbsolutePath());
    }

    private void initRootDir() {
        mRootFile = new File(QPyConstants.ABSOLUTE_PATH+"/notebooks");
        if (!mRootFile.exists()) {
            mRootFile.mkdirs();
        }
    }

    private void initRV() {
        mBinding.rvFiles.setLayoutManager(new LinearLayoutManager(this));
        mNotebookAdapter = new NotebookAdapter(this, mRootFile);
        mNotebookAdapter.setOnItemClickCallback((filePath,isDir) -> {
            if (isDir){
                setParentPath(filePath);
            }else {
                NotebookActivity.start(this, filePath, false);
                finish();
            }
        });
        mBinding.rvFiles.setAdapter(mNotebookAdapter);
    }

    private void initToolbar() {
        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mBinding.toolbar.setNavigationIcon(R.drawable.ic_back);
        mBinding.toolbar.setNavigationOnClickListener(v -> finish());
        mBinding.toolbar.setTitle("Notebooks");
    }

    private void setParentPath(String path){
        if (path.contains(sdcard)){
            String p = path.substring(sdcard.length());
            mBinding.tvPath.setText(p);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        if (mNotebookAdapter.getFile().getAbsoluteFile().equals(mRootFile.getAbsoluteFile())) {
            finish();
        } else {
            mNotebookAdapter.setFile(mNotebookAdapter.getFile().getParentFile());
        }
    }
}
