package org.qpython.qpy.main.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;


import com.loopj.android.http.AsyncHttpResponseHandler;
import com.quseit.base.QBaseApp;
import com.quseit.util.NAction;
import com.quseit.util.NUtil;

import org.apache.http.Header;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.qpython.qpy.R;
import org.qpython.qpy.console.ScriptExec;
import org.qpython.qpy.databinding.ActivityQwebviewBinding;
import org.qpython.qpy.main.app.App;
import org.qpython.qpy.main.event.Bean;
import org.qpython.qpy.main.receiver.DownloadNotebookReceiver;
import org.qpython.qpy.main.utils.Utils;
import org.qpython.qpy.utils.NotebookUtil;
import org.qpython.qpysdk.QPyConstants;
import org.qpython.qpysdk.utils.FileHelper;

import java.io.File;


/**
 * inner web view
 * Created by yhc on 16/9/30.
 */

public class QWebViewActivity extends BaseActivity {
    protected static final String TAG                    = "QWebViewActivity";
    protected final static int    FILECHOOSER_RESULTCODE = 1;
    private static final   int    PURCHASE_REQUEST_CODE  = 233;

    public static final String IS_NO_HEADER = "is_no_header";
    public static final String IS_DRAWER    = "is_drawer";

    private static final String TYPE         = "type";
    public static final  String HTML         = "html";
    public static final  String TITLE        = "title";
    public static final  String URL          = "url";
    public static final  String SRC          = "src";
    public static final  String ACT          = "act";
    public static final  String LOG_PATH     = "LOG_PATH";
    protected            String wvCookie     = "";
    protected            String wvDocument   = "";
    private              String launchScript = "";
    private DownloadNotebookReceiver receiver;

    protected ValueCallback<Uri> mUploadMessage;

    protected final BroadcastReceiver webviewActivityReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String act = intent.getExtras().getString(ACT);
            String url = intent.getExtras().getString(URL);
            Log.d(TAG, "BroadcastReceiver:"+act);

            switch (act != null ? act : "") {
                case "pipconsole": {
                    String[] args = {getApplicationContext().getFilesDir() + "/bin/pip_console.py", getApplicationContext().getFilesDir().toString()};
                    ScriptExec.getInstance().execPyInConsole((Activity) context, args);

                    break;
                }
                case "pipinstall": {
                    String src = intent.getExtras().getString(QWebViewActivity.SRC);
                    String[] args = {getApplicationContext().getFilesDir() + "/bin/pip"+ (NAction.isQPy3(getApplicationContext())?"3":""), "install", url, "-i", src, getApplicationContext().getFilesDir().toString()};
                    ScriptExec.getInstance().execPyInConsole((Activity) context, args);
                    break;
                }
                case "close":
                    finish();
                    break;
                case "launchsrv":
                    String script = getIntent().getStringExtra(LOG_PATH);
                    if (script != null && !script.equals("")) {
                        launchScript = script;
                    }
                    break;
                case "opennotebook":
                    String notebookurl = getIntent().getStringExtra(URL);
                    String downloadDir = "qpython/notebooks";
                    Log.d(TAG, "opennotebook:"+notebookurl);

                    App.getService().downloadFile(getApplicationContext(), notebookurl, NUtil.getFileFromUrl(notebookurl), "", downloadDir);
                    break;
                case "onnext":
                    break;
                case "loadhtml":
                    String data = getIntent().getStringExtra(HTML);
                    binding.wv.loadDataWithBaseURL("file:///android_asset/", data, "text/html", "UTF-8", null);
                    break;

            }
        }
    };
    private ActivityQwebviewBinding binding;

    public static void start(Context context, String title, String url) {
        Intent starter = new Intent(context, QWebViewActivity.class);
        if (context == App.getContext()) {
            starter.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        starter.putExtra(QWebViewActivity.TITLE, title);
        starter.putExtra(QWebViewActivity.URL, url);
        context.startActivity(starter);
    }

    public static void start(Context context, String act, String title, String url, String logPath) {
        Intent starter = new Intent(context, QWebViewActivity.class);
        if (context == App.getContext()) {
            starter.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        starter.putExtra(QWebViewActivity.ACT, act);
        starter.putExtra(QWebViewActivity.TITLE, title);
        starter.putExtra(QWebViewActivity.SRC, url);
        starter.putExtra(QWebViewActivity.LOG_PATH, logPath);
        context.startActivity(starter);
    }

    public static void loadHtml(Context context, String html, String title) {
        Intent starter = new Intent(context, QWebViewActivity.class);
        starter.putExtra(TYPE, HTML);
        starter.putExtra(HTML, html);
        starter.putExtra(TITLE, title);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_qwebview);
        receiver = new DownloadNotebookReceiver();

        Intent i = getIntent();
        String title = i.getStringExtra(TITLE);
        String type = i.getStringExtra(TYPE);
        if (launchScript == null || launchScript.isEmpty())
            launchScript = i.getStringExtra(LOG_PATH);

        setSupportActionBar(binding.lt.toolbar);
        binding.lt.toolbar.setNavigationIcon(R.drawable.ic_back);
        binding.lt.toolbar.setNavigationOnClickListener(view -> QWebViewActivity.this.finish());

        if (title != null) {
            setTitle(title);
        } else {
            setTitle(R.string.app_name);
        }

        switch (type == null ? "" : type) {
            case HTML:
                binding.wv.loadData(i.getStringExtra(HTML), "text/html", null);
                break;
            default:
                initWebView();
                accessUrl();
                break;
        }

        IntentFilter filter = new IntentFilter();
        registerReceiver(webviewActivityReceiver, filter);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        registerReceiver(receiver, new IntentFilter(
//                DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.refresh_menu, menu);
        if (launchScript != null && !launchScript.isEmpty()) {
            MenuItem logItem = menu.findItem(R.id.log_menu);
            logItem.setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh_menu:
                binding.WebViewProgress.setVisibility(View.VISIBLE);
                binding.wv.loadUrl("javascript:window.location.reload( true )");
                break;
            case R.id.log_menu:
                String title = FileHelper.getFileName(launchScript);
                Intent resultIntent = new Intent(this, LogActivity.class);
                if (launchScript.contains("/scripts")) {
                    String proj = new File(launchScript).getName();

                    resultIntent.putExtra(LogActivity.LOG_PATH, QPyConstants.ABSOLUTE_LOG);
                    resultIntent.putExtra(LogActivity.LOG_TITLE, proj);
                } else {
                    String proj = new File(launchScript).getParentFile().getName();
                    resultIntent.putExtra(LogActivity.LOG_PATH, QPyConstants.ABSOLUTE_LOG);
                    resultIntent.putExtra(LogActivity.LOG_TITLE, proj);
                }

                startActivity(resultIntent);
                break;
        }
        return true;
    }

    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(webviewActivityReceiver);
        if (launchScript != null && !launchScript.equals("")) {
            endWebSrv(launchScript);
        }
    }

    protected void endWebSrv(String script) {
        QBaseApp.getInstance().getAsyncHttpClient().get(Utils.getSrv(script) + "/__exit", new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {

            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

            }

        });
    }

    public void accessUrl() {
        if (Utils.netOk(getApplicationContext())) {
            String url;
            Intent i = getIntent();
            if (i.getData() != null) {
                url = i.getDataString();
                Log.d(TAG, "accessUrl:"+url);

            } else {
                url = i.getStringExtra(URL) == null ?
                        i.getStringExtra(Intent.EXTRA_TEXT) == null ?
                                "file:///android_asset/html/index.html"
                                : i.getStringExtra(Intent.EXTRA_TEXT)
                        : i.getStringExtra(URL);
            }

            loadurl(binding.wv, url);

        } else {
            String url = "file:///android_asset/html/index.html";

            Intent i = getIntent();
            if (i.getData() != null) {
                url = i.getDataString();
                Log.d(TAG, "accessUrl:"+url);

            }

            loadurl(binding.wv, url);
        }
    }

    private void writeWebLog(String data) {
        FileHelper.writeToFile(QPyConstants.ABSOLUTE_LOG,data+"\n", true);
    }

    //
    public void initWebView() {// 初始化
        binding.WebViewProgress.setMax(100);

        Bean bean = new Bean(this, binding.wv);
        String act = getIntent().getStringExtra(ACT);

        if (act != null && act.equals("main")) {
            String url = getIntent().getStringExtra(QWebViewActivity.SRC);
            bean.setSrv(url);

            launchScript = getIntent().getStringExtra(LOG_PATH);
//            String title = getIntent().getStringExtra(TITLE);
//            String isNoHead = getIntent().getStringExtra(IS_NO_HEADER);
//            String isDrawer = getIntent().getStringExtra(IS_DRAWER);
        }

        binding.wv.setInitialScale(1);
        binding.wv.getSettings().setAllowFileAccess(true);
        binding.wv.getSettings().setPluginState(WebSettings.PluginState.ON);
        binding.wv.getSettings().setPluginState(WebSettings.PluginState.ON_DEMAND);
        binding.wv.getSettings().setLoadWithOverviewMode(true);
        binding.wv.getSettings().setUseWideViewPort(true);

        binding.wv.getSettings().setJavaScriptEnabled(true);// 可用JS
        binding.wv.addJavascriptInterface(bean, "milib");
        binding.wv.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);// 滚动条风格，为0就是不给滚动条留空间，滚动条覆盖在网页上
        binding.wv.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        binding.wv.getSettings().setBlockNetworkImage(false);
        binding.wv.getSettings().setAllowFileAccess(true);
        binding.wv.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
                writeWebLog("[Console] Loading URL:" + url);
                loadurl(view, url);// 载入网页
                return true;
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                writeWebLog("[Console] Errorcode " + errorCode + " ("+failingUrl+")\n  Description:" + description);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                writeWebLog("[Console] Load PageFinished URL:" + url);

                super.onPageFinished(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                writeWebLog("[Console] Load PageFinished start:" + url);

                binding.wv.requestFocus();
            }
        });

        binding.wv.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int progress) {// 载入进度改变而触发
                if (binding.WebViewProgress != null)
                    binding.WebViewProgress.setProgress(binding.wv.getProgress());
                super.onProgressChanged(view, progress);
            }

            @Override
            public void onConsoleMessage(String message, int lineNumber, String sourceID) {
                writeWebLog("[Console] Line "+lineNumber+ " ("+sourceID+")\n  Message:"+ message);
            }

            public void openFileChooser(ValueCallback<Uri> uploadMsg) {

                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("*/*");
                startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);
            }

            // For Android 3.0+
            public void openFileChooser(ValueCallback uploadMsg, String acceptType) {
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("*/*");
                startActivityForResult(Intent.createChooser(i, "File Browser"), FILECHOOSER_RESULTCODE);
            }

            // For Android 4.1
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("*/*");
                startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);
            }
        });

        binding.wv.getSettings().setLoadWithOverviewMode(true);
    }

    @Override
    public boolean onKeyDown(int keycode, KeyEvent event) {
        if (keycode == KeyEvent.KEYCODE_BACK) {
            if (binding.wv.canGoBack()) {
                WebBackForwardList mWebBackForwardList = binding.wv.copyBackForwardList();
                String historyUrl = mWebBackForwardList.getItemAtIndex(mWebBackForwardList.getCurrentIndex() - 1).getUrl();
                if (historyUrl.startsWith("file:///android_asset/")) {
                    finish();
                    return true;
                } else {
                    binding.wv.goBack();
                    return false;
                }
            } else {
                finish();
                return true;
            }
        } else {
            return super.onKeyDown(keycode, event);
        }
    }

    // if ext endswith .ipynb, you have option to open it with NotebookActivity
    public void loadurl(final WebView view, final String url) {
        URL u = null;
        try {
            u = new URL(url);
            String path = u.getPath();
            if (path!=null
                    && NotebookUtil.isNotebookLibInstall(this)
                    && path.endsWith((NotebookUtil.ext))) {


                new AlertDialog.Builder(this, R.style.MyDialog)
                        .setTitle(R.string.confirm)
                        .setMessage(R.string.notebook_open)
                        .setPositiveButton(R.string.ok, (dialog1, which) -> {
                            dialog1.dismiss();

                            this.finish();

                            NotebookUtil.killNBSrv(this);
                            NotebookUtil.startNotebookService2(this);
                            NotebookActivity.startFromUrl(this, url, true);
                        })
                        .setNegativeButton(R.string.no, (dialog1,which)-> {
                            dialog1.dismiss();
                            view.loadUrl(url);// 载入网页

                        })
                        .create()
                        .show();
            } else {
                view.loadUrl(url);// 载入网页

            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            view.loadUrl(url);// 载入网页

        }

    }

    public void loadContent(final WebView view, final String content, final String historyUrl) {
        new Thread() {
            public void run() {
                view.loadDataWithBaseURL("file:///android_asset/", content, "text/html", "UTF-8", historyUrl);
            }
        }.start();
    }

    public void onRefresh(View v) {
        if (binding.wv != null) {
            // openWaitWindow();
            binding.wv.reload();
        }
    }

    class QPyLib {
        public QPyLib(Context context) {
        }

        @JavascriptInterface
        public void processHTML(String cookie, String data) {
            // LogUtil.d(TAG, "processHTML called(cookie):"+cookie);
            // LogUtil.d(TAG, "processHTML called(data):"+data);
            wvCookie = cookie;
            wvDocument = data;

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(CreateNotebookDownloadFinishEvent event) {
        String notebook = event.fileName;
        Toast.makeText(this, notebook+" is downloaded", Toast.LENGTH_SHORT).show();

    }
    public static class CreateNotebookDownloadFinishEvent {
        public String fileName;
    }

}
