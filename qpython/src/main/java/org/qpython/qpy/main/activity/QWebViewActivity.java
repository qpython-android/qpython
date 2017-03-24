package org.qpython.qpy.main.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.qpython.qpy.R;
import org.qpython.qpy.main.event.Bean;
import org.qpython.qpy.main.utils.Utils;
import org.renpy.android.PythonActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cz.msebera.android.httpclient.Header;


/**
 * Created by yhc on 16/9/30.
 */

public class QWebViewActivity extends BaseActivity {
    protected static final String TAG = "QWebViewActivity";

    protected WebView wv;
    protected String wvCookie = "";
    protected String wvDocument = "";
    protected ProgressBar wvProgressBar;
    protected ProgressDialog pDialog;
    private String launchScript = "";


    private Unbinder mUnbinder;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_program_explorer);
//        setSupportActionBar(mBinding.toolbar);

        setContentView(R.layout.activity_qwebview);
        mUnbinder = ButterKnife.bind(this);

        Intent i = getIntent();
        String title = i.getStringExtra(PythonActivity.EXTRA_CONTENT_URL0);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(view -> QWebViewActivity.this.finish());


        if (title!=null) {
            getSupportActionBar().setTitle(title);

        } else {
            getSupportActionBar().setTitle(R.string.app_name);

        }
        initWebView();
        accessUrl();

        IntentFilter filter = new IntentFilter(".QWebViewActivity");
        registerReceiver(webviewActivityReceiver, filter);

    }

    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(webviewActivityReceiver);
        //Log.d(TAG, "onDestroy:launchScript:"+launchScript);

        if (launchScript!=null && !launchScript.equals("")) {
            endWebSrv(launchScript);
        }
    }

//    OkHttpClient client = new OkHttpClient();
//    @TargetApi(Build.VERSION_CODES.KITKAT)
//    String okget(String url) throws IOException {
//        Request request = new Request.Builder()
//                .url(url)
//                .build();
//
//        try (Response response = client.newCall(request).execute()) {
//            return response.body().string();
//        }
//    }
    protected void endWebSrv(String script) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(Utils.getSrv(script)+"/__exit", new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                // called before request is started
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                Toast.makeText(getApplicationContext(), "OK", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                Toast.makeText(getApplicationContext(), "Failure", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }
        });
//        try {
//            String ret = okget(Utils.getSrv(script)+"/__exit");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        //if (session!=null) {
//        NRequest.get2(this, getSrv(script)+"/__exit", null, new AsyncHttpResponseHandler() {
//            public void onSuccess(String response) {
//                Log.d(TAG, "endWebSrv OK");
//                if (session!=null) {
//                    session.finish();
//                    session = null;
//                }
//
//            }
//            public void onFailure(Throwable e) {
//                //Log.d(TAG, "endWebSrv Failed:"+e.getLocalizedMessage());
//
//            }
//        });

        //}

    }

    public void accessUrl() {
        if (Utils.netOk(getApplicationContext())) {
            String url = "http://qpython.org";
            Intent i = getIntent();
            if (i.getData() != null) {
                url = i.getDataString();
            } else {
                url = "file:///android_asset/html/index.html";

            }

//            if (act!=null && act.equals("main")) {
//                //
//            } else {
//                mediaUrl+="?q="+this.getApplicationContext().getPackageName()+"&lang="+Utils.getLang()+"&v="+Utils.getVersinoCode(this);
//            }

           loadurl(wv, url);

        } else {
            String url = "file:///android_asset/html/index.html";

            Intent i = getIntent();
            if (i.getData() != null) {
                url = i.getDataString();
            }

            loadurl(wv, url);
        }
    }

    //
    public void initWebView() {// 初始化
        if (wvProgressBar == null) {
            wvProgressBar = (ProgressBar) findViewById(R.id.WebViewProgress);
        }
        if (wvProgressBar != null)
            wvProgressBar.setMax(100);

        if (wv == null) {
            wv = (WebView) findViewById(R.id.wv);
        }

        Bean bean = new Bean(this);
        String act = getIntent().getStringExtra(PythonActivity.EXTRA_CONTENT_URL1);

        if (act!=null && act.equals("main")) {
            String title = getIntent().getStringExtra(PythonActivity.EXTRA_CONTENT_URL2);
            String url = getIntent().getStringExtra(PythonActivity.EXTRA_CONTENT_URL3);
            bean.setSrv(url);

            launchScript = getIntent().getStringExtra(PythonActivity.EXTRA_CONTENT_URL4);

            String isNoHead = getIntent().getStringExtra(PythonActivity.EXTRA_CONTENT_URL5);
            String isDrawer = getIntent().getStringExtra(PythonActivity.EXTRA_CONTENT_URL6);
        }

        // wv = new WebView(this);
        wv.getSettings().setJavaScriptEnabled(true);// 可用JS
        wv.addJavascriptInterface(bean, "milib");
        wv.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);// 滚动条风格，为0就是不给滚动条留空间，滚动条覆盖在网页上
        wv.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        wv.getSettings().setBlockNetworkImage(false);
        wv.getSettings().setAllowFileAccess(true);
		/*
		 * try { wv.getSettings().setPluginsEnabled(true); } catch (Exception e) { }
		 */
        wv.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
                Log.d(TAG, "setWebViewClient URL:" + url);
                loadurl(view, url);// 载入网页
                return true;
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Log.d(TAG, "errcode:" + errorCode + "-desc:" + description + "-url:" + failingUrl);
                // loadurl(wv,
                // "file:///android_asset/mbox/md3.html?act=err&info="+description+"&"+NAction.getUserUrl(getApplicationContext()));
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                //view.loadUrl("javascript:window.qpylib.processHTML(document.cookie, document.getElementsByTagName('html')[0].innerHTML);");
                // view.loadUrl("javascript:(function(){document.getElementById('snapNSendBtn').onclick=function(){var bean=window.bean;var title=bean.getTitle();alert(title);}})()");
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                wv.requestFocus();
            }

        });

        wv.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {// 载入进度改变而触发
                if (progress == 100) {
                    // handler.sendEmptyMessage(1);//如果全部载入,隐藏进度对话框
                    // wvProgressBar.setVisibility(View.GONE);
                }
                if (wvProgressBar != null)
                    wvProgressBar.setProgress(wv.getProgress());
                super.onProgressChanged(view, progress);
            }

        });

//		wv.setOnLongClickListener(new OnLongClickListener() {
//			@Override
//			public boolean onLongClick(View v) {
//				// Log.d(TAG, "click detected");
//				return true;
//			}
//		});
        // wv.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        // wv.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        wv.getSettings().setLoadWithOverviewMode(true);
        // wv.getSettings().setJavaScriptEnabled(true);

        // wv.addJavascriptInterface(new JavascriptInterface(MSearchAct.this),
        // "bean");
        // registerForContextMenu(wv);
        // openWaitWindow();

        wv.getSettings().setJavaScriptEnabled(true);// 可用JS
        wv.addJavascriptInterface(new QPyLib(this), "qpylib");

        wv.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);// 滚动条风格，为0就是不给滚动条留空间，滚动条覆盖在网页上
        wv.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        wv.getSettings().setBlockNetworkImage(false);
        wv.getSettings().setAllowFileAccess(true);
        // wv.getSettings().setBuiltInZoomControls(true);// 设置支持缩放

        String ua = wv.getSettings().getUserAgentString();
        wv.getSettings().setUserAgentString(ua + " :QUSEIT");

		/*
		 * try { wv.getSettings().setPluginsEnabled(true); } catch (Exception e) { }
		 */
        wv.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
                Log.d(TAG, "setWebViewClient URL:" + url);
                loadurl(view, url);// 载入网页
                return true;
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Log.d(TAG, "errcode:" + errorCode + "-desc:" + description + "-url:" + failingUrl);
                // loadurl(wv,
                // "file:///android_asset/mbox/md3.html?act=err&info="+description+"&"+NAction.getUserUrl(getApplicationContext()));
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                view.loadUrl("javascript:window.qpylib.processHTML(document.cookie, document.getElementsByTagName('html')[0].innerHTML);");
                // view.loadUrl("javascript:(function(){document.getElementById('snapNSendBtn').onclick=function(){var bean=window.bean;var title=bean.getTitle();alert(title);}})()");
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                wv.requestFocus();
            }
        });

        wv.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {// 载入进度改变而触发
                if (progress == 100) {
                    // handler.sendEmptyMessage(1);//如果全部载入,隐藏进度对话框
                    // wvProgressBar.setVisibility(View.GONE);
                }
                if (wvProgressBar != null)
                    wvProgressBar.setProgress(wv.getProgress());
                super.onProgressChanged(view, progress);
            }

            // The undocumented magic method override
            // Eclipse will swear at you if you try to put @Override here
            // For Android 3.0+

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

        // wv.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        // wv.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        wv.getSettings().setLoadWithOverviewMode(true);
        // wv.getSettings().setJavaScriptEnabled(true);

        // wv.addJavascriptInterface(new JavascriptInterface(MSearchAct.this), "bean");
        // registerForContextMenu(wv);
        // openWaitWindow();
    }

    @Override
    public boolean onKeyDown(int keycode, KeyEvent event) {
        if (keycode == KeyEvent.KEYCODE_BACK) {
            if (wv.canGoBack()) {
                WebBackForwardList mWebBackForwardList = wv.copyBackForwardList();
                String historyUrl = mWebBackForwardList.getItemAtIndex(mWebBackForwardList.getCurrentIndex()-1).getUrl();
                if (historyUrl.startsWith("file:///android_asset/")) {
                    finish();
                } else {
                    wv.goBack();
                    return false;
                }
            } else {
                finish();
            }

        }
        return super.onKeyDown(keycode, event);
    }


    public void loadurl(final WebView view, final String url) {
        view.loadUrl(url);// 载入网页
		/*
		 * new Thread(){ public void run(){ //handler.sendEmptyMessage(0); try{ view.loadUrl(url);//载入网页 if (CONF.DEBUG)
		 * Log.d(TAG, "load url:"+url); } catch (Exception e) { } } }.start();
		 */
    }

    public void loadContent(final WebView view, final String content, final String historyUrl) {
        new Thread() {
            public void run() {
                // handler.sendEmptyMessage(0);
                view.loadDataWithBaseURL("file:///android_asset/", content, "text/html", "UTF-8", historyUrl);
            }
        }.start();
    }

    public void onRefresh(View v) {
        if (wv != null) {
            // openWaitWindow();
            wv.reload();
        }
    }

    class QPyLib {
        public QPyLib(Context context) {
        }

        @JavascriptInterface
        public void processHTML(String cookie, String data) {
            // Log.d(TAG, "processHTML called(cookie):"+cookie);
            // Log.d(TAG, "processHTML called(data):"+data);
            wvCookie = cookie;
            wvDocument = data;

        }
    }

    protected ValueCallback<Uri> mUploadMessage;

    protected final static int FILECHOOSER_RESULTCODE = 1;

    protected final BroadcastReceiver webviewActivityReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Log.d(TAG, "downloadReceiver");
            String act = intent.getExtras().getString(PythonActivity.EXTRA_CONTENT_URL1);
            String url = intent.getExtras().getString(PythonActivity.EXTRA_CONTENT_URL2);

            if (act.equals("pipconsole")) {
                String[] args = {getApplicationContext().getFilesDir()+"/bin/pip_console.py", getApplicationContext().getFilesDir().toString()};
                execPyInConsole(args);

            } else if (act.equals("pipinstall")) {
                String src = intent.getExtras().getString(PythonActivity.EXTRA_CONTENT_URL3);

                String[] args = {getApplicationContext().getFilesDir()+"/bin/pip", "install", url, "-i", src, getApplicationContext().getFilesDir().toString()};
                execPyInConsole(args);
                //Toast.makeText(getApplicationContext(), url, Toast.LENGTH_SHORT).show();

            } else if (act.equals("close")) {
                finish();
            } else if (act.equals("installqpylib")) {
                //
            } else if (act.equals("launchsrv")) {
                String script = getIntent().getStringExtra(PythonActivity.EXTRA_CONTENT_URL4);
                if (script != null && !script.equals("")) {
                    launchScript = script;
                }
            }
        }
    };


}
