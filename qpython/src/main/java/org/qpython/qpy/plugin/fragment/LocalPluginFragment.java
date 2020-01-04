package org.qpython.qpy.plugin.fragment;

import android.content.Intent;
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

import org.qpython.qpy.plugin.LocalPluginManager;
import org.qpython.qpy.plugin.activity.LocalPluginInstallActivity;
import org.qpython.qpy.plugin.adapter.LocalPluginAdapter;
import org.qpython.qpy.plugin.model.LocalPluginBean;

import java.util.List;

import io.realm.Realm;
import rx.Subscription;

public class LocalPluginFragment extends Fragment {
    RecyclerView mRvPlugin;
    LinearLayout noneItemBackground;
    private LocalPluginAdapter mLocalPluginAdapter;
    private Realm              mRealm;
    private Subscription       mRefreshDataSubscription;

    public static LocalPluginFragment newInstance() {
        return new LocalPluginFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_plugin_local, container, false);
        mRvPlugin = (RecyclerView) v.findViewById(R.id.rv_plugin);
        noneItemBackground = (LinearLayout) v.findViewById(R.id.none_item_bg);
        mRealm = Realm.getDefaultInstance();

        v.findViewById(R.id.fab).setOnClickListener(v1 -> {
            Intent intent = new Intent(getActivity(), LocalPluginInstallActivity.class);
            startActivity(intent);
        });
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mLocalPluginAdapter = new LocalPluginAdapter(getContext());
        mRvPlugin.setLayoutManager(new LinearLayoutManager(getContext()));
        mRvPlugin.setAdapter(mLocalPluginAdapter);
        mRefreshDataSubscription = LocalPluginManager.queryAll(mRealm).asObservable()
                .subscribe(this::showData);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mRealm.close();
        mRefreshDataSubscription.unsubscribe();
    }

    private void showData(List<LocalPluginBean> list) {
        if (list == null || list.isEmpty()) {
            noneItemBackground.setVisibility(View.VISIBLE);
        } else {
            noneItemBackground.setVisibility(View.GONE);
        }
        mLocalPluginAdapter.setData(list);
    }
}
