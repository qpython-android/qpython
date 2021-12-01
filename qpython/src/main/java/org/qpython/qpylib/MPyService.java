package org.qpython.qpylib;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.quseit.util.FileHelper;
import com.quseit.util.FileUtils;
import com.quseit.util.NAction;
import com.quseit.util.NUtil;

import org.qpython.qpy.console.ScriptExec;
import org.qpython.qpy.main.app.App;
import org.qpython.qpysdk.QPyConstants;

public class MPyService extends Service {
    protected static final String TAG = "mpyapi";
    protected String param;
    protected String flag    = "";
    protected int    runMode = 0;

    @Override
    public void onCreate() {
        if (!NUtil.isRunning(getApplicationContext(), "org.qpython.qsl4a.QPyScriptService")) {
            Intent intent = new Intent();
            intent.setClassName(this, "org.qpython.sl4alib.QPyScriptService");
            startService(intent);
        }
    }


    private void process(Intent intent) {
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
                    ScriptExec.getInstance().playScript(MPyService.this, path, null, true);

                } else if (type.equals("project")) {
                    ScriptExec.getInstance().playProject(MPyService.this, path, false);
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

                            ScriptExec.getInstance().playScript(MPyService.this, pyfile, null, false);

                        } else {
                            // Compatibility Mode
                            if (pycode != null && pycode.contains("#qpy:console\n") || NAction.isQPy3(getApplicationContext())) {
                                runMode = 3;
                            } else {
                                runMode = 1;
                            }
                            String script = FileUtils.getAbsolutePath(App.getContext()) + "/cache/last.py";
                            FileHelper.putFileContents(this, script, pycode);
                            ScriptExec.getInstance().playScript(MPyService.this, script, null, false);
                        }
                    }
                }
            }
        }
    }

    void handleSendUrl(Intent intent) {
        Uri myURI = intent.getData();
        if (myURI != null) {
            String script = getApplicationContext().getFilesDir() + "/bin/share.py";
            String param = myURI.toString();
            ScriptExec.getInstance().playScript(MPyService.this, script, param, false);
        }
    }

    void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            String script = getApplicationContext().getFilesDir() + "/bin/share.py";
            ScriptExec.getInstance().playScript(MPyService.this, script, sharedText, false);
        }
    }

    void handleSendImage(Intent intent) {
        Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            ScriptExec.getInstance().playQScript(MPyService.this, getApplicationContext().getFilesDir() + "/bin/share.py", imageUri.toString(), false);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        process(intent);
        return super.onStartCommand(intent, flags, startId);
    }

}