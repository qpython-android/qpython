package org.qpython.qpy.codeshare;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.quseit.util.ACache;
import com.quseit.util.FileHelper;
import com.quseit.util.NetStateUtil;

import org.qpython.qpy.R;
import org.qpython.qpy.codeshare.pojo.CloudFile;
import org.qpython.qpy.codeshare.pojo.Gist;

import org.qpython.qpy.main.app.App;
import org.qpython.qpy.main.event.ShareCodeCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


import static org.qpython.qpy.main.server.CacheKey.CLOUD_FILE;

/**
 * FireBase database util
 * Created by Hmei on 2017-08-10.
 */

public class ShareCodeUtil {

    public static final String USAGE = "usage";
    public static final String PROJECT = "project";
    public static final String SCRIPT = "script";
    public static final String PROJECT_PATH = "/projects/";
    public static final String SCRIPTS_PATH = "/scripts/";

    private static final int MAX_FILE = 100;

    private SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(App.getContext());
    private int currentFileCount = -1;
    private Handler mHandler = new Handler(App.getContext().getMainLooper());

    private ShareCodeUtil() {
        if (App.getUser() != null) {
            initUsage();
        }
    }

    public static ShareCodeUtil getInstance() {
        return ShareCodeHolder.INSTANCE;
    }

    /**
     * 上传文件夹
     */

    /**
     * 管理文件内存
     */
    private List<CloudFile> getCacheFile() {
        String content = ACache.get(App.getContext()).getAsString(CLOUD_FILE);
        List<CloudFile> cloudFiles = App.getGson().fromJson(content, new TypeToken<List<CloudFile>>() {
        }.getType());
        return cloudFiles == null ? new ArrayList<>() : cloudFiles;
    }

    private void saveCacheFile(List<CloudFile> cloudFiles) {
        ACache.get(App.getContext()).put(CLOUD_FILE, App.getGson().toJson(cloudFiles));
    }

    private void saveCacheFile(CloudFile cloudFile) {
        List<CloudFile> list = getCacheFile();
        if (!list.contains(cloudFile)) {
            list.add(cloudFile);
        }
        saveCacheFile(list);
    }

    private void deleteCacheFile(CloudFile cloudFile) {
        List<CloudFile> list = getCacheFile();
        if (list.contains(cloudFile)) {
            list.remove(cloudFile);
        }
        saveCacheFile(list);
    }

    private void clearCacheFile() {
        ACache.get(App.getContext()).put(CLOUD_FILE, "");
    }

    /**
     * 初始化用户文件空间
     */
    public void initUsage() {
        // TODO
    }

    public int changeUsage(int change) {
        currentFileCount += change;
        sharedPreferences.edit().putInt(USAGE, currentFileCount).apply();
        return currentFileCount;
    }

    public int getUsage() {
        return currentFileCount;
    }

    private boolean hasSpace(int waiting) {
        if (currentFileCount < 0) {
            Toast.makeText(App.getContext(), "usage not init", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return currentFileCount + waiting < MAX_FILE;
        }
    }

    public interface CommentCallback {
        void commentBean(Gist.CommentBean comment);
    }

    private static class ShareCodeHolder {
        private static final ShareCodeUtil INSTANCE = new ShareCodeUtil();
    }


    private boolean isConnect() {
        if (!NetStateUtil.isConnected(App.getContext())) {
            Toast.makeText(App.getContext(), "connection error!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}