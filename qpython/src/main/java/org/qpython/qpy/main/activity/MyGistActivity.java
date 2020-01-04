package org.qpython.qpy.main.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import org.qpython.qpy.R;
import org.qpython.qpy.databinding.ActivityMyShareBinding;
import org.qpython.qpy.main.adapter.MyShareFragmentAdapter;

/**
 * 文 件 名: MyGistActivity
 * 创 建 人: ZhangRonghua
 * 创建日期: 2017/12/26 14:32
 * 邮   箱: qq798435167@gmail.com
 * 修改时间：
 * 修改备注：
 */

public class MyGistActivity extends BaseActivity {
    private ActivityMyShareBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_share);
        initToolbar();
        initView();
    }

    private void initToolbar() {
        setSupportActionBar(binding.lt.toolbar);
        setTitle(R.string.my_share);
        binding.lt.toolbar.setNavigationIcon(R.drawable.ic_back);
        binding.lt.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void initView() {
        MyShareFragmentAdapter mMyShareFragmentAdapter = new MyShareFragmentAdapter(getSupportFragmentManager());
        binding.vp.setAdapter(mMyShareFragmentAdapter);
        binding.tabs.setupWithViewPager(binding.vp);
    }

    public static void startMyShare(Context context) {
        Intent intent = new Intent(context, MyGistActivity.class);
        context.startActivity(intent);
    }


    public void showProgress(boolean show) {
        if (show) {
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
