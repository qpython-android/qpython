package org.qpython.qpy.main.app;

import android.content.Context;
import android.os.Environment;
//import android.support.multidex.MultiDex;

import com.google.gson.Gson;
import com.quseit.common.updater.Updater;
import com.quseit.common.updater.updatepkg.Apk;
import com.quseit.common.updater.updatepkg.UpdatePackage;
import com.umeng.analytics.MobclickAgent;

import org.qpython.qpy.R;
import org.qpython.qpy.main.model.UpdateJson;
import org.qpython.qpy.plugin.model.CloudPluginBean;
import org.qpython.qsl4a.QSL4APP;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class App extends QSL4APP {
    private static String sBaseDir;
    private static Context sContext;
    private static String sScriptPath;
    private static String sProjectPath;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        //MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();

        MobclickAgent.setScenarioType(getApplicationContext(), MobclickAgent.EScenarioType.E_UM_NORMAL);


//        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(getApplicationContext()).build();
//        Realm.setDefaultConfiguration(realmConfiguration);

        Updater.init(this, "", response -> {
            List<UpdatePackage> list = new ArrayList<>();
            Gson gson = new Gson();
            UpdateJson json = gson.fromJson(response, UpdateJson.class);
            UpdateJson.AppBean bean = json.getApp();
            String name = getResources().getString(R.string.app_name);
            Apk apk = new Apk(name, bean.getVer_name(), Integer.valueOf(bean.getVer()), bean.getVer_desc(), bean.getLink());
            list.add(apk);
            for (UpdateJson.PluginsBean bean1 : json.getPlugins()) {
                CloudPluginBean cloudPluginInfo =
                        new CloudPluginBean(bean1.getPlugin()
                                , bean1.getTitle(),
                                bean1.getSrc(),
                                bean1.getVer(),
                                bean1.getVer_desc(),
                                bean1.getDesc(),
                                bean1.getDst(),
                                bean1.getLink());
                list.add(cloudPluginInfo);
            }
            return list;
        });

        String basePath = String.format("%s/%s", Environment.getExternalStorageDirectory().getAbsolutePath(),"qpython");
        sProjectPath = String.format("%s/%s", basePath, "projects");
        sScriptPath = String.format("%s/%s", basePath, "scripts");
    }

//    public static Context getContext() {
//        return sContext;
//    }
    public static String getScriptPath() {
        return sScriptPath;
    }

    public static String getProjectPath() {
        return sProjectPath;
    }

    public static Context getContext() {
        return sContext;
    }
}
