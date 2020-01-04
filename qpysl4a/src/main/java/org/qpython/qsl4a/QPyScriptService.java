package org.qpython.qsl4a;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import org.qpython.qsl4a.qsl4a.AndroidProxy;
import org.qpython.qsl4a.qsl4a.interpreter.InterpreterConfiguration;
import org.qpython.qsl4a.qsl4a.jsonrpc.RpcReceiverManager;
import org.qpython.qsl4a.qsl4a.util.SPFUtils;

import java.util.concurrent.CountDownLatch;

public class QPyScriptService extends Service {
    private static final String TAG = "QPyScriptService";
    public static String scriptName;

    //private final static int NOTIFICATION_ID = NotificationIdFactory.create();
    private final CountDownLatch mLatch = new CountDownLatch(1);
    private final IBinder mBinder;
    @SuppressWarnings("unused")
    private       boolean killMe;
    private InterpreterConfiguration mInterpreterConfiguration = null;
    private RpcReceiverManager mFacadeManager;
    private AndroidProxy       mProxy;
    @SuppressWarnings("unused")
    private int                mStartId;

    // ------------------------------------------------------------------------------------------------------

    public QPyScriptService() {
//		super(NOTIFICATION_ID);
        super();
        mBinder = new LocalBinder();
    }
    // ------------------------------------------------------------------------------------------------------

    public static String getSP(Context context, String key) {
        String val;
        SharedPreferences obj = context.getSharedPreferences("qsl4a_db", 0);
        val = obj.getString(key, "");
        return val;
    }

    // ------------------------------------------------------------------------------------------------------

    public static void setSP(Context context, String key, String val) {
        SharedPreferences obj = context.getSharedPreferences("qsl4a_db", 0);
        SharedPreferences.Editor wobj;
        wobj = obj.edit();
        wobj.putString(key, val);
        wobj.commit();
    }

    // ------------------------------------------------------------------------------------------------------

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");

        if (mProxy != null) {
            mProxy.shutdown();
        }

        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    // ------------------------------------------------------------------------------------------------------

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    // ------------------------------------------------------------------------------------------------------

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        super.onCreate();
        // clear before run
        /*File logFile = new File( Environment.getExternalStorageDirectory()+"/"+CONF.BASE_PATH+"/"+scriptName.substring(scriptName.lastIndexOf("/")+1)+".log" );
        if (logFile.exists()) {
        	logFile.delete();
        }
		killProcess();	*/
        // River Modify
        this.killMe = false;

        new startMyAsyncTask().execute(101);
    }

    // ------------------------------------------------------------------------------------------------------

    private void startMyMain(final int startId) {
        try {
            mProxy = new AndroidProxy(this, null, true);

            mProxy.startLocal();
            SPFUtils.setSP(getApplicationContext(), "sl4a.hostname", mProxy.getAddress().getHostName());
            SPFUtils.setSP(getApplicationContext(), "sl4a.port", "" + mProxy.getAddress().getPort());
            SPFUtils.setSP(getApplicationContext(), "sl4a.secue", mProxy.getSecret());

            Log.d(TAG, "startMyMain:" + mProxy.getAddress().getHostName() + ":" + mProxy.getAddress().getPort() + ":" + mProxy.getSecret());
            mLatch.countDown();
        } catch (Exception e) {

        }
    }

    RpcReceiverManager getRpcReceiverManager() throws InterruptedException {
        mLatch.await();

        if (mFacadeManager == null) { // Facade manage may not be available on startup.
            mFacadeManager = mProxy.getRpcReceiverManagerFactory()
                    .getRpcReceiverManagers().get(0);
        }
        return mFacadeManager;
    }

    public class LocalBinder extends Binder {
        public QPyScriptService getService() {
            return QPyScriptService.this;
        }
    }

    public class startMyAsyncTask extends AsyncTask<Integer, Integer, Boolean> {
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Boolean doInBackground(Integer... params) {
            Log.d(TAG, "doInBackground");
            startMyMain(params[0]);
            return true;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
        }

        @Override
        protected void onPostExecute(Boolean installStatus) {
            Log.d(TAG, "startMyAsyncTask:onPostExecute");
        }
    }
}