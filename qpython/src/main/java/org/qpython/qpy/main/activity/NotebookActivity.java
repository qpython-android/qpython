package org.qpython.qpy.main.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.quseit.util.Log;
import com.quseit.util.NAction;

import org.qpython.qpy.R;
import org.qpython.qpy.databinding.ActivityNotebookBinding;
import org.qpython.qpy.main.event.Bean;
import org.qpython.qpy.main.widget.LoadingDialog;
import org.qpython.qpy.texteditor.ui.view.EnterDialog;
import org.qpython.qpy.utils.NoteBookAction;
import org.qpython.qpy.utils.NotebookUtil;
import org.qpython.qpy.main.utils.Utils;

import java.io.File;

/**
 * 文 件 名: NotebookActivity
 * 创 建 人: ZhangRonghua
 * 创建日期: 2018/2/5 11:12
 * 修改时间：
 * 修改备注：
 */

public class NotebookActivity extends BaseActivity implements View.OnClickListener {

    private ActivityNotebookBinding mBinding;
    private String lastNotebook;
    private static final String TOKEN = "?token=qpythonotebook";
    private SharedPreferences mSharedPreferences;
    private static final String FILE_PATH = "FILEPATH";
    public static final int REQUEST_SETTTING_CODE = 0X998;

    private Bean mBean;
    //关闭页面标识
    private boolean needClose = true;

    public static void start(Context context, String filePath, final boolean isNew) {
        Log.d("NotebookActivity", "start:"+filePath);
        Intent intent = new Intent(context, NotebookActivity.class);
        if (isNew) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.putExtra(FILE_PATH, filePath);
        context.startActivity(intent);
    }

    public static void startFromUrl(Context context, String url, final boolean isNew) {
        Log.d("NotebookActivity", "startFromUrl:"+url);
        LoadingDialog loadingDialog = new LoadingDialog(context);
        loadingDialog.show();
        NotebookUtil.downloadFile(url, new NotebookUtil.DownloadCallback() {
            @Override
            public void onSuccess(String filePath) {
                start(context, filePath, isNew);
                loadingDialog.dismiss();
            }

            @Override
            public void onFail(String msg) {
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                loadingDialog.dismiss();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_notebook);
        mSharedPreferences = getSharedPreferences("data", MODE_PRIVATE);

        initToolbar();
        if (NotebookUtil.isNotebookLibInstall(this)) {
            initNotebookRes();
            initWebView();
            initListener();
        } else {
            enableNotebookFromSetting();
        }

        IntentFilter filter = new IntentFilter(".QWebViewActivity");
        registerReceiver(webviewActivityReceiver, filter);

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void enableNotebookFromSetting() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_alert)
                .setMessage(R.string.ennable_notebook_first)
                .setPositiveButton(R.string.ok, (dialog, which) -> {
                    dialog.dismiss();
                    startActivityForResult(new Intent(this,SettingActivity.class),REQUEST_SETTTING_CODE);
                    needClose = false;
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                    dialog.dismiss();
                    needClose = true;
                })
                .setOnDismissListener(dialog -> {
                    if (needClose){
                        finish();
                    }
                })
                .show();
    }
    /**
     * enable notebook 后加载资源
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SETTTING_CODE && resultCode == RESULT_OK){
            initNotebookRes();
            NotebookUtil.startNotebookService2(this);
            initWebView();
            initListener();
        }else {
            finish();
        }
    }

    /**
     * 在该页面内的notebook文件选择
     * */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String filePath = intent.getStringExtra(FILE_PATH);
        Log.d("NotebookActivity", "filePath:"+filePath);
        if (filePath != null) {
            lastNotebook = filePath;
            loadNotebook("");
        }
    }

    private String getFileName(String filePath) {
        if (!filePath.contains("/")) {
            return "";
        }
        String name = filePath.substring(filePath.lastIndexOf("/") + 1);

        return name;
    }

    private String getRequestPath(String filePath) {
        if (filePath==null || !filePath.contains(NotebookUtil.NOTEBOOK_DIR)) {
            return "";
        }
        String path = filePath.substring(NotebookUtil.NOTEBOOK_DIR.length())+"";
        return path;
    }

    private void initListener() {

        mBinding.switchTerminalImg.setOnClickListener(this);
        mBinding.stepinImg.setOnClickListener(this);
        mBinding.saveImg.setOnClickListener(this);
        mBinding.markdownAddImg.setOnClickListener(this);
        mBinding.codeAddImg.setOnClickListener(this);
        mBinding.playallImg.setOnClickListener(this);
        mBinding.undoImg.setOnClickListener(this);
        mBinding.refreshImg.setOnClickListener(this);
        mBinding.deleteCellImg.setOnClickListener(this);
        mBinding.cellUpImg.setOnClickListener(this);
        mBinding.cellDownImg.setOnClickListener(this);
        mBinding.switchCodeImg.setOnClickListener(this);
        mBinding.switchMarkdownImg.setOnClickListener(this);

    }

    private void initNotebookRes() {
        String filePath = getIntent().getStringExtra(FILE_PATH);
        Log.d("NotebookActivity", "initNotebookRes:"+filePath);
        //判断是否从文件列表打开
        if (filePath == null) {
            //打开最近一次编辑保存的文件
            lastNotebook = mSharedPreferences.getString("last_notebook", null);
            if (lastNotebook == null) {
                String fileName = "Welcome.ipynb";
                File file = new File(NotebookUtil.NOTEBOOK_DIR + "notebooks", fileName);
                if (file.exists()) {
                    lastNotebook = file.getAbsolutePath();
                } else {
                    lastNotebook = NotebookUtil.createNotebook(this, NotebookUtil.Untitled);
                }
            } else  {
                Log.d(TAG, "lastNotebook:"+lastNotebook);
            }
        } else {
            if (filePath.contains(NotebookUtil.NOTEBOOK_DIR)) {
                lastNotebook = filePath;
            } else {

                Toast.makeText(this, "inalid file", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        if (lastNotebook!=null) {
            mBinding.toolbar.setTitle(getFileName(lastNotebook));
        }
//        if (checkNotebook()) {
//            createServer();
//        }
    }

    /**
     * 开启notebook服务
     */
    private void createServer() {
        //如果服务未开启则开启服务
        AsyncTask.execute(() -> {
            if (!Utils.httpPing(NotebookUtil.NB_SERVER, 3000)) {
                runOnUiThread(() -> NotebookUtil.startNotebookService(getApplication()));
            }
        });

    }

    public static final  String URL          = "url";
    public static final  String ACT          = "act";
    protected static final String TAG        = "NotebookActivity";

    protected final BroadcastReceiver webviewActivityReceiver = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
        @Override
        public void onReceive(Context context, Intent intent) {
            String act = intent.getExtras().getString(ACT);
            String url = intent.getExtras().getString(URL);
            android.util.Log.d(TAG, "BroadcastReceiver:" + act);
            switch (act != null ? act : "") {
                case "onnext": {
                    enableNotebookFromSetting();

                }
            }
        }
    };

    @SuppressLint("ClickableViewAccessibility")
    private void initWebView() {
        mBinding.WebViewProgress.setMax(100);
        //设置js调用的javabean
        mBean = new Bean(this, mBinding.webView);
        mBean.setSrv(NotebookUtil.NOTEBOOK_SERVER + getRequestPath(lastNotebook) + TOKEN);
        mBinding.webView.addJavascriptInterface(mBean, "milib");

        mBinding.webView.getSettings().setSupportMultipleWindows(false);
        mBinding.webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        //设置WebView属性，能够执行Javascript脚本
        mBinding.webView.getSettings().setJavaScriptEnabled(true);
        //设置可以访问文件
        mBinding.webView.getSettings().setAllowFileAccess(true);
        mBinding.webView.setWebViewClient(new WebViewClient() {

            @SuppressWarnings("deprecation")
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String request) {
                Log.d("WEBVIEW", "url:" + request);
                view.loadUrl(request);
//                if (request.endsWith(NotebookUtil.ext)) {
//                    view.clearHistory();
//                }
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                Log.d("webview", error.toString());
            }
        });

        mBinding.webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                mBinding.WebViewProgress.setProgress(mBinding.webView.getProgress());
            }
        });

        //设置默认的加载页面来处理url跳转
        mBinding.webView.loadUrl("file:///android_asset/html/index.html");
    }

    private void initToolbar() {
        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mBinding.toolbar.setNavigationIcon(R.drawable.ic_back);
        mBinding.toolbar.setNavigationOnClickListener(v -> closeActivity());
    }

    private void closeActivity() {
        doAction(NoteBookAction.SAVE_NOTEBOOK);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.switch_terminal_img:
                NotebookListActivity.start(NotebookActivity.this);
                break;
            case R.id.stepin_img:
                //运行并选择下一个cell
                doAction(NoteBookAction.PLAY_CURRENT_CELL);
                break;
            case R.id.markdown_add_img:
                //添加一个markdown
                doAction(NoteBookAction.ADD_CELL_BELOW, NoteBookAction.SWITCH_MARKDOWN_TYPE);
                break;
            case R.id.code_add_img:
                //添加一个code
                doAction(NoteBookAction.ADD_CELL_BELOW);
                break;
            case R.id.playall_img:
                //运行全部
                doAction(NoteBookAction.PLAY_ALL_CELL);
                break;
            case R.id.undo_img:
                //撤销修改
                doAction(NoteBookAction.CELL_UNDO);
                break;
            case R.id.save_img:
                //保存
                saveFile();
                break;
            case R.id.delete_cell_img:
                //删除cell
                doAction(NoteBookAction.DELETE_CELL);
                break;
            case R.id.refresh_img:
                //清空输出，重新加载
                doAction(NoteBookAction.CLEAR_ALL_OUTPUT);
                break;
            case R.id.cell_down_img:
                //移动到下一个cell
                doAction(NoteBookAction.MOVE_CELL_DOWN);
                break;
            case R.id.cell_up_img:
                //移动到上一个cell
                doAction(NoteBookAction.MOVE_CELL_UP);
                break;
            case R.id.switch_code_img:
                //移动到上一个cell
                doAction(NoteBookAction.SWITCH_CODE_TYPE);
                break;
            case R.id.switch_markdown_img:
                //移动到上一个cell
                doAction(NoteBookAction.SWITCH_MARKDOWN_TYPE);
                break;
        }
    }

    private void srvManange() {
        new AlertDialog.Builder(this, R.style.MyDialog)
            .setTitle(R.string.notebook_service)
            .setMessage(R.string.notebook_service_restart)

            .setNegativeButton(R.string.notebook_restart, (dialog1, which) -> {
                dialog1.dismiss();
                NotebookUtil.killNBSrv(this);
                NotebookUtil.startNotebookService2(this);
                initWebView();
            })
            .setPositiveButton(R.string.notebook_reload, (dialog1, which) -> {
                mBinding.webView.reload();

            })
            .create()
            .show();
    }

    private void saveFile() {
        if (lastNotebook.endsWith(NotebookUtil.Untitled + NotebookUtil.ext)) {
            new EnterDialog(this)
                .setTitle(getString(R.string.save))
                .setHint(getString(R.string.file_name))
                .setConfirmListener(new EnterDialog.ClickListener() {
                    @Override
                    public boolean OnClickListener(String name) {
                        doAction(NoteBookAction.SAVE_NOTEBOOK);
                        File file = new File(lastNotebook);
                        File newFile = new File(NotebookUtil.NOTEBOOK_DIR + "notebooks/", name + NotebookUtil.ext);
                        file.renameTo(newFile);
                        lastNotebook = newFile.getAbsolutePath();
                        mBinding.toolbar.setTitle(getFileName(lastNotebook));
                        //toast("save file successful!");
                        return true;
                    }
                }).show();
        } else {
            doAction(NoteBookAction.SAVE_NOTEBOOK);
        }
    }

    public void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public static final String JS_SUFFIX = "javascript:";

    private void doAction(String... actions) {
        for (String action : actions) {
            mBinding.webView.loadUrl(JS_SUFFIX + action);
        }
    }

    /**
     * jupyter未安装则跳转应用市场安装
     */
    private void linkToStore() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("http://nb.qpython.org/install.html"));
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.notebook_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_open:
                //打开文件
                NotebookListActivity.start(NotebookActivity.this);
                break;

            case R.id.menu_add:
                //新增一个文件
                createNewFile();
                break;
            case R.id.menu_new_more:
                srvManange();
                break;
        }
        return true;
    }

    /**
     * 创建一个新的notebook文件
     */
    private void createNewFile() {
        new EnterDialog(this)
            .setTitle(getString(R.string.new_file))
            .setHint(getString(R.string.file_name))
            .setConfirmListener(name -> {
                lastNotebook = NotebookUtil.createNotebook(NotebookActivity.this, name);
                loadNotebook("?kernel_name=python"+ NAction.getPyVer(this));
                return true;
            }).show();
    }

    /**
     * 加载文档
     */
    private void loadNotebook(String kernel) {
        mBinding.toolbar.setTitle(getFileName(lastNotebook));
        String url = NotebookUtil.NOTEBOOK_SERVER + getRequestPath(lastNotebook)+kernel;
        Log.d("NotebookActivity", "loadNotebook:"+url);
        mBinding.webView.loadUrl(url);
    }

    /**
     * 退出时清除webview相关资源
     */
    @Override
    protected void onDestroy() {
        if (mBinding.webView != null) {
            doAction(NoteBookAction.SHUTDOWN_KERNEL);
            mBinding.webView.clearHistory();
            //移除webview并注销
            ((ViewGroup) mBinding.webView.getParent()).removeView(mBinding.webView);
            mBinding.webView.destroy();
        }
        mSharedPreferences.edit().putString("last_notebook", lastNotebook).apply();
        unregisterReceiver(webviewActivityReceiver);

        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    /**
     * 返回键控制webview回退
     * 注：由于只会有一个编辑页，所以并不需要回退
     */
    @Override
    public void onBackPressed() {
        closeActivity();
    }
}
