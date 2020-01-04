package org.qpython.qpy.texteditor.newtexteditor;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.ClipboardManager;
import android.text.Editable;
import android.text.Selection;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.Scroller;

public class MyEditText extends View implements OnGestureListener,TextWatcher, OnScaleGestureListener {
	protected static final String TAG="MyEditText";
	
	public static float mFontScale = 1.0f;
	public static float mLineScale = 1.0f;
	public static int mBackGroundColor = Color.TRANSPARENT;
	public static int mBaseFontColor = Color.BLACK;
	protected MyInputConnection mBaseInputConnection;
	protected int mDownState=0;
	MyLayout mLayout;
	Paint mCursorPaint=new Paint();
	Paint mSelectionPaint=new Paint();
	Paint mSelectionBackgroundPaint=new Paint();
	TextPaint mTextPaint=new TextPaint();
	TextPaint mLineNumberPaint=new TextPaint();
	int i=0;
	private GestureDetector mGestureDetector;
	private ScaleGestureDetector mScaleGestureDetector;
	private Scroller mScroller;
	private ScrollBar mScrollBar;
	private float mSelectBarRadius = 1;
	private Rect mBoundsOfCursor=new Rect();
	private Rect mBounds=new Rect();

	
	public MyEditText(Context context) {
		super(context);
		init();
	}

	public MyEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	public void insertText(CharSequence charSequence){
		mBaseInputConnection.commitText(charSequence, getCursor());
	}
	
	public void setTextSize(float size){
		mTextPaint.setTextSize(size*mFontScale);
		mTextPaint.setTypeface(Typeface.MONOSPACE);
		mLineNumberPaint=new TextPaint(mTextPaint);
		mLineNumberPaint.setColor(Color.GRAY);
		mLineNumberPaint.setTypeface(Typeface.MONOSPACE);
		getLayout().setPaint(mTextPaint);
		getLayout().setLineHeight(size*mFontScale*1.2f*mLineScale);
	}
	
	public Editable getText(){
		return mBaseInputConnection.getEditable();
	}
	
	public void setText(CharSequence charSequence){
		if(mBaseInputConnection==null)
			mBaseInputConnection=new MyInputConnection(this)
		{
		    public boolean clearMetaKeyStates(int states) {
		    	mDownState=0;
		    	return super.clearMetaKeyStates(states);
		    }

			@Override
		    public boolean performContextMenuAction(int id) {
				MyEditText.this.performContextMenuAction(id);
		        return super.performContextMenuAction(id);
		    }

			@Override
			public boolean setSelection(int start, int end) {

				if(start==end){
					post(new Runnable() {
						@Override
						public void run() {
							bringPosToVisible(getCursor());
						}
					});
				}
				else
				if(isMoveSelectionStart()){
					postDelayed(new Runnable() {
						@Override
						public void run() {
							bringPosToVisible(getSelectionStart());
						}
					},100);
				}else
				if(isMoveSelectionEnd()){
					postDelayed(new Runnable() {
						@Override
						public void run() {
							bringPosToVisible(getSelectionEnd());
						}
					},100);
				}else{
					postDelayed(new Runnable() {
						@Override
						public void run() {
							bringPosToVisible(getSelectionEnd());
						}
					},100);
				}
				return super.setSelection(start, end);
			}
		};

		mBaseInputConnection.getEditable().clear();
		if(charSequence!=null){
			mBaseInputConnection.getEditable().insert(0, charSequence);
		}
		else{
		}

		if(getCursor()>mBaseInputConnection.getEditable().length())
			setCursor(mBaseInputConnection.getEditable().length());

		cleanRedo();
		cleanUndo();
		makeLayout();
	}
	
	public MyLayout getLayout(){
		if(mBaseInputConnection==null)
			return null;
		return mBaseInputConnection.getLayout();
	}

	private void init()
	{
		setWillNotCacheDrawing(true);
		setText("");
		mTextPaint.setColor(mBaseFontColor);
		setBackgroundColor(mBackGroundColor);

		mScroller=new Scroller(getContext());
		mGestureDetector=new GestureDetector(getContext(), this);
		mScaleGestureDetector=new ScaleGestureDetector(getContext(), this);
		mScrollBar=new ScrollBar();
		float density=getContext().getResources().getDisplayMetrics().density;
		mScrollBar.setSize(getHeight(),density*4);
		float size=density*12f;
		setTextSize(size);
		mSelectionBackgroundPaint.setColor(Color.argb(0x60, 0x80, 0xf8, 0x80));
		mSelectionPaint.setColor(Color.argb(0xff, 0x80, 0x88, 0xff));
		mLineNumberPaint.setTypeface(Typeface.MONOSPACE);
		float strokeWidth=getContext().getResources().getDisplayMetrics().density;
		mSelectBarRadius=strokeWidth*18;
		if(strokeWidth<1)
			strokeWidth=1;
		mCursorPaint.setStrokeWidth(strokeWidth);
		setScrollContainer(true);
		this.post(new Runnable(){
			int colors[]={
					Color.BLACK,Color.argb(0x80, 0xF0, 0x80, 0x80),
					};
			int num=0;
			@Override
			public void run() {
				if(num>=colors.length)
					num=0;
				mCursorPaint.setColor(colors[num]);
				MyEditText.this.postDelayed(this, 360);
				postInvalidate();
				num++;
			}
		});
		setScrollbarFadingEnabled(true);
	}

	@Override
	public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
		outAttrs.imeOptions=EditorInfo.IME_FLAG_NO_ENTER_ACTION | EditorInfo.IME_ACTION_DONE | EditorInfo.IME_FLAG_NO_EXTRACT_UI;
		outAttrs.inputType=EditorInfo.TYPE_CLASS_TEXT ;//1310738;
		return mBaseInputConnection;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		post(new Runnable() {
			@Override
			public void run() {
				setCursor(getCursor());
				bringPosToVisible(getCursor());
			}
		});
		float density=getContext().getResources().getDisplayMetrics().density;
		mScrollBar.setSize(getHeight(),density*4);
		//mEdgeEffectLeft.setSize(getHeight(),getWidth());
		super.onSizeChanged(w, h, oldw, oldh);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(event.getAction()==MotionEvent.ACTION_DOWN)
		{
			mScroller.abortAnimation();
		}
		mGestureDetector.onTouchEvent(event);
		mScaleGestureDetector.onTouchEvent(event);
		return true;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		if(mLayout!=null)
		{
			int selectLineStart=0;
			int selectLineEnd=0;
			float selectStartX=0;
			float selectEndX=0;
			if(isSelection())
			{
				int start=getSelectionStart();
				int end=getSelectionEnd();
				selectLineStart=mLayout.getLineForOffset(start);
				selectLineEnd=mLayout.getLineForOffset(end);
				selectStartX=mLayout.getPrimaryHorizontal(start);
				selectEndX=mLayout.getPrimaryHorizontal(end);
			}

			int cursor=getCursor();
			float x=mLayout.getPrimaryHorizontal(cursor);
			int line=mLayout.getLineForOffset(cursor);
			mLayout.getLineBounds(line, mBoundsOfCursor);
			canvas.drawLine(x, mBoundsOfCursor.top, x, mBoundsOfCursor.bottom, mCursorPaint);

			canvas.getClipBounds(mBounds);
			int lineStart=mLayout.getLineForVertical(mBounds.top);
			int lineEnd=mLayout.getLineForVertical(mBounds.bottom);
			float descent=mLayout.getPaint().getFontMetrics().descent;
			for(int i=lineStart;i<=lineEnd;i++){

				mLayout.getLineBounds(i, mBounds);
				if(mBounds.left>=0)
				{
					String value=String.valueOf(i+1);
					canvas.drawText(value, mBounds.left-mLineNumberPaint.measureText(value+"0"), mBounds.bottom - descent , mLineNumberPaint);
				}
				if(isSelection())
				{
					if(i>selectLineStart && i<selectLineEnd){
						canvas.drawRect(mBounds, mSelectionBackgroundPaint);
					}
					else
					if(i==selectLineStart )
					{
						if(i==selectLineEnd){
							canvas.drawRect(selectStartX, mBounds.top, selectEndX, mBounds.bottom, mSelectionBackgroundPaint);
						}
						else{
							canvas.drawRect(selectStartX, mBounds.top, mBounds.right, mBounds.bottom, mSelectionBackgroundPaint);
						}
					}
					else
					if(i==selectLineEnd )
					{
						canvas.drawRect(0, mBounds.top, selectEndX, mBounds.bottom, mSelectionBackgroundPaint);
					}
				}
			}

			try {
				mLayout.draw(canvas);
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
			}

			if(isSelection()){
				int r=(int) mSelectBarRadius;
				canvas.drawCircle(x, mBoundsOfCursor.bottom + r, r, mSelectionPaint);

				cursor=getSelectionStart();
				x=mLayout.getPrimaryHorizontal(cursor);
				line=mLayout.getLineForOffset(cursor);
				mLayout.getLineBounds(line, mBoundsOfCursor);
				canvas.drawLine(x, mBoundsOfCursor.top, x, mBoundsOfCursor.bottom, mCursorPaint);

				canvas.drawCircle(x, mBoundsOfCursor.bottom + r, r, mSelectionPaint);
			}
		}
		canvas.save();
		canvas.rotate(90);
		canvas.translate( getScrollY(),-(getScrollX()+getWidth()) );
		mScrollBar.draw(canvas);
		canvas.save();
		/*
		canvas.restore();
		canvas.save();
		canvas.translate(getScrollX(), getScrollY());
		mEdgeEffectTop.draw(canvas);
		canvas.restore();
		canvas.save();
		canvas.rotate(180);
		canvas.translate( -(getScrollX()+getWidth()), -(getScrollY()+getHeight()) );
		mEdgeEffectBottom.draw(canvas);
		canvas.restore();
		canvas.rotate(-90);
		canvas.translate( -(getScrollY()+getHeight()) ,getScrollX() );
		//mEdgeEffectLeft.draw(canvas);
		canvas.restore();
		canvas.save();
		*/
	}

	protected void makeLayout(){
		if(getText()!=null)
		{
			mLayout=mBaseInputConnection.getLayout();
			//mLayout=new StaticLayout(mEditable, mTextPaint, Integer.MAX_VALUE,Layout.Alignment.ALIGN_NORMAL,1f,0f,false);
			//mLayout=new DynamicLayout(getText(),mTextPaint,Integer.MAX_VALUE,Layout.Alignment.ALIGN_NORMAL,1f,0f,false);
		}
	}
	
	public TextPaint getPaint(){
		return mTextPaint;
	}

	private float getWidthOfBit(int bit)
	{
		String str="";
		for(int i=0;i<bit;i++)
		{
			str+='0';
		}
		return getPaint().measureText(str);
	}

	public int getBoundOfLeft(){
		int left=(int) getWidthOfBit(1+getBitOfNum(getLineCount()));
		return -(left);
	}
	
	public int getBoundOfRight(){
		if(mLayout==null)
			return getWidth();
		return Integer.MAX_VALUE/2;
	}
	
	public int getBoundOfTop(){
		return 0;
	}
	
	public int getBoundOfBottom(){
		if(mLayout==null)
			return getHeight();
		int h1=mLayout.getHeight();
		int h2=getHeight();
		return h1>h2?h1:h2;
	}
	
	@Override
	public void scrollTo(int x, int y) {
		int left=getBoundOfLeft();
		int right=getBoundOfRight();
		int top=getBoundOfTop();
		int bottom=getBoundOfBottom();
		if(x<left)
		{
			x=left;
		}
		if(x>right)
			x=right;
		if(y<top)
		{
			y=top;
		}
		if(y>bottom)
		{
			y=bottom;
		}
		mScrollBar.setPosition(bottom-top+getHeight(), y-top, y-top+getHeight());
		super.scrollTo(x, y);
	}

	private int getBitOfNum(int num)
	{
		if(num<10)
			return 1;
		if(num<100)
			return 2;
		if(num<1000)
			return 3;
		if(num<10000)
			return 4;
		if(num<100000)
			return 5;
		if(num<1000000)
			return 6;
		if(num<10000000)
			return 7;
		if(num<100000000)
			return 8;
		return 0;
	}

    public int getLineCount() {
        return mLayout != null ? mLayout.getLineCount() : 0;
    }

    public boolean isSelection(){
    	return getSelectionStart()!=getSelectionEnd();
    }
    
    public int getSelectionStart(){
    	return Selection.getSelectionStart(mBaseInputConnection.getEditable());
    }
    
    public int getSelectionEnd(){
    	return Selection.getSelectionEnd(mBaseInputConnection.getEditable());
    }
    
    public int getCursor(){
    	return getSelectionEnd();
    }
    
    public void bringPosToVisible(int pos){
    	if(mLayout!=null){
    		if(pos>=0 && pos<=getText().length()){
    			float x=mLayout.getPrimaryHorizontal(pos);
    			int line=mLayout.getLineForOffset(pos);
    			mLayout.getLineBounds(line, mBoundsOfCursor);
    			int scrollX=getScrollX();
    			int scrollY=getScrollY();
    			if(mBoundsOfCursor.top<scrollY){
    				scrollY=mBoundsOfCursor.top;
    			}
    			if(mBoundsOfCursor.bottom>scrollY+getHeight())
    				scrollY=mBoundsOfCursor.bottom-getHeight();

    			if(x<scrollX+mTextPaint.getTextSize())
    				scrollX= (int)( x-mTextPaint.getTextSize());

    			if(x>scrollX+getWidth()-mTextPaint.getTextSize())
    				scrollX=(int) (x-getWidth()+mTextPaint.getTextSize());

    			scrollTo(scrollX, scrollY);
    			postInvalidate();
    		}
    	}
    }
    
    public boolean setCursor(int cursor){
    	return setSelection(cursor, cursor);
    }
	
	public void showSoftKeyboard(){
		setFocusableInTouchMode(true);
		requestFocus();
		InputMethodManager inputMethodManager=(InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.showSoftInput(this,InputMethodManager.SHOW_FORCED);
	}

	public boolean isMoveSelectionStart(){
		return mDownState==1;
	}
	
	public boolean isMoveSelectionEnd(){
		return mDownState==2;
	}
	
	private boolean isMoveSelection(){
		return mDownState!=0;
	}
	
	@Override
	public boolean onDown(MotionEvent e) {
		mDownState=0;
		if(isSelection())
		{	
			int cursor=getCursor();
			float x=mLayout.getPrimaryHorizontal(cursor);
			int line=mLayout.getLineForOffset(cursor);
			mLayout.getLineBounds(line, mBoundsOfCursor); 
			int r=(int) mSelectBarRadius;
			int y=mBoundsOfCursor.bottom + r;
			if( Math.sqrt( Math.pow(getScrollX()+e.getX()-x, 2f) + Math.pow(getScrollY()+e.getY()-y, 2f)  ) <= r){
				mDownState=2;
			}

			cursor=getSelectionStart();
			x=mLayout.getPrimaryHorizontal(cursor);
			line=mLayout.getLineForOffset(cursor);
			mLayout.getLineBounds(line, mBoundsOfCursor); 
			r=(int) mSelectBarRadius;
			y=mBoundsOfCursor.bottom + r;
			if( Math.sqrt( Math.pow(getScrollX()+e.getX()-x, 2f) + Math.pow(getScrollY()+e.getY()-y, 2f)  ) <= r){
				mDownState=1;
			}
		}
		return true;
	}

	@Override
	public void onShowPress(MotionEvent e) {
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		if(mLayout!=null){
			int line=mLayout.getLineForVertical(getScrollY()+(int) e.getY());
			int offset=mLayout.getOffsetForHorizontal(line, getScrollX()+(int)e.getX());
			setCursor(offset);
		}
		showSoftKeyboard();
		return false;
	}
	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		if(!isMoveSelection())
		{
			scrollBy((int)distanceX, (int )distanceY);
			invalidate();  
		}
		else{
			int line=mLayout.getLineForVertical(getScrollY()+(int) e2.getY()-(int)(1.5f*mLayout.getLineHeight()) );
			int offset=mLayout.getOffsetForHorizontal(line, getScrollX()+(int)e2.getX());
			int start=getSelectionStart();
			int end=getSelectionEnd();
			if(isMoveSelectionEnd()){
				if(offset<=start)
					offset=start+1;
				if(offset<=getText().length())
				{
					setSelection(start, offset);
					postInvalidate();
				}
			}else
			if(isMoveSelectionStart()){
				if(offset>=end)
					offset=end-1;
				if(offset>=0){
					setSelection(offset, end);
					postInvalidate();
				}
			}
		}
		return true;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		if(isSelection()){
			/*
			PopupMenu popupMenu=new PopupMenu(getContext(), this );
			Menu menu=popupMenu.getMenu();
			menu.add(0, android.R.id.copy, 0, android.R.string.copy);
			menu.add(0, android.R.id.cut, 0, android.R.string.cut);
			menu.add(0, android.R.id.selectAll, 0, android.R.string.selectAll);
			menu.add(0, android.R.id.paste, 0, android.R.string.paste);
			popupMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {
				public boolean onMenuItemClick(MenuItem arg0) {
					int id=arg0.getItemId();
					if(id==android.R.id.paste)
						setCursor(getCursor());
					return performContextMenuAction(id);
				}
			});
			popupMenu.show();*/
		}else
		if(mLayout!=null){
			int line=mLayout.getLineForVertical(getScrollY()+(int) e.getY());
			int offset=mLayout.getOffsetForHorizontal(line, getScrollX()+(int)e.getX());
			if(!selectAtPosition(offset)){
				int start=offset-1;
				if(start<0)
					start=0;
				setSelection(start, offset);
			}
		}
	}

	private boolean selectAtPosition(int position){
		int start=position;
		int end=position;
		Editable editable=getText();
		if(position<0 || position>=editable.length())
			return false;
		char ch=editable.charAt(position);
		if( !(Character.isLetterOrDigit(ch) || ch=='_') )
			return false;
		for(start=position;start>0;start--){
			char indexCh=editable.charAt(start);
			if(!(Character.isLetterOrDigit(indexCh) || indexCh=='_') ){
				start++;
				break;
			}
		}
		
		for(end=position;end<editable.length()-1;end++){
			char indexCh=editable.charAt(end);
			if(!(Character.isLetterOrDigit(indexCh) || indexCh=='_') ){
				break;
			}
		}
		if(start>=end)
			return false;
		setSelection(start, end);
		return true;
	}
	
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		if(!isMoveSelection())
		{
			mScroller.fling(getScrollX(), getScrollY(), -(int)velocityX , -(int)velocityY, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE,  Integer.MAX_VALUE);
    		postInvalidate();  
		}
		return true;
	}
	
    @Override  
    public void computeScroll() {  
    	if (mScroller.computeScrollOffset()) 
    	{
    		scrollTo(mScroller.getCurrX(), mScroller.getCurrY());  
    	}  
    }
    
	public boolean setSelection(int start, int end) {
		mScroller.abortAnimation();
    	return mBaseInputConnection.setSelection(start, end);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch(event.getKeyCode()){
		case KeyEvent.KEYCODE_SHIFT_LEFT:
			mDownState=1;
			break;
		case KeyEvent.KEYCODE_SHIFT_RIGHT:
			mDownState=2;
			break;
		default:
			return false;	
		}
		return true;
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		Editable editable=getText();
		int start=Selection.getSelectionStart(editable);
		int end=Selection.getSelectionEnd(editable);
		if(event.getAction()==KeyEvent.ACTION_UP)
		switch(event.getKeyCode()){
		case KeyEvent.KEYCODE_DEL:
			if(start>=end)
				start=end-1;
			if(start>=0)
			editable.delete(start, end);
			break;
		case KeyEvent.KEYCODE_ENTER:
			editable.replace(start, end, "\n");
			setSelection(start+1, start+1);
			break;
		case KeyEvent.KEYCODE_DPAD_LEFT:
			if(isMoveSelectionStart()){
				start--;
				if(start>=0)
					setSelection(start, end);
			}else if(isMoveSelectionEnd()){
				end--;
				if(end>start)
					setSelection(start, end);
			}else {
				end--;
				if(end>=0)
					setSelection(end, end);
			}
			break;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			if(isMoveSelectionStart()){
				start++;
				if(start<end)
					setSelection(start, end);
			}else if(isMoveSelectionEnd()){
				end++;
				if(end<=editable.length())
					setSelection(start, end);
			}else {
				end++;
				if(end<=editable.length())
					setSelection(end, end);
			}
			break;
		case KeyEvent.KEYCODE_DPAD_UP :
			if(isMoveSelectionStart()){
				int line=mLayout.getLineForOffset(start);
				float x=mLayout.getPrimaryHorizontal(start);
				if(line>0)
				{
					int position=mLayout.getOffsetForHorizontal(line-1, x);
					if(position>=0 && position < end)
						setSelection(position, end);
				}
			}
			else
			{
				int line=mLayout.getLineForOffset(end);
				if(line>0){
					float x=mLayout.getPrimaryHorizontal(end);
					int position=mLayout.getOffsetForHorizontal(line-1, x);
					if(isMoveSelectionEnd()){
						if(position>start && position<=editable.length())
							setSelection(start, position);
					}
					else{
						setCursor(position);
					}
				}
			}
			break;
		case KeyEvent.KEYCODE_DPAD_DOWN:
			if(isMoveSelectionStart()){
				int line=mLayout.getLineForOffset(start);
				if(line<mLayout.getLineCount()-1){
					float x=mLayout.getPrimaryHorizontal(start);
					int position=mLayout.getOffsetForHorizontal(line+1, x);
					if(position>=0 && position < end)
						setSelection(position, end);
				}
			}else{
				int line=mLayout.getLineForOffset(end);
				if(line<mLayout.getLineCount()-1){
					float x=mLayout.getPrimaryHorizontal(end);
					int position=mLayout.getOffsetForHorizontal(line+1, x);
					if(isMoveSelectionEnd()){
						if(position>start && position<=editable.length())
							setSelection(start, position);
					}
					else
						setCursor(position);
				}
			}
			break;
		default:
			return false;	
		}
		return true;
	}

	public boolean performContextMenuAction(int id) {
		Editable editable=getText();
		int start=Selection.getSelectionStart(editable);
		int end=Selection.getSelectionEnd(editable);
		switch (id) {
		case android.R.id.selectAll:
			setSelection(0, editable.length());
			break;
		case android.R.id.cut:
			try {
				ClipboardManager clipboardManager= (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
				clipboardManager.setText(editable.subSequence(start, end));
				editable.delete(start, end);
				setSelection(start, start);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case android.R.id.copy:
			try {
				ClipboardManager clipboardManager= (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
				clipboardManager.setText(editable.subSequence(start, end));
				setSelection(end, end);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case android.R.id.paste:
			try {
				ClipboardManager clipboardManager= (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
				CharSequence charSequence=clipboardManager.getText();
				editable.replace(start, end, charSequence);
				int t=start+charSequence.length();
				setSelection(t,t);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		default:
			return false;
		}
		return true;
	}


	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,int after) {
	}


	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
	}


	@Override
	public void afterTextChanged(Editable s) {
		post(new Runnable() {
			@Override
			public void run() {
				bringPosToVisible(getCursor());
			}
		});
	}

	public void closeInputMethod() {
		Log.i(TAG, "closeInputMethod");
		View editView=this;
	    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
	    boolean isOpen = imm.isActive();
	    if (isOpen) {
	        imm.hideSoftInputFromWindow(editView.getApplicationWindowToken(), 0 );
	    }
	}

	public void setMaxSaveHistory(int maxSaveHistory){
		if(getLayout() instanceof EditableWithLayout)
		{
			((EditableWithLayout)getLayout()).setMaxSaveHistory(maxSaveHistory);
		}
	}

	public void cleanUndo(){
		if(getLayout() instanceof EditableWithLayout)
		{
			((EditableWithLayout)getLayout()).cleanUndo();
		}
	}
	
	public boolean canUndo(){
		if(getLayout() instanceof EditableWithLayout)
		{
			return ((EditableWithLayout)getLayout()).canUndo();
		}
		return false;
	}
	
	public boolean undo(){
		if(getLayout() instanceof EditableWithLayout)
		{
			return ((EditableWithLayout)getLayout()).undo();
		}
		return false;
	}
	
	public void cleanRedo(){
		if(getLayout() instanceof EditableWithLayout)
		{
			((EditableWithLayout)getLayout()).cleanRedo();
		}
	}
	
	public boolean canRedo(){
		if(getLayout() instanceof EditableWithLayout)
		{
			return ((EditableWithLayout)getLayout()).canRedo();
		}
		return false;
		
	}
	
	public boolean redo(){
		if(getLayout() instanceof EditableWithLayout)
		{
			return ((EditableWithLayout)getLayout()).redo();
		}
		return false;
	}
	
	public boolean findString(String str){
		int index=getText().toString().indexOf(str, getCursor());
		if(index == -1)
			return false;
		return setSelection(index, index+str.length());
	}
	
	public boolean replaceString(String str){
		getText().replace(getSelectionStart(), getSelectionEnd(), str);
		return true;
	}
	
	public boolean replaceFindString(String find,String replace){
		if(find.length() <= 0)
			return false;
		setCursor(getSelectionStart());
		if(!findString(find))
			return false;
		replaceString(replace);
		return true;
	}
	
	public boolean replaceAll(String find,String replace){
		int start=getSelectionStart();
		int end = getSelectionEnd();
		boolean finded=false;
		setCursor(0);
		while(replaceFindString(find,replace))
		{
			finded=true;
		}
		if(!finded)
			setSelection(start, end);
		return finded;
	}


	@Override
	public boolean onScale(ScaleGestureDetector detector) {
		float size=mTextPaint.getTextSize();
		float scale=detector.getScaleFactor();
		size*=scale;
		if(size<8)
			return true;
		setTextSize(size);
		scrollTo((int)(getScrollX()*scale),(int)( getScrollY()*scale) );
		postInvalidate();
		return true;
	}


	@Override
	public boolean onScaleBegin(ScaleGestureDetector detector) {
		return true;
	}


	@Override
	public void onScaleEnd(ScaleGestureDetector detector) {
	}
}
