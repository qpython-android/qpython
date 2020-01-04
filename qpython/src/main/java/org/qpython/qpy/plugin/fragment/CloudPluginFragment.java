package org.qpython.qpy.plugin.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.qpython.qpy.R;

import org.qpython.qpy.plugin.CloudPluginManager;
import org.qpython.qpy.plugin.SpaceItemDecoration;
import org.qpython.qpy.plugin.adapter.CloudPluginAdapter;
import org.qpython.qpy.plugin.model.CloudPluginBean;

import java.util.List;

import io.realm.Realm;
import rx.Subscription;

public class CloudPluginFragment extends Fragment {
    RecyclerView rvPlugin;
    LinearLayout noneItemBackground;
    private CloudPluginAdapter mCloudPluginAdapter;
    private Realm mRealm;
    private Subscription mRefreshDataSubscription;

    public static CloudPluginFragment newInstance() {
        return new CloudPluginFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_plugin_cloud, container, false);
        rvPlugin = (RecyclerView) v.findViewById(R.id.rv_plugin);
        noneItemBackground = (LinearLayout) v.findViewById(R.id.none_item_bg);

        mRealm = Realm.getDefaultInstance();
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mCloudPluginAdapter = new CloudPluginAdapter(getActivity());
        rvPlugin.addItemDecoration(new SpaceItemDecoration(1));
        rvPlugin.setLayoutManager(new LinearLayoutManager(getContext()));
        rvPlugin.setAdapter(mCloudPluginAdapter);

        mRefreshDataSubscription = CloudPluginManager.queryAll(mRealm).asObservable()
                .subscribe(this::showData);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mRealm.close();
        mRefreshDataSubscription.unsubscribe();
    }

    private void showData(List<CloudPluginBean> local) {
        if (local == null || local.isEmpty()) {
            noneItemBackground.setVisibility(View.VISIBLE);
        } else {
            noneItemBackground.setVisibility(View.GONE);
        }

        mCloudPluginAdapter.setData(local);

        CloudPluginManager.queryFromNet(plugins -> {
            if (plugins == null || plugins.isEmpty()) {
                noneItemBackground.setVisibility(View.VISIBLE);
            } else {
                noneItemBackground.setVisibility(View.GONE);
            }
            mCloudPluginAdapter.setData(plugins);
        });
    }
}
