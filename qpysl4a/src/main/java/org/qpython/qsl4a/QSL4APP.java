package org.qpython.qsl4a;

import android.app.Activity;
import android.app.Application;

import org.qpython.qsl4a.qsl4a.FutureActivityTaskExecutor;
import org.qpython.qsl4a.qsl4a.Log;
import org.qpython.qsl4a.qsl4a.interpreter.InterpreterConfiguration;
import org.qpython.qsl4a.qsl4a.interpreter.InterpreterConstants;
import org.qpython.qsl4a.qsl4a.trigger.TriggerRepository;

import java.util.Map;
import java.util.concurrent.CountDownLatch;


public class QSL4APP extends Application implements InterpreterConfiguration.ConfigurationObserver {

    Map<String, Integer> movieDirs = null;

    @Override
    public void onCreate() {
        super.onCreate();

        mConfiguration = new InterpreterConfiguration(this);
        mConfiguration.registerObserver(this);
        mConfiguration.startDiscovering(InterpreterConstants.MIME + QSL4AScript.getFileExtension(this));

        //注册crashHandler类
        int xq = 30;
    }  

	
    /*@Override
    public Class<?> getHomeActivityClass() {
    }*/
//
//    @Override
//    public Intent getMainApplicationIntent() {
//    	return null;
//        //return new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.app_url)));
//    }


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
