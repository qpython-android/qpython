package org.qpython.qpy.main.widget;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Checkable;

import org.qpython.qpy.R;

/**
 * Toggle button
 * Created by Hmei on 2017-05-27.
 */

public class ToggleView extends View implements Checkable {

    private static final int DEFAULT_WIDTH                = dp2pxInt(48);
    private static final int DEFAULT_HEIGHT               = dp2pxInt(30);
    /**
     * 动画状态：
     * 1.静止
     * 2.进入拖动
     * 3.处于拖动
     * 4.拖动-复位
     * 5.拖动-切换
     * 6.点击切换
     **/
    private final        int ANIMATE_STATE_NONE           = 0;
    private final        int ANIMATE_STATE_PENDING_DRAG   = 1;
    private final        int ANIMATE_STATE_DRAGING        = 2;
    private final        int ANIMATE_STATE_PENDING_RESET  = 3;
    private final        int ANIMATE_STATE_PENDING_SETTLE = 4;
    private final        int ANIMATE_STATE_SWITCH         = 5;
    private final android.animation.ArgbEvaluator argbEvaluator
            = new android.animation.ArgbEvaluator();
    /**
     * 阴影半径
     */
    private int shadowRadius;
    /**
     * 阴影Y偏移px
     */
    private int shadowOffset;
    /**
     * 阴影颜色
     */
    private int shadowColor;
    /**
     * 背景半径
     */
    private float viewRadius;
    /**
     * 按钮半径
     */
    private float buttonRadius;
    /**
     * 背景高
     */
    private float height;
    /**
     * 背景宽
     */
    private float width;
    /**
     * 背景位置
     */
    private float left;
    private float top;
    private float right;
    private float bottom;
    private float centerX;
    private float centerY;
    /**
     * 背景底色
     */
    private int background;
    /**
     * 背景关闭颜色
     */
    private int uncheckColor;
    /**
     * 背景打开颜色
     */
    private int checkedColor;
    /**
     * 边框宽度px
     */
    private int borderWidth;
    /**
     * 打开指示线颜色
     */
    private int   checkLineColor;
    /**
     * 按钮最左边
     */
    private float buttonMinX;
    /**
     * 按钮最右边
     */
    private float buttonMaxX;
    /**
     * 按钮画笔
     */
    private Paint buttonPaint;
    /**
     * 背景画笔
     */
    private Paint paint;
    /**
     * 当前状态
     */
    private ViewState viewState;
    private ViewState beforeState;
    private ViewState afterState;
    /**
     * 动画状态
     */
    private int   animateState = ANIMATE_STATE_NONE;
    /**
     *
     */
    private ValueAnimator valueAnimator;
    /**
     * 是否选中
     */
    private boolean isChecked;
    /**
     * 是否启用动画
     */
    private boolean enableEffect;
    /**
     * 是否启用阴影效果
     */
    private boolean shadowEffect;
    /**
     * 收拾是否按下
     */
    private boolean isTouchingDown   = false;
    /**
     *
     */
    private boolean isUiInited       = false;
    /**
     *
     */
    private boolean isEventBroadcast = false;
    /**
     * 估值器
     */
    private ArgbEvaluator evaluator;
    private OnCheckedChangeListener onCheckedChangeListener;
    /*******************************************************/
    /**
     * 手势按下的时刻
     */
    private long touchDownTime;
    private Runnable postPendingDrag = () -> {
        if (!isInAnimating()) {
            pendingDragState();
        }
    };
    private ValueAnimator.AnimatorUpdateListener animatorUpdateListener
            = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            float value = (Float) animation.getAnimatedValue();
            switch (animateState) {
                case ANIMATE_STATE_PENDING_SETTLE: {
                }
                case ANIMATE_STATE_PENDING_RESET: {
                }
                case ANIMATE_STATE_PENDING_DRAG: {
                    viewState.checkedLineColor = (int) argbEvaluator.evaluate(
                            value,
                            beforeState.checkedLineColor,
                            afterState.checkedLineColor
                    );

                    viewState.radius = beforeState.radius
                            + (afterState.radius - beforeState.radius) * value;

                    if (animateState != ANIMATE_STATE_PENDING_DRAG) {
                        viewState.buttonX = beforeState.buttonX
                                + (afterState.buttonX - beforeState.buttonX) * value;
                    }

                    viewState.checkStateColor = (int) argbEvaluator.evaluate(
                            value,
                            beforeState.checkStateColor,
                            afterState.checkStateColor
                    );

                    break;
                }
                case ANIMATE_STATE_SWITCH: {
                    viewState.buttonX = beforeState.buttonX
                            + (afterState.buttonX - beforeState.buttonX) * value;

                    float fraction = (viewState.buttonX - buttonMinX) / (buttonMaxX - buttonMinX);

                    viewState.checkStateColor = (int) argbEvaluator.evaluate(
                            fraction,
                            uncheckColor,
                            checkedColor
                    );

                    viewState.radius = fraction * viewRadius;
                    viewState.checkedLineColor = (int) argbEvaluator.evaluate(
                            fraction,
                            Color.TRANSPARENT,
                            checkLineColor
                    );
                    break;
                }
                default:
                case ANIMATE_STATE_DRAGING: {
                }
                case ANIMATE_STATE_NONE: {
                    break;
                }
            }
            postInvalidate();
        }
    };
    private Animator.AnimatorListener animatorListener
            = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            switch (animateState) {
                case ANIMATE_STATE_DRAGING: {
                    break;
                }
                case ANIMATE_STATE_PENDING_DRAG: {
                    animateState = ANIMATE_STATE_DRAGING;
                    viewState.checkedLineColor = Color.TRANSPARENT;
                    viewState.radius = viewRadius;

                    postInvalidate();
                    break;
                }
                case ANIMATE_STATE_PENDING_RESET: {
                    animateState = ANIMATE_STATE_NONE;
                    postInvalidate();
                    break;
                }
                case ANIMATE_STATE_PENDING_SETTLE: {
                    animateState = ANIMATE_STATE_NONE;
                    postInvalidate();
                    broadcastEvent();
                    break;
                }
                case ANIMATE_STATE_SWITCH: {
                    isChecked = !isChecked;
                    animateState = ANIMATE_STATE_NONE;
                    postInvalidate();
                    broadcastEvent();
                    break;
                }
                default:
                case ANIMATE_STATE_NONE: {
                    break;
                }
            }
        }

        @Override
        public void onAnimationCancel(Animator animation) {
        }

        @Override
        public void onAnimationRepeat(Animator animation) {
        }
    };
    public ToggleView(Context context) {
        super(context);
        init(context, null);
    }

    public ToggleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }
    public ToggleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ToggleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    /*******************************************************/
    private static float dp2px(float dp) {
        Resources r = Resources.getSystem();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }

    private static int dp2pxInt(float dp) {
        return (int) dp2px(dp);
    }

    private static int optInt(TypedArray typedArray, int index, int def) {
        if (typedArray == null) {
            return def;
        }
        return typedArray.getInt(index, def);
    }

    private static float optPixelSize(TypedArray typedArray,
                                      int index,
                                      float def) {
        if (typedArray == null) {
            return def;
        }
        return typedArray.getDimension(index, def);
    }

    private static int optPixelSize(TypedArray typedArray,
                                    int index,
                                    int def) {
        if (typedArray == null) {
            return def;
        }
        return typedArray.getDimensionPixelOffset(index, def);
    }

    private static int optColor(TypedArray typedArray, int index, int def) {
        if (typedArray == null) {
            return def;
        }
        return typedArray.getColor(index, def);
    }

    private static boolean optBoolean(TypedArray typedArray, int index, boolean def) {
        if (typedArray == null) {
            return def;
        }
        return typedArray.getBoolean(index, def);
    }

    @Override
    public final void setPadding(int left, int top, int right, int bottom) {
        super.setPadding(0, 0, 0, 0);
    }

    /**
     * 初始化参数
     */
    private void init(Context context, AttributeSet attrs) {

        TypedArray typedArray = null;
        if (attrs != null) {
            typedArray = context.obtainStyledAttributes(attrs, R.styleable.ToggleView);
        }

        shadowEffect = optBoolean(typedArray,
                R.styleable.ToggleView_sb_shadow_effect,
                true);

        shadowRadius = optPixelSize(typedArray,
                R.styleable.ToggleView_sb_shadow_radius,
                dp2pxInt(2.5f));//dp2pxInt(2.5f);

        shadowOffset = optPixelSize(typedArray,
                R.styleable.ToggleView_sb_shadow_offset,
                dp2pxInt(1.5f));//dp2pxInt(1.5f);

        shadowColor = optColor(typedArray,
                R.styleable.ToggleView_sb_shadow_color,
                0X33000000);//0X33000000;

        uncheckColor = optColor(typedArray,
                R.styleable.ToggleView_sb_uncheck_color,
                0XffDDDDDD);//0XffDDDDDD;

        checkedColor = optColor(typedArray,
                R.styleable.ToggleView_sb_checked_color,
                0Xff51d367);//0Xff51d367;

        borderWidth = optPixelSize(typedArray,
                R.styleable.ToggleView_sb_border_width,
                dp2pxInt(1));//dp2pxInt(1);

        checkLineColor = optColor(typedArray,
                R.styleable.ToggleView_sb_checkline_color,
                Color.WHITE);//Color.WHITE;

        int buttonColor = optColor(typedArray,
                R.styleable.ToggleView_sb_button_color,
                Color.WHITE);//Color.WHITE;

        int effectDuration = optInt(typedArray,
                R.styleable.ToggleView_sb_effect_duration,
                300);//300;

        isChecked = optBoolean(typedArray,
                R.styleable.ToggleView_sb_checked,
                false);

        background = optColor(typedArray,
                R.styleable.ToggleView_sb_background,
                Color.WHITE);//Color.WHITE;

        enableEffect = optBoolean(typedArray,
                R.styleable.ToggleView_sb_enable_effect,
                true);

        evaluator = new ArgbEvaluator();

        if (typedArray != null) {
            typedArray.recycle();
        }


        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        buttonPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        buttonPaint.setColor(buttonColor);

        if (shadowEffect) {
            buttonPaint.setShadowLayer(
                    shadowRadius,
                    0, shadowOffset,
                    shadowColor);
        }


        viewState = new ViewState();
        beforeState = new ViewState();
        afterState = new ViewState();

        valueAnimator = ValueAnimator.ofFloat(0f, 1f);
        valueAnimator.setDuration(effectDuration);
        valueAnimator.setRepeatCount(0);

        valueAnimator.addUpdateListener(animatorUpdateListener);
        valueAnimator.addListener(animatorListener);

        super.setClickable(true);
        this.setPadding(0, 0, 0, 0);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if (widthMode == MeasureSpec.UNSPECIFIED
                || widthMode == MeasureSpec.AT_MOST) {
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(DEFAULT_WIDTH, MeasureSpec.EXACTLY);
        }
        if (heightMode == MeasureSpec.UNSPECIFIED
                || heightMode == MeasureSpec.AT_MOST) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(DEFAULT_HEIGHT, MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);


        float viewPadding = Math.max(shadowRadius + shadowOffset, borderWidth);

        height = h - viewPadding - viewPadding;
        width = w - viewPadding - viewPadding;

        viewRadius = height * .5f;
        buttonRadius = viewRadius;

        left = viewPadding;
        top = viewPadding;
        right = w - viewPadding;
        bottom = h - viewPadding;

        centerX = (left + right) * .5f;
        centerY = (top + bottom) * .5f;

        buttonMinX = left + viewRadius;
        buttonMaxX = right - viewRadius;

        if (isChecked()) {
            setCheckedViewState(viewState);
        } else {
            setUncheckViewState(viewState);
        }

        isUiInited = true;

        postInvalidate();

    }

    /**
     * @param viewState
     */
    private void setUncheckViewState(ViewState viewState) {
        viewState.radius = 0;
        viewState.checkStateColor = uncheckColor;
        viewState.checkedLineColor = Color.TRANSPARENT;
        viewState.buttonX = buttonMinX;
    }

    /**
     * @param viewState
     */
    private void setCheckedViewState(ViewState viewState) {
        viewState.radius = viewRadius;
        viewState.checkStateColor = checkedColor;
        viewState.checkedLineColor = checkLineColor;
        viewState.buttonX = buttonMaxX;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.FILL);
        //绘制白色背景
        paint.setColor(background);
        canvas.drawLine(left, centerY, right, centerY, paint);

        //绘制按钮
        drawButton(canvas, viewState.buttonX, centerY);
    }

    /**
     * @param canvas
     * @param x      px
     * @param y      px
     */
    private void drawButton(Canvas canvas, float x, float y) {
        int btnColor = (int) evaluator.evaluate((x - buttonMinX) / (buttonMaxX - buttonMinX), 0XFF9B9B9B, 0XFF4BAC07);
        buttonPaint.setColor(btnColor);
        canvas.drawCircle(x, y, buttonRadius, buttonPaint);
    }

    @Override
    public boolean isChecked() {
        return isChecked;
    }

    @Override
    public void setChecked(boolean checked) {
        if (checked == isChecked()) {
            postInvalidate();
            return;
        }
        toggle(enableEffect, false);
    }

    @Override
    public void toggle() {
        toggle(true);
    }

    /**
     * 切换状态
     *
     * @param animate
     */
    public void toggle(boolean animate) {
        toggle(animate, true);
    }

    private void toggle(boolean animate, boolean broadcast) {
        if (!isEnabled()) {
            return;
        }

        if (isEventBroadcast) {
            throw new RuntimeException("should NOT switch the state in method: [onCheckedChanged]!");
        }
        if (!isUiInited) {
            isChecked = !isChecked;
            if (broadcast) {
                broadcastEvent();
            }
            return;
        }

        if (valueAnimator.isRunning()) {
            valueAnimator.cancel();
        }

        if (!enableEffect || !animate) {
            isChecked = !isChecked;
            if (isChecked()) {
                setCheckedViewState(viewState);
            } else {
                setUncheckViewState(viewState);
            }
            postInvalidate();
            if (broadcast) {
                broadcastEvent();
            }
            return;
        }

        animateState = ANIMATE_STATE_SWITCH;
        beforeState.copy(viewState);

        if (isChecked()) {
            //切换到unchecked
            setUncheckViewState(afterState);
        } else {
            setCheckedViewState(afterState);
        }
        valueAnimator.start();
    }

    /**
     *
     */
    private void broadcastEvent() {
        if (onCheckedChangeListener != null) {
            isEventBroadcast = true;
            onCheckedChangeListener.onCheckedChanged(this, isChecked());
        }
        isEventBroadcast = false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }
        int actionMasked = event.getActionMasked();

        switch (actionMasked) {
            case MotionEvent.ACTION_DOWN: {
                isTouchingDown = true;
                touchDownTime = System.currentTimeMillis();
                //取消准备进入拖动状态
                removeCallbacks(postPendingDrag);
                //预设100ms进入拖动状态
                postDelayed(postPendingDrag, 100);
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                float eventX = event.getX();
                if (isPendingDragState()) {
                    //在准备进入拖动状态过程中，可以拖动按钮位置
                    float fraction = eventX / getWidth();
                    fraction = Math.max(0f, Math.min(1f, fraction));

                    viewState.buttonX = buttonMinX
                            + (buttonMaxX - buttonMinX)
                            * fraction;

                } else if (isDragState()) {
                    //拖动按钮位置，同时改变对应的背景颜色
                    float fraction = eventX / getWidth();
                    fraction = Math.max(0f, Math.min(1f, fraction));

                    viewState.buttonX = buttonMinX
                            + (buttonMaxX - buttonMinX)
                            * fraction;

                    viewState.checkStateColor = (int) argbEvaluator.evaluate(
                            fraction,
                            uncheckColor,
                            checkedColor
                    );
                    postInvalidate();

                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                isTouchingDown = false;
                //取消准备进入拖动状态
                removeCallbacks(postPendingDrag);

                if (System.currentTimeMillis() - touchDownTime <= 300) {
                    //点击时间小于300ms，认为是点击操作
                    toggle();
                } else if (isDragState()) {
                    //在拖动状态，计算按钮位置，设置是否切换状态
                    float eventX = event.getX();
                    float fraction = eventX / getWidth();
                    fraction = Math.max(0f, Math.min(1f, fraction));
                    boolean newCheck = fraction > .5f;
                    if (newCheck == isChecked()) {
                        pendingCancelDragState();
                    } else {
                        isChecked = newCheck;
                        pendingSettleState();
                    }
                } else if (isPendingDragState()) {
                    //在准备进入拖动状态过程中，取消之，复位
                    pendingCancelDragState();
                }
                break;
            }
            case MotionEvent.ACTION_CANCEL: {
                isTouchingDown = false;

                removeCallbacks(postPendingDrag);

                if (isPendingDragState()
                        || isDragState()) {
                    //复位
                    pendingCancelDragState();
                }
                break;
            }
        }
        return true;
    }

    /**
     * 是否在动画状态
     *
     * @return
     */
    private boolean isInAnimating() {
        return animateState != ANIMATE_STATE_NONE;
    }

    /**
     * 是否在进入拖动或离开拖动状态
     *
     * @return
     */
    private boolean isPendingDragState() {
        return animateState == ANIMATE_STATE_PENDING_DRAG
                || animateState == ANIMATE_STATE_PENDING_RESET;
    }

    /**
     * 是否在手指拖动状态
     *
     * @return
     */
    private boolean isDragState() {
        return animateState == ANIMATE_STATE_DRAGING;
    }

    /**
     * 开始进入拖动状态
     */
    private void pendingDragState() {
        if (isInAnimating()) {
            return;
        }
        if (!isTouchingDown) {
            return;
        }

        if (valueAnimator.isRunning()) {
            valueAnimator.cancel();
        }

        animateState = ANIMATE_STATE_PENDING_DRAG;

        beforeState.copy(viewState);
        afterState.copy(viewState);

        if (isChecked()) {
            afterState.checkStateColor = checkedColor;
            afterState.buttonX = buttonMaxX;
            afterState.checkedLineColor = checkedColor;
        } else {
            afterState.checkStateColor = uncheckColor;
            afterState.buttonX = buttonMinX;
            afterState.radius = viewRadius;
        }

        valueAnimator.start();
    }

    /**
     * 取消拖动状态
     */
    private void pendingCancelDragState() {
        if (isDragState() || isPendingDragState()) {
            if (valueAnimator.isRunning()) {
                valueAnimator.cancel();
            }

            animateState = ANIMATE_STATE_PENDING_RESET;
            beforeState.copy(viewState);

            if (isChecked()) {
                setCheckedViewState(afterState);
            } else {
                setUncheckViewState(afterState);
            }
            valueAnimator.start();
        }
    }

    /**
     * 动画-设置新的状态
     */
    private void pendingSettleState() {
        if (valueAnimator.isRunning()) {
            valueAnimator.cancel();
        }

        animateState = ANIMATE_STATE_PENDING_SETTLE;
        beforeState.copy(viewState);

        if (isChecked()) {
            setCheckedViewState(afterState);
        } else {
            setUncheckViewState(afterState);
        }
        valueAnimator.start();
    }

    @Override
    public final void setOnClickListener(OnClickListener l) {
    }

    @Override
    public final void setOnLongClickListener(OnLongClickListener l) {
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener l) {
        onCheckedChangeListener = l;
    }

    public interface OnCheckedChangeListener {
        void onCheckedChanged(ToggleView view, boolean isChecked);
    }


    /*******************************************************/

    /**
     * 保存动画状态
     */
    private static class ViewState {
        /**
         * 按钮x位置[buttonMinX-buttonMaxX]
         */
        float buttonX;
        /**
         * 状态背景颜色
         */
        int   checkStateColor;
        /**
         * 选中线的颜色
         */
        int   checkedLineColor;
        /**
         * 状态背景的半径
         */
        float radius;

        ViewState() {
        }

        private void copy(ViewState source) {
            this.buttonX = source.buttonX;
            this.checkStateColor = source.checkStateColor;
            this.checkedLineColor = source.checkedLineColor;
            this.radius = source.radius;
        }
    }

}

