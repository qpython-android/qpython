package org.qpython.qpy.main.app;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Process;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.gson.Gson;
import com.gyf.cactus.Cactus;
import com.gyf.cactus.callback.CactusCallback;
import com.huawei.agconnect.common.network.AccessNetworkManager;
import com.huawei.hmf.tasks.OnCompleteListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.aaid.HmsInstanceId;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.push.HmsMessaging;
import com.quseit.common.CrashHandler;
import com.quseit.common.updater.downloader.DefaultDownloader;
import com.quseit.util.FileUtils;
import com.squareup.leakcanary.LeakCanary;
import com.xiaomi.channel.commonutils.logger.LoggerInterface;
import com.xiaomi.mipush.sdk.Logger;
import com.xiaomi.mipush.sdk.MiPushClient;

import org.qpython.qpy.R;
import org.qpython.qpy.codeshare.CONSTANT;
import org.qpython.qpy.main.activity.HomeMainActivity;
import org.qpython.qpy.main.server.Service;
import org.qpython.qpy.main.server.gist.Api;
import org.qpython.qpy.main.server.gist.TokenManager;
import org.qpython.qpy.main.server.gist.response.GistBean;
import org.qpython.qpy.main.server.http.Retrofitor;
import org.qpython.qpy.utils.BrandUtil;
import org.qpython.qpy.utils.JumpToUtils;
import org.qpython.qpy.utils.NotebookUtil;
import org.qpython.qpysdk.QPyConstants;
import org.qpython.qsl4a.QPyScriptService;
import org.qpython.qsl4a.QSL4APP;
import org.qpython.qsl4a.qsl4a.LogUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class App extends QSL4APP implements CactusCallback{

    private static final String TAG = "MyApplication";
    public static App appInstance;
    private static Context sContext;
    private static String sScriptPath;
    private static String sProjectPath;
    //private static AppCompatActivity sActivity;

    private static OkHttpClient okHttpClient;
    private static HttpLoggingInterceptor interceptor;
    private static Gson gson;

    private static DefaultDownloader downloader;

    //保存user信息
    private static SharedPreferences mPreferences;

    private static Retrofit.Builder retrofitBuilder;

    private static Service mService;//本地retrofit方法

    public static OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }

    public static HttpLoggingInterceptor getInterceptor() {
        return interceptor;
    }

    public static Gson getGson() {
        return gson;
    }

    public static Retrofit.Builder getRetrofit() {
        return retrofitBuilder;
    }

    public static Service getService() {
        return mService;
    }

    public static String getScriptPath() {
        return sScriptPath;
    }

    public static String getProjectPath() {
        return sProjectPath;
    }

    public static Context getContext() {
        return sContext;
    }

    //    public static AppCompatActivity getActivity() {
//        return sActivity;
//    }
//
//    public static void setActivity(AppCompatActivity activity) {
//        sActivity = activity;
//    }
    public static User getUser() {
        if (mPreferences.getString("email", null) == null) {
            return null;
        }
        User user = new User();
        user.setNick(mPreferences.getString("nick", ""));
        user.setUserName(mPreferences.getString("name", ""));
        user.setUserId(mPreferences.getString("id", ""));
        user.setEmail(mPreferences.getString("email", ""));
        user.setAvatarUrl(mPreferences.getString("avatar", ""));
        return user;
    }

    public static boolean getAgreementStatus(){
        return  mPreferences.getBoolean("user_agree_status",false);
    }

    public static void setAgreementStatus(boolean status){
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean("user_agree_status",status);
        if (!editor.commit()) {
            editor.apply();
        }
    }

    public static DefaultDownloader getDownloader() {
        return downloader;
    }

    public static void setUser(User user) {
        SharedPreferences.Editor editor = mPreferences.edit();
        if (user == null) {
            editor.clear();
            editor.apply();
            return;
        }
        editor.putString("nick", user.getNick());
        editor.putString("name", user.getUserName());
        editor.putString("email", user.getEmail());
        editor.putString("avatar", user.getAvatarUrl());
        editor.putString("id", user.getUserId());
        if (!editor.commit()) {
            editor.apply();
        }

    }

    private static List<String> favorites = new ArrayList<>();

    public static List<String> getFavorites() {
        return favorites;
    }

    public static void addFavorites(GistBean bean) {
        favorites.add(bean.getId());
    }

    public static void setFavorites(List<GistBean> list) {
        for (GistBean bean : list) {
            favorites.add(bean.getId());
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static void initLibs(App app) {
        app.initConfiguration();
        if (LeakCanary.isInAnalyzerProcess(app)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(app);

        sContext = app.getApplicationContext();
        downloader = new DefaultDownloader(sContext);
        // init retrofit relate
        gson = new Gson();
        interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);

        int cacheSize = 10 * 1024 * 1024; // 10 MiB
        Cache cache = new Cache(App.getContext().getCacheDir(), cacheSize);

        okHttpClient = new OkHttpClient.Builder().cache(cache).addInterceptor(interceptor).build();
        retrofitBuilder = new Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create());
        mService = new Service();

        String basePath = FileUtils.getAbsolutePath(app.getApplicationContext());
        sProjectPath = String.format("%s/%s", basePath, "projects");
        sScriptPath = String.format("%s/%s", basePath, "scripts");

        app.initLayoutDir();
        CrashHandler.getInstance().init(app);
        AppInit.init(app);

        Map<String, String> header = new HashMap<>();
        TokenManager.init(app);
        header.put("Content-Type", "application/json");
//        if (!TextUtils.isEmpty(TokenManager.getToken())) {
//            header.put("HTTP_TOKEN", TokenManager.getToken());
//        }
        Retrofitor
                .getInstance()
                .setTimeOut(Retrofitor.DEFAULT_TIMEOUT)
//                .openDebug(BuildConfig.DEBUG)
                //              .supportSSL(!BuildConfig.DEBUG)
                .addHeaders(header)
                .init(Api.BASE_URL);

//        if (NotebookUtil.isNotebookLibInstall(getActivity())) {
//            if (Utils.httpPing(NotebookUtil.NB_SERVER, 300)) {
//                NotebookUtil.startNotebookService(getActivity());
//            }
//        }

        // restart Notebook
        if (NotebookUtil.isNBSrvSet(app)) {

            NotebookUtil.killNBSrv(app);
            NotebookUtil.startNotebookService2(app);
        }

        app.initCactus();

        initPush(app);
        initAnalytics(app);
    }

    /**
     * 初始化分析服务
     * @param context
     */
    private static void initAnalytics(Context context) {
        // todo 暂时注释起来，不启动华为分析服务，因与下载器冲突导致问题
//        AccessNetworkManager.getInstance().setAccessNetwork(true);
    }

    /**
     * 初始化推送
     */
    private static void initPush(Context context) {
        if(BrandUtil.isBrandHuawei()) {
            // 华为通道设置自动初始化
            HmsMessaging.getInstance(context).setAutoInitEnabled(true);
            Log.d(TAG, "Init Push:Huawei");

            try {
                // 主题订阅
                HmsMessaging.getInstance(context).subscribe(JumpToUtils.EXTRA_TOPIC)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(Task<Void> task) {
                                // 获取主题订阅的结果
                                if (task.isSuccessful()) {
                                    Log.i(TAG, "subscribe topic successfully");
                                } else {
                                    Log.e(TAG, "subscribe topic failed, return value is " + task.getException().getMessage());
                                }
                            }
                        });
            } catch (Exception e) {
                Log.e(TAG, "subscribe failed, catch exception : " + e.getMessage());
            }
        } else if(BrandUtil.isBrandXiaoMi()) {
            //初始化小米push推送服务
            if(shouldInit(context)) {
                MiPushClient.registerPush(context, CONSTANT.MI_PUSH_APP_ID, CONSTANT.MI_PUSH_APP_KEY);
            }
            //打开Log
            LoggerInterface newLogger = new LoggerInterface() {
                @Override
                public void setTag(String tag) {
                    // ignore
                }

                @Override
                public void log(String content, Throwable t) {
                    Log.d(TAG, content, t);
                }

                @Override
                public void log(String content) {
                    Log.d(TAG, content);
                }
            };
            Logger.setLogger(context, newLogger);
        }
    }

    /**
     * 小米通知，判断是否需要初始化
     * @return
     */
    private static boolean shouldInit(Context context) {
        ActivityManager am = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = context.getApplicationInfo().processName;
        int myPid = Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appInstance = this;
        mPreferences = getSharedPreferences("user", 0);
        if (App.getAgreementStatus()) {
            initLibs(this);
        }
    }

    private void initCactus() {
        boolean isKeepAlive = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getString(R.string.key_alive), false);
        LogUtil.e("isKeepAlive:" + isKeepAlive);
        if (!isKeepAlive){
            LogUtil.e("doWork0000000");
            if (!isRunService( "org.qpython.qsl4a.QPyScriptService")) {
                startPyService();
            }
            return;
        }
        Cactus.getInstance()
                .isDebug(true)
                .setTitle("QPython")
                .setContent("QPython service is alive")
                .setLargeIcon(R.drawable.ic_launcher)
                .setSmallIcon(R.drawable.ic_launcher)
                .hideNotificationAfterO(false)
                .addCallback(this)
                .register(this);
    }

    private void initLayoutDir() {
        if (Build.VERSION.SDK_INT >= 17) {
            Resources resources = getResources();
            Configuration config = resources.getConfiguration();
            Locale locale = config.locale;
            config.setLayoutDirection(locale);
            resources.updateConfiguration(config, resources.getDisplayMetrics());
        }
    }

    /**
     * 保活的回调接口
     * @param i
     */
    @Override
    public void doWork(int i) {
        LogUtil.e("doWork1111111");
        if (!isRunService( "org.qpython.qsl4a.QPyScriptService")) {
            startPyService();
        }
    }

    /**
     * 保活的回调接口
     */
    @Override
    public void onStop() {}

    private void startPyService() {
        LogUtil.e("doWork22222");
        Log.d(TAG, "startPyService");
        Intent intent = new Intent(this, QPyScriptService.class);
        startService(intent);
    }

    /**
     * 判断服务是否在运行
     *
     * @param serviceName
     * @return 服务名称为全路径 例如com.ghost.WidgetUpdateService
     */
    public boolean isRunService(String serviceName) {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceName.equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
