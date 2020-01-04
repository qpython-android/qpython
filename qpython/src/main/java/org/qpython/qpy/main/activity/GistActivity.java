package org.qpython.qpy.main.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.qpython.qpy.R;
import org.qpython.qpy.databinding.ActivityCommunityBinding;
import org.qpython.qpy.main.adapter.ViewPagerAdapter;
import org.qpython.qpy.main.fragment.GistFragment;
import org.qpython.qpy.main.server.gist.TokenManager;
import org.qpython.qpy.main.server.gist.indexScreen.GistHomeControler;
import org.qpython.qpy.main.server.gist.indexScreen.GistHomeView;
import org.qpython.qpy.main.server.gist.response.GistBean;
import org.qpython.qpy.main.server.model.CourseAdModel.QpyBean.ExtAdBean.FeaturedBean;

import java.util.ArrayList;
import java.util.List;


/**
 * 文 件 名: GistActivity
 * 创 建 人: ZhangRonghua
 * 创建日期: 2017/12/25 15:41
 * 邮   箱: qq798435167@gmail.com
 * 修改时间：
 * 修改备注：
 */

public class GistActivity extends BaseActivity implements GistHomeView {

    private ActivityCommunityBinding binding;
    private List<FeaturedBean>       mADBeans;
    private GistHomeControler        mGistHomeControler;
    private GistFragment             latestFragment, featureFragment;
    private int scrollHeight;// 记录滚动超出多少后显示顶部的Tab

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_community);
        mGistHomeControler = new GistHomeControler(this);
        initToolbar(getString(R.string.gist));
        initTab(binding.tabs.root);
        initTab(binding.topTabs.root);
        initFragment();
        initListener();
        getData();
        getAD();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void loadMore() {
        mGistHomeControler.loadMore();
    }

    private void initToolbar(String title) {
        setSupportActionBar(binding.lt.toolbar);
        binding.lt.toolbar.setNavigationIcon(R.drawable.ic_back);
        binding.lt.toolbar.setNavigationOnClickListener(v -> finish());
        setTitle(title);
    }

    private void initTab(TabLayout tabLayout) {
        tabLayout.addTab(tabLayout.newTab().setText(R.string.newest_course));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.featured));
        tabLayout.setTabTextColors(getResources().getColor(R.color.white), getResources().getColor(R.color.colorAccent));
        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.colorAccent));

        binding.vp.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                binding.vp.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void initFragment() {
        List<Fragment> fragments = new ArrayList<>();
        latestFragment = new GistFragment();
        featureFragment = new GistFragment();
        fragments.add(latestFragment);
        fragments.add(featureFragment);

        ViewPagerAdapter adapter = new ViewPagerAdapter<>(getSupportFragmentManager(), fragments);
        binding.vp.setAdapter(adapter);
        binding.vp.setOffscreenPageLimit(2);
    }

    private void initListener() {
        binding.adSlide.setOnUrlBackCall(i -> {
            FeaturedBean bean = mADBeans.get(i);
            QWebViewActivity.start(this, bean.getAd_man(), bean.getAd_link());
        });
        binding.getRoot().getViewTreeObserver().addOnGlobalLayoutListener(() -> scrollHeight = binding
                .adSlide.getHeight());

        binding.scroll.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (scrollY > scrollHeight) {
                binding.topTabs.root.setVisibility(View.VISIBLE);
            } else {
                binding.topTabs.root.setVisibility(View.GONE);
            }
        });
    }

    public static void startCommunity(Context context) {
        Intent intent = new Intent(context, GistActivity.class);
        context.startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!TextUtils.isEmpty(TokenManager.getToken())) {
            getMenuInflater().inflate(R.menu.community_menu, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.news_edit) {
            MyGistActivity.startMyShare(this);
        }
        return super.onOptionsItemSelected(item);
    }

    public void getData() {
        mGistHomeControler.refresh();
    }

    public void getAD() {
        mGistHomeControler.getAD();
    }

    @Override
    public void showError(String msg) {
    }

    @Override
    public void hideError() {
    }

    @Override
    public void setAD(List<FeaturedBean> list) {
        mADBeans = list;
        List<String> urls = new ArrayList<>();
        for (FeaturedBean featuredBean : list) {
            urls.add(featuredBean.getAd_img());
        }
        binding.adSlide.setImagesFromUrl(urls);
    }

    @Override
    public void hideAd() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void showLoading() {
    }

    @Override
    public void hideLoading() {
        binding.progressBar.setVisibility(View.GONE);
    }

    @Override
    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setData(List<GistBean> list) {
        ArrayList<GistBean> latest = new ArrayList<>(), feature = new ArrayList<>();
        for (GistBean gistBean : list) {
            if (gistBean.isFeature()) {
                feature.add(gistBean);
            } else {
                latest.add(gistBean);
            }
        }
        latestFragment.setDataList(latest);
        featureFragment.setDataList(feature);
    }

    @Override
    public void loadMoreGist(List<GistBean> list) {
//        mCommunityAdapter.loadMore(list);
    }

    @Override
    public void favorite(boolean is) {

    }
}
