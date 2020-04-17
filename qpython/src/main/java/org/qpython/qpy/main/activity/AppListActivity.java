package org.qpython.qpy.main.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.quseit.util.FileHelper;
import com.quseit.util.FolderUtils;
import com.quseit.util.NAction;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.qpython.qpy.R;
import org.qpython.qpy.console.ScriptExec;
import org.qpython.qpy.main.adapter.AppListAdapter;
import org.qpython.qpy.main.event.AppsLoader;
import org.qpython.qpy.main.model.AppModel;
import org.qpython.qpy.main.model.QPyScriptModel;
import org.qpython.qpysdk.QPyConstants;
import org.qpython.qpysdk.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Local App list
 * Created by Hmei on 2017-05-22.
 */

public class AppListActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<ArrayList<AppModel>> {
    public static final String TYPE_SCRIPT = "script";

    private List<AppModel> dataList;
    private AppListAdapter adapter;

    public static void start(Context context, String type) {
        Intent starter = new Intent(context, AppListActivity.class);
        starter.putExtra("type", type);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        runShortcut();
        setContentView(R.layout.activity_local_app);
        initView();
        EventBus.getDefault().register(this);
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