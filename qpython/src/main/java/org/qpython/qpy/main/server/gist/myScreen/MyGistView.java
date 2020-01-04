package org.qpython.qpy.main.server.gist.myScreen;

import org.qpython.qpy.main.server.gist.BaseView;
import org.qpython.qpy.main.server.gist.response.GistBean;

import java.util.List;

/**
 * 文 件 名: MyGistView
 * 创 建 人: ZhangRonghua
 * 创建日期: 2018/3/9 17:16
 * 修改时间：
 * 修改备注：
 */

public interface MyGistView extends BaseView {
    void deleteSuccess(String id);
    void addFavorites(List<GistBean> list);
    void addGists(List<GistBean> list);
}
