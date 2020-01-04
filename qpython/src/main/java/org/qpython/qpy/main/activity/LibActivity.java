package org.qpython.qpy.main.activity;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.qpython.qpy.R;
import org.qpython.qpy.databinding.ActivityLibBinding;
import org.qpython.qpy.main.adapter.LibPagerAdapter;
import org.qpython.qpy.main.fragment.RefreshFragment;
import org.qpython.qpy.main.receiver.DownloadReceiver;

/**
 * Manager Lib
 * Created by Hmei on 2017-05-27.
 */

public class LibActivity extends AppCompatActivity {
    private static final String OPEN_AIPY_ACTION = "org.qpython.qpy.OPEN_AIPY_FRAGMENT";
    private ActivityLibBinding binding;
    private DownloadReceiver   receiver;
    private int                position;
    private LibPagerAdapter    adapter;

    public static void start(Context context) {
        Intent starter = new Intent(context, LibActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, new IntentFilter(
                DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_lib);
        receiver = new DownloadReceiver();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        setTitle(R.string.lib_manager);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(v -> finish());

        initTabs();
        handleIntent();
    }

    private void handleIntent() {
        Intent intent = getIntent();
        String action = intent.getAction() == null ? "" : intent.getAction();
        switch (action) {
            case OPEN_AIPY_ACTION:
                binding.vp.setCurrentItem(2);
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
        try {
            if (receiver != null) {
                unregisterReceiver(receiver);
            }
        } catch (IllegalArgumentException ignore) {
            // TODO: 2017-06-21 reopen this activity may cause crash, have not find the reason yet
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.refresh_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.refresh_menu) {
            Fragment currentFragment = adapter.getItem(position);
            if (currentFragment instanceof RefreshFragment) {
                ((RefreshFragment) currentFragment).refresh(true);
            }
        }
        return true;
    }

    private void initTabs() {
        binding.tabs.addTab(binding.tabs.newTab().setText(R.string.tools));
        binding.tabs.addTab(binding.tabs.newTab().setText(R.string.qpypi));
        binding.tabs.addTab(binding.tabs.newTab().setText(R.string.aipy));
        binding.tabs.setTabGravity(TabLayout.GRAVITY_FILL);
        binding.tabs.setTabTextColors(getResources().getColor(R.color.white), getResources().getColor(R.color.colorAccent));
        binding.tabs.setSelectedTabIndicatorColor(getResources().getColor(R.color.colorAccent));
        adapter = new LibPagerAdapter(getSupportFragmentManager());
        binding.vp.setAdapter(adapter);

        binding.vp.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(binding.tabs));
        binding.tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                binding.vp.setCurrentItem(tab.getPosition());
                position = tab.getPosition();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(DownloadFinishedEvent event) {
        if (receiver != null) {
            receiver.setEnqueue(event.queueId);
        }
    }

    public static class DownloadFinishedEvent {
        public long queueId;
    }
}
