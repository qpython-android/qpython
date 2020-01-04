package org.qpython.qpy.utils;

import android.support.v7.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.quseit.base.QBaseApp;
import com.quseit.config.BASE_CONF;
import com.quseit.common.db.AppLog;
import com.quseit.common.db.CacheLog;
import com.quseit.common.db.UserLog;
import com.quseit.util.DateTimeHelper;
import com.quseit.util.FileHelper;
import com.quseit.util.NAction;
import com.quseit.util.NUtil;
import com.quseit.util.VeDate;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.qpython.qpy.R;
import org.qpython.qpy.main.app.App;
import org.qpython.qpy.main.server.model.SettingModel;
import org.qpython.qpysdk.QPyConstants;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class UpdateHelper {

    private static final String TAG = "UpdateHelper";

    private static void checkConfUpdate(final Context context) {
        if (NUtil.netCheckin(context.getApplicationContext())) {
            // clear db cache
            CacheLog cDB = new CacheLog(context);
            cDB.cleanCache();

            RequestParams myParam = new RequestParams();
            String types = "11,12,13";
            String limit = BASE_CONF.LOG_LIMIT;
            UserLog userLog = new UserLog(context.getApplicationContext());
            String xlogs = "";
            try {
                xlogs = userLog.getLogs(types, 0, limit, "ASC");
            } catch (OutOfMemoryError e) {
                Log.d(TAG, "err when getLogs:" + e.getMessage());
                xlogs = "";
                userLog.deleteAllStat_0_Log();
            }
            final String logs = xlogs;
            myParam.put("time", DateTimeHelper.getDateMin());
            myParam.put("logs", logs);

			/* 手机客户信息 */
            String collectInfos = BASE_CONF.COLLECT_INFO;

            Field[] fields = Build.class.getDeclaredFields();
            for (Field field : fields) {
                try {
                    field.setAccessible(true);
                    String k = field.getName();
                    if (k != null) {
                        k = k.toLowerCase().trim();
                    }
                    if (collectInfos.contains("#" + k + "#")) {
                        myParam.put(k, field.get(null).toString());
                    }

                } catch (Exception e) {
                    Log.e(TAG, "Error occured when collect crash info", e);
                }
            }

            if (BASE_CONF.DEBUG)
                Log.d(TAG, "checkUpdate:" + logs);
            String updateUrl = NAction.getUpdateHost(context.getApplicationContext());
            if (updateUrl.equals("")) {
                updateUrl = confGetUpdateURL(context, 1);
            }
            if (!BASE_CONF.DEBUG)
                Log.d(TAG, "checkUpdate:" + updateUrl + "?" + NAction.getUserUrl(context.getApplicationContext()));

            QBaseApp.getInstance().getAsyncHttpClient().post(context.getApplicationContext(), updateUrl + "?" + NAction.getUserUrl(context.getApplicationContext()),
                    myParam, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject result) {
                            if (result == null) {
                                return;
                            }
                            if (BASE_CONF.DEBUG)
                                Log.d(TAG, "checkUpdate-result:" + result.toString());
                            UserLog log = new UserLog(context);
                            log.deleteAllStat_0_Log();

                            Gson gson = App.getGson();
                            SettingModel settings = gson.fromJson(result.toString(), SettingModel.class);
                            try {
                                NAction.setExtConf(context, gson.toJson(settings.getQpy().getExt2()));
                                NAction.setExtAdConf(context, gson.toJson(settings.getQpy().getExt_ad()));
                                NAction.setUpdateCheckTime(context);
                            } catch (NullPointerException e) {
                                //Toast.makeText(context, R.string.get_setting_data_fail+"(e)"+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            // waitingWindow.dismiss();
                            Log.d(TAG, "Error in checkConfUpdate:" + throwable.getMessage());
                        }
                    });
        }
    }

    private static String confGetUpdateURL(Context context, int flag) {
        if (flag == 2) {
            return QPyConstants.LOG_URL + context.getPackageName() + "/" + NUtil.getVersinoCode(context);
        } else if (flag == 3) {
            return QPyConstants.AD_URL + context.getPackageName() + "/" + NUtil.getVersinoCode(context) + "?"
                    + NAction.getUserUrl(context.getApplicationContext());
        } else if (flag == 5) {
            return QPyConstants.IAP_LOG_URL + context.getPackageName() + "/" + NUtil.getVersinoCode(context) + "?"
                    + NAction.getUserUrl(context.getApplicationContext());

        } else {
            return QPyConstants.UPDATE_URL + context.getPackageName() + "/" + NUtil.getVersinoCode(context);

        }
    }

    public static void checkConfUpdate(Context context, String root) {
        int now = VeDate.getStringDateHourAsInt();
        int lastCheck = NAction.getUpdateCheckTime(context);
        PackageInfo pInfo = null;
        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if ((pInfo == null ? 0 : pInfo.versionCode) < BASE_CONF.UPDATE_VER) {
            NAction.setUpdateHost(context, "");
            checkConfUpdate(context.getApplicationContext());

        } else {
            if (!sendCrashLog(context.getApplicationContext())) {

                int q = NAction.getUpdateQ(context.getApplicationContext());
                if (q == 0) {
                    q = BASE_CONF.UPDATEQ;
                }

                if ((now - lastCheck) >= q) { // 每q小时检查一次更新/清空一下不必要的cache
                    checkUpdate(context, true);

                    checkConfUpdate(context.getApplicationContext());

                    // 清空图片目录的缓存
                    String cacheDir = Environment.getExternalStorageDirectory() + "/" + root + "/" + BASE_CONF.DCACHE + "/";
                    FileHelper.clearDir(cacheDir, 0, false);

                }
            }
        }

    }

    private static boolean sendCrashLog(final Context context) {
        if (NUtil.netCheckin(context.getApplicationContext())) {
            Log.d(TAG, "notifyErr");

            RequestParams myParam = new RequestParams();
            //String updateUrl = NAction.getUpdateHost(context.getApplicationContext());
            //if (updateUrl.equals("")) {
            String   updateUrl = confGetUpdateURL(context, 2);
            //}
//            else {
//                updateUrl = NAction.getExtP(context, "conf_send_log_host");
//            }

            String collectInfos = NAction.getExtP(context, "conf_get_log_cls");
            if (collectInfos.equals("")) {
                collectInfos = BASE_CONF.COLLECT_INFO;
            }
            Field[] fields = Build.class.getDeclaredFields();
            for (Field field : fields) {
                try {
                    field.setAccessible(true);
                    String k = field.getName();
                    if (k != null) {
                        k = k.toLowerCase().trim();
                    }
                    if (collectInfos.contains("#" + k + "#")) {
                        myParam.put(k, field.get(null).toString());
                    }
                    // LogUtil.d(TAG, field.getName() + " : " + field.get(null));

                } catch (Exception e) {
                    Log.e(TAG, "an error occured when collect crash info", e);
                }
            }

            AppLog appDB = new AppLog(context);
            final ArrayList<String[]> logs = appDB.getLogs(0);
            if (logs.size() != 0) {
                JSONArray descArray = new JSONArray();
                // LogUtil.d(TAG, "notifyErr:"+logs);
                for (int i = 0; i < logs.size(); i++) {
                    String[] items = logs.get(i);
                    myParam.put("act", "report_err");
                    descArray.put(items[5]);

					/*
                     * myParam.put("report_id", items[0]); myParam.put("report_title", items[1]);
					 * myParam.put("report_ver", items[2]); myParam.put("report_time", items[3]);
					 * myParam.put("report_stat", items[4]); myParam.put("report_desc", items[5]);
					 * myParam.put("report_userno", items[6]); myParam.put("report_id", items[0]);
					 * myParam.put("report_title", items[1]); myParam.put("report_ver", items[2]);
					 * myParam.put("report_time", items[3]); myParam.put("report_stat", items[4]);
					 * myParam.put("report_desc", items[5]); myParam.put("report_userno", items[6]);
					 */

                    appDB.deleteLog(Long.parseLong(items[0]));
                }
                myParam.put("report_desc", descArray.toString());

                new AsyncHttpClient().post(context.getApplicationContext(), updateUrl + "?" + NAction.getUserUrl(context.getApplicationContext()),
                        myParam, new JsonHttpResponseHandler() { });
                return true;
            } else {
                // LogUtil.d(TAG, "notifyErr no need");
                return false;
            }
        } else {
            return false;
        }
    }

    private static void checkUpdate(final Context context, final boolean isAuto) {
        QBaseApp.getInstance().getAsyncHttpClient().post(context, BASE_CONF.UPDATER_URL, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                int ver = 0;
                String downloadUrl = "";
                String verName = "";
                String desc = "";

                int lastPluginVer = 0;
                int currentPluginVer = 0;
                String jsonPlugin = NAction.getExtPluginsConf(context);
                try {
                    JSONObject app = response.getJSONObject(BASE_CONF.APP_KEY);
                    ver = app.getInt(BASE_CONF.VERSION_KEY);
                    verName = app.getString(BASE_CONF.VERSION_NAME_KEY);
                    downloadUrl = app.getString(BASE_CONF.DOWNLOAD_LINK_KEY);
                    desc = app.getString(BASE_CONF.VERIOSN_DESC_KEY);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    if ("".equals(jsonPlugin)) {
                        lastPluginVer = BASE_CONF.plugin_ver;
                    } else {
                        lastPluginVer = new JSONObject(jsonPlugin).getJSONObject(BASE_CONF.APP_KEY).getInt(BASE_CONF.VERSION_PLUGIN_KEY);
                    }
                    currentPluginVer = response.getJSONObject(BASE_CONF.APP_KEY).getInt(BASE_CONF.VERSION_PLUGIN_KEY);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (lastPluginVer < currentPluginVer || NUtil.getVersinoCode(context.getApplicationContext()) < ver) {
                    //弹出提示更新
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    final String finalDownloadUrl = downloadUrl;
                    final int finalVer = ver;
                    final int finalLastPluginVer = lastPluginVer;
                    final int finalCurrentPluginVer = currentPluginVer;
                    alert.setTitle(com.quseit.android.R.string.up_soft_state_found)
                            .setMessage(verName + "\n" + desc)
                            .setPositiveButton(context.getString(com.quseit.android.R.string.up_soft), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    if (NUtil.getVersinoCode(context.getApplicationContext()) < finalVer) {
                                        //app更新
                                        Intent intent = new Intent();
                                        intent.setAction("android.intent.action.VIEW");
                                        Uri content_url = Uri.parse(finalDownloadUrl);
                                        intent.setData(content_url);
                                        context.startActivity(intent);
                                    }

                                    if (finalLastPluginVer < finalCurrentPluginVer) {
                                        //插件更新
                                        JSONArray plugins = null;
                                        try {
                                            plugins = response.getJSONArray(BASE_CONF.PLUGIN_KEY);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        for (int i = 0; i < plugins.length(); i++) {
                                            JSONObject plugin = null;
                                            try {
                                                plugin = plugins.getJSONObject(i);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            if (!(plugin == null)) {
                                                try {
                                                    final String dst = plugin.getString("dst");
                                                    final String link = plugin.getString("link");
                                                    new Thread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            boolean ret = FileHelper.getUrlAsFile(link, context.getApplicationContext().getFilesDir() + "/" + dst);
                                                            if (ret) {
//                                                                runOnUiThread(new Runnable() {
//                                                                    @Override
//                                                                    public void run() {
//                                                                        Toast.makeText(context, "插件已更新，请重启app", Toast.LENGTH_SHORT).show();
//                                                                    }
//                                                                });
                                                            } else {
//                                                                runOnUiThread(new Runnable() {
//                                                                    @Override
//                                                                    public void run() {
//                                                                        Toast.makeText(context, "插件更新失败", Toast.LENGTH_SHORT).show();
//                                                                    }
//                                                                });
                                                            }
                                                        }
                                                    }).start();
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                        NAction.setExtPluginsConf(context, response.toString());
                                    }
                                }
                            }).setNegativeButton(context.getString(com.quseit.android.R.string.promote_cancel), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alert.create().show();
                } else {
                    if (!isAuto) {
//                        closeWaitWindow();
//                        alertUpdateDialog2(verName);
                    }

                }
            }
        });

    }

    /*

        key: 16 bit string
        param: 64 bit string
     */
    public static void submitIAPLog(final Context context, String key, String param) {
        RequestParams myParam = new RequestParams();
        myParam.put("idt", key);
        myParam.put("param", param);
        new AsyncHttpClient().post(context, confGetUpdateURL(context, 5),
                myParam, new JsonHttpResponseHandler() { }
        );
    }
}
