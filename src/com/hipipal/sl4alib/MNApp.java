package com.hipipal.sl4alib;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;

import com.googlecode.android_scripting.FutureActivityTaskExecutor;

import com.googlecode.android_scripting.Log;
import com.googlecode.android_scripting.interpreter.InterpreterConfiguration;
import com.googlecode.android_scripting.interpreter.InterpreterConstants;
import com.googlecode.android_scripting.interpreter.InterpreterConfiguration.ConfigurationObserver;
import com.googlecode.android_scripting.trigger.TriggerRepository;
import com.zuowuxuxi.common.CrashHandler;
import com.zuowuxuxi.util.NAction;
import com.zuowuxuxi.util.NUtil;

import android.content.Context;
import android.content.Intent;
import greendroid.app.GDApplication;


public class MNApp extends GDApplication implements ConfigurationObserver {

	Map<String, Integer> movieDirs = null;
	GoogleAnalyticsTracker tracker;
	@Override  
    public void onCreate() {  
        super.onCreate();  
        
        mConfiguration = new InterpreterConfiguration(this);
        mConfiguration.registerObserver(this);
        mConfiguration.startDiscovering(InterpreterConstants.MIME + SL4AScript.getFileExtension(this));

        CrashHandler crashHandler = CrashHandler.getInstance();  
        //注册crashHandler类  
        crashHandler.init(getApplicationContext()); 
    	tracker = GoogleAnalyticsTracker.getInstance();
    	String x = NAction.getExtP(getApplicationContext(), "ga_gap");
    	int xq = 30;
    	if (!x.equals("")) {
    		xq = Integer.valueOf(x);
    	}
    	String gtid = CONF.GOOGLE_TRACKER_ID;
    	String gtid2 = NAction.getExtP(getApplicationContext(), "ga_gtid");
    	if (!gtid2.equals("")) {
    		gtid = gtid2;
    	}
        tracker.startNewSession(gtid, xq, getApplicationContext());
    }  
	
    public void trackPageView(String page) {
    	if (tracker!=null) {
    		tracker.trackPageView("/"+NUtil.getVersinoCode(getApplicationContext())+"-"+page);
    	}
    }
    
    public void stopTraker() {
    	if (tracker!=null) {
    		tracker.stopSession();
    	}
    }
	
    /*@Override
    public Class<?> getHomeActivityClass() {
    }*/
    
    @Override
    public Intent getMainApplicationIntent() {
    	return null;

        //return new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.app_url)));
    }
    
    public void logout(Context context) {
    }



    private volatile boolean receivedConfigUpdate = false;
    private final CountDownLatch mLatch = new CountDownLatch(1);

    private final FutureActivityTaskExecutor mTaskExecutor = new FutureActivityTaskExecutor(this);
    private TriggerRepository mTriggerRepository;

    protected InterpreterConfiguration mConfiguration;

    public FutureActivityTaskExecutor getTaskExecutor() {
      return mTaskExecutor;
    }


    public InterpreterConfiguration getInterpreterConfiguration() {
      return mConfiguration;
    }

    public TriggerRepository getTriggerRepository() {
      return mTriggerRepository;
    }
    

    @Override
    public void onConfigurationChanged() {
        receivedConfigUpdate = true;
        mLatch.countDown();
    }

    public boolean readyToStart() {
      try {
        mLatch.await();
      } catch (InterruptedException e) {
        Log.e(e);
      }
      return receivedConfigUpdate;
    }


}
