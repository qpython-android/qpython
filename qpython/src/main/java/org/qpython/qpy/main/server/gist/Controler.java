package org.qpython.qpy.main.server.gist;


import org.qpython.qpy.main.server.gist.response.ResponseBean;
import org.qpython.qpy.main.server.gist.service.GistService;
import org.qpython.qpy.main.server.gist.service.GistServiceFactory;

import java.util.LinkedList;
import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 文 件 名: Controler
 * 创 建 人: ZhangRonghua
 * 创建日期: 2018/3/8 16:22
 * 修改时间：
 * 修改备注：
 */

public class Controler<V extends BaseView> {
    protected GistService mGistService;
    protected V mView;
    private List<Subscription> mSubscriptions = new LinkedList<>();

    public Controler(V view) {
        mGistService = GistServiceFactory.gist();
        mView = view;
    }

    protected  <T extends ResponseBean> void logic(Observable<T> observable, boolean loading,
                                                   ResponseHandler<T>
            handler) {
        Subscription subscription = observable
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(() -> {
                    if (loading)
                        mView.showLoading();
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnTerminate(() -> {
                    if (loading)
                        mView.hideLoading();
                })
                .subscribe(handler);
        mSubscriptions.add(subscription);
    }

    public void onDestroy() {
        //解除订阅
        if (mSubscriptions != null)
            for (Subscription subscription : mSubscriptions) {
                if (subscription != null && !subscription.isUnsubscribed()) {
                    subscription.unsubscribe();
                }
            }
        mView = null;
    }
}
