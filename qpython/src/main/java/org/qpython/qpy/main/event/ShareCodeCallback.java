package org.qpython.qpy.main.event;

import org.qpython.qpy.codeshare.pojo.BookmarkerList;
import org.qpython.qpy.codeshare.pojo.CloudFile;
import org.qpython.qpy.codeshare.pojo.Gist;
import org.qpython.qpy.codeshare.pojo.GistBase;
import org.qpython.qpy.codeshare.pojo.UploadedProject;

import java.util.List;


public class ShareCodeCallback {
    public void getGistBaseList(List<GistBase> gistList) {

    }
    public void getGistDetail(Gist gistList) {
    }

    public void getNewestCommit(Gist.HistoryBean gist) {
    }

    public void getMyGistList(List<GistBase> gistBase) {
    }

    public  void getMyBookMarkList(List<BookmarkerList> bookmarkerList) {
    }

    public void getCommentList(List<Gist.CommentBean> commentList) {

    }

    public void getProjectsFileList(List<UploadedProject> projectList) {

    }

    public void getScriptsFileList(List<CloudFile> cloudFiles) {

    }

    public void getUsage(int usage) {

    }

    public void getFileContent(String content) {

    }

    public void getUsageSpace(String usage) {

    }
}
