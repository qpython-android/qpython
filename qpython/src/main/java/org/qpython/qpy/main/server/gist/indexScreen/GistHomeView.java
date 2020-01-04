package org.qpython.qpy.main.server.gist.indexScreen;

import org.qpython.qpy.main.server.gist.BaseView;
import org.qpython.qpy.main.server.gist.response.ADBean;
import org.qpython.qpy.main.server.gist.response.GistBean;
import org.qpython.qpy.main.server.model.CourseAdModel;

import java.util.List;

/**
 * 文 件 名: GistHomeView
 * 创 建 人: ZhangRonghua
 * 创建日期: 2018/3/9 11:47
 * 修改时间：
 * 修改备注：
 */

public interface GistHomeView extends BaseView {
    void setData(List<GistBean> list);
    void loadMoreGist(List<GistBean> list);
    void favorite(boolean is);
    void showError(String msg);
    void hideError();
    void setAD(List<CourseAdModel.QpyBean.ExtAdBean.FeaturedBean> list);
    void hideAd();
}
