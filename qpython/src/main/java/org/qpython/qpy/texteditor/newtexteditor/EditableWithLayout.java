package org.qpython.qpy.texteditor.newtexteditor;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Selection;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;

public class EditableWithLayout implements Editable,MyLayout {
	protected static final String TAG="EditableWithLayout";
	public static boolean mEnableHightLight=true;
    List<LineBody> mLineBodies=new ArrayList<LineBody>();
    List<SpanBody> mSpanBodies=new ArrayList<SpanBody>();
	private int mLength=0;
	private char []mText=new char[0];
	private TextPaint mTextPaint ;
	private TextPaint mSpanPaint ;
	private TextWatcher mTextWatcher=null;
    private int mSelectionStart=0;
    private int mSelectionEnd=0;
    private int mComposingStart=0;
    private int mComposingEnd=0;
    private Rect mRect=new Rect();
    private Rect mRectLine=new Rect();
    private float mLineHeight;
    private float mTabWidth;
    private ForegroundColorSpan mDefaultColorSpan;
	private int mMaxSaveHistory;
	private Stack<ReplaceBody> mUndoBodies = new Stack<ReplaceBody>();
	private Stack<ReplaceBody> mRedoBodies = new Stack<ReplaceBody>();
	public EditableWithLayout() {
		setPaint(new TextPaint());
		analysisLines();
	}
	
	static void charsCopyStartToEnd(char []src,int where,char []dst,int start,int end){
		for(;start<end;start++,where++){
			dst[start]=src[where];
		}
	}
	
	static void charsCopyEndToStart(char []src,int where,char []dst,int start,int end){
		int srcend=where+end-start;
		for(;end>=start;end--,srcend--){
			dst[end]=src[srcend];
		}
	}

	@Override
	public float getLineHeight() {
		Log.i(TAG, "getLineHeight:"+mLineHeight);
		return mLineHeight;
	}

	public void setLineHeight(float lineHeight){
		Log.i(TAG, "setLineHeight:"+lineHeight);
		mLineHeight=lineHeight;
	}
	
	@Override
	public int length() {
		return mLength;
	}
	
	private void resizeTo(int size){
		if(mText.length>size)
			return ;
		char[] text=new char[size+4096];
		System.arraycopy(mText, 0, text, 0, length());
		mText=text;
	}

	@Override
	public char charAt(int index) {
		return mText[index];
	}

	@Override
	public CharSequence subSequence(int start, int end) {
		return String.copyValueOf(mText, start, end-start);
	}
	
	@Override
	public void getChars(int start, int end, char[] dest, int destoff) {
        for (int i = start; i < end; i++)
            dest[destoff++] = mText[i];
	}

	@Override
	public void setSpan(Object what, int start, int end, int flags) {
		if( what instanceof TextWatcher )
		{
			mTextWatcher=(TextWatcher) what;
		}
		else
		if( what == Selection.SELECTION_START )
		{
			Log.i(TAG, "set SELECTION_START");
			mSelectionStart=start;
		}
		else
		if( what == Selection.SELECTION_END )
		{
			Log.i(TAG, "set SELECTION_END");
			mSelectionEnd=start;
		}
		else
		if( what instanceof ForegroundColorSpan ){
		}
		else
		if( what == MyInputConnection.COMPOSING  )
		{
			Log.i(TAG, "set COMPOSING");
			mComposingStart=start;
			mComposingEnd=end;
		}
	}
	
	public void sendTextBeforeChanged(CharSequence s,int start,int count,int after){
		if(mTextWatcher!=null)
			mTextWatcher.beforeTextChanged(s, start, count, after);
	}
	
	public void sendOnTextChanged(CharSequence s,int start,int before,int count){
		for(int i=0;i<mSpanBodies.size();i++)
		{
			SpanBody spanBody=mSpanBodies.get(i);
			if(spanBody.mEnd>start){
				spanBody.mEnd+=count;
			}
			if(spanBody.mStart>start){
				spanBody.mStart+=count;
			}
			if(spanBody.length()<=0)
			{
				mSpanBodies.remove(i);
				i--;
			}
		}
		if(mTextWatcher!=null)
			mTextWatcher.onTextChanged(s, start, before, count);
	}
	
	public void sendTextAfterChanged(){
		if(mTextWatcher!=null)
		{
			mTextWatcher.afterTextChanged(this);
		}
	}

	@Override
	public void removeSpan(Object what) {
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T[] getSpans(int start, int end, Class<T> type) {

		return (T[]) Array.newInstance(type, 0);
	}
	
	private List<SpanBody> getColorSpanBodies(int start, int end){
		LinkedList<SpanBody> bodies=new LinkedList<SpanBody>();
		int startSpan=0;
		int endSpan=mSpanBodies.size()-1;
		int pos=0;
		if(endSpan>=0)
		while(true)
		{
			int center=(startSpan+endSpan)/2;
			SpanBody centerBody=mSpanBodies.get(center);
			int ret=centerBody.compileTo(start);
			if(ret==0){
				pos=center;
				break;
			}
			if(center >= endSpan){
				pos=center;
				break;
			}
			if(ret>0)
			{
				startSpan=center+1;
			}else{
				endSpan=center-1;
			}
		}

		int proStart=start;
		for(;pos<mSpanBodies.size();pos++){
			SpanBody body=mSpanBodies.get(pos);
			if(body.mStart>=end)
				break;
			if(body.hasSub(start, end))
			{
				if(body.mStart>proStart){
					bodies.add(new SpanBody(mDefaultColorSpan, proStart, body.mStart, 0));
				}
				int tend=body.mEnd<=end?body.mEnd:end;
				int tstart=body.mStart>=start?body.mStart:start;
				if(tend-tstart>0)
					bodies.add(new SpanBody(body.mSpan, tstart, tend, 0));
				proStart=tend;
				if(proStart>=end)
					break;
			}
		}
		if(proStart<end)
			bodies.add(new SpanBody(mDefaultColorSpan, proStart, end, 0));
		/*
		if(mSpanBodies!=null)
		{
			Iterator<SpanBody> iterator=mSpanBodies.iterator();
			int proStart=start;
			while(iterator.hasNext()){
				SpanBody body=iterator.next();
				if(body.hasSub(start, end))
				{
					if(body.mStart>proStart){
						bodies.add(new SpanBody(mDefaultColorSpan, proStart, body.mStart, 0));
					}
					int tend=body.mEnd<=end?body.mEnd:end;
					int tstart=body.mStart>=start?body.mStart:start;
					if(tend-tstart>0)
						bodies.add(new SpanBody(body.mSpan, tstart, tend, 0));
					proStart=tend;
					if(proStart>=end)
						break;
				}
			}
			if(proStart<end)
				bodies.add(new SpanBody(mDefaultColorSpan, proStart, end, 0));

		}
		else{
			bodies.add(new SpanBody(mDefaultColorSpan, start, end, 0));
		}
		*/
		//LogUtil.i(TAG, "sumLoop:"+sumLoop);
		return bodies;
	}

	@Override
	public int getSpanStart(Object tag) {
		if(tag == Selection.SELECTION_START)
			return mSelectionStart;
		if(tag == Selection.SELECTION_END)
			return mSelectionEnd;
		if(tag == MyInputConnection.COMPOSING )
			return mComposingStart;
		return 0;
	}

	@Override
	public int getSpanEnd(Object tag) {
		if(tag == Selection.SELECTION_END)
			return mSelectionEnd;
		if(tag == Selection.SELECTION_START)
			return mSelectionStart;
		if(tag == MyInputConnection.COMPOSING )
			return mComposingEnd;
		return 0;
	}

	@Override
	public int getSpanFlags(Object tag) {
		return 0;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public int nextSpanTransition(int start, int limit, Class type) {
		return 0;
	}
	
	@Override
	public Editable replace(int st, int en, CharSequence text, int start,int end) {
		cleanRedo();
		return replace(st,en,text,start,end,true);
	}

	public Editable replace(int st, int en, CharSequence text, int start,int end,boolean saveToUndo) {
		int deleteLen=en-st;
		int insertLen=end-start;
		int addLen=insertLen-deleteLen;
		resizeTo(length()+addLen);
		if(deleteLen<0||insertLen<0)
			return this;
		if(deleteLen==0&&insertLen==0)
			return this;
		if(saveToUndo){
			if(mMaxSaveHistory>0){
				if(mUndoBodies.size()>mMaxSaveHistory)
					mUndoBodies.remove(0);
				ReplaceBody body=new ReplaceBody(st, en, subSequence(st, en),text, start, end, mSelectionStart, mSelectionEnd);
				if(!mUndoBodies.isEmpty() && mUndoBodies.peek().addBody(body))
				{
				}
				else
					mUndoBodies.push(body);
			}
		}

		sendOnTextChanged(this, st, length(), addLen);
		sendTextBeforeChanged(this, st,addLen ,length()+addLen);

		if(mSelectionStart==st && mSelectionEnd==en){
			mSelectionStart=mSelectionEnd=en+addLen;
		}
		else
		{
			if(mSelectionStart>=st){
				mSelectionStart+=addLen;
				if(mSelectionStart<st)
					mSelectionStart=st;
			}
			if(mSelectionEnd>=st){
				mSelectionEnd+=addLen;
				if(mSelectionEnd<st)
					mSelectionEnd=st;
			}
		}

		if(addLen>0)
			charsCopyEndToStart(mText, en, mText, en+addLen, length()+addLen);
		else
		if(addLen<0)
			charsCopyStartToEnd(mText, en, mText, en+addLen, length()+addLen);
		if(insertLen>0)
			TextUtils.getChars(text, start, end, mText, st);
		mLength+=addLen;
		if(mSelectionStart<0)
			mSelectionStart=0;
		if(mSelectionEnd<0)
			mSelectionEnd=0;
		if(mSelectionStart>mLength)
			mSelectionStart=mLength;
		if(mSelectionEnd>mLength)
			mSelectionEnd=mLength;

		analysisLines();
		sendTextAfterChanged();
		return this;
	}

	@Override
	public Editable replace(int st, int en, CharSequence text) {
		return replace(st, en, text, 0, text.length());
	}

	@Override
	public Editable insert(int where, CharSequence text, int start, int end) {
		int len = end-start;
		if(where<0 || where>length() || len<=0)
			return this;
		return replace(where,where, text, start, end);
	}

	@Override
	public Editable insert(int where, CharSequence text) {
		return insert(where,text,0,text.length());
	}
	
	@Override
	public Editable delete(int st, int en) {
		return replace(st, en, null, 0, 0);
	}

	@Override
	public Editable append(CharSequence text) {
		return null;
	}

	@Override
	public Editable append(CharSequence text, int start, int end) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Editable append(char text) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void clear() {
		sendOnTextChanged(this, 0, length(), length());
		sendTextBeforeChanged(this, 0, length(), 0);
		mLength=0;
		sendTextAfterChanged();
	}

	@Override
	public void clearSpans() {
		// TODO Auto-generated method stub

	}

	@Override
	public InputFilter[] getFilters() {
		return null;
	}

	@Override
	public void setFilters(InputFilter[] filters) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getLineForOffset(int offset) {
		if(mLineBodies.isEmpty())
			return 0;
		int startLine=0;
		int endLine=getLineCount()-1;
		if(mLineBodies instanceof ArrayList )
		{
			while(true){
				int centerLine=(startLine+endLine)/2;
				LineBody bodyCenter=mLineBodies.get(centerLine);
				int ret=bodyCenter.compileTo(offset);
				if(ret==0)
					return centerLine;
				if(centerLine>=endLine)
					break;
				if(ret<0)
					endLine=centerLine-1;
				else
					startLine=centerLine+1;
			}
		}
		else
		{
			for(int i=0;i<mLineBodies.size();i++)
			{
				LineBody body=mLineBodies.get(i);
				if(body.in(offset))
					return i;
			}
		}
		return endLine;
	}

	@Override
	public float getPrimaryHorizontal(int start) {
		int line=getLineForOffset(start);
		LineBody body= mLineBodies.get(line);
		Paint paint=getPaint();
		float horizontal=0f;
		for(int offset=body.mStart;offset<start;offset++){
			float tx=0;
			if(mText[offset]=='\t')
				tx=mTabWidth - (horizontal % mTabWidth);
			else
				tx=paint.measureText(mText, offset, 1);
			horizontal+=tx;
		}
		/*
		try {
			//horizontal=getPaint().measureText(this, body.mStart, start);
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		return horizontal;
	}

	@Override
	public void getLineBounds(int line, Rect rect) {
		rect.left=0;
		rect.right=Integer.MAX_VALUE/2;
		float top=line*mLineHeight;
		rect.top=(int) top;
		rect.bottom=(int) (top+mLineHeight);
	}

	@Override
	public int getLineForVertical(int top) {
		int line=(int) (top/mLineHeight);
		if(line<0)
			line=0;
		if(line>=getLineCount())
			line=getLineCount()-1;
		return line;
	}

	public void draw(Canvas canvas) {
		long timeStart = System.currentTimeMillis();
		canvas.getClipBounds(mRect);
		int lastLine=getLineCount()-1;
		int startLine=getLineForVertical(mRect.top);
		int endLine=getLineForVertical(mRect.bottom);
		float descent=mTextPaint.getFontMetrics().descent;
		for(int i=startLine;i<=endLine;i++)
		{
			getLineBounds(i, mRectLine);
			LineBody body=mLineBodies.get(i);
			int proTab=body.mStart;
			int nextTab;
			float startX=0;
			float lineY=mRectLine.bottom-descent;
			int start;
			int count;
			while(true){
				nextTab=getNextTab(proTab);
				if(nextTab==-1)
					break;
				if( proTab!=nextTab ){
					start=proTab;
					count=nextTab-proTab;
					List<SpanBody> bodies=getColorSpanBodies(start, start+count);
					Iterator<SpanBody> iterator=bodies.iterator();
					while(iterator.hasNext()){
						SpanBody spanBody=iterator.next();
						mSpanPaint.setColor( ((ForegroundColorSpan)spanBody.mSpan).getForegroundColor() );
						float tstartX=startX+measureText(mText, spanBody.mStart, spanBody.length());
						if(tstartX>mRect.left)
							canvas.drawText(mText, spanBody.mStart, spanBody.length(), startX, lineY, mSpanPaint);
						startX=tstartX;
						if(startX>mRect.right)
							break;
					}
					if(startX>mRect.right)
						break;
				}
				startX+=mTabWidth-(startX % mTabWidth);
				proTab=nextTab+1;
			}

			start=proTab;
			if(lastLine==i)
				count=body.mEnd-proTab;
			else
				count=body.mEnd-proTab-1;

			if(count>0)
			{
				List<SpanBody> bodies=getColorSpanBodies(start, start+count);
				Iterator<SpanBody> iterator=bodies.iterator();
				while(iterator.hasNext()){
					SpanBody spanBody=iterator.next();
					mSpanPaint.setColor( ((ForegroundColorSpan)spanBody.mSpan).getForegroundColor() );
					float tstartX=startX+measureText(mText, spanBody.mStart, spanBody.length());
					if(tstartX>mRect.left)
						canvas.drawText(mText, spanBody.mStart, spanBody.length(), startX, lineY, mSpanPaint);
					startX=tstartX;
					if(startX>mRect.right)
						break;
				}
			}

		}
		long timeEnd = System.currentTimeMillis();
	}
	
	/*
	Bitmap mCacheBitmap=null;
	Bitmap mCacheBitmapBk=null;
	Rect mPerRect=new Rect();
	Rect mNowRect=new Rect();
	@Override
	public void draw(Canvas canvas)
	{
		canvas.getClipBounds(mNowRect);
		if( mCacheBitmap==null || mPerRect.width()!=mNowRect.width() || mPerRect.height()!=mNowRect.height() )
		{//size change 
			mPerRect.set(0, 0, 0, 0);
			try {
				mCacheBitmap=null;
				mCacheBitmapBk=null;
				mCacheBitmap=Bitmap.createBitmap(mNowRect.width(), mNowRect.height(), Config.ARGB_4444);
				mCacheBitmapBk=Bitmap.createBitmap(mNowRect.width(), mNowRect.height(), Config.ARGB_4444);
			} catch (Exception e) {
				mCacheBitmap=null;
				mCacheBitmapBk=null;
			} catch (Error e) {
				mCacheBitmap=null;
				mCacheBitmapBk=null;
			}
		}
		if(mCacheBitmap!=null)
		{
			Bitmap tempBitmap=mCacheBitmap;
			mCacheBitmap=mCacheBitmapBk;
			mCacheBitmapBk=tempBitmap;
			
			Canvas canvas2=new Canvas(mCacheBitmap);
			canvas2.drawColor(Color.TRANSPARENT, Mode.CLEAR);
			if(mPerRect.width()>0||mPerRect.height()>0)
			{
				int dx=mPerRect.left-mNowRect.left;
				int dy=mPerRect.top-mNowRect.top;
				canvas2.drawBitmap(mCacheBitmapBk, dx, dy, mSpanPaint);
				canvas2.translate(-mNowRect.left, -mNowRect.top);
				if(dy>0)
				{
					canvas2.save();
					canvas2.clipRect(mNowRect.left, mNowRect.top, mNowRect.right, mPerRect.top);
					drawReal(canvas2);
					canvas2.restore();
				}
				else
				if(dy<0)
				{
					canvas2.save();
					canvas2.clipRect(mNowRect.left, mPerRect.bottom, mNowRect.right, mNowRect.bottom);
					drawReal(canvas2);
					canvas2.restore();
				}
				else  //if(dy==0)
				{
					LogUtil.i(TAG, "if(dy==0)");
					if(dx>0)
					{
						canvas2.clipRect(mNowRect.left, mNowRect.top, mPerRect.left, mNowRect.bottom );
						drawReal(canvas2);
					}
					else
					if(dx<0)
					{
						canvas2.clipRect(mPerRect.right, mNowRect.top,mNowRect.right, mNowRect.bottom );
						drawReal(canvas2);
					}
				}
			}
			else
			{
				canvas2.translate(-mNowRect.left, -mNowRect.top);
				drawReal(canvas2);
			}
			canvas.drawBitmap(mCacheBitmap, mNowRect.left, mNowRect.top, mSpanPaint);
			mPerRect.set(mNowRect);
		}
		else
			drawReal(canvas);
	}
	*/
	
	@SuppressWarnings("unused")
	private void drawTest(Canvas canvas)
	{
		float startY=0;
		for(int y=0;y<40;y++)
		{
			float startX=0;
			for(int x=0;x<18;x++)
			{
				String text=""+x;
				mSpanPaint.setColor(Color.rgb(255, 0, 0));
				canvas.drawText(text, startX, startY, mSpanPaint);
				startX+=mSpanPaint.measureText(text);

				text=",";
				mSpanPaint.setColor(Color.rgb(0, 0, 0));
				canvas.drawText(text, startX, startY, mSpanPaint);
				startX+=mSpanPaint.measureText(text);

			}
			startY+=mSpanPaint.getTextSize();
		}
	}
	
	public float measureText(char []text,int index,int count)
	{
		return mSpanPaint.measureText(text,index,count);
	}
	
	private int getNextTab(int offset){
		int len=length();
		while(offset<len ){
			if(mText[offset]=='\n')
				return -1;
			if(mText[offset]=='\t')
				return offset;
			offset++;
		}
		return -1;
	}

	private void analysisLines(){
		mLineBodies.clear();
		int start=0;
		for(int i=0;i<mLength;i++){
			if(mText[i]=='\n'){
				mLineBodies.add(new LineBody(start, i+1));
				start=i+1;
			}
		}
		mLineBodies.add(new LineBody(start, mLength));
	}
	
	@Override
	public int getLineCount() {
		return mLineBodies.size();
	}
	
	@Override
	public Paint getPaint() {
		return mTextPaint;
	}

	public void setPaint(TextPaint paint){
		mTextPaint=paint;
		mLineHeight=paint.getTextSize();
		mTabWidth=mTextPaint.measureText("0000");
		mSpanPaint=new TextPaint(paint);
		mDefaultColorSpan=new ForegroundColorSpan(mTextPaint.getColor());
	}
	
	@Override
	public int getOffsetForHorizontal(int line, float x) {
		LineBody body=mLineBodies.get(line);
		Paint paint = getPaint();
		float sumx=0;
		int pos=body.mStart;
		if(x<=0)
			return pos;
		for(;pos<body.mEnd;pos++){
			float tx=0;
			if(mText[pos]=='\t')
				tx=mTabWidth - (sumx % mTabWidth);
			else
				tx=paint.measureText(mText, pos, 1);
			if(x<=sumx+tx/2)
				return pos;
			sumx+=tx;
		}
		int lineCount=getLineCount();
		if(line==lineCount-1)
			return body.mEnd;
		return body.mEnd-1;
	}

	@Override
	public int getHeight() {
		return (int) (getLineCount()*mLineHeight);
	}	

	@Override
	public String toString() {
		return String.copyValueOf(mText, 0, length());
	}
	public void applyColorSpans(List<SpanBody> spans){
		if(mEnableHightLight)
			mSpanBodies=spans;
	}

	public void addColorSpan(SpanBody spanBody){
		if(mEnableHightLight)
			mSpanBodies.add(spanBody);
	}
	@Override
	public int getLineOffset(int line) {
		return mLineBodies.get(line).mStart;
	}
	
	public void setMaxSaveHistory(int maxSaveHistory){
		mMaxSaveHistory=maxSaveHistory;
	}

	public void cleanUndo(){
		mUndoBodies.clear();
	}
	
	public boolean canUndo(){
		Log.i(TAG, "canUndo :"+!mUndoBodies.isEmpty());
		return !mUndoBodies.isEmpty();
	}
	
	public boolean undo(){
		if(!canUndo())
			return false;
		ReplaceBody body=mUndoBodies.pop();
		ReplaceBody replaceBody=body.getUndoBody();
		if(mMaxSaveHistory>0){
			if(mRedoBodies.size()>mMaxSaveHistory)
				mRedoBodies.remove(0);
			mRedoBodies.push(body);
		}
		replace(replaceBody.mSt, replaceBody.mEn, replaceBody.mText, replaceBody.mStart, replaceBody.mEnd, false);
		if(replaceBody.mText==null || replaceBody.mText.length()==0 || replaceBody.mEnd-replaceBody.mStart==0 )
		{
			mSelectionStart=replaceBody.mSt;
			mSelectionEnd=mSelectionStart;
		}
		else
		{
			mSelectionStart=replaceBody.mSt;
			mSelectionEnd=replaceBody.mSt+replaceBody.mEnd-replaceBody.mStart;
		}
		return true;
	}
	
	public void cleanRedo(){
		mRedoBodies.clear();
	}
	
	public boolean canRedo(){
		Log.i(TAG, "canRedo :"+!mRedoBodies.isEmpty());
		return !mRedoBodies.isEmpty();
		
	}
	
	public boolean redo(){
		if(!canRedo())
			return false;
		ReplaceBody body=mRedoBodies.pop();
		ReplaceBody replaceBody=body.getRedoBody();
		if(mMaxSaveHistory>0){
			if(mUndoBodies.size()>mMaxSaveHistory)
				mUndoBodies.remove(0);
			mUndoBodies.push(body);
		}
		replace(replaceBody.mSt, replaceBody.mEn, replaceBody.mText, replaceBody.mStart, replaceBody.mEnd, false);
		if(replaceBody.mText==null || replaceBody.mText.length()==0 || replaceBody.mEnd-replaceBody.mStart==0 )
		{
			mSelectionStart=replaceBody.mSt;
			mSelectionEnd=mSelectionStart;
		}
		else
		{
			mSelectionStart=replaceBody.mSt;
			mSelectionEnd=replaceBody.mSt+replaceBody.mEnd-replaceBody.mStart;
		}
		return true;
	}
	
}


class LineBody{
	int mStart;
	int mEnd;
	public LineBody(int start,int end) {
		mStart=start;
		mEnd=end;
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
		return mEnd-mStart-1;
	}	
}



class ReplaceBody{
	int mSt;
	int mEn;
	CharSequence mSubtext;
	CharSequence mText;
	int mStart;
	int mEnd;
	int mSelectionStart;
	int mSelectionEnd;
	public ReplaceBody(int st, int en,CharSequence subtext, CharSequence text, int start,int end,int selectionStart,int selectionEnd) {
		mSt=st;
		mEn=en;
		mSubtext=subtext;
		mText=text;
		mStart=start;
		mEnd=end;
		mSelectionStart=selectionStart;
		mSelectionEnd=selectionEnd;
	}
	
	public ReplaceBody getUndoBody(){
		return new ReplaceBody(mSt, mSt+mEnd-mStart, mText,mSubtext, 0, mSubtext.length(), mSelectionStart, mSelectionEnd);
	}
	
	public ReplaceBody getRedoBody(){
		return this;
	}
	
	public boolean isDelete(){
		if(mEn-mSt>0&&mStart==0&&mEnd==0)
			return true;
		return false;
	}
	
	public boolean isInsert(){
		if(mSt==mEn && mText!=null && mText.length()!=0 &&mEnd-mStart>0)
			return true;
		Log.i("isInsert", "false");
		return false;
	}
	
	public boolean addBody(ReplaceBody body){
		if( isDelete() && body.isDelete() && mSt==body.mEn )
		{
			this.mSt=body.mSt;
			this.mSubtext=body.mSubtext.toString()+this.mSubtext;
			this.mSelectionStart=body.mSelectionStart;
			return true;
		}
		Log.i("addBody", "addBody");
		if( isInsert() && body.isInsert() && mSt+mText.length()==body.mSt) 
		{
			Log.i("addBody", "�ϲ�����Ĳ��� ");
			this.mText=this.mText+body.mText.subSequence(body.mStart, body.mEnd).toString();
			this.mEnd+=body.mEnd-body.mStart;
			this.mSelectionEnd = body.mSelectionEnd;
			return true;
		}
		return false;
	}
}
