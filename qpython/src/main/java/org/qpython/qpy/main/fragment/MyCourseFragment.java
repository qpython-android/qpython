package org.qpython.qpy.main.fragment;


import android.content.Intent;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.qpython.qpy.R;
import org.qpython.qpy.databinding.FragmentMyCourseBinding;
import org.qpython.qpy.main.activity.CourseActivity;
import org.qpython.qpy.main.activity.SignInActivity;
import org.qpython.qpy.main.adapter.MyCourseAdapter;
import org.qpython.qpy.main.app.App;
import org.qpython.qpy.main.server.MySubscriber;
import org.qpython.qpy.main.server.model.CourseModel;
import org.qpython.qpy.main.server.model.MyCourse;
import org.qpython.qpy.main.widget.GridSpace;

import java.util.ArrayList;


public class MyCourseFragment extends Fragment {
    public static final int REQUEST_CODE = 4803;
    private FragmentMyCourseBinding binding;
    private MyCourseAdapter         adapter;
    private ArrayList<MyCourse.DataBean> dataList = new ArrayList<>();
    private boolean init;

    public float dp2px(float dp) {
        Resources r = Resources.getSystem();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_course, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = DataBindingUtil.bind(view);
        binding.explore.setOnClickListener(v -> ((CourseActivity) getActivity()).switchToFeature());
        getDataList();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (/*!init && */App.getUser() != null) {
            getDataList();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void needRefresh(boolean needRefresh) {
        if (needRefresh) {
            getDataList();
        }
    }

    public void getDataList() {
        if (App.getUser() != null) {
            init = true;
            binding.pb.setVisibility(View.VISIBLE);
            App.getService().getMyCourse(App.getUser().getEmail(), new MySubscriber<MyCourse>() {
                @Override
                public void onNext(MyCourse o) {
                    super.onNext(o);
                    if (adapter == null) {
                        adapter = new MyCourseAdapter(getActivity(), dataList);
                        binding.swipeList.setLayoutManager(new LinearLayoutManager(getContext()) {
                            @Override
                            public boolean canScrollVertically() {
                                return false;
                            }
                        });
                        binding.swipeList.addItemDecoration(new GridSpace(1, (int) dp2px(10), false));
                        binding.swipeList.setNestedScrollingEnabled(false);
                        binding.swipeList.setAdapter(adapter);
                    }
                    dataList.clear();
                    binding.pb.setVisibility(View.GONE);
                    for (MyCourse.DataBean dataBean : o.getData()) {
                        if (!"notready".equals(dataBean.getType())) {
                            dataList.add(dataBean);
                        }
                    }
//                    debugAddFakeData();
                    if (dataList.size() == 0) {
                        binding.emptyHint.setVisibility(View.VISIBLE);
                        binding.explore.setVisibility(View.VISIBLE);
                        binding.explore.setText(R.string.explore_course);
                        binding.explore.setOnClickListener(v -> ((CourseActivity) getActivity()).switchToFeature());
                    } else {
                        binding.emptyHint.setVisibility(View.GONE);
                        binding.explore.setVisibility(View.GONE);
                        adapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onError(Throwable e) {
                    binding.pb.setVisibility(View.GONE);
                }
            });
        } else {
            init = false;
            binding.explore.setText(R.string.login_now);
            binding.explore.setOnClickListener(v -> getActivity().
                    startActivityForResult(new Intent(getActivity(), SignInActivity.class), REQUEST_CODE));
            binding.explore.setVisibility(View.VISIBLE);
            binding.emptyHint.setVisibility(View.VISIBLE);
        }
    }

    private void debugAddFakeData() {

        MyCourse.DataBean unFunding = App.getGson().fromJson("{\n" +
                "            \"info\": {\n" +
                "                \"description\": \"How to start QPython\",\n" +
                "                \"title\": \"QPython Quick Start\",\n" +
                "                \"open\": 0,\n" +
                "                \"downloads\": \"10,000+\",\n" +
                "                \"level\": 1,\n" +
                "                \"rdate\": \"2017-06-15\",\n" +
                "                \"link\": \"http://edu.qpython.org/qpython-quick-start/index.html\",\n" +
                "                \"type\": \"free\",\n" +
                "                \"logo\": \"http://edu.qpython.org/static/course-qpython-quick-start.png\",\n" +
                "                \"smodule\": \"qpython-quick-start\"\n" +
                "            },\n" +
                "            \"list\": [\n" +
                "                {\n" +
                "                    \"gd\": \"course_free\",\n" +
                "                    \"created\": \"2018021109\"\n" +
                "                }\n" +
                "            ],\n" +
                "            \"type\": \"crowdfunding\",\n" +
                "            \"course\": \"qpython-quick-start\"\n" +
                "        }", MyCourse.DataBean.class);
        MyCourse.DataBean funding = App.getGson().fromJson("{\n" +
                "            \"info\": {\n" +
                "                \"description\": \"How to start QPython\",\n" +
                "                \"title\": \"QPython Quick Start\",\n" +
                "                \"open\": 1,\n" +
                "                \"downloads\": \"10,000+\",\n" +
                "                \"level\": 1,\n" +
                "                \"rdate\": \"2017-06-15\",\n" +
                "                \"link\": \"http://edu.qpython.org/qpython-quick-start/index.html\",\n" +
                "                \"type\": \"free\",\n" +
                "                \"logo\": \"http://edu.qpython.org/static/course-qpython-quick-start.png\",\n" +
                "                \"smodule\": \"qpython-quick-start\"\n" +
                "            },\n" +
                "            \"list\": [\n" +
                "                {\n" +
                "                    \"gd\": \"course_free\",\n" +
                "                    \"created\": \"2018021109\"\n" +
                "                }\n" +
                "            ],\n" +
                "            \"type\": \"crowdfunding\",\n" +
                "            \"course\": \"qpython-quick-start\"\n" +
                "        }", MyCourse.DataBean.class);
        dataList.add(unFunding);
        dataList.add(funding);
    }


}
