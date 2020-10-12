package org.qpython.qpy.main.activity;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.widget.Toast;

import com.quseit.util.FileHelper;
import com.quseit.util.FolderUtils;
import com.quseit.util.NAction;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.qpython.qpy.R;
import org.qpython.qpy.console.ScriptExec;
import org.qpython.qpy.console.shortcuts.ShortcutReceiver;
import org.qpython.qpy.main.adapter.AppListAdapter;
import org.qpython.qpy.main.event.AppsLoader;
import org.qpython.qpy.main.model.AppModel;
import org.qpython.qpy.main.model.QPyScriptModel;
import org.qpython.qpy.utils.ShortcutUtil;
import org.qpython.qpysdk.QPyConstants;
import org.qpython.qpysdk.utils.Utils;
import org.qpython.qsl4a.qsl4a.LogUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.qpython.qpy.R2.string.show;

/**
 * Local App list
 * Created by Hmei on 2017-05-22.
 */

public class AppListActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<ArrayList<AppModel>> {
    public static final String TYPE_SCRIPT = "script";
    private static final int REQUEST_INSTALL_SHORTCUT = 0;

    private List<AppModel> dataList;
    private AppListAdapter adapter;

    ShortcutReceiver receiver;

    public static void start(Context context, String type) {
        Intent starter = new Intent(context, AppListActivity.class);
        starter.putExtra("type", type);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initShortcutReceiver();
        runShortcut();
        setContentView(R.layout.activity_local_app);
        initView();
        EventBus.getDefault().register(this);
    }

    private void initShortcutReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_CREATE_SHORTCUT);
        filter.addAction("com.android.launcher.action.INSTALL_SHORTCUT");
        filter.addAction("android.content.pm.action.CONFIRM_PIN_SHORTCUT");
        filter.addAction(Intent.ACTION_VIEW);

        receiver = new ShortcutReceiver();
        registerReceiver(receiver,filter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (needLog && event != null) {
            showLogDialog(event);
            needLog = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver != null){
            unregisterReceiver(receiver);
        }
        EventBus.getDefault().unregister(this);
    }

    private void runShortcut() {
        if (Intent.ACTION_VIEW.equals(getIntent().getAction())) {
            String path = getIntent().getStringExtra("path");
            boolean isProj = getIntent().getBooleanExtra("isProj", false);
            if (isProj) {
                ScriptExec.getInstance().playProject(this, path, false);
            } else {
                ScriptExec.getInstance().playScript(this, path, null, false);
            }
            finish();
        }
    }

    QPyScriptModel mBean;
    private void initView() {
        dataList = new ArrayList<>();
        adapter = new AppListAdapter(dataList, getIntent().getStringExtra("type"), this);
        adapter.setCallback(new AppListAdapter.Callback() {
            @Override
            public void runScript(QPyScriptModel item) {
                ScriptExec.getInstance().playScript(AppListActivity.this, item.getPath(), null, false);
            }

            @Override
            public void runProject(QPyScriptModel item) {
                ScriptExec.getInstance().playProject(AppListActivity.this, item.getPath(), false);
            }

            @Override
            public void createShortcut(QPyScriptModel item) {
                mBean = item;
//                if (!checkPermission()){
//                    return;
//                }
                createShortcutOnThis();
//                test();
            }

            @Override
            public void exit() {
                AppListActivity.this.finish();
            }
        });

        RecyclerView appsView = findViewById(R.id.rv_app);
        GridLayoutManager mLayoutManager = new GridLayoutManager(this, 3, LinearLayoutManager.VERTICAL, false);
        appsView.setLayoutManager(mLayoutManager);
        appsView.setAdapter(adapter);

        ((TextView) findViewById(R.id.tv_folder_name)).setText(R.string.qpy_app);
        findViewById(R.id.iv_back).setOnClickListener(view -> AppListActivity.this.finish());

        getScriptList();
    }

//    private boolean checkPermission() {
//        if (Build.VERSION.SDK_INT >= 23) {
//            int checkPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.INSTALL_SHORTCUT);
//            LogUtil.e("checkPermission" + checkPermission);
//            LogUtil.e("checkPermission" + PackageManager.PERMISSION_GRANTED);
//            if (checkPermission != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INSTALL_SHORTCUT}, REQUEST_INSTALL_SHORTCUT);
//                return false;
//            } else {
//                return true;
//            }
//        } else {
//            return true;
//        }
//    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if (requestCode == REQUEST_INSTALL_SHORTCUT) {
//            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(this, R.string.toast_read_permission_deny, Toast.LENGTH_SHORT).show();
//            } else {
//                createShortcutOnThis();
//            }
//        }
//    }

    private void test(){
        judgeShortcutNameV2("org.qpython.qpy");
    }

    private void createShortcutOnThis(){
        if (mBean == null){
            return;
        }

        Intent intent = new Intent();
        intent.setClass(this, AppListActivity.class);
        intent.setAction(Intent.ACTION_VIEW);
        intent.putExtra("type", "script");
        intent.putExtra("path", mBean.getPath());
        intent.putExtra("isProj", mBean.isProj());

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ShortcutManager mShortcutManager = getSystemService(ShortcutManager.class);
            if (mShortcutManager.isRequestPinShortcutSupported()) {
                ShortcutInfo pinShortcutInfo =
                        new ShortcutInfo.Builder(this, mBean.getLabel())
                                .setShortLabel(mBean.getLabel())
                                .setLongLabel(mBean.getLabel())
                                .setIcon(Icon.createWithResource(this, mBean.getIconRes()))
                                .setIntent(intent)
                                .build();
                Intent pinnedShortcutCallbackIntent =
                        mShortcutManager.createShortcutResultIntent(pinShortcutInfo);
                PendingIntent successCallback = PendingIntent.getBroadcast(this, 0,
                        pinnedShortcutCallbackIntent, 0);
                LogUtil.e("createShortcut: " + "111111111111");
                mShortcutManager.requestPinShortcut(pinShortcutInfo,
                        successCallback.getIntentSender());
                LogUtil.e("createShortcut: " + mBean.getLabel());
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        judgeShortcutNameV2("org.qpython.qpy");
//                        judgeShortcutName(mBean.getLabel());
                    }
                },200);
            }
        } else {
            //Adding shortcut for MainActivity
            //on Home screen
            Intent addIntent = new Intent();
            addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
            addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, mBean.getLabel());
            addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                    Intent.ShortcutIconResource.fromContext(getApplicationContext(),
                            mBean.getIconRes()));
            addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
            getApplicationContext().sendBroadcast(addIntent);
            Toast.makeText(this, getString(R.string.shortcut_create_suc, mBean.getLabel()), Toast.LENGTH_SHORT).show();
        }
    }

//    private void judgeShortcutName(String name) {
//        int shortcutNum = 0;
//        for (String packageName : ShortcutUtil.getAllTheLauncher(getApplicationContext())) {
//            LogUtil.e("packageName111111: " + packageName);
//            if (name.equals(packageName)) {
//                shortcutNum ++;
//            }
//        }
//        LogUtil.e("packageName222222: " + shortcutNum);
//    }

    private void judgeShortcutNameV2(String name) {
        if (!ShortcutUtil.getShortcutInfo(getApplicationContext()).isEmpty()){
            return;
        }
        Toast.makeText(this, getString(R.string.shortcut_create_fail), Toast.LENGTH_SHORT).show();
    }


    private void getScriptList() {
        try {
            String projectPath = NAction.isQPy3(this.getApplicationContext())? QPyConstants.DFROM_PRJ3:QPyConstants.DFROM_PRJ2;
            File[] projectFiles = FileHelper.getABSPath(QPyConstants.ABSOLUTE_PATH + "/" + projectPath).listFiles();
            if (projectFiles != null) {
                Arrays.sort(projectFiles, FolderUtils.sortByName);
                dataList.clear();
                for (File file : projectFiles) {
                    if (file.isDirectory()) {
                        dataList.add(new QPyScriptModel(file));
                    }
                }
            }
            String scriptPath = NAction.isQPy3(this.getApplicationContext())?QPyConstants.DFROM_QPY3:QPyConstants.DFROM_QPY2;

            File[] files = FileHelper.getFilesByType(FileHelper.getABSPath(QPyConstants.ABSOLUTE_PATH + "/" + scriptPath));
            if (files!=null && files.length > 0) {
                Arrays.sort(files, FolderUtils.sortByName);
                for (File file : files) {
                    dataList.add(new QPyScriptModel(file));
                }
            }
            adapter.notifyDataSetChanged();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Loader<ArrayList<AppModel>> onCreateLoader(int id, Bundle args) {
        return new AppsLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<AppModel>> loader, ArrayList<AppModel> data) {
        dataList.addAll(data);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<AppModel>> loader) {
        dataList.clear();
        adapter.notifyDataSetChanged();
    }

    boolean              needLog;
    ScriptExec.LogDialog event;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(ScriptExec.LogDialog event) {
        this.event = event;
        needLog = true;
    }

    public void showLogDialog(ScriptExec.LogDialog event) {
        AlertDialog alertDialog = new AlertDialog.Builder(this, R.style.MyDialog)
                .setTitle(R.string.last_log)
                .setMessage(com.quseit.qpyengine.R.string.open_log)
                .setNegativeButton(R.string.no, ((dialog, which) -> dialog.dismiss()))
                .setPositiveButton(R.string.ok, (dialog, which) -> {

                    Utils.checkRunTimeLog(this, event.title, event.path);

                    dialog.dismiss();
                })
                .create();
        alertDialog.show();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.fade_out);
    }
}