package org.qpython.qpylib;

//package org.qpython.qpylib;
//

import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Window;

import com.quseit.base.QBaseApp;

import org.qpython.qpy.console.ScriptExec;

import com.quseit.util.FileHelper;
import com.quseit.util.NAction;
import com.quseit.util.NUtil;

import org.qpython.qpy.main.activity.BaseActivity;
import org.qpython.qpysdk.QPyConstants;

import java.io.File;

public class MPyApi extends BaseActivity {
    public static final    int    SleepFinish = 1;
    protected static final String TAG         = "mpyapi";
    protected String param;
    protected String flag    = "";
    protected int    runMode = 0;
    Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {

            if (msg.what == SleepFinish) {
                finish();
            }
            super.handleMessage(msg);
        }
    };
    private boolean           live       = false;
    private String            logF       = null;
    private Handler           handler    = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.d(TAG, "process");
            process();
        }
    };
    @SuppressWarnings("unused")
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //if (!BASE_CONF.DEBUG)
            Log.d(TAG, "onServiceConnected");
            //binded = true;
            handler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    handler.sendEmptyMessage(0);
                }

            }, 202);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            //if (!BASE_CONF.DEBUG)
            Log.d(TAG, "onServiceDisconnected");
            //binded = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        IntentFilter filter = new IntentFilter(".MPyApi");
//        registerReceiver(mReceiver, filter);

        if (!NUtil.isRunning(getApplicationContext(), "org.qpython.qsl4a.QPyScriptService")) {
            Intent intent = new Intent();
            intent.setClassName(this, "org.qpython.sl4alib.QPyScriptService");
            startService(intent);
        }

        process();
    }

    private void process() {
        Intent intent = this.getIntent();
        String action = intent.getAction();
        String xtype = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && xtype != null) {
            if ("text/plain".equals(xtype)) {
                handleSendText(intent);
            } else if (xtype.startsWith("image/")) {
                handleSendImage(intent);
            }
        } else if (Intent.ACTION_VIEW.equals(action)) {
            handleSendUrl(intent);
        } else {
            Bundle bundle = intent.getExtras();
            String param0 = intent.getStringExtra(QPyConstants.EXTRA_CONTENT_URL0);
            if (param0 != null && param0.equals("shortcut")) {
                String type = intent.getStringExtra(QPyConstants.EXTRA_CONTENT_URL1);
                String path = intent.getStringExtra(QPyConstants.EXTRA_CONTENT_URL2);
                if (type.equals("script")) {
                    ScriptExec.getInstance().playScript(this,path, null, true);
                } else if (type.equals("project")) {
                    ScriptExec.getInstance().playProject(this,path, false);
                }

            } else if (bundle != null) {
                String act = bundle.getString("act");
                if (act != null) {
                    if (act.equals("onPyApi")) {//
                        String pycode = bundle.getString("pycode");
                        String pyfile = bundle.getString("pyfile");
                        param = bundle.getString("param");
                        if (param != null && param.equals("fileapi")) {
                            runMode = 2;
                            ScriptExec.getInstance().playScript(this,pyfile, null, false);
                        } else {
                            // Compatibility Mode
                            if (pycode.contains("#qpy:console\n") || NAction.isQPy3(getApplicationContext())) {
                                runMode = 3;
                            } else {
                                runMode = 1;
                            }
                            String script = QPyConstants.ABSOLUTE_PATH + "/cache/last.py";
                            FileHelper.putFileContents(this, script, pycode);
                            ScriptExec.getInstance().playScript(this,script, null, false);
                        }
                    }
                }
            }
        }
        new SleepThread().start();
    }

    void handleSendUrl(Intent intent) {
        Uri myURI = intent.getData();

        if (myURI != null) {
            String script = getApplicationContext().getFilesDir() + "/bin/share.py";
            String param = myURI.toString();
            ScriptExec.getInstance().playQScript(this,script, param, false);
        }
    }

    void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            //Toast.makeText(this,"sharedText:"+sharedText, Toast.LENGTH_SHORT).show();
            //onMenu();
            String script = getApplicationContext().getFilesDir() + "/bin/share.py";
            String param = sharedText;
            //new QPyTask().execute(script, param);
            ScriptExec.getInstance().playQScript(this,script, param, false);

            // Update UI to reflect text being shared
        }
    }

    void handleSendImage(Intent intent) {
        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            //onMenu();
            ScriptExec.getInstance().playQScript(this, getApplicationContext().getFilesDir() + "/bin/share.py", imageUri.toString(), false);

            // Update UI to reflect image being shared
        }
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        onAPIEnd();
        super.onDestroy();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        //onAPIEnd();
        onAPIEnd();
        super.onResume();

        Intent intent = this.getIntent();
        String param0 = intent.getStringExtra(QPyConstants.EXTRA_CONTENT_URL0);
        if (param0 != null && param0.equals("shortcut")) {

            finish();

        } else {
//    		viewLog();
            new SleepThread().start();


        }
        //flag = "end";
    }

    @SuppressWarnings("deprecation")
    protected void onAPIEnd() {

        if (!live) {
            Intent rIntent = new Intent();
            Bundle rBundle = new Bundle();

            if (flag.equals("qedit")) {

                if (param != null && !param.equals("")) {

                } else {
                    try {
                        String root = QBaseApp.getInstance().getOrCreateRoot(QPyConstants.DFROM_RUN);
                        File f = new File(root, ".last_tmp.py");
                        param = f.getAbsolutePath().toString();
                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                }

            }

            String result = FileHelper.getFileContents(logF);

            rBundle.putString("result", result);
            rBundle.putString("param", param);
            rBundle.putString("flag", flag);
            rBundle.putString("log", QPyConstants.ABSOLUTE_LOG);

            rIntent.putExtras(rBundle);

            //NotificationManager notificationManager = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
            //notificationManager.cancel(1000);
            //LogUtil.d(TAG, "onAPIEnd:"+logF);

            File logFile = new File(logF);

            if (logFile.exists()) {
                live = true;

                NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

                MPyApi.this.setResult(RESULT_OK, rIntent);
                MPyApi.this.finish();

            } else {
                if (runMode == 3) { // console 模式
                    MPyApi.this.setResult(RESULT_OK, rIntent);
                    MPyApi.this.finish();
                }

            }
        }
    }

    class SleepThread extends Thread implements Runnable {

        @Override
        public void run() {
            try {
                sleep(3000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Message msg = new Message();
            msg.what = SleepFinish;
            MPyApi.this.myHandler.sendMessage(msg);
        }
    }
}