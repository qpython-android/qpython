package org.renpy.android;

import org.libsdl.app.SDLActivity;
import org.qpython.qpysdk.QPyConstants;
import org.qpython.qpysdk.utils.FileHelper;
import org.qpython.qpysdk.utils.Utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnSystemUiVisibilityChangeListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.quseit.util.NAction;
import com.quseit.util.NUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

public class PythonSDLActivity extends SDLActivity {

    public final static int    PY_NOTI_FLAG = 123400;
    public static final String NOTIFY       = "notify";
    public static final String ACT          = "act";
    public static final String PATH         = "path";
    public static final String DEBUG_FLAG   = "debug_flag";
    public static final String ROOT         = "root";
    public static final String FULL         = "full";

    private final String TAG = "PythonSDLActivity";
    /**
     * This exists so python code can access this activity.
     */
    public static PythonSDLActivity mActivity = null;

    /**
     * The layout that contains the SDL view. VideoPlayer uses this to add
     * its own view on on top of the SDL view.
     */
    public FrameLayout mFrameLayout;


    /**
     * A layout that contains mLayout. This is a 3x3 grid, with the layout
     * in the center. The idea is that if someone wants to show an ad, they
     * can stick it in one of the other cells..
     */
    public LinearLayout mVbox;

    ResourceManager resourceManager;


    protected String[] getLibraries() {
        return new String[] {
            "png16",
            "SDL2",
            "SDL2_image",
            "SDL2_ttf",
            "SDL2_gfx",
            "SDL2_mixer",
            "python2.7",
            "main",
        };
    }


    // GUI code. /////////////////////////////////////////////////////////////


    public void addView(View view, int index) {
        mVbox.addView(view, index, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, (float) 0.0));
    }

    public void removeView(View view) {
        mVbox.removeView(view);
    }

    @Override
    public void setContentView(View view) {
        mFrameLayout = new FrameLayout(this);
        mFrameLayout.addView(view);

        mVbox = new LinearLayout(this);
        mVbox.setOrientation(LinearLayout.VERTICAL);
        mVbox.addView(mFrameLayout, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, (float) 1.0));

        super.setContentView(mVbox);
    }


    private void setupMainWindowDisplayMode() {
        View decorView = setSystemUiVisibilityMode();
        decorView.setOnSystemUiVisibilityChangeListener(new OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                setSystemUiVisibilityMode(); // Needed to avoid exiting immersive_sticky when keyboard is displayed
            }
        });
    }

    private View setSystemUiVisibilityMode() {
        View decorView = getWindow().getDecorView();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            int options;
            options =
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                    | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

            decorView.setSystemUiVisibility(options);

        }

        return decorView;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus) {
            setupMainWindowDisplayMode();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setupMainWindowDisplayMode();
    }

    // Code to unpack python and get things running ///////////////////////////

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
    public void unpackData(final String resource, File target) {

        /**
         * Delete main.pyo unconditionally. This fixes a problem where we have
         * a main.py newer than main.pyo, but start.c won't run it.
         */
        new File(target, "main.py").delete();

        boolean shouldUnpack = false;

        // The version of data in memory and on disk.
        String data_version = resourceManager.getString(resource + "_version");
        String disk_version = null;

        String filesDir = target.getAbsolutePath();
        String disk_version_fn = filesDir + "/" + resource + ".version";

        // If no version, no unpacking is necessary.
        if (data_version != null) {

            try {
                byte buf[] = new byte[64];
                InputStream is = new FileInputStream(disk_version_fn);
                int len = is.read(buf);
                disk_version = new String(buf, 0, len);
                is.close();
            } catch (Exception e) {
                disk_version = "";
            }

            if (! data_version.equals(disk_version)) {
                shouldUnpack = true;
            }
        }


        // If the disk data is out of date, extract it and write the
        // version file.
        if (shouldUnpack) {
            Log.v("python", "Extracting " + resource + " assets.");

            // Delete old libraries & renpy files.
            recursiveDelete(new File(target, "lib"));
            recursiveDelete(new File(target, "renpy"));

            target.mkdirs();

            AssetExtract ae = new AssetExtract(this);
            if (!ae.extractTar(resource + ".mp3", target.getAbsolutePath())) {
                toastError("Could not extract " + resource + " data.");
            }

            try {
                // Write .nomedia.
                new File(target, ".nomedia").createNewFile();

                // Write version file.
                FileOutputStream os = new FileOutputStream(disk_version_fn);
                os.write(data_version.getBytes());
                os.close();
            } catch (Exception e) {
                Log.w("python", e);
            }
        }

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

    public static native void nativeSetEnv(String variable, String value);

    public void initEnviron() {
        Log.v("python", "initEnviron");

        Intent intent = getIntent();

        String script = null;
        if (intent != null && intent.getData() != null) {
            script = intent.getData().getPath();
        }
        File path;

        if (script!=null) {
            path = new File(script).getParentFile();
            nativeSetEnv("ANDROID_ARGUMENT", path.getAbsolutePath());

        } else {
            nativeSetEnv("ANDROID_ARGUMENT", QPyConstants.ABSOLUTE_PATH);

        }




    }
    public void preparePython() {
        Log.v("python", "Starting preparePython.");

        Intent intent = getIntent();
        String script = null;
        if (intent != null && intent.getData() != null) {

            script = intent.getData().getPath();
        }


        mActivity = this;

        resourceManager = new ResourceManager(this);

        File oldExternalStorage = new File(Environment.getExternalStorageDirectory(), getPackageName());
        File externalStorage = getExternalFilesDir(null);
        File path;

        if (externalStorage == null) {
            externalStorage = oldExternalStorage;
        }

        if (script!=null) {
            path = new File(script).getParentFile();
            if (path.getName().startsWith("scripts")) {
                nativeSetEnv("ANDROID_MAIN", new File(script).getName());

            } else {
                nativeSetEnv("ANDROID_MAIN", "main.py");

            }

        } else {
            nativeSetEnv("ANDROID_MAIN", "main.py");

            path = getFilesDir();
        }

        nativeSetEnv("ANDROID_LOG", QPyConstants.ABSOLUTE_LOG);

        File filesDir = getFilesDir();
        //unpackData("private", getFilesDir());
        // unpackData("public", externalStorage);

        nativeSetEnv("ANDROID_ARGUMENT", path.getAbsolutePath());
        nativeSetEnv("ANDROID_PRIVATE", getFilesDir().getAbsolutePath());
        nativeSetEnv("ANDROID_PUBLIC", QPyConstants.ABSOLUTE_PATH);
        nativeSetEnv("ANDROID_OLD_PUBLIC", oldExternalStorage.getAbsolutePath());

        nativeSetEnv("LD_LIBRARY_PATH", ".:"+filesDir+"/lib/"+":"+filesDir+"/:"+filesDir.getParentFile()+"/lib/");
        nativeSetEnv("AP_HOST", Utils.getSP(mActivity, "sl4a.hostname"));
        nativeSetEnv("AP_PORT", Utils.getSP(mActivity, "sl4a.port"));
        nativeSetEnv("AP_HANDSHAKE", Utils.getSP(mActivity, "sl4a.secue"));

        // Figure out the APK path.
        String apkFilePath;
        ApplicationInfo appInfo;
        PackageManager packMgmr = getApplication().getPackageManager();

        try {
            appInfo = packMgmr.getApplicationInfo(getPackageName(), 0);
            apkFilePath = appInfo.sourceDir;
        } catch (NameNotFoundException e) {
            apkFilePath = "";
        }

        nativeSetEnv("ANDROID_APK", apkFilePath);

        String expansionFile = getIntent().getStringExtra("expansionFile");

//        if (expansionFile != null) {
//            nativeSetEnv("ANDROID_EXPANSION", expansionFile);
//        }

        nativeSetEnv("PYTHONOPTIMIZE", "2");
        nativeSetEnv("PYTHONHOME", getFilesDir().getAbsolutePath());
        nativeSetEnv("PYTHONPATH", path.getAbsolutePath() + ":" + getFilesDir().getAbsolutePath() + "/lib");

        Log.v("python", "Finished preparePython.");

    };

    // Code to support devicePurchase. /////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //preparePython();

        //  Intent intent = getIntent();
//        boolean isLandscape = true;
//        if (intent != null && intent.getData() != null) {
//            String script = intent.getData().getPath();
//            String header_512 = FileHelper.getFileContents(script, 512);
//                isLandscape = header_512.contains("#qpy:landscape");
//
//            LogUtil.d(TAG, "onCreate:"+script);
//
//        }

//        if (isLandscape) {
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//        } else {
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        }
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
    }

    // Code to support public APIs. ////////////////////////////////////////////

    public void openUrl(String url) {
        Log.i("python", "Opening URL: " + url);

        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    @SuppressLint("MissingPermission")
    public void vibrate(double s) {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (v != null) {
            v.vibrate((int) (1000 * s));
        }
    }

    public int getDPI() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.densityDpi;
    }

    public PowerManager.WakeLock wakeLock = null;

    public void setWakeLock(boolean active) {
        if (wakeLock == null) {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "Screen On");
            wakeLock.setReferenceCounted(false);
        }

        if (active) {
            wakeLock.acquire();
        } else {
            wakeLock.release();
        }
    }

}
