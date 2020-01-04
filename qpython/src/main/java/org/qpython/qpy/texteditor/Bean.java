package org.qpython.qpy.texteditor;

import android.content.Context;

public class Bean{
	protected String title;
    protected Context context;
    //private final String TAG = "BEAN";
 
    public Bean(Context context) {
    	this.context = context;
    }

    public String getTitle(){
        return this.title;
    }

    public void setTitle(String title){
        this.title = title;
    }
}
