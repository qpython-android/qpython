package org.qpython.qpy.main.server.gist.detailScreen;

import org.qpython.qpy.main.server.gist.BaseView;
import org.qpython.qpy.main.server.gist.response.CommentBean;
import org.qpython.qpy.main.server.gist.response.GistBean;

import java.util.List;

/**
 * 文 件 名: DetailView
 * 创 建 人: ZhangRonghua
 * 创建日期: 2018/3/9 10:57
 * 修改时间：
 * 修改备注：
 */

public interface DetailView extends BaseView {
    void setData(GistBean gistBean);

    void loadMoreComments(List<CommentBean> list);

    void favorite(boolean is);

    void addComment(CommentBean comment);

    void fork();

    void forkSuccess();

    void refresh(String id);
}
