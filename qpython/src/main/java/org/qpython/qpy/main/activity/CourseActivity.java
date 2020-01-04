package org.qpython.qpy.main.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.quseit.util.ACache;

import org.qpython.qpy.R;
import org.qpython.qpy.databinding.ActivityCourseBinding;
import org.qpython.qpy.main.adapter.ViewPagerAdapter;
import org.qpython.qpy.main.app.App;
import org.qpython.qpy.main.fragment.CourseFragment;
import org.qpython.qpy.main.fragment.MyCourseFragment;
import org.qpython.qpy.main.server.CacheKey;
import org.qpython.qpy.main.server.MySubscriber;
import org.qpython.qpy.main.server.model.CourseAdModel;
import org.qpython.qpy.main.server.model.CourseModel;
import org.qpython.qsl4a.qsl4a.LogUtil;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Course
 * Created by Hmei on 2017-06-28.
 */

public class CourseActivity extends AppCompatActivity {
    private ActivityCourseBinding binding;
    private CourseFragment        recommend, latest;
    private MyCourseFragment my;
    private String TAG = "CourseActivity";

    private int scrollHeight;// 记录滚动超出多少后显示顶部的Tab
    private List<CourseModel> recommendList = new ArrayList<>(), latestList = new ArrayList<>();

    public static void start(Context context) {
        Intent starter = new Intent(context, CourseActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_course);
        binding.topTabs.root.setVisibility(View.GONE);
        setSupportActionBar(binding.lt.toolbar);
        binding.lt.toolbar.setNavigationIcon(R.drawable.ic_back);
        binding.lt.toolbar.setNavigationOnClickListener(v -> finish());

        setTitle(getString(R.string.course));
        initTab(binding.tabs.root);
        initTab(binding.topTabs.root);
        initFragment();
        initListener();
        initData();
        initAD();
    }

    @Override
    protected void onDestroy() {
        LogUtil.d("onDestroy");
        binding.adSlide.stop();
        super.onDestroy();
        //binding.adSlide;
    }

    private void initTab(TabLayout tabLayout) {
        tabLayout.addTab(tabLayout.newTab().setText(R.string.newest_course));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.featured));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.my));
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
        recommend = new CourseFragment();
        recommend.setTypeVisible(true);
        latest = new CourseFragment();
        latest.setTypeVisible(false);
        my = new MyCourseFragment();

        List<Fragment> fragments = new ArrayList<>();
        fragments.add(latest);
        fragments.add(recommend);
        fragments.add(my);

        ViewPagerAdapter adapter = new ViewPagerAdapter<>(getSupportFragmentManager(), fragments);
        binding.vp.setAdapter(adapter);
        binding.vp.setOffscreenPageLimit(3);
    }

    private void initListener() {

        binding.root.getViewTreeObserver().addOnGlobalLayoutListener(() -> scrollHeight =
                binding.adSlide.getHeight());

        binding.scroll.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (scrollY > scrollHeight) {
                binding.topTabs.root.setVisibility(View.VISIBLE);
            } else {
                binding.topTabs.root.setVisibility(View.GONE);
            }
        });
    }

    private void initData() {
        App.getService().getCourse()
                .subscribeOn(Schedulers.io())
                .flatMap(Observable::from)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MySubscriber<CourseModel>() {
                    @Override
                    public void onNext(CourseModel o) {
                        switch (o.getCat()) {
                            case "new":
                                latestList.add(o);
                                break;
                            case "featured":
                                recommendList.add(o);
                                break;
                        }
                    }

                    @Override
                    public void onCompleted() {
                        ACache.get(CourseActivity.this).put(CacheKey.COURSE_RECOMMEND, App.getGson().toJson(recommendList), ACache.TIME_HOUR);
                        ACache.get(CourseActivity.this).put(CacheKey.COURSE_LATEST, App.getGson().toJson(latestList), ACache.TIME_HOUR);
                        binding.progressBar.setVisibility(View.GONE);
                        recommend.setDataList(recommendList);
                        latest.setDataList(latestList);
                    }

                    @Override
                    public void onError(Throwable e) {
                        binding.progressBar.setVisibility(View.GONE);
                        recommend.setDataList(recommendList);
                        latest.setDataList(latestList);
                    }
                });
    }

    private void initAD() {
        List<String> url = new ArrayList<>(), imageUrl = new ArrayList<>(), title = new ArrayList<>();
        App.getService().getCourseAd()
                .subscribeOn(Schedulers.io())
                .flatMap(courseAdModel -> Observable.from(courseAdModel.getQpy().getExt_ad().getFeatured()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CourseAdModel.QpyBean.ExtAdBean.FeaturedBean>() {
                    @Override
                    public void onCompleted() {
                        binding.adSlide.setImagesFromUrl(imageUrl);
                        binding.adSlide.setOnUrlBackCall(i -> QWebViewActivity.start(CourseActivity.this, getString(R.string.qpy_featured), url.get(i)));
                    }

                    @Override
                    public void onError(Throwable e) {
                        binding.adSlide.setVisibility(View.GONE);
                    }

                    @Override
                    public void onNext(CourseAdModel.QpyBean.ExtAdBean.FeaturedBean featuredBean) {
                        title.add(featuredBean.getAd_link());
                        imageUrl.add(featuredBean.getAd_img());
                        url.add(featuredBean.getAd_link());
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case MyCourseFragment.REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) my.getDataList();
                break;
        }
    }

    public void switchToFeature() {
        binding.tabs.root.getTabAt(1).select();
    }
}
