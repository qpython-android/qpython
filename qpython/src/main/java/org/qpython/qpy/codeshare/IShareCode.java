//package org.qpython.qpy.codeshare;
//
//import android.app.Activity;
//
//import org.qpython.qpy.codeshare.pojo.CloudFile;
//import org.qpython.qpy.codeshare.pojo.Gist;
//
//import java.util.List;
//
///**
// * Created by Hmei
// * 11/30/17
// */
//public interface IShareCode {
//
//    /**
//     * @return single instance
//     */
//    static IShareCode getInstance() {
//        return null;
//    }
//
//    /**
//     * 新建脚本Gist
//     *
//     * @param title    Gist标题
//     * @param desc     Gist描述
//     * @param msg      Commit message
//     * @param content  File content
//     * @param listener 上传成功后的监听
//     */
//    void createScriptGist(String title, String desc, String msg, String content, Object listener);
//
//    /**
//     * 新建项目Gist
//     *
//     * @param projectName        项目名
//     * @param desc               上传描述
//     * @param msg                Commit message
//     * @param paths              项目文件路径列表
//     * @param completionListener 上传成功后的监听
//     */
//    void createProjectGist(String projectName, String desc, String msg, List<String> paths, Object completionListener);
//
//    /**
//     * 提交Commit
//     *
//     * @param gist_id   Gist id
//     * @param msg      Commit message
//     * @param content  File content
//     * @param projName project name
//     * @param fileName file name
//     */
//    void commitGist(String gist_id, String msg, String content, String projName, String fileName);
//
//    /**
//     * 对某个Gist提交评论
//     *
//     * @param gist_id   Gist Id
//     * @param comment  Commit message
//     * @param isProj   is project?
//     * @param callback callback
//     */
//    void sendComment(String gist_id, String comment, boolean isProj, CommentCallback callback);
//
//    /**
//     * 回复某个Gist下的评论
//     *
//     * @param gist_id    Gist Id
//     * @param to        回复给to
//     * @param comment   Commit message
//     * @param reComment 回复的内容
//     * @param isProj    is project?
//     * @param callback  callback
//     */
//    void sendComment(String gist_id, String to, String comment, String reComment, boolean isProj, CommentCallback callback);
//
//    /**
//     * Add bookmark
//     *
//     * @param gist_id gist id
//     */
//    void bookmark(String gist_id);
//
//    /**
//     * Cancel bookmark
//     *
//     * @param gist_id gist id
//     */
//    void cancelBookmark(String gist_id);
//
//    /**
//     * 获取上传的Scripts列表
//     *
//     * @param callback callback
//     */
//    void getBaseScriptGistList(ShareCodeCallback callback);
//
//    /**
//     * 获取上传的Projects列表
//     *
//     * @param callback
//     */
//    void getBaseProjectGistList(ShareCodeCallback callback);
//
//    /**
//     * @param gist_id
//     * @param isProj
//     * @param callback
//     */
//    void getGistDetail(String gist_id, boolean isProj, ShareCodeCallback callback);
//
//    /**
//     * @param callback
//     */
//    void getMyGistList(ShareCodeCallback callback);
//
//    void getMyBookmarkList(ShareCodeCallback callback);
//
//    void getGistCommentList(String gist_id, ShareCodeCallback callback);
//
//    boolean uploadFolder(String path, long size, Object completionListener);
//
//    boolean uploadFile(String path, long size, Object completionListener);
//
//    boolean checkSpace(long size);
//
//    void getUploadedScripts(boolean forceRefresh, Activity context, ShareCodeCallback callback);
//
//    void locateUsage(long usage, Activity context);
//
//    void deleteUploadScript(CloudFile cloudFile, Object listener);
//
//    public interface CommentCallback {
//        void commentBean(Gist.CommentBean comment);
//    }
//}
