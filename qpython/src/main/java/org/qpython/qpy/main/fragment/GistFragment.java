package org.qpython.qpy.main.fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.qpython.qpy.R;
import org.qpython.qpy.databinding.FragmentRvBinding;
import org.qpython.qpy.main.activity.GistActivity;
import org.qpython.qpy.main.adapter.GistAdapter;
import org.qpython.qpy.main.server.gist.GistEvent;
import org.qpython.qpy.main.server.gist.response.GistBean;

import java.util.List;

import static org.qpython.qpy.main.server.gist.GistEvent.ADD_COMMENT;
import static org.qpython.qpy.main.server.gist.GistEvent.DELETE_SUC;
import static org.qpython.qpy.main.server.gist.GistEvent.FAVORITE;
import static org.qpython.qpy.main.server.gist.GistEvent.UNFAVORITE;
import static org.qpython.qpy.main.server.gist.GistEvent.UPDATE_GIST_SUC;

/**
 * Created by Hmei
 * 2018/4/17.
 */

public class GistFragment extends Fragment {
    private FragmentRvBinding binding;
    private GistAdapter       adapter;
    private GistActivity      activity;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (GistActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        return inflater.inflate(R.layout.fragment_rv, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = DataBindingUtil.bind(view);
        adapter = new GistAdapter(getContext());
        binding.swipeList.setLayoutManager(new LinearLayoutManager(getContext()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        binding.swipeList.setNestedScrollingEnabled(false);
        binding.swipeList.setAdapter(adapter);
    }

    @Subscribe
    public void onEvent(GistEvent gistEvent) {
        String event = gistEvent.name;
        switch (event) {
            case DELETE_SUC:
                adapter.delete(gistEvent.content);
                break;
            case UNFAVORITE:
                adapter.addFav(gistEvent.content, false);
                break;
            case FAVORITE:
                adapter.addFav(gistEvent.content, true);
                break;
            case ADD_COMMENT:
                adapter.addCommentNum(gistEvent.content);
                break;
            case UPDATE_GIST_SUC:
                activity.getData();
                break;
        }
    }

    public void setDataList(List<GistBean> data) {
        if (data.size() == 0) {
            binding.emptyHint.setVisibility(View.VISIBLE);
            return;
        }
        if (adapter != null) {
            adapter.setData(data);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
