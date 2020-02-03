package org.qpython.qpy.main.activity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Toast;

import com.quseit.util.NAction;
import com.quseit.util.NUtil;
import com.umeng.analytics.MobclickAgent;

import org.qpython.qpy.R;
import org.qpython.qpy.console.ShellTermSession;
import org.qpython.qpy.console.util.TermSettings;
import org.qpython.qpy.main.server.gist.TokenManager;
import org.renpy.android.ResourceManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Map;


public class BaseActivity extends AppCompatActivity {
    // QPython interfaces
    private static final int SCRIPT_CONSOLE_CODE = 1237;
    private static final int PID_INIT_VALUE      = -1;
    private static final int DEFAULT_BUFFER_SIZE = 8192;
    private static final int LOG_NOTIFICATION_ID = (int) System.currentTimeMillis();
    private ArrayList<String> mArguments = new ArrayList<>();
    private InputStream  mIn;
    private OutputStream mOut;
    private Map<Integer, PermissionAction> mActionMap = new ArrayMap<>();
    private TermSettings     mSettings;
    private ShellTermSession session;

    private boolean permissionGrant = true;

    protected static ShellTermSession createTermSession(Context context, TermSettings settings, String initialCommand, String path) {
        ShellTermSession session = null;
        try {
            session = new ShellTermSession(context, settings, initialCommand, path);
            session.setProcessExitMessage(context.getString(R.string.process_exit_message));

        } catch (IOException e) {
            e.printStackTrace();
        }

        return session;
    }

    protected void toast(String content) {
        Toast.makeText(this, content, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(this.getLocalClassName());
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MobclickAgent.setDebugMode(true);
        MobclickAgent.openActivityDurationTrack(false);
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(this.getLocalClassName());
        MobclickAgent.onPause(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionAction action = mActionMap.get(requestCode);
        if (action != null) {
            if (grantResults.length > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                action.onGrant();
            } else {
                action.onDeny();
            }

        }
        mActionMap.remove(action);
    }


    public final void checkPermissionDo(String[] permissions, PermissionAction action) {
        if (Build.VERSION.SDK_INT >= 23) {
            boolean granted = true;
            for (String permission : permissions) {
                int checkPermission = ContextCompat.checkSelfPermission(this, permission);
                granted = checkPermission == PackageManager.PERMISSION_GRANTED;
            }
            if (!granted) {
                int code = permissions.hashCode() & 0xffff;
                mActionMap.put(code, action);
                ActivityCompat.requestPermissions(this, permissions, code);
            } else {
                action.onGrant();
            }
        } else {
            action.onGrant();
        }
    }


    // feedback
    public void onFeedback(String feedback) {

        String app = getString(R.string.app_name);
        int ver = NUtil.getVersinoCode(getApplicationContext());
        String subject = MessageFormat.format(getString(com.quseit.android.R.string.feeback_email_title), app, ver, Build.PRODUCT);

        String lastError = "";
        String code = NAction.getCode(getApplicationContext());
        File log = new File(Environment.getExternalStorageDirectory() + "/" + code + "_last_err.log");
        if (log.exists()) {
            lastError = com.quseit.util.FileHelper.getFileContents(log.getAbsolutePath());
        }

        String body = MessageFormat.format(getString(R.string.feedback_email_body), Build.PRODUCT,
                Build.VERSION.RELEASE, Build.VERSION.SDK, lastError, feedback);

        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + getString(R.string.ui_feedback_mail)));

        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, session == null ? body : session.getTranscriptText().trim());
        try {
            startActivity(Intent.createChooser(intent,
                    getString(R.string.email_transcript_chooser_title)));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this,
                    R.string.email_transcript_no_email_activity_found,
                    Toast.LENGTH_LONG)
                    .show();
        }
    }

    protected boolean checkExpired(final String resource, String filesDir, String tag) {
        ResourceManager resourceManager = new ResourceManager(this);

        String data_version = resourceManager.getString(resource + "_version");
        String disk_version = "0";

        // If no version, no unpacking is necessary.
        if (data_version == null) {
            return false;
        }

        // Check the current disk version, if any.
        String disk_version_fn = filesDir + "/lib/" + tag + "_" + resource + ".version";

        try {
            byte buf[] = new byte[64];
            InputStream is = new FileInputStream(disk_version_fn);
            int len = is.read(buf);
            disk_version = new String(buf, 0, len);
            is.close();
        } catch (Exception e) {

            disk_version = "0";
            //Mint.logException(e);

        }

        //LogUtil.d(TAG, "data_version:"+Math.round(Double.parseDouble(data_version))+"-disk_version:"+Math.round(Double.parseDouble(disk_version))+"-RET:"+(int)(Double.parseDouble(data_version)-Double.parseDouble(disk_version)));
        if ((int) (Double.parseDouble(data_version) - Double.parseDouble(disk_version)) > 0 || disk_version.equals("0")) {
            try {
                FileOutputStream os = new FileOutputStream(disk_version_fn);
                try {
                    os.write(data_version.getBytes());
                    os.close();

                } catch (IOException e) {
                    e.printStackTrace();
                    //Mint.logException(e);

                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                //Mint.logException(e);

            }


            return true;
        } else {
            return false;
        }
    }

    public interface PermissionAction {
        void onGrant();

        void onDeny();
    }

    public void ifLogin(Login afterLogin) {
        if (!TextUtils.isEmpty(TokenManager.getToken())) {
            afterLogin.process();
        } else {
            Toast.makeText(this, R.string.login_first, Toast.LENGTH_SHORT).show();
        }
    }

    public interface Login {
        void process();
    }

}
