package org.qpython.qpy.texteditor.newtexteditor;

public class SpanBody{
	Object mSpan;
	int mStart;
	int mEnd;
	int mFlag;
	public SpanBody(Object span,int start,int end,int flag) {
		mSpan=span;
		mStart=start;
		mEnd=end;
		mFlag=flag;
	}
	public boolean hasSub(int start,int end){
		if(start<mStart)
		{
			if(end>mStart)
				return true;
		}
		else
		if(start>=mEnd){
			return false;
		}
		else{
			return true;
		}
		return false;
	}
	
	public boolean in(int pos){
		if(pos>=mStart && pos<mEnd)
			return true;
		return false;
	}
	
	public int compileTo(int pos){
		if(pos<mStart)
			return -1;
		if(pos>=mEnd)
			return 1;
		return 0;
	}
	public int length()
	{
		return mEnd-mStart;
	}
}
