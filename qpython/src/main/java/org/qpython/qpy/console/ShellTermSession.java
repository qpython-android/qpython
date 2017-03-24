package org.qpython.qpy.console;/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import org.qpython.qpy.console.compont.FileCompat;
import org.qpython.qpy.console.util.TermSettings;
import org.qpython.qsl4a.qsl4a.util.SPFUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import jackpal.androidterm.TermExec;

/**
 * A terminal session, controlling the process attached to the session (usually
 * a shell). It keeps track of process PID and destroys it's process group
 * upon stopping.
 */
public class ShellTermSession extends GenericTermSession {
    private int mProcId;
    private Thread mWatcherThread;

    private String mInitialCommand;

    private static final int PROCESS_EXITED = 1;
    private Handler mMsgHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (!isRunning()) {
                return;
            }
            if (msg.what == PROCESS_EXITED) {
                onProcessExit((Integer) msg.obj);
            }
        }
    };

    private Context context;
    private String pyPath;
    public ShellTermSession(Context context, TermSettings settings, String initialCommand,String pyPath) throws IOException {
        super(ParcelFileDescriptor.open(new File("/dev/ptmx"), ParcelFileDescriptor.MODE_READ_WRITE),
                settings, false);

        this.context = context;
        this.pyPath = pyPath;

        initializeSession();

        setTermOut(new ParcelFileDescriptor.AutoCloseOutputStream(mTermFd));
        setTermIn(new ParcelFileDescriptor.AutoCloseInputStream(mTermFd));

        mInitialCommand = initialCommand;

        mWatcherThread = new Thread() {
            @Override
            public void run() {
                Log.i(TermDebug.LOG_TAG, "waiting for: " + mProcId);
                int result = TermExec.waitFor(mProcId);
                Log.i(TermDebug.LOG_TAG, "Subprocess exited: " + result);
                mMsgHandler.sendMessage(mMsgHandler.obtainMessage(PROCESS_EXITED, result));
            }
        };
        mWatcherThread.setName("Process watcher");
    }

    private void initializeSession() throws IOException {
        /*TermSettings settings = mSettings;

        //int[] processId = new int[1];

        String path = System.getenv("PATH");
        if (settings.doPathExtensions()) {
            String appendPath = settings.getAppendPath();
            if (appendPath != null && appendPath.length() > 0) {
                path = path + ":" + appendPath;
            }

            if (settings.allowPathPrepend()) {
                String prependPath = settings.getPrependPath();
                if (prependPath != null && prependPath.length() > 0) {
                    path = prependPath + ":" + path;
                }
            }
        }
        if (settings.verifyPath()) {
            path = checkPath(path);
        }
        String[] env = new String[21];
        File filesDir = this.context.getFilesDir();

        env[0] = "TERM=" + settings.getTermType();
        env[1] = "PATH=" + this.context.getFilesDir()+"/bin"+":"+path;
        env[2] = "HOME=" + settings.getHomePath();

        env[3] = "LD_LIBRARY_PATH=.:"+filesDir+"/lib/"+":"+filesDir+"/:"+filesDir.getParentFile()+"/lib/";
        env[4] = "PYTHONHOME="+filesDir;
        env[5] = "ANDROID_PRIVATE="+filesDir;

        // HACKED FOR QPython
        File externalStorage = new File(Environment.getExternalStorageDirectory(), "qpython");

        if (!externalStorage.exists()) {
            externalStorage.mkdir();
        }

        env[6] = "PYTHONPATH="
                +filesDir+"/lib/python2.7/site-packages/:"
                +filesDir+"/lib/python2.7/:"
                +filesDir+"/lib/python27.zip:"
                +filesDir+"/lib/python2.7/lib-dynload/:"
                +externalStorage+"/lib/python2.7/site-packages/:"
                +pyPath;

            //env[14] = "IS_QPY2=1";
        env[7] = "PYTHONSTARTUP="+filesDir+"/lib/python2.7/site-packages/qpy.py";


        env[8] = "PYTHONOPTIMIZE=2";
        env[9] = "TMPDIR="+externalStorage+"/cache";
        File td = new File(externalStorage+"/cache");
        if (!td.exists()) {
            td.mkdir();
        }

        env[10] = "AP_HOST="+ SPFUtils.getSP(this.context, "sl4a.hostname");
        env[11] = "AP_PORT="+SPFUtils.getSP(this.context, "sl4a.port");
        env[12] = "AP_HANDSHAKE="+SPFUtils.getSP(this.context, "sl4a.secue");

        env[13] = "ANDROID_PUBLIC="+externalStorage;
        env[14] = "ANDROID_PRIVATE="+this.context.getFilesDir().getAbsolutePath();
        env[15] = "ANDROID_ARGUMENT="+pyPath;

        env[16] = "QPY_USERNO="+ SPFUtils.getUserNoId(context);
        env[17] = "QPY_ARGUMENT="+SPFUtils.getExtConf(context);
        env[18] = "PYTHONDONTWRITEBYTECODE=1";
        env[19] = "TMP="+externalStorage+"/cache";
        env[20] = "ANDROID_APP_PATH="+externalStorage+"";

        File enf = new File(context.getFilesDir()+"/bin/init.sh");
        //if (! enf.exists()) {
        String content = "#!/system/bin/sh";
        for (int i=0;i<env.length;i++) {
            content += "\nexport "+env[i];
        }
        FileHelper.putFileContents(context, enf.getAbsolutePath(), content.trim());
        try {
            FileUtils.chmod(enf, 0755);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("ShellTermSession", "initializeSession:"+settings.getShell());

        createSubprocess(settings.getShell(), env);*/
       /* mProcId = processId[0];

        setTermOut(new FileOutputStream(mTermFd));
        setTermIn(new FileInputStream(mTermFd));*/
        TermSettings settings = mSettings;

        String path = System.getenv("PATH");
        if (settings.doPathExtensions()) {
            String appendPath = settings.getAppendPath();
            if (appendPath != null && appendPath.length() > 0) {
                path = path + ":" + appendPath;
            }

            if (settings.allowPathPrepend()) {
                String prependPath = settings.getPrependPath();
                if (prependPath != null && prependPath.length() > 0) {
                    path = prependPath + ":" + path;
                }
            }
        }
        if (settings.verifyPath()) {
            path = checkPath(path);
        }
        File filesDir = this.context.getFilesDir();
        File externalStorage = new File(Environment.getExternalStorageDirectory(), "qpython");

        String[] env = new String[21];

        env[0] = "TERM=" + settings.getTermType();
        env[1] = "PATH=" + this.context.getFilesDir()+"/bin"+":"+path;
        env[2] = "HOME=" + settings.getHomePath();
        env[3] = "LD_LIBRARY_PATH=.:"+filesDir+"/lib/"+":"+filesDir+"/:"+filesDir.getParentFile()+"/lib/";
        env[4] = "PYTHONHOME="+filesDir;
        env[5] = "ANDROID_PRIVATE="+filesDir;
        env[6] = "PYTHONPATH="
                +filesDir+"/lib/python2.7/site-packages/:"
                +filesDir+"/lib/python2.7/:"
                +filesDir+"/lib/python27.zip:"
                +filesDir+"/lib/python2.7/lib-dynload/:"
                +externalStorage+"/lib/python2.7/site-packages/:"
                +pyPath;
        env[7] = "PYTHONSTARTUP="+filesDir+"/lib/python2.7/site-packages/qpy.py";
        env[8] = "PYTHONOPTIMIZE=2";
        env[9] = "TMPDIR="+externalStorage+"/cache";
        env[10] = "AP_HOST="+ SPFUtils.getSP(this.context, "sl4a.hostname");
        env[11] = "AP_PORT="+SPFUtils.getSP(this.context, "sl4a.port");
        env[12] = "AP_HANDSHAKE="+ SPFUtils.getSP(this.context, "sl4a.secue");
        env[13] = "ANDROID_PUBLIC="+externalStorage;
        env[14] = "ANDROID_PRIVATE="+this.context.getFilesDir().getAbsolutePath();
        env[15] = "ANDROID_ARGUMENT="+pyPath;
        env[16] = "QPY_USERNO="+ SPFUtils.getUserNoId(context);
        env[17] = "QPY_ARGUMENT="+SPFUtils.getExtConf(context);
        env[18] = "PYTHONDONTWRITEBYTECODE=1";
        env[19] = "TMP="+externalStorage+"/cache";
        env[20] = "ANDROID_APP_PATH="+externalStorage+"";


        mProcId = createSubprocess(settings.getShell(), env);
    }

    private String checkPath(String path) {
        String[] dirs = path.split(":");
        StringBuilder checkedPath = new StringBuilder(path.length());
        for (String dirname : dirs) {
            File dir = new File(dirname);
            if (dir.isDirectory() && FileCompat.canExecute(dir)) {
                checkedPath.append(dirname);
                checkedPath.append(":");
            }
        }
        return checkedPath.substring(0, checkedPath.length() - 1);
    }

    @Override
    public void initializeEmulator(int columns, int rows) {
        super.initializeEmulator(columns, rows);

        mWatcherThread.start();
        sendInitialCommand(mInitialCommand);
    }

    private void sendInitialCommand(String initialCommand) {
        if (initialCommand.length() > 0) {
            write(initialCommand + '\r');
        }
    }

    private int createSubprocess(String shell, String[] env) throws IOException {
        ArrayList<String> argList = parse(shell);
        String arg0;
        String[] args;

        try {
            arg0 = argList.get(0);
            File file = new File(arg0);
            if (!file.exists()) {
                Log.e(TermDebug.LOG_TAG, "Shell " + arg0 + " not found!");
                throw new FileNotFoundException(arg0);
            } else if (!FileCompat.canExecute(file)) {
                Log.e(TermDebug.LOG_TAG, "Shell " + arg0 + " not executable!");
                throw new FileNotFoundException(arg0);
            }
            args = argList.toArray(new String[1]);
        } catch (Exception e) {
            argList = parse(mSettings.getFailsafeShell());
            arg0 = argList.get(0);
            args = argList.toArray(new String[1]);
        }

        return TermExec.createSubprocess(mTermFd, arg0, args, env);
    }

    private ArrayList<String> parse(String cmd) {
        final int PLAIN = 0;
        final int WHITESPACE = 1;
        final int INQUOTE = 2;
        int state = WHITESPACE;
        ArrayList<String> result = new ArrayList<String>();
        int cmdLen = cmd.length();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < cmdLen; i++) {
            char c = cmd.charAt(i);
            if (state == PLAIN) {
                if (Character.isWhitespace(c)) {
                    result.add(builder.toString());
                    builder.delete(0, builder.length());
                    state = WHITESPACE;
                } else if (c == '"') {
                    state = INQUOTE;
                } else {
                    builder.append(c);
                }
            } else if (state == WHITESPACE) {
                if (Character.isWhitespace(c)) {
                    // do nothing
                } else if (c == '"') {
                    state = INQUOTE;
                } else {
                    state = PLAIN;
                    builder.append(c);
                }
            } else if (state == INQUOTE) {
                if (c == '\\') {
                    if (i + 1 < cmdLen) {
                        i += 1;
                        builder.append(cmd.charAt(i));
                    }
                } else if (c == '"') {
                    state = PLAIN;
                } else {
                    builder.append(c);
                }
            }
        }
        if (builder.length() > 0) {
            result.add(builder.toString());
        }
        return result;
    }

    private void onProcessExit(int result) {
        onProcessExit();
    }

    @Override
    public void finish() {
        hangupProcessGroup();
        super.finish();
    }

    /**
     * Send SIGHUP to a process group, SIGHUP notifies a terminal client, that the terminal have been disconnected,
     * and usually results in client's death, unless it's process is a daemon or have been somehow else detached
     * from the terminal (for example, by the "nohup" utility).
     */
    void hangupProcessGroup() {
        TermExec.sendSignal(-mProcId, 1);
    }

    //QPython
    public static void loadLibrary(File libPath) {

        System.load(libPath+"/libsdl.so");
        System.load(libPath+"/libsdl_image.so");
        System.load(libPath+"/libsdl_ttf.so");
        System.load(libPath+"/libsdl_mixer.so");
        try {
            System.load(libPath+"/libpython2.7.so");
            System.load(libPath+"/libapplication.so");
            System.load(libPath+"/libsdl_main.so");
            System.load(libPath+"/libsqlite3.so");
        } catch(UnsatisfiedLinkError e) {
        }
        //String libPath = new File(Environment.getExternalStorageDirectory(), mActivity.getPackageName()).toString()+"/"+CONF.DFROM_LIB;

        try {
            System.load(libPath + "/lib/python2.7/lib-dynload/_io.so");
            System.load(libPath + "/lib/python2.7/lib-dynload/unicodedata.so");
            System.load(libPath + "/lib/python2.7/lib-dynload/_sqlite3.so");
        } catch(UnsatisfiedLinkError e) {
            Log.d(TermDebug.LOG_TAG, ""+e.getMessage());
        }

        /*try {
            System.load(libPath + "/lib/python2.7/lib-dynload/_imaging.so");
            System.load(libPath + "/lib/python2.7/lib-dynload/_imagingft.so");
            System.load(libPath + "/lib/python2.7/lib-dynload/_imagingmath.so");
        } catch(UnsatisfiedLinkError e) {
        }*/
    }

}
