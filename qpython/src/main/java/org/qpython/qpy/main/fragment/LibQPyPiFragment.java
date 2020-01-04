package org.qpython.qpy.main.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.quseit.util.ACache;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.qpython.qpy.R;
import org.qpython.qpy.databinding.FragmentQpyPiBinding;
import org.qpython.qpy.main.adapter.LibListAdapter;
import org.qpython.qpy.main.app.App;
import org.qpython.qpy.main.server.CacheKey;
import org.qpython.qpy.main.server.MySubscriber;
import org.qpython.qpy.main.server.model.QpypiModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * Nested fragments only available after API 17,
 * of course it can be work around but .. ha ha
 * <p>
 * Created by Hmei on 2017-06-07.
 */

public class LibQPyPiFragment extends RefreshFragment {

    private List<QpypiModel>     dataList;
    private LibListAdapter       adapter;
    private FragmentQpyPiBinding binding;
    private TextView             header;
    private boolean              isSaverOn;
    private final String         TAG = "LibQPyPiFragment";

    @Override
    public void onResume() {
        super.onResume();
//        if (installPosition != -1 && new File(getDownloadPath(
//                dataList.get(installPosition).getSmodule(),
//                dataList.get(installPosition).getVer()))
//                .exists()) {
//            dataList.get(installPosition).setInstalled(true);
//            adapter.notifyItemChanged(installPosition);
//        }
        //在下载完库之后刷新数据
        if (installPosition != -1 && adapter!=null){
            refresh(!isSaverOn);
            //将其还原防止重复刷新
            installPosition = -1;
        }
        EventBus.getDefault().register(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_qpy_pi, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initDataList();
        initView();
        initListener();
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    private void initDataList() {
        Log.d(TAG, "initDataList");
        isSaverOn = PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean(getString(R.string.key_saver), true);
        dataList = new ArrayList<>();
        adapter = new LibListAdapter<>(dataList);
        refresh(!isSaverOn);
    }

    @Override
    public void refresh(boolean forceRefresh) {
        Log.d(TAG, "refresh:"+forceRefresh);
        dataList.clear();
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.netError.setVisibility(View.GONE);
        adapter.notifyDataSetChanged();
        App.getService().getQPyPi(forceRefresh, new MySubscriber<List<QpypiModel>>() {
            @Override
            public void onCompleted() {
                binding.progressBar.setVisibility(View.GONE);
                binding.swipeList.setVisibility(View.VISIBLE);
                if (header != null) {
                    header.setText(getString(R.string.last_refresh, ACache.get(getContext()).getAsString(CacheKey.QPYPI_LAST_REFRESH)));
                }
            }

            @Override
            public void onError(Throwable e) {
                binding.progressBar.setVisibility(View.GONE);
                binding.swipeList.setVisibility(View.GONE);
                binding.netError.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNext(List<QpypiModel> libModels) {
                String f;
                for (QpypiModel lib : libModels) {
                    f = getDownloadPath(lib.getTmodule());
                    Log.d(TAG, "onNext:"+f);

                    if (new File(f).exists()) {
                        lib.setInstalled(true);
                    }
                }
                ACache.get(getContext()).put(CacheKey.QPYPI, tostring(libModels));
                dataList.addAll(libModels);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void initView() {
        binding.swipeList.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.swipeList.setSwipeMenuCreator(getTypeMenu());
        binding.swipeList.setFocusable(false);

//        String last_update = ACache.get(getContext()).getAsString(CacheKey.QPYPI_LAST_REFRESH);
//        if (isSaverOn && last_update != null && !last_update.isEmpty()) {
//            header = (TextView) getActivity().getLayoutInflater().inflate(R.layout.header_last_refresh_time, binding.swipeList, false);
//            header.setText(getString(R.string.last_refresh, last_update));
//            binding.swipeList.addHeaderView(header);
//        }

    }

    private void initListener() {
        adapter.setClick(position -> binding.swipeList.smoothOpenRightMenu(position));
        binding.swipeList.setSwipeMenuItemClickListener(getListener(dataList, adapter));
        binding.swipeList.setAdapter(adapter);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(LibProjectFragment.CreateLibFinishEvent event) {
        for (QpypiModel libModel : dataList) {
            if (libModel.getTitle().equals(event.fileName)) {
                libModel.setInstalled(true);
                adapter.notifyDataSetChanged();
            }
        }
    }


}
