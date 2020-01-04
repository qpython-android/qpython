package org.qpython.qpy.texteditor.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import org.qpython.qpy.R;
import org.qpython.qpy.texteditor.ui.adapter.PathListAdapter;

import static android.support.v7.widget.helper.ItemTouchHelper.ACTION_STATE_IDLE;

/**
 * To
 * Created by Hmei on 2017-05-23.
 */

public class MyTouchHelperCallback extends ItemTouchHelper.SimpleCallback {

    // we want to cache these and not allocate anything repeatedly in the onChildDraw method
    private Drawable        background;
    private Drawable        xMark;
    private int             xMarkMargin;
    private boolean         initiated;
    private Context         context;
    private PathListAdapter adapter;
    public MyTouchHelperCallback(int dragDirs, int swipeDirs, PathListAdapter adapter,Context context) {
        super(dragDirs, swipeDirs);
        this.adapter = adapter;
        this.context = context;
    }

    private void init() {
        background = new ColorDrawable(Color.RED);
        xMark = ContextCompat.getDrawable(context, R.drawable.ic_clear_24dp);
        xMark.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        xMarkMargin = (int) context.getResources().getDimension(R.dimen.default_margin);
        initiated = true;
    }

    @Override
    public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int position = viewHolder.getAdapterPosition();
        if (adapter.isPendingRemoval(position)) {
            return ACTION_STATE_IDLE;
        }
        return super.getSwipeDirs(recyclerView, viewHolder);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
        int swipedPosition = viewHolder.getAdapterPosition();
        adapter.pendingRemoval(swipedPosition);
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        View itemView = viewHolder.itemView;

        // not sure why, but this method get's called for viewholder that are already swiped away
        if (viewHolder.getAdapterPosition() == -1) {
            // not interested in those
            return;
        }

        if (!initiated) {
            init();
        }

        // draw red background
        background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
        background.draw(c);

        // draw x mark
        int itemHeight = itemView.getBottom() - itemView.getTop();
        int intrinsicWidth = xMark.getIntrinsicWidth();
        int intrinsicHeight = xMark.getIntrinsicWidth();

        int xMarkLeft = itemView.getRight() - xMarkMargin - intrinsicWidth;
        int xMarkRight = itemView.getRight() - xMarkMargin;
        int xMarkTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
        int xMarkBottom = xMarkTop + intrinsicHeight;
        xMark.setBounds(xMarkLeft, xMarkTop, xMarkRight, xMarkBottom);

        xMark.draw(c);

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }


    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }
}
