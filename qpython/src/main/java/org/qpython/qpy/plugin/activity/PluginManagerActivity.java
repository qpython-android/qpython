package org.qpython.qpy.plugin.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import org.qpython.qpy.R;

import org.qpython.qpy.plugin.adapter.PluginPagerAdapter;

public class PluginManagerActivity extends AppCompatActivity {

    Toolbar mToolbar;

    TabLayout mTabLayout;

    ViewPager mViewPager;

    public static void start(Context context) {
        Intent starter = new Intent(context, PluginManagerActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plugin_manager);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);

        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(v -> finish());
    }

    @Override
    protected void onStart() {
        super.onStart();
        PagerAdapter adapter = new PluginPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
