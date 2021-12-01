package org.qpython.qpy.main.event;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.qpython.qpy.R;
import org.qpython.qpy.main.activity.NotebookActivity;
import org.qpython.qpy.main.activity.PurchaseActivity;
import org.qpython.qpy.main.activity.QWebViewActivity;
import org.qpython.qpy.main.activity.SettingActivity;
import org.qpython.qpy.main.app.App;
import org.qpython.qpy.main.app.CONF;
import org.qpython.qpy.main.utils.Utils;
import org.qpython.qpy.texteditor.EditorActivity;

import com.quseit.util.FileHelper;
import com.quseit.util.FileUtils;

import org.qpython.qpy.utils.NotebookUtil;
import org.qpython.qpysdk.QPyConstants;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.net.URL;

public class Bean {
    private final String TAG = "BEAN";
    protected String  title;
    protected Context context;
    protected WebView webview;
    protected int     dialogIndex;
    protected String  srv;

    public Bean(Context context, WebView webview) {
        this.context = context;
        this.webview = webview;
    }

    @JavascriptInterface
    public String getSrv() {
        return this.srv;
    }

    @JavascriptInterface
    public void setSrv(String srv) {
        this.srv = srv;
    }

    @JavascriptInterface
    public void close() {
        Intent intent1 = new Intent(".QWebViewActivity");
        intent1.putExtra(QWebViewActivity.ACT, "close");
        context.sendBroadcast(intent1);
    }

    @JavascriptInterface
    public void alert(String info) {
        Intent intent1 = new Intent(".QWebViewActivity");
        intent1.putExtra(QWebViewActivity.ACT, "alert");
        intent1.putExtra(QWebViewActivity.URL, info);

        context.sendBroadcast(intent1);
    }

    @JavascriptInterface
    public void showWait() {
        Intent intent1 = new Intent(".QWebViewActivity");
        intent1.putExtra(QWebViewActivity.ACT, "showwait");
        context.sendBroadcast(intent1);
    }

    @JavascriptInterface
    public void closeWait() {
        Intent intent1 = new Intent(".QWebViewActivity");
        intent1.putExtra(QWebViewActivity.ACT, "closewait");
        context.sendBroadcast(intent1);
    }

    @JavascriptInterface
    public void pipConsole() {
        Intent intent1 = new Intent(".QWebViewActivity");
        intent1.putExtra(QWebViewActivity.ACT, "pipconsole");
        context.sendBroadcast(intent1);
    }

    @JavascriptInterface
    public void pipInstall(String link, String src) {
        Intent intent1 = new Intent(".QWebViewActivity");
        intent1.putExtra(QWebViewActivity.ACT, "pipinstall");
        intent1.putExtra(QWebViewActivity.URL, link);
        intent1.putExtra(QWebViewActivity.SRC, src);
        context.sendBroadcast(intent1);
    }

    @JavascriptInterface
    public void libMan() {
        Intent intent1 = new Intent(".QWebViewActivity");
        intent1.putExtra(QWebViewActivity.ACT, "libman");

        context.sendBroadcast(intent1);
    }

    @JavascriptInterface
    public void showDrawerMenu(String menuPythonActivity) {
        Intent intent1 = new Intent(".QWebViewActivity");
        intent1.putExtra(QWebViewActivity.ACT, "showdrawermenu");
        intent1.putExtra(QWebViewActivity.URL, menuPythonActivity);
        context.sendBroadcast(intent1);
    }

    @JavascriptInterface
    public boolean isSrvOk(String srv) {
        try {
            URL u = new URL(srv.equals("") ? this.srv : srv);
            int port = 80;
            if (u.getPort() != -1) {
                port = u.getPort();
            }
            String url = u.getProtocol() + "://" + u.getHost() + ":" + port + "/";
            boolean ret = Utils.httpPing(url, 3000);
            Log.d("Bean", "isSrvOk:"+url+" - "+ret);
            return ret;

        } catch (MalformedURLException e) {
            //LogUtil.d("Bean", "MalformedURLException:"+e);
            return false;
        }
    }

    @JavascriptInterface
    public void openUrl(String url) {
        Intent intent = Utils.openRemoteLink(context, url);
        context.startActivity(intent);
    }

    @JavascriptInterface
    public void call(String number) {
        Intent phoneIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + number));
        context.startActivity(phoneIntent);
    }

    @JavascriptInterface
    public String isNetworkOk(Context context) {
        if (Utils.netOk(context)) {
            return "1";
        } else {
            return "0";
        }
    }

    @JavascriptInterface
    public String returnTmpScript(String xcode, String flag, String param) {
        try {
            //
            File root = new File(FileUtils.getLibDownloadTempPath(App.getContext()));
            if (root != null) {
                FileHelper.clearDir(root.toString(), 0, false);
            }

            //String py = FileHelper.getBasePath(QBaseApp.getInstance().getRoot(), PythonActivity.DFROM_QPY)+"/"+PythonActivity.API_SCRIPT;
            String py;
            String code;
            if (flag.equals("qedit")) {
                if (param != null && !param.equals("")) {
                    File f = new File(param);
                    py = new File(f.getParentFile(), ".last_tmp.py").toString();
                } else {
                    py = new File(root, ".last_tmp.py").toString();
                }
                if (xcode.contains("#{HEADER}")) {
                    code = xcode.replace("#{HEADER}", "");
                } else {
                    code = xcode;
                }

            } else {
                py = FileUtils.getAbsolutePath(App.getContext()) + "/cache/main.py";
                if (xcode.contains("#{HEADER}")) {
                    code = xcode.replace("#{HEADER}", "PARAM = '" + param + "'");

                } else {
                    code = "PARAM = '" + param + "'\n" + xcode;
                }
            }
            //LogUtil.d(TAG, "py:"+py);
            File pyCache = new File(py);
            if (!pyCache.exists()) {
                pyCache.createNewFile();
            }
            byte[] xcontent = code.getBytes();
            RandomAccessFile accessFile = new RandomAccessFile(pyCache.getAbsoluteFile(), "rwd");
            accessFile.setLength(xcontent.length);
            accessFile.seek(0);
            accessFile.write(xcontent, 0, xcontent.length);
            accessFile.close();

            return py;
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    @JavascriptInterface
    public void qeditor(String content) {

        String py = returnTmpScript(content, "qedit", null);
        Uri uri = Uri.fromFile(new File(py));
        EditorActivity.start(context, uri);
    }

    @JavascriptInterface
    public String qpyChecklibinstall(String cat, String smodule) {
        final File libFile = getLibFile(smodule, cat);

        if (libFile.exists()) {
            return "1";
        } else {
            return "0";
        }
    }

    @JavascriptInterface
    public File getLibFile(String smodule, String cat) {
        //String code = NAction.getCode(this);
        boolean isQpy3 = Utils.isQPy3(context);
        String base = isQpy3 ? "python3.2/site-packages" : "python2.7/site-packages";
        String sbase = isQpy3 ? "python3.2" : "python2.7";
        String ubase = isQpy3 ? "scripts3" : "scripts";
        String pbase = isQpy3 ? "projects3" : "projects";

        File libFile;
        if (cat.equals("script")) {
            libFile = new File(FileUtils.getPath(App.getContext()), "qpython/" + ubase + "/" + smodule);

        } else if (cat.equals("user")) {
            libFile = new File(FileUtils.getPath(App.getContext()), "qpython/" + pbase + "/" + smodule);

        } else if (cat.equals("component")) {
            libFile = new File(FileUtils.getPath(App.getContext()), "qpython/lib/" + smodule);

        } else {
            libFile = new File(context.getFilesDir(), "/lib/" + sbase + "/site-packages/" + smodule);


        }
        return libFile;
    }

    @JavascriptInterface
    public void setQBTitle(String title) {
        Intent intent1 = new Intent(".QWebViewActivity");
        intent1.putExtra(QWebViewActivity.ACT, "settitle");
        intent1.putExtra(QWebViewActivity.URL, title);
        context.sendBroadcast(intent1);

    }

    @JavascriptInterface
    public void showLog() {
        Intent intent2 = new Intent(".QWebViewActivity");
        intent2.putExtra(QWebViewActivity.ACT, "notifylog");
        intent2.putExtra(QWebViewActivity.URL, "");

        context.sendBroadcast(intent2);
    }

    @JavascriptInterface
    public void feedback(String logfile) {
        Intent intent2 = new Intent(".QWebViewActivity");
        intent2.putExtra(QWebViewActivity.ACT, "feedback");
        intent2.putExtra(QWebViewActivity.URL, logfile);

        context.sendBroadcast(intent2);
    }

    @JavascriptInterface
    public void loadConsole(String script) {
        //LogUtil.d(TAG, "loadConsole:"+script);
        Intent intent1 = new Intent(".QWebViewActivity");
        intent1.putExtra(QWebViewActivity.ACT, "launchsrv");
        intent1.putExtra(QWebViewActivity.URL, script);
        context.sendBroadcast(intent1);

    }

    @JavascriptInterface
    public void setActivityTitle(String title) {
    }

    @JavascriptInterface
    public String getTitle() {
        return this.title;
    }

    @JavascriptInterface
    public void setTitle(String title) {
        this.title = title;
    }

    @JavascriptInterface
    public void openPurchaseActivity(String articleID) {
        PurchaseActivity.start(context, articleID);
    }

    @JavascriptInterface
    public String getPurchaseNumber(String articleID) {
        JSONObject ret = FileHelper.getUrlAsJO(CONF.IAP_NUM_REQUEST_URL+articleID);
        if (ret!=null) {
            try {
                int total = ret.getInt("number");
                if (total>1000) {
                    return "> 5,000";
                } else if (total>500) {
                    return "1000 - 5000";
                } else if (total>100) {
                    return "500 - 1000";
                } else if (total>50) {
                    return "100 - 500";
                } else if (total>20) {
                    return "50 - 100";
                } else if (total>5) {
                    return "10 - 50";
                } else {
                    return "1 - 10";
                }
            } catch (JSONException e) {
                return "1 - 10";
            }
        } else {
            return "1 - 10";
        }
    }

    @JavascriptInterface
    public void qpynotebook(){
        boolean notebookenable = NotebookUtil.isNotebookEnable(context);
        if (notebookenable) {
            Toast.makeText(context, R.string.ennable_notebook_first, Toast.LENGTH_SHORT).show();
            return;
        }
        NotebookActivity.start(context,null, false);
    }

    @JavascriptInterface
    public void onNext(String act) {
        Log.d(TAG, "onNext");
        Intent intent1 = new Intent(".QWebViewActivity");
        intent1.putExtra(QWebViewActivity.ACT, "onnext");
        intent1.putExtra(QWebViewActivity.URL, act);
        context.sendBroadcast(intent1);
    }

    @JavascriptInterface
    public void openNotebook(String url){
        Log.d(TAG, "openNotebook:"+url);
        boolean notebookenable = NotebookUtil.isNotebookEnable(context);
        if (!notebookenable) {
            Toast.makeText(context, R.string.ennable_notebook_first, Toast.LENGTH_SHORT).show();
            SettingActivity.startActivity(context);
            return;
        }

        NotebookActivity.startFromUrl(context,url, false);
    }
    // for debug
    @JavascriptInterface
    public void loadHtml(String data) {
        Log.d(TAG, "loadHtml");
        Intent intent1 = new Intent(".QWebViewActivity");
        intent1.putExtra(QWebViewActivity.ACT, "loadhtml");
        intent1.putExtra(QWebViewActivity.HTML, data);

        context.sendBroadcast(intent1);

        //this.webview.loadDataWithBaseURL("file:///android_asset/", data, mimeType, encoding, null);
    }

}
