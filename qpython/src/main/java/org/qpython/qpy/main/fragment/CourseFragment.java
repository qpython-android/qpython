package org.qpython.qpy.main.fragment;


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
import android.widget.TextView;

import org.qpython.qpy.R;
import org.qpython.qpy.databinding.FragmentCourseBinding;
import org.qpython.qpy.main.adapter.CourseAdapter;
import org.qpython.qpy.main.server.model.CourseModel;
import org.qpython.qpy.main.widget.GridSpace;

import java.util.ArrayList;
import java.util.List;


public class CourseFragment extends Fragment {

    private FragmentCourseBinding binding;
    private CourseAdapter         adapter;
    private boolean               isTypeVisible;
    private List<CourseModel> basic = new ArrayList<>(),
            web                     = new ArrayList<>(),
            aipy                    = new ArrayList<>(),
            arvr                    = new ArrayList<>(),
            other                   = new ArrayList<>(),
            dataList                = new ArrayList<>();

    public float dp2px(float dp) {
        Resources r = Resources.getSystem();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_course, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = DataBindingUtil.bind(view);
        adapter = new CourseAdapter(getActivity(), dataList);
        binding.swipeList.setLayoutManager(new LinearLayoutManager(getContext()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        binding.swipeList.addItemDecoration(new GridSpace(1, (int) dp2px(10), false));
        binding.swipeList.setNestedScrollingEnabled(false);
        binding.swipeList.setAdapter(adapter);
        binding.typeLayout.setVisibility(isTypeVisible ? View.VISIBLE : View.GONE);
        if (isTypeVisible) initTypeLayout();
    }

    public void setDataList(List<CourseModel> data) {
        if (data.size() == 0) {
            binding.emptyHint.setVisibility(View.VISIBLE);
            return;
        }
        for (CourseModel datum : data) {
            switch (datum.getType()) {
                case "basic":
                    basic.add(datum);
                    break;
                case "web":
                    web.add(datum);
                    break;
                case "aipy":
                    aipy.add(datum);
                    break;
                case "arvr":
                    arvr.add(datum);
                    break;
                case "other":
                    other.add(datum);
                    break;
                default:
                    basic.add(datum);
                    break;
            }
        }
        dataList.addAll(basic);
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }

    private int selectedIndex;

    private void initTypeLayout() {
        for (int i = 0; i < binding.typeLayout.getChildCount(); i++) {
            int finalI = i;
            binding.typeLayout.getChildAt(i).setOnClickListener(v -> {
                if (finalI != selectedIndex) {
                    updateSelected(finalI, true);
                    updateSelected(selectedIndex, false);
                    selectedIndex = finalI;
                }
            });
        }
    }

    private void updateSelected(int index, boolean isLight) {
        int res;
        List<CourseModel> typeList;
        switch (index) {
            case 0:
                res = isLight ? R.drawable.basics_light : R.drawable.basics;
                typeList = basic;
                break;
            case 1:
                res = isLight ? R.drawable.database_light : R.drawable.database;
                typeList = other;
                break;
            case 2:
                res = isLight ? R.drawable.arvr_light : R.drawable.arvr;
                typeList = arvr;
                break;
            case 3:
                res = isLight ? R.drawable.aipy_light : R.drawable.aipy;
                typeList = aipy;
                break;
            case 4:
                res = isLight ? R.drawable.web_light : R.drawable.web;
                typeList = web;
                break;
            default:
                res = 0;
                typeList = basic;
                break;
        }
        ((TextView) binding.typeLayout.getChildAt(index)).setTextColor(getResources().getColor(isLight ? R.color.theme_green : R.color.white));
        ((TextView) binding.typeLayout.getChildAt(index)).setCompoundDrawablesWithIntrinsicBounds(0, res, 0, 0);

        if (isLight) {
            dataList.clear();
            dataList.addAll(typeList);
            adapter.notifyDataSetChanged();
        }
    }

    public void setTypeVisible(boolean typeVisible) {
        isTypeVisible = typeVisible;
    }
}
