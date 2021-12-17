package org.qpython.qpy.codeshare;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.quseit.util.ACache;
import com.quseit.util.FileHelper;
import com.quseit.util.NetStateUtil;

import org.json.JSONObject;
import org.qpython.qpy.R;
import org.qpython.qpy.codeshare.pojo.BookmarkerList;
import org.qpython.qpy.codeshare.pojo.CloudFile;
import org.qpython.qpy.codeshare.pojo.Gist;
import org.qpython.qpy.codeshare.pojo.GistBase;
import org.qpython.qpy.main.app.App;
import org.qpython.qpy.main.app.CONF;
import org.qpython.qpy.texteditor.common.TextFileUtils;
import org.qpython.qpysdk.utils.DateTimeHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.functions.Action1;

import static org.qpython.qpy.codeshare.CONSTANT.DOT_REPLACE;
import static org.qpython.qpy.codeshare.CONSTANT.SLASH_REPLACE;
import static org.qpython.qpy.main.server.CacheKey.CLOUD_FILE;

/**
 * FireBase database util
 * Created by Hmei on 2017-08-10.
 */

public class ShareCodeUtil {
    private static final boolean CLEAR = false;

    private static final String CLOUD        = "cloud";
    private static final String GIST         = "gist";
    private static final String USER         = "user";
    private static final String BASE         = "base";
    private static final String GIST_LIST    = "gist_list";
    private static final String HISTORY      = "history";
    private static final String COMMENT      = "comment";
    private static final String BOOKMARK     = "bookmark";
    private static final String COMMIT       = "commit";
    private static final String INDEX        = "index";
    private static final String USAGE        = "usage";
    private static final String PROJECT      = "project";
    private static final String SCRIPT       = "script";
    private static final String OTHER        = "other";
    private static final String PROJECT_PATH = "/projects/";
    private static final String SCRIPTS_PATH = "/scripts/";

    private static final int MAX_FILE = 100;

    private String            email;
    private String            userName;
    private String            avatarUrl;

    private SharedPreferences sharedPreferences;
    private int currentFileCount = -1;

    private ShareCodeUtil() {
        if (App.getUser() != null) {
            email = App.getUser().getEmail().replace(".", "_");
            userName = App.getUser().getUserName();
            avatarUrl = App.getUser().getAvatarUrl();
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(App.getContext());
        } else {
            email = "";
            userName = "";
            avatarUrl = "";
        }
        //LogUtil.d("ShareCodeUtil", "ShareCodeUtil:"+App.getUser().getEmail());

        Log.d("ShareCodeUtil", "ShareCodeUtil:"+email);
    }

    public static ShareCodeUtil getInstance() {
        return ShareCodeHolder.INSTANCE;
    }

    @SuppressWarnings({"DEBUG_ONLY"})
    public void clearAll() {
        if (CLEAR) {
        }
    }

    public interface CommentCallback {
        void commentBean(Gist.CommentBean comment);
    }

    private static class ShareCodeHolder {
        private static final ShareCodeUtil INSTANCE = new ShareCodeUtil();
    }
}