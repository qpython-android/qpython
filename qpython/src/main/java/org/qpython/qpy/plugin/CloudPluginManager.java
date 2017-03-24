package org.qpython.qpy.plugin;

import android.widget.Toast;

import com.google.gson.Gson;
import org.qpython.qpy.R;

import org.qpython.qpy.main.app.App;
import org.qpython.qpy.main.model.UpdateJson;
import org.qpython.qpy.plugin.model.CloudPluginBean;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import org.qpython.qpy.utils.FileUtils;

public class CloudPluginManager {
    private static final String INSTALL_DIR = App.getContext().getFilesDir().getAbsolutePath();
    private static final String URL = "http://tubebook.net/tubebook/conf/";
    private static OkHttpClient client = new OkHttpClient();

    public static boolean checkInstalled(CloudPluginBean plugin) {
        return query(plugin.getName()) != null;
    }

    public static boolean checkUpdate(CloudPluginBean remotePlugin) {
        CloudPluginBean localPlugin = query(remotePlugin.getName());
        return localPlugin != null && remotePlugin.getVersionCode() > localPlugin.getVersionCode();
    }

    public static void install(CloudPluginBean plugin, File file, boolean isSilence) {
        String destPath = getCompletePath(plugin);
        File dest = new File(destPath);

        Realm realm = null;
        try {
            dest.getParentFile().mkdirs();
            dest.createNewFile();
            FileUtils.copyFile(file, dest);
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(plugin);
            realm.commitTransaction();

            if (!isSilence) {
                Toast.makeText(App.getContext(), R.string.toast_plugin_install_complete, Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
    }

    private static CloudPluginBean query(String name) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            CloudPluginBean cloudPluginBean = realm.where(CloudPluginBean.class)
                    .equalTo("name", name)
                    .findFirst();

            if (cloudPluginBean == null) {
                return null;
            }

            File file = new File(getCompletePath(cloudPluginBean));
            if (!file.exists()) {
                realm.beginTransaction();
                cloudPluginBean.deleteFromRealm();
                realm.commitTransaction();
                return null;
            }

            // 创建不受 realm 监控的拷贝
            return realm.copyFromRealm(cloudPluginBean);
        } catch (Exception e) {
            return null;
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
    }

    public static RealmResults<CloudPluginBean> queryAll(Realm realm) {
        RealmResults<CloudPluginBean> plugins = realm.where(CloudPluginBean.class)
                .findAll();

        for (CloudPluginBean plugin : plugins) {
            File file = new File(getCompletePath(plugin));
            if (!file.exists()) {
                realm.beginTransaction();
                plugin.deleteFromRealm();
                realm.commitTransaction();
            }
        }

        return plugins;
    }

    public static void queryFromNet(Callback callback) {
        Observable
                .create(new Observable.OnSubscribe<String>() {
                    @Override
                    public void call(Subscriber<? super String> subscriber) {
                        Request request = new Request.Builder()
                                .url(URL)
                                .build();
                        try {
                            String s = client.newCall(request).execute().body().string();
                            subscriber.onNext(s);
                        } catch (IOException e) {
                            subscriber.onError(e);
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .map(s -> {
                    List<CloudPluginBean> plugins = new ArrayList<>();
                    Gson gson = new Gson();
                    UpdateJson json = gson.fromJson(s, UpdateJson.class);
                    for (UpdateJson.PluginsBean bean1 : json.getPlugins()) {
                        CloudPluginBean cloudPluginBean =
                                new CloudPluginBean(bean1.getPlugin(), bean1.getTitle(), bean1.getSrc(),
                                        bean1.getVer(), bean1.getVer_desc(), bean1.getDesc(), bean1.getDst(), bean1.getLink());
                        plugins.add(cloudPluginBean);
                    }
                    return plugins;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(plugins -> callback.result(plugins),
                        e -> e.printStackTrace());
    }

    public static long count() {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            return realm.where(CloudPluginBean.class)
                    .count();
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
    }

    public static void uninstall(CloudPluginBean cloudPluginBean) {
        CloudPluginBean local = query(cloudPluginBean.getName());
        if (local == null) {
            return;
        }
        Realm realm = null;
        try {
            // 清除本地文件
            File file = new File(getCompletePath(local));
            if (file.exists()) {
                file.delete();
            }

            // 清除数据库数据
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.where(CloudPluginBean.class)
                    .equalTo("name", local.getName())
                    .findFirst()
                    .deleteFromRealm();
            realm.commitTransaction();

            Toast.makeText(App.getContext(), R.string.toast_plugin_uninstall_complete, Toast.LENGTH_SHORT).show();
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
    }

    public static String getCompletePath(CloudPluginBean cloudPluginBean) {
        return String.format("%s%s%s", INSTALL_DIR, "/", cloudPluginBean.getPath());
    }

    public interface Callback {
        void result(List<CloudPluginBean> plugins);
    }
}
