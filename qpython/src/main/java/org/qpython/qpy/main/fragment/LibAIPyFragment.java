package org.qpython.qpy.main.fragment;


import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.quseit.util.ACache;
import com.quseit.util.NAction;

import org.qpython.qpy.R;
import org.qpython.qpy.databinding.FragmentRefreshRvBinding;
import org.qpython.qpy.main.adapter.LibListAdapter;
import org.qpython.qpy.main.app.App;
import org.qpython.qpy.main.server.CacheKey;
import org.qpython.qpy.main.server.model.BaseLibModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;

public class LibAIPyFragment extends RefreshFragment {

    private LibListAdapter<BaseLibModel> adapter;
    private FragmentRefreshRvBinding     binding;

    private List<BaseLibModel> dataList;
    private boolean            isSaverOn;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_refresh_rv, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = DataBindingUtil.bind(view);
        binding.progressBar.setVisibility(View.GONE);
        dataList = new ArrayList<>();
        isSaverOn = PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean(getString(R.string.key_saver), true);
        binding.netError.setVisibility(View.GONE);
        adapter = new LibListAdapter<>(dataList);
        initList();
        refresh(!isSaverOn);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter == null) {
            adapter = new LibListAdapter<>(dataList);
            initList();
            refresh(!isSaverOn);
        }
        //在下载完库之后刷新数据
        else if (installPosition != -1) {
            refresh(!isSaverOn);
            //将其还原防止重复刷新
            installPosition = -1;
        }
    }


    private void initList() {
        binding.swipeList.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.swipeList.setSwipeMenuCreator(getTypeMenu());

        if (isAIPyInstall()) {
            View description = getActivity().getLayoutInflater().inflate(R.layout.header_lib_description, binding.swipeList, false);
            binding.swipeList.addHeaderView(description);
        }

        if (adapter != null) {
            adapter.setClick(position -> binding.swipeList.smoothOpenRightMenu(position));
            binding.swipeList.setSwipeMenuItemClickListener(getListener(dataList, adapter));
            binding.swipeList.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }

    private boolean isAIPyInstall() {
        PackageManager pm = getActivity().getPackageManager();
        try {
            pm.getPackageInfo(getString(R.string.aipy_app_id), PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    @Override
    public void refresh(boolean forceRefresh) {
        binding.netError.setVisibility(View.GONE);
        dataList.clear();
        adapter.notifyDataSetChanged();
        binding.progressBar.setVisibility(View.VISIBLE);
        App.getService().getAIPyList(NAction.isQPy3(getActivity()),forceRefresh, new Subscriber<List<BaseLibModel>>() {
            @Override
            public void onCompleted() {
                binding.swipeList.setVisibility(View.VISIBLE);
                binding.progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(Throwable e) {
                binding.swipeList.setVisibility(View.GONE);
                binding.progressBar.setVisibility(View.GONE);
                binding.netError.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNext(List<BaseLibModel> o) {
                for (BaseLibModel lib : o) {
                    if (new File(getDownloadPath(lib.getTmodule())).exists()) {
                        lib.setInstalled(true);
                    }
                }
                ACache.get(getContext()).put(CacheKey.AIPY, tostring(o));
                dataList.addAll(o);
                adapter.notifyDataSetChanged();
            }
        });
    }

}
