package org.qpython.qsl4a;

import android.app.Application;

import com.quseit.base.MyApp;

import org.qpython.qsl4a.qsl4a.FutureActivityTaskExecutor;
import org.qpython.qsl4a.qsl4a.LogUtil;
import org.qpython.qsl4a.qsl4a.interpreter.InterpreterConfiguration;
import org.qpython.qsl4a.qsl4a.interpreter.InterpreterConstants;
import org.qpython.qsl4a.qsl4a.trigger.TriggerRepository;

import java.util.Map;
import java.util.concurrent.CountDownLatch;


public class QSL4APP extends MyApp implements InterpreterConfiguration.ConfigurationObserver {

    private final CountDownLatch mLatch = new CountDownLatch(1);
    private final FutureActivityTaskExecutor mTaskExecutor = new FutureActivityTaskExecutor(this);

	
    /*@Override
    public Class<?> getHomeActivityClass() {
    }*/
//
//    @Override
//    public Intent getMainApplicationIntent() {
//    	return null;
//        //return new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.app_url)));
//    }
    protected InterpreterConfiguration mConfiguration;
    Map<String, Integer> movieDirs = null;
    private volatile boolean receivedConfigUpdate = false;
    private TriggerRepository mTriggerRepository;

    protected QSL4APP() {
        super();
    }


    @Override
    public void onCreate() {
        super.onCreate();

        mConfiguration = new InterpreterConfiguration(this);
        mConfiguration.registerObserver(this);
        mConfiguration.startDiscovering(InterpreterConstants.MIME + QSL4AScript.getFileExtension(this));

        //注册crashHandler类
        int xq = 30;
    }

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
            LogUtil.e(e);
        }
        return receivedConfigUpdate;
    }


}
