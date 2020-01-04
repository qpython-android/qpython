package org.qpython.qpy.main.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.qpython.qpy.R;
import org.qpython.qpy.main.adapter.GistAdapter;
import org.qpython.qpy.main.server.gist.GistEvent;
import org.qpython.qpy.main.server.gist.myScreen.MyGistControler;
import org.qpython.qpy.main.server.gist.myScreen.MyGistView;
import org.qpython.qpy.main.server.gist.response.GistBean;

import java.util.List;

import static org.qpython.qpy.main.server.gist.GistEvent.ADD_COMMENT;
import static org.qpython.qpy.main.server.gist.GistEvent.DELETE;
import static org.qpython.qpy.main.server.gist.GistEvent.FAVORITE;
import static org.qpython.qpy.main.server.gist.GistEvent.REFRESH_COLLECTION;
import static org.qpython.qpy.main.server.gist.GistEvent.UNFAVORITE;
import static org.qpython.qpy.main.server.gist.GistEvent.UPDATE_GIST_SUC;

/**
 * 文 件 名: MyGistFragment
 * 创 建 人: ZhangRonghua
 * 创建日期: 2017/12/26 14:45
 * 邮   箱: qq798435167@gmail.com
 * 修改时间：
 * 修改备注：
 */

public class MyGistFragment extends Fragment implements MyGistView {
    public static final String TYPE       = "TYPE";
    public static final int    GIST       = 0;
    public static final int    COLLECTION = 1;

    private int             type;
    private View            mView;
    private GistAdapter     mNewsAdapter;
    private MyGistControler mMyGistControler;

    private TextView    hint;
    private ProgressBar mProgressBar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMyGistControler = new MyGistControler(this);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMyGistControler.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public static MyGistFragment newInstance(int type) {
        MyGistFragment myShareFragment = new MyGistFragment();
        Bundle data = new Bundle();
        data.putInt(TYPE, type);
        myShareFragment.setArguments(data);
        return myShareFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mView == null) {
            type = getArguments().getInt(TYPE);
            mView = inflater.inflate(R.layout.fragment_my_share, container, false);
            mProgressBar = mView.findViewById(R.id.progress_bar);
            hint = mView.findViewById(R.id.empty);
            RecyclerView mRecyclerView = mView.findViewById(R.id.rv_myshare);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            mNewsAdapter = new GistAdapter(getContext(), this);
            mNewsAdapter.setShowDelete(type == GIST);
            mRecyclerView.setAdapter(mNewsAdapter);
        }
        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        refresh();
    }

    public void refresh() {
        if (type == GIST) {
            mMyGistControler.getMyGists();
        } else if (type == COLLECTION) {
            mMyGistControler.getMyFavorites();
        }
    }

    @Subscribe
    public void onEvent(GistEvent event) {
        switch (event.name) {
            case DELETE:
                if (type == GIST) mMyGistControler.deleteGist(event.content);
                break;
            case ADD_COMMENT:
                mNewsAdapter.addCommentNum(event.content);
                break;
            case REFRESH_COLLECTION:
                if (type == COLLECTION) refresh();
                break;
            case FAVORITE:
                if (type == COLLECTION) mNewsAdapter.addFav(event.content, true);
                break;
            case UNFAVORITE:
                if (type == COLLECTION) mNewsAdapter.delete(event.content);
                break;
            case UPDATE_GIST_SUC:
                if (type == GIST) refresh();
                break;

        }
    }


    @Override
    public void showLoading() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void showToast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void deleteSuccess(String id) {
        mNewsAdapter.delete(id);
    }

    @Override
    public void addFavorites(List<GistBean> list) {
        if (list == null || list.size() == 0) {
            showHint(true);
        } else {
            showHint(false);
            mNewsAdapter.setData(list);
        }
    }

    public void showHint(boolean isShow) {
        hint.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    @Override
    public void addGists(List<GistBean> list) {
        addFavorites(list);
    }
}
