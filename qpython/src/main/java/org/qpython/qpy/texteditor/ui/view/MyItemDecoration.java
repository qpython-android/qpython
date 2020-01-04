package org.qpython.qpy.texteditor.ui.view;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 *
 * Created by Hmei on 2017-05-23.
 */

public class MyItemDecoration extends RecyclerView.ItemDecoration {
    // we want to cache this and not allocate anything repeatedly in the onDraw method
    private Drawable background;
    private boolean  initiated;

    private void init() {
        background = new ColorDrawable(Color.parseColor("#FFD14136"));
        initiated = true;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {

        if (!initiated) {
            init();
        }

        // only if animation is in progress
        if (parent.getItemAnimator().isRunning()) {

            // some items might be animating down and some items might be animating up to close the gap left by the removed item
            // this is not exclusive, both movement can be happening at the same time
            // to reproduce this leave just enough items so the first one and the last one would be just a little off screen
            // then remove one from the middle

            // find first child with translationY > 0
            // and last one with translationY < 0
            // we're after a rect that is not covered in recycler-view views at this point in time
            View lastViewComingDown = null;
            View firstViewComingUp = null;

            // this is fixed
            int left = 0;
            int right = parent.getWidth();

            // this we need to find out
            int top = 0;
            int bottom = 0;

            // find relevant translating views
            int childCount = parent.getLayoutManager().getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = parent.getLayoutManager().getChildAt(i);
                if (child.getTranslationY() < 0) {
                    // view is coming down
                    lastViewComingDown = child;
                } else if (child.getTranslationY() > 0) {
                    // view is coming up
                    if (firstViewComingUp == null) {
                        firstViewComingUp = child;
                    }
                }
            }

            if (lastViewComingDown != null && firstViewComingUp != null) {
                // views are coming down AND going up to fill the void
                top = lastViewComingDown.getBottom() + (int) lastViewComingDown.getTranslationY();
                bottom = firstViewComingUp.getTop() + (int) firstViewComingUp.getTranslationY();
            } else if (lastViewComingDown != null) {
                // views are going down to fill the void
                top = lastViewComingDown.getBottom() + (int) lastViewComingDown.getTranslationY();
                bottom = lastViewComingDown.getBottom();
            } else if (firstViewComingUp != null) {
                // views are coming up to fill the void
                top = firstViewComingUp.getTop();
                bottom = firstViewComingUp.getTop() + (int) firstViewComingUp.getTranslationY();
            }

            background.setBounds(left, top, right, bottom);
            background.draw(c);

        }
        super.onDraw(c, parent, state);
    }
}
