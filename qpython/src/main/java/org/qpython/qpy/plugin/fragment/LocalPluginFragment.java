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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.realm.Realm;
import rx.Subscription;

public class LocalPluginFragment extends Fragment {
    private Unbinder mUnbinder;
    private LocalPluginAdapter mLocalPluginAdapter;
    private Realm mRealm;
    private Subscription mRefreshDataSubscription;

    @BindView(R.id.rv_plugin)
    RecyclerView mRvPlugin;

    @BindView(R.id.none_item_bg)
    LinearLayout noneItemBackground;

    public static LocalPluginFragment newInstance() {
        return new LocalPluginFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_plugin_local, container, false);
        mUnbinder = ButterKnife.bind(this, v);
        mRealm = Realm.getDefaultInstance();
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
        mUnbinder.unbind();
        mRealm.close();
        mRefreshDataSubscription.unsubscribe();
    }


    @OnClick(R.id.fab)
    void startLocalPluginInstallActivity() {
        Intent intent = new Intent(getActivity(), LocalPluginInstallActivity.class);
        startActivity(intent);
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
