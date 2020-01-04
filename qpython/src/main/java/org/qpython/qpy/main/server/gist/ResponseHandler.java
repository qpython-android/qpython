package org.qpython.qpy.main.server.gist;

import org.qpython.qpy.main.server.gist.response.ResponseBean;

import rx.Observer;

/**
 * 文 件 名: ResponseHandler
 * 创 建 人: ZhangRonghua
 * 创建日期: 2018/3/8 16:26
 * 修改时间：
 * 修改备注：
 */

public abstract class ResponseHandler<T extends ResponseBean> implements Observer<T> {
    private BaseView baseView;

    public ResponseHandler() {
    }

    public ResponseHandler(BaseView baseView) {
        this.baseView = baseView;
    }

    @Override
    public void onNext(T t) {
        if (t.getCode() == 0) {
            onSuccess(t);
        } else {
            onError(t.getMessage());
        }
    }

    @Override
    public void onCompleted() {
    }

    @Override
    public void onError(Throwable e) {
        e.getMessage();
    }

    public abstract void onSuccess(T t);

    public void onError(String msg) {
        if (baseView != null) baseView.showToast(msg);
    }
}
