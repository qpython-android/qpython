package org.qpython.qpy.main.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.quseit.util.ImageDownLoader;

import org.qpython.qpy.R;
import org.qpython.qpy.codeshare.CONSTANT;
import org.qpython.qpy.codeshare.ShareCodeUtil;
import org.qpython.qpy.databinding.ActivityUserBinding;
import org.qpython.qpy.main.app.App;
import org.qpython.qpy.main.app.CONF;

import java.io.File;

public class UserActivity extends AppCompatActivity {
    ActivityUserBinding binding;

    public static void start(Context context) {
        Intent starter = new Intent(context, UserActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user);
        setSupportActionBar(binding.toolbar);
        setTitle(getString(R.string.me));
        binding.toolbar.setNavigationIcon(R.drawable.ic_back);
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        ImageDownLoader.setImageFromUrl(this, binding.avatar, App.getUser().getAvatarUrl());
        binding.name.setText(App.getUser().getNick());
        //binding.email.setText(App.getUser().getEmail());

        binding.usage.setText(R.string.my_space_empty);
        binding.logout.setOnClickListener(v -> logout());
        binding.myShareLayout.setOnClickListener(v -> MyGistActivity.startMyShare(UserActivity.this));
        setUsage();
    }

    private void setUsage() {
    }

    private void logout() {

    }
}