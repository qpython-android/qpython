package org.qpython.qpy.program;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;

public class ItemMenuLayout extends FrameLayout {
    private static final String TAG = "ItemMenuLayout";

    private static ItemMenuLayout sOpenMenuView = null;
    private View mMenu;
    private OnClickListener mListener;
    private int mDuration = 300;

    public ItemMenuLayout(Context context) {
        super(context);
    }

    public ItemMenuLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ItemMenuLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        if (getChildCount() != 2) {
            throw new IllegalStateException("Must be have two child view");
        }

        mMenu = getChildAt(1);
        LayoutParams params = (LayoutParams) mMenu.getLayoutParams();
        params.gravity = Gravity.RIGHT;
        mMenu.setLayoutParams(params);

        super.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onClick(v);
                } else {
                    if (!isMenuVisible()) {
                        showMenu();
                    }
                }
            }
        });
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mMenu.setVisibility(GONE);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (MotionEvent.ACTION_UP == ev.getAction()) {
            if (sOpenMenuView != null) {
                sOpenMenuView.hideMenu();
            }
        }

        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (sOpenMenuView != null) {
            sOpenMenuView.hideMenu();
            ;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        mListener = l;
    }

    private boolean isMenuVisible() {
        return mMenu.getVisibility() == View.VISIBLE;
    }

    private void showMenu() {
        sOpenMenuView = this;
        mMenu.setVisibility(VISIBLE);
        Animation anim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 1.0f,
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0);
        anim.setDuration(mDuration);
        mMenu.startAnimation(anim);
    }

    private void hideMenu() {
        sOpenMenuView = null;
        Animation anim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 1.0f,
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0);
        anim.setDuration(mDuration);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mMenu.setVisibility(GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mMenu.startAnimation(anim);
    }
}
