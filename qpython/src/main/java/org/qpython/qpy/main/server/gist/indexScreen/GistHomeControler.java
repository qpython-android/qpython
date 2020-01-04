package org.qpython.qpy.main.server.gist.indexScreen;

import org.qpython.qpy.R;
import org.qpython.qpy.main.app.App;
import org.qpython.qpy.main.server.gist.Controler;
import org.qpython.qpy.main.server.gist.ResponseHandler;
import org.qpython.qpy.main.server.gist.TokenManager;
import org.qpython.qpy.main.server.gist.request.BaseRequest;
import org.qpython.qpy.main.server.gist.response.ADBean;
import org.qpython.qpy.main.server.gist.response.GistBean;
import org.qpython.qpy.main.server.gist.response.ResponseBean;
import org.qpython.qpy.main.server.model.CourseAdModel;

import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 文 件 名: GistHomeControler
 * 创 建 人: ZhangRonghua
 * 创建日期: 2018/3/9 11:47
 * 修改时间：
 * 修改备注：
 */

public class GistHomeControler extends Controler<GistHomeView> {

    private int page = 0;

    public GistHomeControler(GistHomeView view) {
        super(view);
    }

    public void getAD() {
        App.getService().getCourseAd()
                .subscribeOn(Schedulers.io())
                .flatMap(courseAdModel -> rx.Observable.just(courseAdModel.getQpy().getExt_ad()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CourseAdModel.QpyBean.ExtAdBean>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.showToast(e.getMessage());
                        mView.showError(e.getMessage());
                    }

                    @Override
                    public void onNext(CourseAdModel.QpyBean.ExtAdBean adBean) {
                        if (adBean.getGistshare() == null || adBean.getGistshare().size() == 0) {
                            mView.hideAd();
                        } else {
                            mView.setAD(adBean.getGistshare());
                        }
                    }
                });
    }

    public void getGist() {
        logic(mGistService.getAllGists(/*page*/), true, new ResponseHandler<ResponseBean<List<GistBean>>>() {
            @Override
            public void onSuccess(ResponseBean<List<GistBean>> listResponseBean) {
                if (listResponseBean.success()) {
                    page++;
                    if (listResponseBean.getData() == null || listResponseBean.getData().size() == 0) {
                        mView.showError(App.getContext().getResources().getString(R.string.no_data));
                    } else {
                        mView.setData(listResponseBean.getData());
                    }
                } else {
                    mView.showError(listResponseBean.getMessage());
                }
            }

            @Override
            public void onError(String msg) {
                mView.showToast(msg);
                mView.showError("net error");
            }
        });
    }

    public void refresh() {
        page = 0;
        getGist();
    }

    public void loadMore() {
        if (page == -1) {
            mView.showToast("no more");
        } else {
            logic(mGistService.getAllGists(/*page*/), false, new ResponseHandler<ResponseBean<List<GistBean>>>() {
                @Override
                public void onSuccess(ResponseBean<List<GistBean>> listResponseBean) {
                    if (listResponseBean.success() && listResponseBean.getData().size() > 0) {
                        page++;
                        mView.loadMoreGist(listResponseBean.getData());
                    } else {
                        mView.showToast(listResponseBean.getMessage());
                        page = -1;
                    }
                }

                @Override
                public void onError(String msg) {
                    mView.showToast(msg);
                }
            });
        }
    }

    public void favoriteGist(String id) {
        logic(mGistService.favoriteGist(TokenManager.getToken(), new BaseRequest(id)), false, new
                ResponseHandler<ResponseBean>(mView) {
                    @Override
                    public void onSuccess(ResponseBean responseBean) {
                        if (responseBean.success()) {
                            boolean isFav = (Boolean) responseBean.getData();
                            mView.favorite(isFav);
                            if (isFav) {
                                App.getFavorites().add(id);
                            } else {
                                App.getFavorites().remove(id);
                            }
                        }

                    }
                });
    }
}
