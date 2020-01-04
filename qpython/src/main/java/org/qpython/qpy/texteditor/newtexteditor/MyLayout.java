package org.qpython.qpy.texteditor.newtexteditor;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;

public interface MyLayout {
	public float getLineHeight();

	public void setLineHeight(float lineHeight);
	
	public int getLineForOffset(int start);

	public int getLineOffset(int line);
	
	public float getPrimaryHorizontal(int start);

	public void getLineBounds(int line, Rect mBoundsOfCursor);

	public int getLineForVertical(int top);

	public void draw(Canvas canvas);

	public int getLineCount();

	public Paint getPaint();

	public void setPaint(TextPaint paint);

	public int getOffsetForHorizontal(int line, float x);

	public int getHeight();
}
