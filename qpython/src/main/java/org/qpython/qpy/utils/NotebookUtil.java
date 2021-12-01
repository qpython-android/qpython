package org.qpython.qpy.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.quseit.base.QBaseApp;
import com.quseit.util.FileUtils;
import com.quseit.util.NAction;
import com.quseit.util.NStorage;
import com.quseit.util.NUtil;

import org.apache.http.Header;
import org.qpython.qpy.R;
import org.qpython.qpy.console.ScriptExec;
import org.qpython.qpy.main.app.App;
import org.qpython.qpysdk.QPyConstants;
import org.qpython.qpysdk.QPySDK;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 文 件 名: NotebookCheckUtil
 * 创 建 人: ZhangRonghua
 * 创建日期: 2018/2/8 09:35
 * 修改时间：
 * 修改备注：
 */

public class NotebookUtil {

    private static final String TAG = "NotebookUtil";
    public static final String RELEASE_PATH = FileUtils.getAbsolutePath(App.getContext()) + "/.notebook";
    public static final String NB_SERVER = "http://127.0.0.1:13000";
    public static final String KILL_SERVER = NB_SERVER + "/__exit";
    public static final String NOTEBOOK_SERVER = NB_SERVER + "/notebooks/";

    public static final String NOTEBOOK_DIR = FileUtils.getAbsolutePath(App.getContext())+"/";
    public static final String ext = ".ipynb";
    public static final String Untitled = "Untitled";

    public static boolean isNotebookEnable(Context context) {
        boolean isNLI = isNotebookLibInstall(context);
        Log.d("NotebookUtil", "isNotebookEnable:" + "-" + isNLI);
        return isNLI;
    }



    /**
     * 检查notebook库是否安装
     */
    public static boolean isNotebookLibInstall(Context context) {
        boolean common = new File(context.getFilesDir().getAbsolutePath() + "/lib/notebook.zip").exists();
        if (NAction.isQPy3(context)) {
            return common & new File(context.getFilesDir().getAbsoluteFile()+"/bin/jupyter").exists();
        } else {
            return common & new File(context.getFilesDir().getAbsoluteFile()+"/bin/jupyter2").exists();
        }
    }

    /**
     * 释放相关资源
     */
    public static boolean extraData(Context context) {
        //如果解压成功则lib目录下回出现libzmq.so文件
        if (new File(context.getFilesDir().getAbsolutePath() + "/lib", "notebook.zip").exists()) {
            //对bin目录下的文件修改执行权限
            File bind = new File(context.getFilesDir().getAbsolutePath() + "/bin");
            if (bind.listFiles() != null) {
                for (File bin : bind.listFiles()) {
                    try {
                        org.qpython.qpysdk.utils.FileUtils.chmod(bin, 0755);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            }
            if (context.getPackageName().equals("com.hipipal.qpyplus")) {  // old version
                // Rename correct files to
                String[] list = {"jupyter-chq", "jupyter2-chq", "jupyter-notebook-chq", "jupyter2-notebook-chq"};
                for (int i=0;i<list.length;i++) {
                    String f = list[i];
                    String t = f.replace("-chq","");

                    File ff = new File(context.getFilesDir().getAbsolutePath() + "/bin", f);
                    File tf = new File(context.getFilesDir().getAbsolutePath() + "/bin", t);
                    if (ff.exists()) {
                        ff.renameTo(tf);

                    }
                }
            }

        } else {
            return false;
        }
        return true;
    }



    /**
     * 创建一个的notebook
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static String createNotebook(Context context, String fileName) {
        InputStream is = null;
        FileOutputStream fileOutputStream = null;
        File file = new File(NOTEBOOK_DIR + "notebooks/", fileName + ext);
        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            fileOutputStream = new FileOutputStream(file);
            is = context.getAssets().open(Untitled + ext);

            int lenght = is.available();
            byte[] buffer = new byte[lenght];
            is.read(buffer);
            fileOutputStream.write(buffer);
            fileOutputStream.flush();
            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (is != null)
                    is.close();
                if (fileOutputStream != null)
                    fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 关闭后台服务
     */
    public static void killServer() {
        QBaseApp.getInstance().getAsyncHttpClient().get(KILL_SERVER, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {

            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

            }
        });
    }

    /**
     * 下载文件
     */
    public static void downloadFile(String url, DownloadCallback callback) {
        Log.d("NotebookUtil", "downloadFile:" + url);
        final Handler handler = new Handler(Looper.getMainLooper());
        OkHttpClient client = App.getOkHttpClient();
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                handler.post(() -> {
                    callback.onFail(e.getMessage());
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                FileOutputStream fos = null;
                byte[] buff = new byte[2048];
                int len = -1;
                try {
                    String filePath = getTempFilePath(url);
                    Log.d("NotebookUtil", "onResponse:downloadFile:" + filePath);
                    if (filePath == null) {
                        handler.post(() -> {
                            callback.onFail("Failed to download " + url);
                        });
                        return;
                    }
                    fos = new FileOutputStream(filePath);
                    is = response.body().byteStream();
                    while ((len = is.read(buff)) != -1) {
                        fos.write(buff, 0, len);
                    }
                    fos.flush();
                    handler.post(() -> {
                        callback.onSuccess(filePath);
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    handler.post(() -> {
                        callback.onFail(e.getMessage());
                    });
                } finally {
                    if (is != null) {
                        is.close();
                    }
                    if (fos != null) {
                        fos.close();
                    }
                }
            }
        });

    }

    private static String getTempFilePath(String url) {
        //LogUtil.d("NotebookUtil", "getTempFilePath:"+url);
        File dir = new File(FileUtils.getAbsolutePath(App.getContext()), "notebooks");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String fileName = NUtil.getFileFromUrl(url);
        File file = new File(dir, fileName);
        Log.d("NotebookUtil", "getTempFilePath:" + file);

        try {
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return file.getAbsolutePath();
    }

    public interface DownloadCallback {
        void onSuccess(String filePath);

        void onFail(String msg);
    }

    public static void startNotebookService2(Context context) {
        int notebookPid;
        if (NAction.isQPy3(context)) {
            notebookPid = ScriptExec.getInstance().playQScript(context, context.getFilesDir().getAbsolutePath() + "/bin/nb_man.py", null,false);

        } else {
            notebookPid = ScriptExec.getInstance().playQScript(context, context.getFilesDir().getAbsolutePath() + "/bin/nb2_man.py", null,false);
        }
        Log.d("NotebookUtil", "startNotebookService2:"+notebookPid);
        NStorage.setSP(context, "notebook.pid", ""+notebookPid);
    }

    public static void startNotebookService(Context context) {
        if (NAction.isQPy3(context)) {
            ScriptExec.getInstance().playDScript(context, context.getFilesDir().getAbsolutePath() + "/bin/nb_man.py", null,false);
        } else {
            ScriptExec.getInstance().playDScript(context, context.getFilesDir().getAbsolutePath() + "/bin/nb2_man.py", null,false);
        }
    }

    public static boolean isNBSrvSet(Context context) {
        boolean isset = getNBPid2(context) != -1;
        Log.d(TAG, "isNBSrvSet:"+(isset?"yes":"no"));
        return isset;
    }

    public static void killNBSrv(Context context) {
        int pid = getNBPid2(context);

        Log.d(TAG, "killNBSrv:"+pid);

        if (pid!=-1) {
            android.os.Process.killProcess(pid);
        }
        NStorage.setSP(context, "notebook.pid", "");

    }

    public static int getNBPid2(Context context) {
        String _pid = NStorage.getSP(context, "notebook.pid");
        if (!_pid.equals("")) {
            return Integer.valueOf(_pid);
        } else {
            return -1;
        }
    }

    public static String getNbResFk(Context context) {
        return NAction.isQPy3(context)?QPyConstants.KEY_NOTEBOOK_RES: QPyConstants.KEY_NOTEBOOK2_RES;
    }
    public static String getNBLink(Context context) {
        String nb_link = NAction.isQPy3(context)?"https://dl.qpy.io/notebook3.json":"https://dl.qpy.io/notebook2.json";
        return nb_link+"?"+NAction.getUserUrl(context);
    }

}
