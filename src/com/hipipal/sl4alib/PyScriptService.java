package com.hipipal.sl4alib;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.android.python27.config.GlobalConstants;
import com.android.python27.process.MyScriptProcess;
import com.googlecode.android_scripting.AndroidProxy;
import com.googlecode.android_scripting.jsonrpc.RpcReceiverManager;
import com.googlecode.android_scripting.interpreter.InterpreterConfiguration;
import com.zuowuxuxi.util.NStorage;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

//public class PyScriptService extends ForegroundService {

public class PyScriptService extends Service {
	private static final String TAG = "PyScriptService";

	//private final static int NOTIFICATION_ID = NotificationIdFactory.create();
	private final CountDownLatch mLatch = new CountDownLatch(1);
	private final IBinder mBinder;
	@SuppressWarnings("unused")
	private MyScriptProcess myScriptProcess;
	
	@SuppressWarnings("unused")
	private static PyScriptService instance;
	@SuppressWarnings("unused")
	private boolean killMe;
	public static String scriptName;
	  
	private InterpreterConfiguration mInterpreterConfiguration = null;
	private RpcReceiverManager mFacadeManager;
    private AndroidProxy mProxy;
    
    private static Context context = null;
    @SuppressWarnings("unused")
	private int mStartId;
    static {
      instance = null;
    }
    
    // ------------------------------------------------------------------------------------------------------

	public class LocalBinder extends Binder {
		public PyScriptService getService() {
			return PyScriptService.this;
		}
	}

    // ------------------------------------------------------------------------------------------------------

	@Override
	public void onDestroy() {
		Log.d(TAG, "onDestroy");

		if (mProxy!=null) {
			mProxy.shutdown();
		}
		
		super.onDestroy();
	}

    // ------------------------------------------------------------------------------------------------------

	public PyScriptService() {
//		super(NOTIFICATION_ID);
		super();

		mBinder = new LocalBinder();
	}

    // ------------------------------------------------------------------------------------------------------

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

    // ------------------------------------------------------------------------------------------------------

    public static Context getAppContext() {
        return PyScriptService.context;
    }
    
    // ------------------------------------------------------------------------------------------------------

	@Override
	public void onCreate() {
		Log.d(TAG, "onCreate");
		super.onCreate();
		PyScriptService.context = getApplicationContext();
		
		// clear before run
        /*File logFile = new File( Environment.getExternalStorageDirectory()+"/"+CONF.BASE_PATH+"/"+scriptName.substring(scriptName.lastIndexOf("/")+1)+".log" );
        if (logFile.exists()) {
        	logFile.delete();
        }
		killProcess();	*/	
		// River Modify
		instance = this;
	    this.killMe = false;

		new startMyAsyncTask().execute(101);
	}

    // ------------------------------------------------------------------------------------------------------

	@SuppressWarnings("deprecation")
	@Override
	public void onStart(Intent intent, final int startId) {
		super.onStart(intent, startId);
	}

    // ------------------------------------------------------------------------------------------------------

	public class startMyAsyncTask extends AsyncTask<Integer, Integer, Boolean> {
		@Override
		protected void onPreExecute() {
		}
	
		@Override
		protected Boolean doInBackground(Integer... params) {	    
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

	// ------------------------------------------------------------------------------------------------------

	private void startMyMain(final int startId) {
		mStartId = startId;
		if (scriptName == null) {
			scriptName = this.getFilesDir().getAbsolutePath() + "/" + GlobalConstants.PYTHON_MAIN_SCRIPT_NAME;
		}
		
		File script = new File(scriptName);
		
		// arguments
		ArrayList<String> args = new ArrayList<String>();
		args.add(scriptName);
		args.add("--foreground");

		// env var
		Map<String, String> environmentVariables = null;
		environmentVariables = new HashMap<String, String>();
	    //environmentVariables.put("PYTHONPATH", this.getFilesDir().getAbsolutePath() + "/python/lib/python2.7/lib-dynload" + ":" + this.getFilesDir().getAbsolutePath() + "/python/lib/python2.7"+ ":" + this.getFilesDir().getAbsolutePath() + "/python/lib/python27.zip"+ ":" +Environment.getExternalStorageDirectory().getAbsolutePath()+ "/"  + this.getPackageName() + "/extras/site-packages" );
	    //environmentVariables.put("TEMP", Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + this.getPackageName() + "/extras/tmp");
	    //environmentVariables.put("PYTHONHOME", this.getFilesDir().getAbsolutePath() + "/python");
	    //environmentVariables.put("LD_LIBRARY_PATH", this.getFilesDir().getAbsolutePath() + "/python/lib" + ":" + this.getFilesDir().getAbsolutePath() + "/python/lib/python2.7/lib-dynload");			
		
		//File pythonBinary = new File(this.getFilesDir().getAbsolutePath() + "/python/bin/python");
		//File pythonBinary = new File("/system/bin/id");
		// launch script
		try {
			mProxy = new AndroidProxy(this, null, true);

			mProxy.startLocal();
			NStorage.setSP(getAppContext(), "sl4a.hostname", mProxy.getAddress().getHostName());
			NStorage.setSP(getAppContext(), "sl4a.port", ""+mProxy.getAddress().getPort());
			NStorage.setSP(getAppContext(), "sl4a.secue", mProxy.getSecret());
			
			Log.d(TAG, "startMyMain:"+mProxy.getAddress().getHostName()+":"+mProxy.getAddress().getPort()+":"+mProxy.getSecret());
			mLatch.countDown();
			
			//Intent intent1 = new Intent(".PythonActivity");
			//sendBroadcast(intent1);
		      
			myScriptProcess = MyScriptProcess.launchScript(this, script, mInterpreterConfiguration, mProxy, new Runnable() {
				@Override
				public void run() {
					Log.d(TAG, "myScriptProcess");
					mProxy.shutdown();
					stopSelf(startId);						
					// hard force restart
	//				if (!ScriptService.this.killMe) {
	//					startMyMain();				        	
	//				}
				}
			}, script.getParent(),  Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + this.getPackageName(), args, environmentVariables, null);
		} catch (Exception e) {
			
		}
	}
	
    // ------------------------------------------------------------------------------------------------------

	RpcReceiverManager getRpcReceiverManager() throws InterruptedException {
		mLatch.await();
		
		if (mFacadeManager==null) { // Facade manage may not be available on startup.
			mFacadeManager = mProxy.getRpcReceiverManagerFactory()
				.getRpcReceiverManagers().get(0);
		}
		return mFacadeManager;
	}
	
	public void updateNotify(Message msg) {
		updatePositionHandler.sendMessage(msg);
	}
	
	@SuppressLint("HandlerLeak")
	private Handler updatePositionHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			@SuppressWarnings("unused")
			String file = (String)msg.obj;
			//updateNotification(msg.what, file);
		}
	};	
}