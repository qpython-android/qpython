package org.qpython.qpy.main.server.gist.createScreen;

import org.qpython.qpy.main.server.gist.BaseView;

/**
 * 文 件 名: CreateView
 * 创 建 人: ZhangRonghua
 * 创建日期: 2018/3/9 10:50
 * 修改时间：
 * 修改备注：
 */

public interface CreateGistView extends BaseView {
    void onSuccess(String id);
}
