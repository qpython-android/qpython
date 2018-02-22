package org.renpy.android;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import org.qpython.qpysdk.utils.FileHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class PythonActivity extends Activity implements Runnable {
	protected static final String TAG = "QPythonActivity";

	// The audio thread for streaming audio...
    private static AudioThread mAudioThread = null;

    // The SDLSurfaceView we contain.
    public static SDLSurfaceView mView = null;
	public static PythonActivity mActivity = null;
	public static ApplicationInfo mInfo = null;
    // Did we launch our thread?
    private boolean mLaunchedThread = false;

    private ResourceManager resourceManager;
    private boolean debugLog = true;

    // The path to the directory contaning our external storage.
    private File externalStorage;

    // The path to the directory containing the game.
    private File mPath = null;
    private File mScript;

    boolean _isPaused = false;

    private static final String DB_INITIALIZED = "db_initialized";

    boolean isMain = false;

    public final static int PY_NOTI_FLAG = 123400;
    public static final String EXTRA_CONTENT_URL0 = "org.qpython.qpysdk.extra.CONTENT_URL0";
    public static final String EXTRA_CONTENT_URL1 = "org.qpython.qpysdk.extra.CONTENT_URL1";
    public static final String EXTRA_CONTENT_URL2 = "org.qpython.qpysdk.extra.CONTENT_URL2";
    public static final String EXTRA_CONTENT_URL3 = "org.qpython.qpysdk.extra.CONTENT_URL3";
    public static final String EXTRA_CONTENT_URL4 = "org.qpython.qpysdk.extra.CONTENT_URL4";
    public static final String EXTRA_CONTENT_URL5 = "org.qpython.qpysdk.extra.CONTENT_URL5";
    public static final String EXTRA_CONTENT_URL6 = "org.qpython.qpysdk.extra.CONTENT_URL6";
    public static final String EXTRA_CONTENT_URL7 = "org.qpython.qpysdk.extra.CONTENT_URL7";
    public static final String EXTRA_CONTENT_URL8 = "org.qpython.qpysdk.extra.CONTENT_URL8";
    public static final String EXTRA_CONTENT_URL9 = "org.qpython.qpysdk.extra.CONTENT_URL9";
    public final static String LIB_DIR = "lib";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Hardware.context = this;
        Action.context = this;
        mActivity = this;

        getWindowManager().getDefaultDisplay().getMetrics(Hardware.metrics);

        resourceManager = new ResourceManager(this);
        //externalStorage = new File(Environment.getExternalStorageDirectory(), getPackageName());
        String debugFlag = getIntent().getStringExtra(EXTRA_CONTENT_URL5);
        if (debugFlag!=null) {
        	if (debugFlag.equals("0")) {
        		debugLog = false;
        	}
        }
        String root = getIntent().getStringExtra(EXTRA_CONTENT_URL4);
        if (root != null) {
        	File n = new File(root);
        	if (n.exists()) {
        		externalStorage = n;
        	} else {
        		externalStorage = new File(Environment.getExternalStorageDirectory(), root);
        	}
        } else {
            externalStorage = new File(Environment.getExternalStorageDirectory(), "qpython");
        }
        Log.d(TAG, "externalStorage:"+externalStorage);
        mScript = null;
        // Figure out the directory where the game is. If the game was
        // given to us via an intent, then we use the scheme-specific
        // part of that intent to determine the file to launch. We
        // also use the android.txt file to determine the orientation.
        //
        // Otherwise, we use the public data, if we have it, or the
        // private data if we do not.
        String act = getIntent().getStringExtra(EXTRA_CONTENT_URL1);

        boolean fullscreen = false;
        if (act!=null && act.equals("execute")) {
            mPath = new File(getIntent().getStringExtra(EXTRA_CONTENT_URL2));

			File f = new File(mPath, "main.py");
			if (f.exists()) {
	        	if (FileHelper.getFileContents(f.toString()).contains("#qpy:fullscreen")) {
	        		fullscreen = true;
	        	}
			}


	        Project p = Project.scanDirectory(mPath);

	        if (p != null) {
	            if (p.landscape) {
	                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	            } else {
	                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	            }

				if ( p.fullscreen ) {
					fullscreen = true;
				}

	        }
        } else {
        	String fn = getIntent().getStringExtra(EXTRA_CONTENT_URL2);
        	if (fn == null) {
        		isMain = true;

        		fn = "/sdcard/qpython/launcher.py";
        	}
        	mScript = new File(fn);
        	if (!mScript.exists()) {
				String file1 = FileHelper.LoadDataFromAssets(this, "main.py");
				FileHelper.writeToFile(fn, file1);

        	}
        	mPath = mScript.getParentFile();

        	if (FileHelper.getFileContents(mScript.toString()).contains("#qpy:fullscreen")) {
        		fullscreen = true;
        	}
        	//Log.d(TAG, "mscript:"+mScript+"-mPath"+mPath);
        }

        //
        if (fullscreen) {
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        // Let old apps know they started.
        try {
            FileWriter f = new FileWriter(new File(mPath, ".launch"));
            f.write("started");
            f.close();
        } catch (IOException e) {
            // pass
        }


        updateNotification(mPath.toString());

        //Log.d("PythonActivity", "mPath:"+mPath.toString());
        // go to fullscreen mode
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        String full = getIntent().getStringExtra(EXTRA_CONTENT_URL3);

		File logFile = new File(mPath, ".run.log");
		if (logFile.exists()) {
			logFile.delete();
		}

		//this.bindService(new Intent(PythonActivity.this, PyScriptService.class), connection, BIND_AUTO_CREATE);

		String s = "";
		if (mScript!=null) {
			s = mScript.getName();
		} else {
			File f = new File(mPath, "main.py");
			if (f.exists()) {
				s = "main.py";
			} else {
				f = new File(mPath, "main.pyo");
				if (f.exists()) {
					s = "main.pyo";
				}
			}
		}

		/*try {
			this.mInfo = this.getPackageManager().getApplicationInfo(
					this.getPackageName(), PackageManager.GET_META_DATA);
			Log.v(TAG, "metadata fullscreen is" + this.mInfo.metaData.get("fullscreen"));
			if ( (Integer)this.mInfo.metaData.get("fullscreen") == 1 ) {
				getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
						WindowManager.LayoutParams.FLAG_FULLSCREEN);
			}
		} catch (PackageManager.NameNotFoundException e) {

		}*/
		//

//		if (isMain) {	//
//			Intent intent = new Intent();
//			intent.setClassName(PythonActivity.this, "com.hipipal.sl4alib.PyScriptService");
//			this.bindService(intent, connection, BIND_AUTO_CREATE);
//		}


		Log.d(TAG, "[RUN param:"+externalStorage.getAbsolutePath().toString()+"-"+mPath.getAbsolutePath().toString()+"-"+s+"]");
        mView = new SDLSurfaceView(
                this,
                mPath.getAbsolutePath().toString(),
                s,
                externalStorage.getAbsolutePath().toString()
                );

        IntentFilter filter = new IntentFilter(".PythonActivity");
        registerReceiver(mReceiver, filter);

		startPyScreen();
    }

	private ServiceConnection connection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			//if (!CONF.DEBUG)
			Log.d(TAG, "onServiceConnected");
			//binded = true;
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			//if (!CONF.DEBUG)
			Log.d(TAG, "onServiceDisconnected");
			//binded = false;
		}
	};



    public void startPyScreen() {
        // Start showing an SDLSurfaceView.
        Hardware.view = mView;
        setContentView(mView);

        // Force the background window color if asked
        /*if ( this.mInfo.metaData.containsKey("android.background_color") ) {
        	getWindow().getDecorView().setBackgroundColor(
        			this.mInfo.metaData.getInt("android.background_color"));
        }*/

    }

    /**
     * Show an error using a toast. (Only makes sense from non-UI
     * threads.)
     */
    public void toastError(final String msg) {

        final Activity thisActivity = this;
        runOnUiThread(new Runnable () {
                public void run() {
                    Toast.makeText(thisActivity, msg, Toast.LENGTH_LONG).show();
                }
            });

        // Wait to show the error.
        synchronized (this) {
            try {
                this.wait(1000);
            } catch (InterruptedException e) {
            }
        }
    }

    public void recursiveDelete(File f) {
        if (f.isDirectory()) {
            for (File r : f.listFiles()) {
                recursiveDelete(r);
            }
        }
        f.delete();
    }


    /**
     * This determines if unpacking one the zip files included in
     * the .apk is necessary. If it is, the zip file is unpacked.
     */
    public void unpackDataInPyAct(final String resource, File target) {

        // The version of data in memory and on disk.
        String data_version = resourceManager.getString(resource + "_version");
        String disk_version = "0";

        // If no version, no unpacking is necessary.
        if (data_version == null) {
            return;
        }

        // Check the current disk version, if any.
        String filesDir = target.getAbsolutePath();
        String disk_version_fn = filesDir + "/" + resource + ".version";

        try {
            byte buf[] = new byte[64];
            InputStream is = new FileInputStream(disk_version_fn);
            int len = is.read(buf);
            disk_version = new String(buf, 0, len);
            is.close();
        } catch (Exception e) {
            disk_version = "0";
        }

        // If the disk data is out of date, extract it and write the
        // version file.
        /*boolean extract = false;
        if (disk_version.equals("")) {
        	extract = true;
        } else {
        	Float data_v = Float.parseFloat(data_version);
        	Float disk_v = Float.parseFloat(disk_version);
        	if (data_v.intValue()>disk_v.intValue()) {
        		extract = true;
        	}
        }*/
        //Log.d(TAG, "data_version:"+Math.round(Double.parseDouble(data_version))+"-disk_version:"+Math.round(Double.parseDouble(disk_version))+"-RET:"+(int)(Double.parseDouble(data_version)-Double.parseDouble(disk_version)));
        if ((int)(Double.parseDouble(data_version)-Double.parseDouble(disk_version))>0 || disk_version.equals("0")) {
            Log.v(TAG, "Extracting " + resource + " assets.");

            //recursiveDelete(target);
            target.mkdirs();

            AssetExtract ae = new AssetExtract(this);
            if (!ae.extractTar(resource + ".mp3", target.getAbsolutePath())) {
                toastError("Could not extract " + resource + " data.");
            }

            try {
            	/*if (resource.equals("private")) {
            		Toast.makeText(getApplicationContext(), R.string.first_load, Toast.LENGTH_SHORT).show();
            	}*/
                // Write .nomedia.
                new File(target, ".nomedia").createNewFile();

                // Write version file.
                FileOutputStream os = new FileOutputStream(disk_version_fn);
                os.write(data_version.getBytes());
                os.close();
            } catch (Exception e) {
                Log.w("python", e);
                toastError("Could not extract " + resource + " data, make sure your device have enough space.");
            }

        } else {
        	Log.d(TAG, "NO MEDIA EXTRACTED");
        }
    }

    public static void loadLibrary(File libPath) {
        System.loadLibrary("sdl");
        System.loadLibrary("sdl_image");
        System.loadLibrary("sdl_ttf");
        System.loadLibrary("sdl_mixer");
        System.loadLibrary("python2.7");
        System.loadLibrary("application");
        System.loadLibrary("sdl_main");


        try {
            System.loadLibrary("pymodules");
        } catch(UnsatisfiedLinkError e) {
            Log.e("PythonActivity","Exception occured when loading pymodules ocre:"+e.getLocalizedMessage());
        }

        try {
            System.loadLibrary("sqlite3");
            System.load(libPath + "/lib/python2.7/lib-dynload/_sqlite3.so");
        } catch(UnsatisfiedLinkError e) {
            Log.e("PythonActivity","Exception occured when loading libsqlite3 ocre:"+e.getLocalizedMessage());
        }
        //String libPath = new File(Environment.getExternalStorageDirectory(), mActivity.getPackageName()).toString()+"/"+CONF.DFROM_LIB;

        try {
            System.load(libPath + "/lib/python2.7/lib-dynload/_io.so");
            System.load(libPath + "/lib/python2.7/lib-dynload/unicodedata.so");

        } catch(UnsatisfiedLinkError e) {
        	Log.d("PythonActivity", "Exception occured when loading python modules:"+e.getLocalizedMessage());
        }

        try {
            System.load(libPath + "/lib/python2.7/lib-dynload/_imaging.so");
            System.load(libPath + "/lib/python2.7/lib-dynload/_imagingft.so");
            System.load(libPath + "/lib/python2.7/lib-dynload/_imagingmath.so");
        } catch(UnsatisfiedLinkError e) {
        }
    }

    public void run() {
        unpackDataInPyAct("private", getFilesDir());
        unpackDataInPyAct("public", new File(externalStorage+"/"+LIB_DIR));

        if ( mAudioThread == null ) {
            Log.i("python", "starting audio thread");
            mAudioThread = new AudioThread(this);
        }

        //
        runOnUiThread(new Runnable () {
                public void run() {
                	if (mView !=null) {
                		mView.start();
                	}
                }
            });
    }

    @Override
    protected void onPause() {
        _isPaused = true;
        super.onPause();

        if (mView != null) {
            mView.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        _isPaused = false;

        if (!mLaunchedThread) {
            mLaunchedThread = true;
            new Thread(this).start();
        }

        if (mView != null) {
            mView.onResume();
        }
    }

    public boolean isPaused() {
        return _isPaused;
    }

    @Override
    public boolean onKeyDown(int keyCode, final KeyEvent event) {
        if (mView != null && mView.mStarted && SDLSurfaceView.nativeKey(keyCode, 1, event.getUnicodeChar())) {
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, final KeyEvent event) {
        //Log.i("python", "key up " + mView + " " + mView.mStarted);
        if (mView != null && mView.mStarted && SDLSurfaceView.nativeKey(keyCode, 0, event.getUnicodeChar())) {
            return true;
        } else {
            return super.onKeyUp(keyCode, event);
        }
    }

    @Override
    public boolean dispatchTouchEvent(final MotionEvent ev) {
        if (mView != null) {
            mView.onTouchEvent(ev);
            return true;
        } else {
            return super.dispatchTouchEvent(ev);
        }
    }

    @Override
	public void onDestroy() {
		//Log.d(TAG, "onDestroy"+mScript.getAbsolutePath());

		unregisterReceiver(mReceiver);
		if (isMain) {
			this.unbindService(connection);
		}
		//this.unbindService(connection);

		if (mView != null) {
			mView.onDestroy();
			mView = null;
		}
		//Log.i(TAG, "on destroy (exit1)");

		//this.finish();
		// 发送通知给对应的activity
		String code;
		if (mScript!=null) {
			code = FileHelper.getFileContents(mScript.getAbsolutePath());
		} else {
			code = "";
		}

		File noexit = new File(mPath, ".noexit");

		if (!noexit.exists() && !code.contains("#qpy:noend")) {
			Intent intent1 = new Intent(".MIndexAct");
			sendBroadcast(intent1);

			Intent intent2 = new Intent(".UProfileAct");
			sendBroadcast(intent2);

			updateNotification(mPath.toString());

		} else {
			updateNotification(mPath.toString());
		}

		try{
			super.onDestroy();
		} catch (Exception e) {

		}
	}

    public static void start_service(String serviceTitle, String serviceDescription,
            String pythonServiceArgument) {
        Intent serviceIntent = new Intent(PythonActivity.mActivity, PythonService.class);
        String argument = PythonActivity.mActivity.getFilesDir().getAbsolutePath();
        String filesDirectory = PythonActivity.mActivity.mPath.getAbsolutePath();
        serviceIntent.putExtra("androidPrivate", argument);
        serviceIntent.putExtra("androidArgument", filesDirectory);
        serviceIntent.putExtra("pythonHome", argument);
        serviceIntent.putExtra("pythonPath", argument + ":" + filesDirectory + "/lib");
        serviceIntent.putExtra("serviceTitle", serviceTitle);
        serviceIntent.putExtra("serviceDescription", serviceDescription);
        serviceIntent.putExtra("pythonServiceArgument", pythonServiceArgument);
        PythonActivity.mActivity.startService(serviceIntent);
    }
    public static void stop_service() {
        Intent serviceIntent = new Intent(PythonActivity.mActivity, PythonService.class);
        PythonActivity.mActivity.stopService(serviceIntent);
    }

    //----------------------------------------------------------------------------
    // Listener interface for onNewIntent
    //

    public interface NewIntentListener {
        void onNewIntent(Intent intent);
    }

    private List<NewIntentListener> newIntentListeners = null;

    public void registerNewIntentListener(NewIntentListener listener) {
        if ( this.newIntentListeners == null )
            this.newIntentListeners = Collections.synchronizedList(new ArrayList<NewIntentListener>());
        this.newIntentListeners.add(listener);
    }

    public void unregisterNewIntentListener(NewIntentListener listener) {
        if ( this.newIntentListeners == null )
            return;
        this.newIntentListeners.remove(listener);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if ( this.newIntentListeners == null )
            return;
        if ( this.mView != null )
            this.mView.onResume();
        synchronized ( this.newIntentListeners ) {
            Iterator<NewIntentListener> iterator = this.newIntentListeners.iterator();
            while ( iterator.hasNext() ) {
                (iterator.next()).onNewIntent(intent);
            }
        }
    }

    //----------------------------------------------------------------------------
    // Listener interface for onActivityResult
    //

    public interface ActivityResultListener {
        void onActivityResult(int requestCode, int resultCode, Intent data);
    }

    private List<ActivityResultListener> activityResultListeners = null;

    public void registerActivityResultListener(ActivityResultListener listener) {
        if ( this.activityResultListeners == null )
            this.activityResultListeners = Collections.synchronizedList(new ArrayList<ActivityResultListener>());
        this.activityResultListeners.add(listener);
    }

    public void unregisterActivityResultListener(ActivityResultListener listener) {
        if ( this.activityResultListeners == null )
            return;
        this.activityResultListeners.remove(listener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if ( this.activityResultListeners == null )
            return;
        if ( this.mView != null )
            this.mView.onResume();
        synchronized ( this.activityResultListeners ) {
            Iterator<ActivityResultListener> iterator = this.activityResultListeners.iterator();
            while ( iterator.hasNext() )
                (iterator.next()).onActivityResult(requestCode, resultCode, intent);
        }
    }


////////////////
    protected final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "mReceiver");
			startPyScreen();
		}
    };


	protected void updateNotification(String scriptFile) {
		String script = scriptFile.substring(scriptFile.lastIndexOf("/")+1);
		String logFile = scriptFile+"/.run.log";
		//Log.d(TAG, "updateNotification:(scriptFile)"+scriptFile+"(logFile)"+logFile);

	    NotificationManager notificationManager = (NotificationManager)this.getSystemService(this.NOTIFICATION_SERVICE);

	    if (mScript!=null) {
	    	script = mScript.getName();
	    }
//
//		Intent updateIntent = new Intent(this, OLogAct.class);
//		updateIntent.putExtra(CONF.EXTRA_CONTENT_URL1, logFile);
//		updateIntent.putExtra(CONF.EXTRA_CONTENT_URL2, script);
//
//		boolean notiFlag = true;
//
//        PendingIntent updatePendingIntent = PendingIntent.getActivity(this, DateTimeHelper.getTimeAsInt(), updateIntent,0);
//        Notification notification = new Notification(R.drawable.ic_about, "QPython log", 1000);
//
//        // This contentIntent is a noop.
//        //PendingIntent contentIntent = PendingIntent.getService(this, 0, new Intent(), 0);
//        notification.contentIntent = updatePendingIntent;
//        notification.setLatestEventInfo(this, "QPython", "Log : "+script, updatePendingIntent);
//        notification.flags = Notification.FLAG_AUTO_CANCEL;
//
//        notificationManager.notify(PY_NOTI_FLAG, notification);
	}
}

