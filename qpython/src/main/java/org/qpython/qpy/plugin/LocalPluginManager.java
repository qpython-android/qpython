package org.qpython.qpy.plugin;

import android.widget.Toast;

import org.qpython.qpy.R;
import org.qpython.qpy.main.app.App;
import org.qpython.qpy.plugin.model.LocalPluginBean;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import io.realm.Realm;
import io.realm.RealmResults;
import org.qpython.qpy.utils.FileUtils;

public class LocalPluginManager {
    private static final String INSTALL_DIR = App.getContext().getFilesDir() + "/user";

    public static boolean install(File plugin) {
        Realm realm = null;
        try {
            LocalPluginBean pluginInfo = checkPluginValid(plugin);
            File dest = new File(getCompletePath(pluginInfo));
            dest.getParentFile().mkdirs();
            dest.createNewFile();
            FileUtils.copyFile(plugin, dest);
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(pluginInfo);
            realm.commitTransaction();

            Toast.makeText(App.getContext(), R.string.toast_plugin_install_complete, Toast.LENGTH_SHORT).show();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(App.getContext(), R.string.toast_plugin_install_failure, Toast.LENGTH_SHORT).show();
            return false;
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
    }

    public static void uninstall(LocalPluginBean plugin) {
        Realm realm = null;
        try {
            File file = new File(getCompletePath(plugin));
            if (file.exists()) {
                file.delete();
            }

            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.where(LocalPluginBean.class)
                    .equalTo("name", plugin.getName())
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

    public static RealmResults<LocalPluginBean> queryAll(Realm realm) {
        return realm.where(LocalPluginBean.class).findAll();
    }

    public static String getCompletePath(LocalPluginBean pluginInfo) {
        return INSTALL_DIR + "/" + pluginInfo.getName();
    }

    private static LocalPluginBean checkPluginValid(File file) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String head = reader.readLine();

            if (!head.startsWith("#tbplugin")) {
                throw new IllegalStateException(file.getName() + " is not a valid plugin");
            }

            String[] info = head.split("\\|\\|");
            if (info.length != 3) {
                throw new IllegalStateException(file.getName() + " is not a valid plugin");
            }

            return new LocalPluginBean(file.getName(), info[1], info[2]);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
