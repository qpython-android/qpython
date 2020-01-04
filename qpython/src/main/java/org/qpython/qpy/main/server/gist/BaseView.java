package org.qpython.qpy.main.server.gist;

/**
 * 文 件 名: BaseView
 * 创 建 人: ZhangRonghua
 * 创建日期: 2018/3/8 16:42
 * 修改时间：
 * 修改备注：
 */

public interface BaseView {
    void showLoading();
    void hideLoading();
    void showToast(String msg);
}
