package org.qpython.qpy.codeshare;

import android.app.Activity;
import android.content.SharedPreferences;

import com.google.gson.reflect.TypeToken;

import org.qpython.qpy.main.app.App;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import static org.qpython.qpy.codeshare.CONSTANT.CLOUDED_MAP;
import static org.qpython.qpy.codeshare.CONSTANT.IS_UPLOAD_INIT;

/**
 * 已上传云端的文件列表
 * Created by Hmei on 17/9/7.
 */

public class Clouded {
    SharedPreferences sp;
    private Map<String, Boolean> clouded;

    private Clouded() {
    }

    public static Clouded getInstance() {
        return CloudedHolder.INSTANCE;
    }

    public Map<String, Boolean> getClouded() {
        return clouded;
    }

    public void setClouded(Map<String, Boolean> clouded) {
        this.clouded = clouded;
    }

    public void writeToSP(Activity context) {
        if (clouded.size() > 0) {
            Type type = new TypeToken<HashMap<String, Boolean>>() {
            }.getType();
            sp.edit().putBoolean(IS_UPLOAD_INIT, true)
                    .putString(CLOUDED_MAP, App.getGson().toJson(clouded, type)).apply();
        }
    }

    private static class CloudedHolder {
        private static final Clouded INSTANCE = new Clouded();
    }

}
