package org.qpython.qpy.main.activity;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.qpython.qpy.R;
import org.qpython.qpy.console.ShellTermSession;
import org.qpython.qpy.console.TermActivity;
import org.qpython.qpy.console.util.TermSettings;
import org.qpython.qpy.main.utils.Utils;
import org.qpython.qpysdk.utils.FileHelper;
import org.qpython.qpysdk.utils.StreamGobbler;
import org.qpython.qsl4a.qsl4a.util.SPFUtils;
import org.renpy.android.PythonActivity;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class BaseActivity extends AppCompatActivity {
    private Map<Integer, PermissionAction> mActionMap = new ArrayMap<>();

    protected void toast(String content) {
        Toast.makeText(this, content, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionAction action = mActionMap.get(requestCode);
        if (action != null) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                action.onGrant();
            } else {
                action.onDeny();
            }
        }
        mActionMap.remove(action);
    }

    public final void checkPermissionDo(String permission, PermissionAction action) {
        if (Build.VERSION.SDK_INT >= 23) {
            int checkPermission = ContextCompat.checkSelfPermission(this, permission);
            if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                int code = permission.hashCode() & 0xffff;
                mActionMap.put(code, action);
                ActivityCompat.requestPermissions(this, new String[]{permission}, code);
            } else {
                action.onGrant();
            }
        } else {
            action.onGrant();
        }
    }

    public interface PermissionAction {
        void onGrant();

        void onDeny();
    }

    // QPython interfaces
    private static final int SCRIPT_EXEC_CODE = 1235;
    private static final int SCRIPT_API_CODE = 1236;
    private static final int SCRIPT_CONSOLE_CODE = 1237;

    private TermSettings mSettings;
    private SharedPreferences mPrefs;
    private ShellTermSession session;

    private static final int PID_INIT_VALUE = -1;
    private static final int DEFAULT_BUFFER_SIZE = 8192;
    ArrayList<String> mArguments = new ArrayList<String>();
    InputStream mIn;
    OutputStream mOut;
    FileDescriptor mFd;


    public void playScript(String script, String arg, boolean notify) {
        Log.d("BaseActivity", "playScript:"+script);
        File sf = new File(script);
        String content = FileHelper.getFileContents(script);

        boolean isProj = sf.getName().equals("main.py") || sf.getName().equals("main.pyo");
        boolean isWeb = content.contains("#qpy:webapp");
        boolean isQApp = content.contains("#qpy:qpyapp");
        boolean isDrawer = content.contains("#qpy:drawer");

        boolean isCons = ((!content.contains("#qpy:kivy") && !isQApp));

        if (isWeb) {
            boolean isNoHead = content.contains("#qpy:fullscreen");

            String title = "QWebApp";
            Pattern titlePattern = Pattern.compile("#qpy:webapp:(.+)", Pattern.CASE_INSENSITIVE);
            Matcher matcher0 = titlePattern.matcher(content);
            if (matcher0.find()) {
                title = matcher0.group(1);
            }

            String srv = "http://localhost";
            Pattern srvPattern = Pattern.compile("#qpy://(.+)[\\s]+", Pattern.CASE_INSENSITIVE);
            Matcher matcher1 = srvPattern.matcher(content);

            if (matcher1.find()) {
                srv = "http://" + matcher1.group(1);
            }

            //Log.d(TAG, "title:"+title+"-srv:"+srv);
            playWebApp(title, sf.getAbsolutePath(), srv, arg, isNoHead, isDrawer);

        } else if (isCons) {

            File log = new File(sf.getParentFile(), "last.log");
            if (log.exists()) {    // clear log
                log.delete();
            }

            String[] args = {script, sf.getParentFile().toString()};
            execPyInConsole(args);

        } else if (isQApp) {
            if (Build.VERSION.SDK_INT <= 10) {
                playWebViewSrv(script, arg, notify);
            } else {
                playQScript(script, arg, notify);
            }

        } else {

            if (isProj) {
                playProject(sf.getParentFile().getAbsolutePath(), notify);
            } else {
                File log = new File(sf.getParentFile(), ".run.log");

                if (log.exists()) {    // clear log
                    log.delete();
                }

                if (Utils.isOpenGL2supported(this)) {
                    Intent intent = new Intent(this, PythonActivity.class);
                    intent.putExtra(PythonActivity.EXTRA_CONTENT_URL1, "plusscript");
                    intent.putExtra(PythonActivity.EXTRA_CONTENT_URL2, script);
                    intent.putExtra(PythonActivity.EXTRA_CONTENT_URL5, notify ? "1" : "0");

                    startActivity(intent);
                } else {
                    Toast.makeText(this, "OpenGL2 is not supported", Toast.LENGTH_SHORT).show();
                    String[] args = {script, sf.getParentFile().toString()};
                    execPyInConsole(args);
                }
            }
        }
    }

    Handler hd = new Handler() {

        public void handleMessage(Message msg) {//覆盖handleMessage方法
            switch (msg.what) {
                case 1:
                    JSONArray ja  = (JSONArray) msg.obj;
                    String url = "http://www.qpython.org";
                    try {
                        String script = ja.getString(2);
                        url = ja.getString(1);
                        String title = ja.getString(0);
                        //Log.d("BaseActivity", "handler:title:"+title+"-url:"+url+"-script:"+script);

                        Utils.startWebActivityWithUrl(getApplicationContext(), title, url, script, false, false);
                    } catch (JSONException e) {

                        e.printStackTrace();
                    }

//                    new FinestWebView.Builder(getApplicationContext())
//                            .theme(R.style.WebViewTheme)
//                            .titleDefault("QPythonWebApp")
//                            .webViewBuiltInZoomControls(false)
//                            .webViewDisplayZoomControls(false)
//                            .dividerHeight(0)
//                            .gradientDivider(false)
//                            .swipeRefreshColorRes(R.color.colorAccent)
//                            .setCustomAnimations(R.anim.activity_open_enter, R.anim.activity_open_exit, R.anim.activity_close_enter, R.anim.activity_close_exit)
//                            .webViewJavaScriptEnabled(true)
//                            .show(url);

                    break;
            }
        }
    };

    private void startWebApp(final String title, final String srv, final String script) {
        //Utils.startWebActivityWithUrl(this, title, srv, script);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!Utils.isSrvOk(srv)) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Message m = new Message();
                m.what = 1;
                JSONArray ja = new JSONArray();
                try {
                    ja.put(0,title);
                    ja.put(1,srv);
                    ja.put(2,script);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                m.obj = ja;

                hd.sendMessage(m);

            }
        }).start();


    }

    public void playWebApp(final String title, String script, final String srv, String arg, boolean isNoHead, boolean isDrawer) {
        if (Build.VERSION.SDK_INT <= 10) {
            playWebViewSrv(script, arg, true);
        } else {
            playQScript(script, arg, false);
        }

        Utils.startWebActivityWithUrl(this, title, srv, script, isNoHead, isDrawer);

        /*hd.postDelayed(new Runnable() {
            @Override
            public void run() {
                startWebApp(title, srv, script);
            }
        }, 1000);
        String documentLink = "file:///android_asset/html/index.html";*/
        //Uri.Builder b = Uri.parse(documentLink).buildUpon();
        //Toast.makeText(this, "PlayWebApp", Toast.LENGTH_LONG).show();

        //Intent intent = NAction.openRemoteLink(getApplicationContext(), documentLink);
//        Intent intent = new Intent(getApplicationContext(), FinestWebViewActivity.class);
//        intent.putExtra(PythonActivity.EXTRA_CONTENT_URL1, "main");
//        intent.putExtra(PythonActivity.EXTRA_CONTENT_URL2, title);
//        intent.putExtra(PythonActivity.EXTRA_CONTENT_URL3, srv);
//        intent.putExtra(PythonActivity.EXTRA_CONTENT_URL4, script);
//        intent.putExtra(PythonActivity.EXTRA_CONTENT_URL5, isNoHead?"1":"0");
//        intent.putExtra(PythonActivity.EXTRA_CONTENT_URL6, isDrawer?"drawer":"");



//        intent.setData(b.build());
//        startActivity(intent);
    }

    protected void playWebViewSrv(String script, String arg, boolean flag) {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        mSettings = new TermSettings(getResources(), mPrefs);
        //String[] mArgs = this.getIntent().getStringArrayExtra("PYTHONARGS");
        //Log.d(TAG, "found srv:"+script);
        File logFile;
        if (flag) {
            logFile = new File(Environment.getExternalStorageDirectory(), "qpython/log/last.log");
        } else {
            File f = new File(script).getParentFile();
            logFile = new File(f.toString() + "/last.log");
        }
        if (!logFile.getAbsoluteFile().getParentFile().exists()) {
            logFile.getAbsoluteFile().getParentFile().mkdirs();
        }

        String[] mArgs = {script, " " + (arg != null ? arg : "") + " >" + logFile.getAbsoluteFile() + " 2>&1"};
        createWebTermSession(mArgs);

    }

    public void execPyInConsole(String[] args) {
        Intent intent = new Intent(this, TermActivity.class);
//            intent.setClassName(this.getPackageName(), "jackpal.androidterm.Term");
        intent.putExtra(PythonActivity.EXTRA_CONTENT_URL0, "main");
        intent.putExtra("PYTHONARGS", args);
        startActivityForResult(intent, SCRIPT_CONSOLE_CODE);

    }

    public void playQScript(final String script, String argv1, boolean notify) {
        String binaryPath = "";

        File f = new File(script);

        if (Build.VERSION.SDK_INT >= 20) {
            binaryPath = getApplicationContext().getFilesDir() + "/bin/python-android5";
            ;
        } else {
            binaryPath = getApplicationContext().getFilesDir() + "/bin/python";
            ;
        }


        int[] pid = new int[1];

        mArguments.add(script);
        if (argv1 != null) {
            mArguments.add(argv1);
        }
        String[] argumentsArray = mArguments.toArray(new String[mArguments.size()]);

        final File mLog;
        String bpath = getApplication().getFilesDir().getAbsolutePath();
        if (script.startsWith(bpath)) {
            mLog = new File(String.format("%s", Environment.getExternalStorageDirectory() + "/qpython/log/" + f.getName() + ".last.log"));

        } else {
            mLog = new File(String.format("%s", Environment.getExternalStorageDirectory() + "/qpython/log/last.log"));

        }
        File logDir = mLog.getParentFile();
        if (!logDir.exists()) {
            FileHelper.createDirIfNExists(logDir.getAbsolutePath());
        }
        //mLog = new File( Environment.getExternalStorageDirectory()+"/"+getName()+".log" );
        //Log.d("Process", "logFile:"+mLog.getAbsolutePath());

        mFd = com.googlecode.android_scripting.Exec.createSubprocess(binaryPath, argumentsArray, getEnvironmentArray(f.getParentFile() + ""), getWorkingDirectory(), pid);
        //Log.d("QPY", "binaryPath:"+binaryPath+"-argumentsArray:"+argumentsArray+"-getEnvironmentArray:"+getEnvironmentArray()+"-getWorkingDirectory:"+getWorkingDirectory()+"-pid:"+pid);
        final AtomicInteger mPid = new AtomicInteger(PID_INIT_VALUE);

        mPid.set(pid[0]);
        mOut = new FileOutputStream(mFd);
        mIn = new StreamGobbler(new FileInputStream(mFd), mLog, DEFAULT_BUFFER_SIZE);
        long mStartTime = System.currentTimeMillis();

        Message msg = new Message();
        msg.obj = mArguments.get(0);
        logHandler.sendMessage(msg);

        new Thread(new Runnable() {
            public void run() {
                int returnValue = com.googlecode.android_scripting.Exec.waitFor(mPid.get());
                //long mEndTime = System.currentTimeMillis();
                int pid = mPid.getAndSet(PID_INIT_VALUE);
                //Log.d("", "out:"+mFd.out.toString());

                Message msg = new Message();
                msg.what = returnValue;
                msg.obj = mArguments.get(0);

                Log.d("QPY", "Process " + pid + " exited with result code " + returnValue + ".");

                try {
                    mIn.close();
                } catch (IOException e) {
                    Log.e("QPY", e.getMessage());
                }

                try {
                    mOut.close();
                } catch (IOException e) {
                    Log.e("QPY", e.getMessage());
                }

                //context.updateNotify(msg);

            }
        }).start();
    }

    public void playProject(String project, boolean notify) {
        Log.d("BaseActivity", "playProject:"+project);
        String script = project + "/main.py";
        String script2 = project + "/main.pyo";
        File sf = new File(script);
        File sf2 = new File(script2);
        if (sf.exists()) {
            if (Utils.isOpenGL2supported(this)) {
                //Log.d(TAG, "project:"+script);
                String content = FileHelper.getFileContents(script);
                boolean isQApp = content.contains("#qpy:qpyapp");

                boolean isCons = !content.contains("#qpy:kivy") && !isQApp;
                boolean isWeb = content.contains("#qpy:webapp");

                if (isWeb) {
                    boolean isNoHead = content.contains("#qpy:fullscreen");
                    boolean isDrawer = content.contains("#qpy:drawer");

                    String title = "QWebApp";
                    Pattern titlePattern = Pattern.compile("#qpy:webapp:(.+)", Pattern.CASE_INSENSITIVE);
                    Matcher matcher0 = titlePattern.matcher(content);
                    if (matcher0.find()) {
                        title = matcher0.group(1);
                    }

                    String srv = "http://localhost";
                    Pattern srvPattern = Pattern.compile("#qpy://(.+)[\\s]+", Pattern.CASE_INSENSITIVE);
                    Matcher matcher1 = srvPattern.matcher(content);

                    if (matcher1.find()) {

                        srv = "http://" + matcher1.group(1);
                    }
                    //Log.d(TAG, "title:"+title+"-srv:"+srv);
                    playWebApp(title, sf.getAbsolutePath(), srv, null, isNoHead, isDrawer);

                } else if (isCons) {
                    playScript(script, null, notify);
                } else if (isQApp) {
                    // TODO
                    playQScript(script, null, notify);

                } else {

                    Intent intent = new Intent(this, PythonActivity.class);
                    intent.putExtra(PythonActivity.EXTRA_CONTENT_URL1, "execute");
                    intent.putExtra(PythonActivity.EXTRA_CONTENT_URL2, project);
                    intent.putExtra(PythonActivity.EXTRA_CONTENT_URL5, notify ? "1" : "0");

                    startActivity(intent);
                }
            } else {
                String content = FileHelper.getFileContents(script);
                boolean isWeb = content.contains("#qpy:webapp");
                if (isWeb) {
                    boolean isNoHead = content.contains("#qpy:fullscreen");
                    boolean isDrawer = content.contains("#qpy:drawer");
                    String title = "QWebApp";
                    Pattern titlePattern = Pattern.compile("#qpy:webapp:(.+)", Pattern.CASE_INSENSITIVE);
                    Matcher matcher0 = titlePattern.matcher(content);
                    if (matcher0.find()) {

                        title = matcher0.group(1);
                    }

                    String srv = "http://localhost";
                    Pattern srvPattern = Pattern.compile("#qpy://(.+)[\\s]+", Pattern.CASE_INSENSITIVE);
                    Matcher matcher1 = srvPattern.matcher(content);

                    if (matcher1.find()) {

                        srv = "http://" + matcher1.group(1);
                    }
                    //Log.d(TAG, "title:"+title+"-srv:"+srv);
                    playWebApp(title, sf.getAbsolutePath(), srv, null, isNoHead, isDrawer);
                } else {

                    Toast.makeText(this, "OpenGL2 is not supported", Toast.LENGTH_SHORT).show();
                    String[] args = {project + "/main.py", project};
                    execPyInConsole(args);
                }
            }

        } else if (sf2.exists()) {
            Intent intent = new Intent(this, PythonActivity.class);
            intent.putExtra(PythonActivity.EXTRA_CONTENT_URL1, "execute");
            intent.putExtra(PythonActivity.EXTRA_CONTENT_URL2, project);
            intent.putExtra(PythonActivity.EXTRA_CONTENT_URL5, notify ? "1" : "0");

            startActivity(intent);
        } else {
            Toast.makeText(this, "Project file doesn't exit", Toast.LENGTH_SHORT).show();
        }

    }

    private void createWebTermSession(String[] mArgs) {
        TermSettings settings = mSettings;
        //ShellTermSession session;
        String scmd = getApplicationContext().getFilesDir() + "/bin/qpython.sh";

        if (Build.VERSION.SDK_INT >= 20) {
            scmd = getApplicationContext().getFilesDir() + "/bin/qpython-android5.sh";
        }

        session = createTermSession(this, settings, scmd + " \"" + mArgs[0] + "\"  " + mArgs[1], "");
        mArgs = null;

        session.setFinishCallback(null);
        session.shellRun();
        //}
    }

    private String[] getEnvironmentArray(String pyPath) {

        String path = System.getenv("PATH");
        String[] env = new String[18];

        File filesDir = getFilesDir();

        List<String> environmentVariables = new ArrayList<String>();

        environmentVariables.add("PATH=" + filesDir + "/bin" + ":" + path);
        environmentVariables.add("LD_LIBRARY_PATH=" + ".:" + filesDir + "/lib/" + ":" + filesDir + "/:" + filesDir.getParentFile() + "/lib/");
        environmentVariables.add("PYTHONHOME=" + filesDir);
        environmentVariables.add("ANDROID_PRIVATE=" + filesDir);

        //environmentVariables.put("PYTHONHOME", this.getFilesDir().getAbsolutePath() + "/python");
        //environmentVariables.put("LD_LIBRARY_PATH", this.getFilesDir().getAbsolutePath() + "/python/lib" + ":" + this.getFilesDir().getAbsolutePath() + "/python/lib/python2.7/lib-dynload");

        File externalStorage = new File(Environment.getExternalStorageDirectory(), "qpython");

        if (!externalStorage.exists()) {
            externalStorage.mkdir();
        }
//        if (isQPy3) {
//            environmentVariables.add("PYTHONPATH="+externalStorage+"/lib/python3.2/site-packages/:"
//                    +filesDir+"/lib/python3.2/lib/:"
//                    +filesDir+"/lib/python3.2/site-packages/:"
//                    +filesDir+"/lib/python3.2/python32.zip:"
//                    +filesDir+"/lib/python3.2/lib-dynload/:"
//                    +pyPath);
//
//            environmentVariables.add("IS_QPY3=1");
//
//
//        } else {

        environmentVariables.add("PYTHONPATH=" + externalStorage + "/lib/python2.7/site-packages/:"
                + filesDir + "/lib/python2.7/site-packages/:"
                + filesDir + "/lib/python2.7/:"
                + filesDir + "/lib/python27.zip:"
                + filesDir + "/lib/python2.7/lib-dynload/:"
                + pyPath);


        //environmentVariables.add("PYTHONSTARTUP="+externalStorage+"/lib/python2.7/site-packages/qpythoninit.py");

        //}

        environmentVariables.add("PYTHONOPTIMIZE=2");

        environmentVariables.add("TMPDIR=" + externalStorage + "/cache");
        environmentVariables.add("TEMP=" + externalStorage + "/cache");

        File td = new File(externalStorage + "/cache");
        if (!td.exists()) {
            td.mkdir();
        }

        environmentVariables.add("AP_HOST="+ SPFUtils.getSP(this, "sl4a.hostname"));
        environmentVariables.add("AP_PORT="+SPFUtils.getSP(this, "sl4a.port"));
        environmentVariables.add("AP_HANDSHAKE="+SPFUtils.getSP(this, "sl4a.secue"));

        environmentVariables.add("ANDROID_PUBLIC=" + externalStorage);
        environmentVariables.add("ANDROID_PRIVATE=" + this.getFilesDir().getAbsolutePath());
        environmentVariables.add("ANDROID_ARGUMENT=" + pyPath);
        environmentVariables.add("QPY_USERNO="+SPFUtils.getUserNoId(this));
        environmentVariables.add("QPY_ARGUMENT="+SPFUtils.getExtConf(this));
        environmentVariables.add("PYTHONDONTWRITEBYTECODE=1");

        for (Map.Entry<String, String> entry : System.getenv().entrySet()) {
            environmentVariables.add(entry.getKey() + "=" + entry.getValue());
        }
        
        String[] environment = environmentVariables.toArray(new String[environmentVariables.size()]);
        return environment;
    }

    public String getWorkingDirectory() {
        return Environment.getExternalStorageDirectory() + "/qpython/";
    }

    public String getName() {
        return "QPython";
    }

    private Handler logHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            notifyLog(FileHelper.getFileName(mArguments.get(0)));
        }
    };

    public void notifyLog(String title) {
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

//        Intent updateIntent = new Intent(this, OLogAct.class);
//        updateIntent.putExtra(CONF.EXTRA_CONTENT_URL2, title);
//
//        PendingIntent updatePendingIntent = PendingIntent.getActivity(this, DateTimeHelper.getTimeAsInt(),updateIntent,0);
//        Notification notification = new Notification(R.drawable.ic_about, this.getString(R.string.m_title_log), 1000);
//        notification.contentIntent = updatePendingIntent;
//        notification.setLatestEventInfo(this, this.getString(R.string.app_name), this.getString(R.string.m_title_log), updatePendingIntent);
//        notification.flags = Notification.FLAG_AUTO_CANCEL;
//        notificationManager.notify(CONF.PY_NOTI_FLAG, notification);
    }

    protected static ShellTermSession createTermSession(Context context, TermSettings settings, String initialCommand, String path) {
        ShellTermSession session = null;
        try {
            session = new ShellTermSession(context,settings, initialCommand, path);
            session.setProcessExitMessage(context.getString(R.string.process_exit_message));

        } catch (IOException e) {
            e.printStackTrace();
        }

        return session;
    }


}
