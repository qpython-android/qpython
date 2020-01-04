package org.qpython.qpy.main.activity;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import org.qpython.qpy.R;
import org.qpython.qpy.databinding.ActivityCodeShareBinding;
import org.qpython.qpy.main.adapter.ViewPagerAdapter;
import org.qpython.qpy.main.fragment.CodeFragment;

import java.util.ArrayList;
import java.util.List;

public class CodeShareActivity extends AppCompatActivity {
    ActivityCodeShareBinding binding;
    ArgbEvaluator evaluator = new ArgbEvaluator();

    public static void start(Context context) {
        Intent starter = new Intent(context, CodeShareActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_code_share);
        initView();
        initFragment();
    }

    private void initView() {
        setSupportActionBar(binding.lt.toolbar);
        binding.lt.toolbar.setNavigationIcon(R.drawable.ic_back);
        binding.lt.toolbar.setNavigationOnClickListener(v -> finish());
        setTitle(R.string.community);
    }

    private void initFragment() {
        binding.btnProject.setOnClickListener(v -> binding.viewPager.setCurrentItem(0));
        binding.btnScript.setOnClickListener(v -> binding.viewPager.setCurrentItem(1));

        List<CodeFragment> fragments = new ArrayList<>();
        fragments.add(CodeFragment.newInstance(CodeFragment.PROGRAM));
        fragments.add(CodeFragment.newInstance(CodeFragment.SCRIPT));
        binding.viewPager.setAdapter(new ViewPagerAdapter<>(getSupportFragmentManager(), fragments));
        binding.viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                int showColor = (int) evaluator.evaluate(positionOffset, 0XFF4A4A4A, 0XFF4BAC07);
                int dismissColor = (int) evaluator.evaluate(positionOffset, 0XFF4BAC07, 0XFF4A4A4A);
                int textShowColor = (int) evaluator.evaluate(positionOffset, 0XFFFFFFFF, 0XFF4BAC07);
                int textDismissColor = (int) evaluator.evaluate(positionOffset, 0XFF4BAC07, 0XFFFFFFFF);
                if (position == 0) {
                    binding.btnProject.setTextColor(textDismissColor);
                    binding.btnScript.setTextColor(textShowColor);
                    binding.lineOne.setBackgroundColor(dismissColor);
                    binding.lineTwo.setBackgroundColor(showColor);
                } else {
                    binding.btnProject.setTextColor(textShowColor);
                    binding.btnScript.setTextColor(textDismissColor);
                    binding.lineOne.setBackgroundColor(showColor);
                    binding.lineTwo.setBackgroundColor(dismissColor);
                }
            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        binding.btnProject.setSelected(true);
                        binding.btnScript.setSelected(false);
                        break;
                    case 1:
                        binding.btnProject.setSelected(false);
                        binding.btnScript.setSelected(true);
                        break;
                }
            }
        });
    }
}
