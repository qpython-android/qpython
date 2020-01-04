package org.qpython.qpy.main.listener;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * 文 件 名: LoadmoreListener
 * 创 建 人: ZhangRonghua
 * 创建日期: 2017/12/29 10:51
 * 邮   箱: qq798435167@gmail.com
 * 修改时间：
 * 修改备注：
 */

public abstract class LoadmoreListener extends RecyclerView.OnScrollListener {

    //用来标记是否正在向最后一个滑动
    boolean isSlidingToLast = false;

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
        // 当不滚动时
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            //获取最后一个完全显示的ItemPosition
            int lastVisibleItem = manager.findLastCompletelyVisibleItemPosition();
            int totalItemCount = manager.getItemCount();

            // 判断是否滚动到底部，并且是向右滚动
            if (lastVisibleItem == (totalItemCount - 1) && isSlidingToLast&&totalItemCount>0) {
                //加载更多功能的代码
                OnLoadmore();
            }
        }
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        //dx用来判断横向滑动方向，dy用来判断纵向滑动方向
        if (dy > 0) {
            //大于0表示正在向上滚动
            isSlidingToLast = true;
        } else {
            //小于等于0表示停止或向下滚动
            isSlidingToLast = false;
        }
    }

    public abstract void OnLoadmore();
}
