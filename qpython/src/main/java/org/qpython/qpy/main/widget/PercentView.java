package org.qpython.qpy.main.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import org.qpython.qpy.R;

/**
 * Created by Hmei
 * 1/10/18.
 */

public class PercentView extends View {
    public static final int CIRCLE_ANGLE = 360;

    private int percent = 0;
    private int   lightColor;
    private int   dimColor;
    private float width;
    private float textSize;
    private Paint paint;
    private RectF rect;

    {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        rect = new RectF();
    }

    public PercentView(Context context) {
        super(context);
    }

    public PercentView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.PercentView);
        lightColor = typedArray.getInt(R.styleable.PercentView_pv_light_color, getResources().getColor(R.color.theme_yellow));
        dimColor = typedArray.getInt(R.styleable.PercentView_pv_dim_color, getResources().getColor(R.color.white));
        width = typedArray.getFloat(R.styleable.PercentView_pv_width, 10);
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int size = MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(size, size);
        rect.set(width / 2, width / 2, size - width / 2, size - width / 2);
        paint.setStrokeWidth(width);
        textSize = size / 4;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float lightAngle = CIRCLE_ANGLE * (percent / 100.00f);

        // draw circle
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(lightColor);
        canvas.drawArc(rect, -90, lightAngle, false, paint);
        paint.setColor(dimColor);
        canvas.drawArc(rect, lightAngle - 90, CIRCLE_ANGLE - lightAngle, false, paint);

        // draw text
        paint.setColor(lightColor);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(textSize);
        paint.setFakeBoldText(true);
        canvas.drawText(percent + "%", (rect.right - rect.left + width) / 2, (rect.bottom - rect.top - width) / 2 + paint.getFontSpacing() / 2, paint);
    }

    public int getPercent() {
        return percent;
    }

    public void setPercent(int percent) {
        this.percent = percent;
        invalidate();
    }

    public int getLightColor() {
        return lightColor;
    }

    public void setLightColor(int lightColor) {
        this.lightColor = lightColor;
    }

    public int getDimColor() {
        return dimColor;
    }

    public void setDimColor(int dimColor) {
        this.dimColor = dimColor;
    }
}
