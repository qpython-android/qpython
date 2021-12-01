package org.qpython.qpy.codeshare;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.common.reflect.TypeToken;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.quseit.util.ACache;
import com.quseit.util.FileHelper;
import com.quseit.util.FileUtils;
import com.quseit.util.NetStateUtil;

import org.json.JSONObject;
import org.qpython.qpy.R;
import org.qpython.qpy.codeshare.pojo.BookmarkerList;
import org.qpython.qpy.codeshare.pojo.CloudFile;
import org.qpython.qpy.codeshare.pojo.Gist;
import org.qpython.qpy.codeshare.pojo.GistBase;
import org.qpython.qpy.main.app.App;
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

    private DatabaseReference reference;
    private String            email;
    private String            userName;
    private String            avatarUrl;

    private SharedPreferences sharedPreferences;
    private int currentFileCount = -1;

    private ShareCodeUtil() {
        reference = FirebaseDatabase.getInstance().getReference();
        if (App.getUser() != null) {
            email = App.getUser().getEmail().replace(".", "_");
            userName = App.getUser().getUserName();
            avatarUrl = App.getUser().getAvatarUrl();
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(App.getContext());
            initUsage(null);
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
            reference.child(CLOUD).child(email).removeValue(
                    (databaseError, databaseReference) ->
                            Toast.makeText(App.getContext(), "Clear", Toast.LENGTH_SHORT).show());
        }
    }

    public void createScriptGist(String title, String desc, String msg, String content, DatabaseReference.CompletionListener listener) {
        String date = DateTimeHelper.getDate();
        // Add to all gist repo
        Gist gist = new Gist();
        gist.setTitle(title);
        gist.setAuthor(userName);
        gist.setAvatar(avatarUrl);
        gist.setDescribe(desc);
        gist.setDate(date);
        DatabaseReference path = reference.child(GIST).child(SCRIPT).push();// gist/script/<gist_id>
        String gistId = path.getKey();
        path.setValue(gist, listener);
        commitGist(gistId, msg, content, null, null);

        // Add to user repo
        GistBase baseGist = new GistBase();
        baseGist.setAuthor(userName);
        baseGist.setAvatar(avatarUrl);
        baseGist.setDate(date);
        baseGist.setTitle(title);
        reference.child(USER).child(email).child(GIST_LIST).child(SCRIPT).child(gistId).setValue(baseGist);// user/<email>/gist_list/script/<gist_id>

        // Add to base repo
        reference.child(BASE).child(SCRIPT).child(gistId).setValue(baseGist); // base/script/<gist_id>
    }

    public void createProjectGist(String projectName, String desc, String msg, List<String> paths, DatabaseReference.CompletionListener completionListener) {
        for (String path : paths) {
            File file = new File(path);
            String code = TextFileUtils.readTextFile(file);
            String fileName = file.getName().replace(".", DOT_REPLACE);

            String date = DateTimeHelper.getDate();
            // Add to all gist repo
            Gist gist = new Gist();
            gist.setTitle(fileName);
            gist.setAuthor(userName);
            gist.setAvatar(avatarUrl);
            gist.setDescribe(desc);
            gist.setDate(date);
            DatabaseReference subReference = reference.child(GIST).child(PROJECT);// gist/script/<gist_id>
            String gistId = subReference.getKey();
            subReference.child(gistId).child(projectName).child(fileName).setValue(gist, completionListener);// gist/project/<gist_id>/<projectName>/<fileName>
            commitGist(gistId, msg, code, projectName, fileName);

            // Add to user repo
            GistBase baseGist = new GistBase();
            baseGist.setAuthor(userName);
            baseGist.setAuthor(avatarUrl);
            baseGist.setDate(date);
            baseGist.setTitle(fileName);
            // user/<email>/gist_list/project/<gist_id>/<projectName>/<fileName>
            reference.child(USER).child(email).child(GIST_LIST).child(PROJECT).child(gistId).child(projectName).child(fileName).setValue(baseGist);

            // Add to base repo
            // base/project/<gist_id>/<projectName>/<fileName>
            reference.child(BASE).child(PROJECT).child(gistId).child(projectName).child(fileName).setValue(baseGist);
        }
    }

    public void commitGist(String gistId, String msg, String content, String projName, String fileName) {
        Gist.HistoryBean historyBean = new Gist.HistoryBean();
        historyBean.setData(DateTimeHelper.getDate());
        historyBean.setMassage(msg);
        historyBean.setContent(content);
        if (projName != null) {
            DatabaseReference subRe = reference.child(GIST).child(PROJECT).child(gistId).child(projName).child(fileName).child(HISTORY).push();
            String historyId = subRe.getKey();
            subRe.child(historyId).setValue(historyBean); // gist/<gist_id>/history/<historyId>}
        } else {
            DatabaseReference subRe = reference.child(GIST).child(SCRIPT).child(gistId).child(HISTORY).push();
            String historyId = subRe.getKey();
            subRe.child(historyId).setValue(historyBean); // gist/<gist_id>/history/<historyId>}
            reference.child(GIST).child(SCRIPT).child(gistId).child("lastCommitCode").setValue(content);
        }
    }

    public void sendComment(String gistId, String comment, boolean isProj, CommentCallback callback) {
        sendComment(gistId, "", comment, "", isProj, callback);
    }

    /**
     * @param comment   评论
     * @param reComment 被回复的评论
     */
    public void sendComment(String gistId, String to, String comment, String reComment, boolean isProj, CommentCallback callback) {
        Gist.CommentBean commentBean = new Gist.CommentBean();
        commentBean.setFrom_content(comment);
        commentBean.setData(DateTimeHelper.getDate());
        commentBean.setFrom(userName);
        commentBean.setAvatar(avatarUrl);
        commentBean.setRe(to);
        commentBean.setRe_content(reComment);
        callback.commentBean(commentBean);
        DatabaseReference subRe = reference.child(GIST).child(isProj ? PROJECT : SCRIPT).child(gistId).child(COMMENT).push();
        String commentId = subRe.getKey();
        subRe.child(commentId).setValue(commentBean);
    }

    public void bookmark(String gistId) {
        reference.child(GIST).child(gistId).child(BOOKMARK).child(email).setValue(userName);
        reference.child(USER).child(email).child(BOOKMARK).child(gistId).setValue(true);
    }

    public void cancelBookmark(String gistId) {
        reference.child(GIST).child(gistId).child(BOOKMARK).child(email).removeValue();
        reference.child(USER).child(email).child(BOOKMARK).child(gistId).removeValue();
    }

    public void getBaseScriptGistList(Action1<List<GistBase>> callback) {
        reference.child(BASE).child(SCRIPT).addListenerForSingleValueEvent(new SimpleValueEventListener() {
            @Override
            public void onDataGet(HashMap<String, Object> value) {
                handleKey(value,callback);
            }
        });
    }

    private void handleKey(HashMap value,Action1<List<GistBase>> callback){
        List<GistBase> dataList = new ArrayList<>();
        for (Object key : value.keySet()) {
            GistBase gistBase = App.getGson().fromJson(new JSONObject((Map) value.get(key)).toString(), GistBase.class);
            gistBase.setId((String) key);
            dataList.add(gistBase);
        }
        Observable.just(dataList)
                .subscribe(callback);
    }

    public void getBaseProjectGistList(Action1<List<GistBase>> callback) {
        reference.child(BASE).child(PROJECT).addListenerForSingleValueEvent(new SimpleValueEventListener() {
            @Override
            public void onDataGet(HashMap<String, Object> value) {
//                List<GistBase> dataList = new ArrayList<>();
//                for (Object key : value.keySet()) {
//                    GistBase gistBase = App.getGson().fromJson(new JSONObject((Map) value.get(key)).toString(), GistBase.class);
//                    gistBase.setId((String) key);
//                    dataList.add(gistBase);
//                }
//                Observable.just(dataList)
//                        .subscribe(callback);
                handleKey(value,callback);
            }
        });
    }

    public void getGistDetail(String gistId, boolean isProj, Action1<Gist> callback) {
        reference.child(GIST).child(isProj ? PROJECT : SCRIPT).child(gistId).addListenerForSingleValueEvent(new SimpleValueEventListener() {
            @Override
            public void onDataGet(HashMap value) {
                JSONObject jsonObject = new JSONObject(value);
                Gist gist = App.getGson().fromJson(String.valueOf(jsonObject), Gist.class);

                HashMap historyMap = (HashMap) value.get(HISTORY);
                if (historyMap != null) {
                    List<Gist.HistoryBean> historyList = new ArrayList<>();
                    for (Object historyKey : historyMap.keySet()) {
                        Gist.HistoryBean historyBean = App.getGson().fromJson(String.valueOf(new JSONObject((Map) historyMap.get(historyKey))), Gist.HistoryBean.class);
                        historyBean.setHistoryId((String) historyKey);
                        historyList.add(historyBean);
                    }
                    gist.setHistory(historyList);
                }

                HashMap commentMap = (HashMap) value.get(COMMENT);
                if (commentMap != null) {
                    List<Gist.CommentBean> commentList = new ArrayList<>();
                    for (Object commentKey : commentMap.keySet()) {
                        Gist.CommentBean commentBean = App.getGson().fromJson(String.valueOf(new JSONObject((Map) commentMap.get(commentKey))), Gist.CommentBean.class);
                        commentBean.setId((String) commentKey);
                        commentList.add(commentBean);
                    }
                    gist.setComment(commentList);
                }

                HashMap bookmarkMap = (HashMap) value.get(BOOKMARK);
                if (bookmarkMap != null) {
                    List<Gist.BookmarkerBean> bookmarkerList = new ArrayList<>();
                    for (Object bookmarkKey : bookmarkMap.keySet()) {
                        Gist.BookmarkerBean bookmarkerBean = App.getGson().fromJson(String.valueOf(new JSONObject((Map) bookmarkMap.get(bookmarkKey))), Gist.BookmarkerBean.class);
                        bookmarkerBean.setId((String) bookmarkKey);
                        bookmarkerList.add(bookmarkerBean);
                    }
                    gist.setBookmarker(bookmarkerList);
                }
                Observable.just(gist)
                        .subscribe(callback);
            }
        });
    }

    // TODO: 2017-08-14 Need to fix
    public void getMyGistList(Action1<List<GistBase>> callback) {
        reference.child(USER).child(GIST_LIST).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap object = (HashMap) dataSnapshot.getValue();
                List<GistBase> userGist = new ArrayList<>();
                if (object == null) {
                    return;
                }
                for (Object key : object.keySet()) {
                    userGist.add(App.getGson().fromJson(new JSONObject((Map) object.get(key)).toString(), GistBase.class));
                }
                Observable.just(userGist)
                        .subscribe(callback);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getMyBookmarkList(Action1<List<BookmarkerList>> callback) {
        reference.child(USER).child(BOOKMARK).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap object = (HashMap) dataSnapshot.getValue();
                List<BookmarkerList> userGist = new ArrayList<>();
                if (object == null) {
                    return;
                }
                for (Object key : object.keySet()) {
                    userGist.add(App.getGson().fromJson(new JSONObject((Map) object.get(key)).toString(), BookmarkerList.class));
                }
                Observable.just(userGist)
                        .subscribe(callback);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getGistCommentList(String gistId, Action1<List<Gist.CommentBean>> callback) {
        reference.child(GIST).child(gistId).child(COMMENT).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap object = (HashMap) dataSnapshot.getValue();
                List<Gist.CommentBean> commentList = new ArrayList<>();
                if (object == null) {
                    return;
                }
                for (Object key : object.keySet()) {
                    commentList.add(App.getGson().fromJson(new JSONObject((Map) object.get(key)).toString(), Gist.CommentBean.class));
                }
                Observable.just(commentList)
                        .subscribe(callback);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public boolean uploadFolder(String path, DatabaseReference.CompletionListener completionListener, int[] size) {
        File folder = new File(path);
        // 获取该文件夹下符合后缀文件及非隐藏文件

        if (!folder.isDirectory()) {
            return false;
        }
        List<File> files = FileHelper.filterExt(folder, App.getContext().getResources().getStringArray(R.array.support_file_ext), size[0]);
        if (files.size() == 0) {
            Toast.makeText(App.getContext(), R.string.no_file, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!hasSpace(files.size())) {
            return false;
        }

        for (File file : files) {
            uploadFile(file.getAbsolutePath(), completionListener);
        }

//        // 如果在project目录下
//        if ("projects".equals(folder.getName())) {
//            DatabaseReference projNode = reference.child(CLOUD).child(email).child(PROJECT).child(folder.getName().replace(".", DOT_REPLACE));
//            for (int i = 0; i < files.size(); i++) {
//                String date = DateTimeHelper.AGO_FULL_DATE_FORMATTER.format(new Date(files.get(i).lastModified()));
//                String subPath = files.get(i).getAbsolutePath();
//                String subKey = subPath.substring(subPath.indexOf(folder.getName()) + folder.getName().length(), subPath.length()).replace(".", DOT_REPLACE).replace("/", SLASH_REPLACE);
//                if (i == files.size() - 1) {
//                    projNode.child(subKey)
//                            .child(date)
//                            .setValue(TextFileUtils.readTextFile(files.get(i)), completionListener);
//                } else {
//                    projNode.child(subKey)
//                            .child(date)
//                            .setValue(TextFileUtils.readTextFile(files.get(i)));
//                }
//                reference.child(CLOUD)
//                        .child(email)
//                        .child(INDEX)
//                        .child(PROJECT)
//                        .child(folder.getName().replace(".", DOT_REPLACE) + subKey)
//                        .setValue(date);
//            }
//        } else if ("scripts".equals(folder.getName())) {
//            // 上传整个scripts文件夹
//            for (int i = 0; i < files.size(); i++) {
//                String abs_path = files.get(i).getAbsolutePath();
//                String key = abs_path
//                        .substring(abs_path.indexOf(SCRIPTS_PATH) + SCRIPTS_PATH.length())
//                        .replace(".", DOT_REPLACE)
//                        .replace("/", SLASH_REPLACE);
//                DatabaseReference content = reference.child(CLOUD)
//                        .child(email)
//                        .child(SCRIPT)
//                        .child(key)
//                        .child(DateTimeHelper.AGO_FULL_DATE_FORMATTER.format(new Date(files.get(i).lastModified())));
//                if (i == files.size() - 1) {
//                    content.setValue(TextFileUtils.readTextFile(files.get(i)), completionListener);
//                } else {
//                    content.setValue(TextFileUtils.readTextFile(files.get(i)));
//                }
//
//                reference.child(CLOUD)
//                        .child(email)
//                        .child(INDEX)
//                        .child(SCRIPT)
//                        .child(key)
//                        .setValue(DateTimeHelper.AGO_FULL_DATE_FORMATTER.format(new Date(files.get(i).lastModified())));
//            }
//        } else {
//            // 普通文件夹
//            int fileListSize = files.size();
//            for (File file : files) {
//                fileListSize--;
//                final String rootNode = "/qpython/";
//                String node = file.getAbsolutePath().substring(path.indexOf(rootNode) + rootNode.length());
//                DatabaseReference child =
//                        reference
//                                .child(CLOUD)
//                                .child(email)/*.child(folder.getName().replace(".", DOT_REPLACE))*/
//                                .child(OTHER)
//                                .child(node.replace(".", DOT_REPLACE).replace("/", SLASH_REPLACE))
//                                .child(DateTimeHelper.getDate());
//                if (fileListSize == 0) {
//                    child.setValue(TextFileUtils.readTextFile(file), completionListener);
//                } else {
//                    child.setValue(TextFileUtils.readTextFile(file));
//                }
//
//                reference.child(CLOUD)
//                        .child(email)
//                        .child(INDEX)
//                        .child(OTHER)
//                        .child(node.replace(".", DOT_REPLACE).replace("/", SLASH_REPLACE))
//                        .setValue(DateTimeHelper.getDate());
//            }
//        }
        return true;
    }

    public boolean uploadFile(String path, DatabaseReference.CompletionListener completionListener) {
        File file = new File(path);

        if (!hasSpace(1)) {
            return false;
        }

        if (path.contains(PROJECT_PATH)) {
            String subPath = path.substring(path.indexOf(PROJECT_PATH) + PROJECT_PATH.length());
            String projName = subPath.split("/")[0];
            reference.child(CLOUD)
                    .child(email)
                    .child(PROJECT)
                    .child(projName)
                    .child(subPath.replace(".", DOT_REPLACE).replace("/", SLASH_REPLACE).replace(projName, ""))
                    .child(DateTimeHelper.AGO_FULL_DATE_FORMATTER.format(new Date(file.lastModified())))
                    .setValue(TextFileUtils.readTextFile(file), completionListener);

            reference.child(CLOUD)
                    .child(email)
                    .child(INDEX)
                    .child(PROJECT)
                    .child(subPath.replace(".", DOT_REPLACE).replace("/", SLASH_REPLACE))
                    .setValue(DateTimeHelper.AGO_FULL_DATE_FORMATTER.format(new Date(file.lastModified())));
        } else if (path.contains(SCRIPTS_PATH)) {
            reference.child(CLOUD)
                    .child(email)
                    .child(SCRIPT)
                    .child(file.getName().replace(".", DOT_REPLACE))
                    .child(DateTimeHelper.AGO_FULL_DATE_FORMATTER.format(new Date(file.lastModified())))
                    .setValue(TextFileUtils.readTextFile(file), completionListener);

            reference.child(CLOUD)
                    .child(email)
                    .child(INDEX)
                    .child(SCRIPT)
                    .child(file.getName().replace(".", DOT_REPLACE))
                    .setValue(DateTimeHelper.AGO_FULL_DATE_FORMATTER.format(new Date(file.lastModified())));
        } else {
            final String rootNode = "/qpython/";
            String uploadKey = path.substring(path.indexOf(rootNode) + rootNode.length());
            reference.child(CLOUD)
                    .child(email)
                    .child(OTHER)
                    .child(uploadKey.replace(".", DOT_REPLACE).replace("/", SLASH_REPLACE))
                    .child(DateTimeHelper.getDate())
                    .setValue(TextFileUtils.readTextFile(file), completionListener);

            reference.child(CLOUD)
                    .child(email)
                    .child(INDEX)
                    .child(OTHER)
                    .child(uploadKey.replace(".", DOT_REPLACE).replace("/", SLASH_REPLACE))
                    .setValue(DateTimeHelper.getDate());
        }
        return true;
    }

    public void getUploadedScripts(boolean forceRefresh, Activity context, Action1<List<CloudFile>> callback) {
        if (CLEAR) {
            return;
        }
        String content = FileHelper.getFileContents(FileUtils.getCloudMapCachePath(context.getApplicationContext()));
        List<CloudFile> cloudFiles = content == null ? null : App.getGson().fromJson(content, new TypeToken<List<CloudFile>>() {
        }.getType());
        if (cloudFiles != null && !forceRefresh) {
            Observable.just(cloudFiles)
                    .subscribe(callback);
        } else {
            email = App.getUser()!=null?App.getUser().getEmail().replace(".","_"):"";
            if (email.equals("")) {
                Toast.makeText(context, "Waiting the firebase to initializ...",Toast.LENGTH_SHORT).show();

            } else {
                reference.child(CLOUD).child(email).child(INDEX).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        HashMap<String, Object> value = (HashMap<String, Object>) dataSnapshot.getValue();
                        if (value == null) {
                            resetUsage(0);
                            Observable.just(new ArrayList<CloudFile>())
                                    .subscribe(callback);
                            return;
                        }
                        List<CloudFile> cloudFiles = new ArrayList<>();
                        for (String node : value.keySet()) {
                            switch (node) {
                                case SCRIPT:
                                    HashMap<String, Object> script;
                                    script = (HashMap<String, Object>) value.get(SCRIPT);
                                    for (String o : script.keySet()) {
                                        CloudFile cloudFile = new CloudFile();
                                        cloudFile.setName(o);
                                        cloudFile.setKey(SCRIPT + "/" + o);
                                        cloudFile.setPath(SCRIPTS_PATH);
                                        cloudFile.setUploadTime((String) script.get(o));
                                        cloudFiles.add(cloudFile);
                                    }
                                    break;
                                case PROJECT:
                                    HashMap<String, Object> project = (HashMap<String, Object>) value.get(PROJECT);
                                    for (String s : project.keySet()) {
                                        CloudFile cloudFile = new CloudFile();
                                        cloudFile.setKey(PROJECT + "/" + s);
                                        String[] nodes = s.split(SLASH_REPLACE);
                                        cloudFile.setName(nodes[nodes.length - 1]);
                                        cloudFile.setProjectName(nodes[0]);
                                        cloudFile.setPath(s.replace(nodes[0], ""));
                                        cloudFile.setUploadTime((String) project.get(s));
                                        cloudFiles.add(cloudFile);
                                    }
                                    break;
                                default:
                                    HashMap<String, Object> other = (HashMap<String, Object>) value.get(OTHER);
                                    for (String s : other.keySet()) {
                                        CloudFile cloudFile = new CloudFile();
                                        cloudFile.setPath(s);
                                        cloudFile.setKey(OTHER + "/" + s);
                                        String[] nodes = s.split(SLASH_REPLACE);
                                        boolean index = false;
                                        for (String node1 : nodes) {
                                            if (index) {
                                                cloudFile.setProjectName(node1);
                                                break;
                                            } else if (node1.equals("projects3")) {
                                                index = true;
                                            }
                                        }
                                        cloudFile.setName(nodes[nodes.length - 1]);
                                        cloudFile.setUploadTime((String) other.get(s));
                                        cloudFiles.add(cloudFile);
                                    }
                                    break;
                            }
                        }
                        resetUsage(cloudFiles.size());
                        Observable.just(cloudFiles)
                                .subscribe(callback);
                        ACache.get(context).put(CLOUD_FILE, App.getGson().toJson(cloudFiles));

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }
    }

    public void getFileContent(String path, Action1<String> callback) {
        reference.child(CLOUD)
                .child(email)
                .child(path)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        HashMap<String, String> dateContent = (HashMap<String, String>) dataSnapshot.getValue();
                        String latest = "";
                        for (String s : dateContent.keySet()) {
                            latest = DateTimeHelper.isLater(s, latest) ? s : latest;
                        }

                        Observable.just(dateContent.get(latest))
                                .subscribe(callback);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public void deleteUploadScript(CloudFile cloudFile, DatabaseReference.CompletionListener listener) {
        reference.child(CLOUD)
                .child(email)
                .child(cloudFile.getKey())
                .removeValue(listener);
        reference.child(CLOUD)
                .child(email)
                .child(INDEX)
                .child(cloudFile.getKey())
                .removeValue();
        changeUsage(-1);
    }

    public void initUsage(Action1<Integer> callback) {
        if (CLEAR) {
            return;
        }
        if (currentFileCount == -1) {
            if (NetStateUtil.isConnected(App.getContext())) {
                reference.child(CLOUD)
                        .child(email)
                        .child(USAGE)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue() != null) {
                                    currentFileCount = ((Long) dataSnapshot.getValue()).intValue();
                                } else {
                                    currentFileCount = 0;
                                }
                                if (callback != null) {
                                    Observable.just(currentFileCount)
                                            .subscribe(callback);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
            }
        } else {
            callback.call(currentFileCount);
        }
    }

    public int changeUsage(int change) {
        currentFileCount += change;
        reference.child(CLOUD)
                .child(email)
                .child(USAGE)
                .setValue(currentFileCount);
        return currentFileCount;
    }

    public void resetUsage(int value) {
        currentFileCount = value;
        reference.child(CLOUD)
                .child(email)
                .child(USAGE)
                .setValue(currentFileCount);
    }

    private boolean hasSpace(int waiting) {
        if (currentFileCount < 0) {
            Toast.makeText(App.getContext(), R.string.usage_not_init, Toast.LENGTH_SHORT).show();
            return false;
        } else if (currentFileCount + waiting > MAX_FILE) {
            Toast.makeText(App.getContext(), R.string.no_space, Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    public interface CommentCallback {
        void commentBean(Gist.CommentBean comment);
    }

    private static class ShareCodeHolder {
        private static final ShareCodeUtil INSTANCE = new ShareCodeUtil();
    }

    abstract class SimpleValueEventListener implements ValueEventListener {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            HashMap<String, Object> object = (HashMap<String, Object>) dataSnapshot.getValue();
            if (object == null) {
                return;
            }
            onDataGet(object);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }

        public abstract void onDataGet(HashMap<String, Object> value);
    }
}