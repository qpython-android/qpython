package org.qpython.qpysdk;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.Message;
import android.util.Log;

import org.qpython.qpysdk.utils.AssetExtract;
import org.qpython.qpysdk.utils.FileUtils;
import org.qpython.qpysdk.utils.ResourceManager;
import org.qpython.qpysdk.utils.StreamGobbler;

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

/**
 * Created by yhc on 16/2/22.
 */
public class QPySDK {

    private final String TAG = "QPySDK";
    private ResourceManager resourceManager;

    Context context;
    Activity activity;

    ArrayList<String> mArguments = new ArrayList<String>();
    private static final int PID_INIT_VALUE = -1;
    private static final int DEFAULT_BUFFER_SIZE = 8192;
    InputStream mIn;
    OutputStream mOut;
    FileDescriptor mFd;

    public QPySDK(Context con, Activity act) {
        resourceManager = new ResourceManager(act);
        activity = act;
        context = con;
    }

    void _recursiveDelete(File f) {
        if (f.isDirectory()) {
            if (f.listFiles()!=null) {
                for (File r : f.listFiles()) {
                    _recursiveDelete(r);
                }
            }
        }
        f.delete();
    }




    public void runPyService() {
        Log.d(TAG, "runPyService");
        String launchScript = this.context.getFilesDir() + "/main.py";
        playQScript(launchScript);
    }

    public void playQScript(final String script) {
        Log.d(TAG, "playQScript:" + script);
        String binaryPath = "";

        File f = new File(script);

        if (Build.VERSION.SDK_INT >= 20) {
            binaryPath = this.context.getFilesDir() + "/bin/python-android5";
            ;
        } else {
            binaryPath = this.context.getFilesDir() + "/bin/python";
            ;
        }


        int[] pid = new int[1];

        mArguments.add(script);
        String[] argumentsArray = mArguments.toArray(new String[mArguments.size()]);

        final File mLog = new File(String.format("%s", Environment.getExternalStorageDirectory() + "/.qpysdk.run.log"));
        File logDir = mLog.getParentFile();

        mFd = com.googlecode.android_scripting.Exec.createSubprocess(binaryPath, argumentsArray, getEnvironmentArray(f.getParentFile() + ""), Environment.getExternalStorageDirectory() + "/", pid);
        //Log.d("QPY", "binaryPath:"+binaryPath+"-argumentsArray:"+argumentsArray+"-getEnvironmentArray:"+getEnvironmentArray()+"-getWorkingDirectory:"+getWorkingDirectory()+"-pid:"+pid);
        final AtomicInteger mPid = new AtomicInteger(PID_INIT_VALUE);

        mPid.set(pid[0]);
        mOut = new FileOutputStream(mFd);
        mIn = new StreamGobbler(new FileInputStream(mFd), mLog, DEFAULT_BUFFER_SIZE);
        long mStartTime = System.currentTimeMillis();


        new Thread(new Runnable() {
            public void run() {
                int returnValue = com.googlecode.android_scripting.Exec.waitFor(mPid.get());
                //long mEndTime = System.currentTimeMillis();
                int pid = mPid.getAndSet(PID_INIT_VALUE);
                Log.d("", "out:" + mFd.out.toString());

                Message msg = new Message();
                msg.what = returnValue;
                msg.obj = mArguments.get(0);

                Log.d(TAG, "Process " + pid + " exited with result code " + returnValue + ".");

                try {
                    mIn.close();
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                }

                try {
                    mOut.close();
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                }

                //context.updateNotify(msg);

            }
        }).start();
    }

    private String[] getEnvironmentArray(String pyPath) {
        String path = System.getenv("PATH");

        File filesDir = this.context.getFilesDir();

        List<String> environmentVariables = new ArrayList<String>();

        environmentVariables.add("PATH=" + filesDir + "/bin" + ":" + path);
        environmentVariables.add("LD_LIBRARY_PATH=" + ".:" + filesDir + "/lib/" + ":" + filesDir + "/:" + filesDir.getParentFile() + "/lib/");
        environmentVariables.add("PYTHONHOME=" + filesDir);
        environmentVariables.add("ANDROID_PRIVATE=" + filesDir);

        File externalStorage = new File(Environment.getExternalStorageDirectory(), "org.qpython.qpy");

        environmentVariables.add("PYTHONPATH=" + externalStorage + "/lib/python2.7/site-packages/:"
                + filesDir + "/lib/python2.7/site-packages/:"
                + filesDir + "/lib/python2.7/:"
                + filesDir + "/lib/python27.zip:"
                + filesDir + "/lib/python2.7/lib-dynload/:"
                + pyPath);


        //environmentVariables.add("PYTHONSTARTUP=" + externalStorage + "/lib/python2.7/site-packages/qpythoninit.py");
        environmentVariables.add("PYTHONOPTIMIZE=2");

        environmentVariables.add("TMPDIR=" + externalStorage + "/cache");
        environmentVariables.add("TEMP=" + externalStorage + "/cache");

        File td = new File(externalStorage + "/cache");
        if (!td.exists()) {
            td.mkdirs();
        }


        environmentVariables.add("ANDROID_PUBLIC=" + externalStorage);
        environmentVariables.add("ANDROID_PRIVATE=" + this.context.getFilesDir().getAbsolutePath());
        environmentVariables.add("ANDROID_ARGUMENT=" + pyPath);
        environmentVariables.add("PYTHONDONTWRITEBYTECODE=1");

        for (Map.Entry<String, String> entry : System.getenv().entrySet()) {
            environmentVariables.add(entry.getKey() + "=" + entry.getValue());
        }
        String[] environment = environmentVariables.toArray(new String[environmentVariables.size()]);
        return environment;
    }


    public void extractRes(final String resource, File target) {
        Log.d(TAG, "extractRes:" + target);
        // The version of data in memory and on disk.
        String data_version = resourceManager.getString(resource + "_version");
        String disk_version = null;

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
            disk_version = "";
        }

        // If the disk data is out of date, extract it and write the
        // version file.
        if (!data_version.equals(disk_version)) {
            Log.v("python", "Extracting " + resource + " assets.");

            _recursiveDelete(target);
            target.mkdirs();

            AssetExtract ae = new AssetExtract(activity);
            if (!ae.extractTar(resource + ".mp3", target.getAbsolutePath())) {
                //toastError("Could not extract " + resource + " data.");
            } else {

                try {
                    new File(target, ".nomedia").createNewFile();

                    // Write version file.
                    FileOutputStream os = new FileOutputStream(disk_version_fn);
                    os.write(data_version.getBytes());
                    os.close();
                } catch (Exception e) {
                    Log.w("unpackData", e);
                }
            }
        }

        if (resource.startsWith("private")) {
            File bind = new File(this.context.getFilesDir() + "/bin");
            if (bind.listFiles() != null) {
                for (File bin : bind.listFiles()) {
                    try {
                        //Log.d(TAG, "chmod:"+bin.getAbsolutePath());

                        FileUtils.chmod(bin, 0755);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }

    }
}
