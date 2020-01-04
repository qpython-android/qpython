package org.qpython.qpy.texteditor;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.quseit.common.CrashHandler;

//import greendroid.app.GDApplication;

public class MNApp extends Application {

	//Map<String, Integer> movieDirs = null;

	//GoogleAnalyticsTracker tracker;

	@Override  
    public void onCreate() {  
        super.onCreate();  
        CrashHandler crashHandler = CrashHandler.getInstance();
        //注册crashHandler类  
        crashHandler.init(getApplicationContext());
    	//tracker = GoogleAnalyticsTracker.getInstance();
//    	String x = NAction.getExtP(getApplicationContext(), "ga_gap");
//    	int xq = 30;
//    	if (!x.equals("")) {
//    		xq = Integer.valueOf(x);
//    	}
//    	String gtid = CONF.GOOGLE_TRACKER_ID;
//    	String gtid2 = NAction.getExtP(getApplicationContext(), "ga_gtid");
//    	if (!gtid2.equals("")) {
//    		gtid = gtid2;
//    	}
        //tracker.startNewSession(gtid, xq, getApplicationContext());
    }  

    public void logout(Context context) {
    }


}
