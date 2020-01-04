package org.qpython.qpy.main.widget.scheduleview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import org.qpython.qpy.R;

import java.util.List;

/**
 * Created by Hmei
 * 1/9/18.
 */
public class ScheduleView extends View {
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private int   dotCount;
    private int   lightColor;
    private int   dimColor;
    private float radius;
    private float width;
    private float margin;
    private float textSize;

    private int   lightDotCount;
    private float linePercent;

    private List<DotObj> dotObjs;


    public ScheduleView(Context context) {
        super(context);
    }

    public ScheduleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ScheduleView);
        init(typedArray);
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measuredWidth = MeasureSpec.getSize(widthMeasureSpec);
        int measuredHeight = (int) (2 * (paint.getFontSpacing() + radius + margin + 1));
        measuredWidth = resolveSize(measuredWidth, widthMeasureSpec);
        measuredHeight = resolveSize(measuredHeight, heightMeasureSpec);
        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    private void init(TypedArray typedArray) {
        dotCount = typedArray.getInt(R.styleable.ScheduleView_dot_count, 3);
        lightColor = typedArray.getInt(R.styleable.ScheduleView_light_color, getResources().getColor(R.color.theme_yellow));
        dimColor = typedArray.getInt(R.styleable.ScheduleView_dim_color, getResources().getColor(R.color.theme_gray));
        radius = typedArray.getDimensionPixelSize(R.styleable.ScheduleView_dot_radius, 20);
        width = typedArray.getDimensionPixelSize(R.styleable.ScheduleView_line_width, 5);
        margin = typedArray.getDimensionPixelSize(R.styleable.ScheduleView_margin, 8);
        textSize = typedArray.getDimensionPixelSize(R.styleable.ScheduleView_text_size, 30);
        paint.setTextSize(textSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (dotObjs != null) {
            drawText(canvas);
        } else {
            drawLine(canvas);
            drawDots(canvas);
        }
    }

    private void drawText(Canvas canvas) {
        Rect textRect = new Rect();
        paint.getTextBounds(dotObjs.get(dotObjs.size() - 1).getDotName(), 0, dotObjs.get(dotObjs.size() - 1).getDotName().length(), textRect);
        float lastTextLength = textRect.right - textRect.left;

        paint.getTextBounds(dotObjs.get(0).getDotName(), 0, dotObjs.get(0).getDotName().length(), textRect);
        float firstTextLength = textRect.right - textRect.left;

        float dotGap = (canvas.getWidth() - (firstTextLength + lastTextLength) / 2) / (dotCount - 1);
        float[] centerTextX = new float[dotObjs.size()];

        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(textSize);
        invalidate();
        paint.setColor(lightColor);

        int i = 0;

        for (DotObj dotObj : dotObjs) {
            if (i == lightDotCount) {
                paint.setColor(dimColor);
            }
            centerTextX[i] = firstTextLength / 2 + i * dotGap + getPaddingLeft();
            canvas.drawText(dotObj.getDotName(), centerTextX[i], paint.getFontSpacing(), paint);
            i++;
        }

        drawTextLine(canvas, centerTextX[0], centerTextX[dotObjs.size() - 1], paint.getFontSpacing());
        drawTextDot(canvas, centerTextX, paint.getFontSpacing());
        drawTextNum(canvas, centerTextX, paint.getFontSpacing());
    }

    private void drawTextLine(Canvas canvas, float start, float end, float textBottom) {
        float divider = (end - start) * linePercent + start;
        float top = textBottom + margin + radius;

        paint.setStrokeWidth(width);
        paint.setColor(lightColor);
        canvas.drawLine(start, top, divider, top, paint);
        paint.setColor(dimColor);
        canvas.drawLine(divider, top, end, top, paint);
    }

    private void drawTextDot(Canvas canvas, float[] centerTextX, float textBottom) {
        float cy = textBottom + margin + radius;
        paint.setColor(lightColor);
        for (int i = 0; i < centerTextX.length; i++) {
            if (i == lightDotCount) {
                paint.setColor(dimColor);
            }
            canvas.drawCircle(centerTextX[i], cy, radius, paint);
        }
    }

    private void drawTextNum(Canvas canvas, float[] centerTextX, float textBottom) {
        float startY = textBottom + 2 * (margin + radius) + paint.getFontSpacing();
        paint.setColor(dimColor);
        paint.setTextAlign(Paint.Align.CENTER);
        for (int i = 0; i < centerTextX.length; i++) {
            canvas.drawText(dotObjs.get(i).getDotNum(), centerTextX[i], startY, paint);
        }
    }

    private void drawLine(Canvas canvas) {
        float top = (canvas.getHeight() - width) / 2;
        float bottom = (canvas.getHeight() + width) / 2;
        float divider = canvas.getWidth() * linePercent;

        paint.setColor(lightColor);
        canvas.drawRect(0, top, divider, bottom, paint);
        paint.setColor(dimColor);
        canvas.drawRect(divider, top, canvas.getWidth(), bottom, paint);
    }

    private void drawDots(Canvas canvas) {
        float dotGap = (canvas.getWidth() - radius * 2) / (dotCount - 1);
        paint.setColor(lightColor);
        for (int i = 0; i < dotCount; i++) {
            if (i == lightDotCount) {
                paint.setColor(dimColor);
            }
            canvas.drawCircle(radius + i * dotGap,
                    canvas.getHeight() / 2,
                    radius, paint);
        }
    }

    public void setDotCount(int dotCount) {
        this.dotCount = dotCount;
        invalidate();
        requestLayout();
    }

    public void setLightColor(int lightColor) {
        this.lightColor = lightColor;
        invalidate();
        requestLayout();
    }

    public void setDimColor(int dimColor) {
        this.dimColor = dimColor;
        invalidate();
        requestLayout();
    }

    public void setRadius(float radius) {
        this.radius = radius;
        invalidate();
        requestLayout();
    }

    public void setWidth(float width) {
        this.width = width;
        invalidate();
        requestLayout();
    }

    public void setMargin(float margin) {
        this.margin = margin;
        invalidate();
        requestLayout();
    }

    public void setPercent(float fundingCount) {
        int[] fundingNum = new int[dotObjs.size()];
        for (int i = 0; i < dotObjs.size(); i++) {
            fundingNum[i] = Integer.parseInt(dotObjs.get(i).getDotNum());
        }

        if (fundingCount < fundingNum[0]) {
            linePercent = 0;
            lightDotCount = 0;
        } else if (fundingCount < fundingNum[1]) {
            linePercent = (fundingCount - fundingNum[0]) / (fundingNum[1] - fundingNum[0]) / 2;
            lightDotCount = 1;
        } else if (fundingCount < fundingNum[2]) {
            linePercent = 0.5f + ((fundingCount - fundingNum[1]) / (fundingNum[2] - fundingNum[1]) / 2);
            lightDotCount = 2;
        } else {
            this.setVisibility(GONE);
        }
        invalidate();
        requestLayout();
    }

    public void setDotObj(List<DotObj> dotObjs) {
        this.dotCount = dotObjs.size();
        this.dotObjs = dotObjs;
        invalidate();
        requestLayout();
    }
}
