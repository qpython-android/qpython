package org.qpython.qpy.texteditor.newtexteditor;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class ScrollBar {
	final static float MINI_SCALE=0.1f;
	float mAll=100;
	float mStart=8;
	float mEnd=9;
	float mWidth;
	float mHeight;
	Paint mPaint=new Paint();
	public ScrollBar() {
		mPaint.setColor(Color.GRAY);
	}
	
	public void draw(Canvas canvas){
		float x=mWidth*(mStart/mAll);
		float y=mWidth*(mEnd/mAll);
		canvas.drawRect(x, 0, y, mHeight, mPaint);
	}
	
	public void setSize(float width,float height)
	{
		mWidth=width;
		mHeight=height;
	}
	
	public void setPosition(float all,float start,float end){
		mAll=all;
		if(mAll<=0)
			mAll=1;
		mStart=start;
		mEnd=end;
		float scale=(mEnd-mStart)/mAll;
		if(scale<MINI_SCALE)
		{
			mStart-=mAll*(MINI_SCALE-scale)/2;
			mEnd+=mAll*(MINI_SCALE-scale)/2;
		}
	}
}
