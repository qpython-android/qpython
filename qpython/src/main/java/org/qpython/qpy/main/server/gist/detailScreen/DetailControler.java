package org.qpython.qpy.main.server.gist.detailScreen;

import org.greenrobot.eventbus.EventBus;
import org.qpython.qpy.main.app.App;
import org.qpython.qpy.main.server.gist.Controler;
import org.qpython.qpy.main.server.gist.GistEvent;
import org.qpython.qpy.main.server.gist.ResponseHandler;
import org.qpython.qpy.main.server.gist.TokenManager;
import org.qpython.qpy.main.server.gist.request.BaseRequest;
import org.qpython.qpy.main.server.gist.request.CommentRequest;
import org.qpython.qpy.main.server.gist.request.UpdateGistRequest;
import org.qpython.qpy.main.server.gist.response.CommentBean;
import org.qpython.qpy.main.server.gist.response.GistBean;
import org.qpython.qpy.main.server.gist.response.ResponseBean;

import java.util.List;

/**
 * 文 件 名: DetailControler
 * 创 建 人: ZhangRonghua
 * 创建日期: 2018/3/9 10:57
 * 修改时间：
 * 修改备注：
 */

public class DetailControler extends Controler<DetailView> {

    private int page = 1;

    public DetailControler(DetailView view) {
        super(view);
    }

    public void getDetail(String id) {
        logic(mGistService.getGistDetail(id), true, new ResponseHandler<ResponseBean<GistBean>>(mView) {
            @Override
            public void onSuccess(ResponseBean<GistBean> gistBeanResponseBean) {
                if (gistBeanResponseBean.success()) {
                    GistBean bean = gistBeanResponseBean.getData();
                    bean.setFavorite(App.getFavorites().contains(bean.getId()));
                    mView.setData(bean);
                } else {
                    mView.showToast(gistBeanResponseBean.getMessage());
                }
            }

        });
    }

    public void loadMoreComment(String id) {
        logic(mGistService.getCommentMore(id, page), true, new ResponseHandler<ResponseBean<List<CommentBean>>>(mView) {
            @Override
            public void onSuccess(ResponseBean<List<CommentBean>> listResponseBean) {
                if (listResponseBean.success()) {
                    mView.loadMoreComments(listResponseBean.getData());
                    page++;
                } else {
                    mView.showToast(listResponseBean.getMessage());
                }
            }

        });
    }

    public void refresh(String id) {
        page = 1;
        getDetail(id);
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
                                EventBus.getDefault().post(new GistEvent(GistEvent.FAVORITE,id));
                            } else {
                                App.getFavorites().remove(id);
                                EventBus.getDefault().post(new GistEvent(GistEvent.UNFAVORITE,id));
                            }
                        }
                    }
                });
    }

    public void comment(String gistId, String comment, String toId) {
        logic(mGistService.commentGist(TokenManager.getToken(), new CommentRequest(comment, toId, gistId)), true, new
                ResponseHandler<ResponseBean<CommentBean>>(mView) {
                    @Override
                    public void onSuccess(ResponseBean<CommentBean> responseBean) {
                        if (responseBean.success()) {
                            mView.addComment(responseBean.getData());
                        } else {
                            mView.showToast(responseBean.getMessage());
                        }
                    }

                });
    }

    public void forkGist(String id) {
        logic(mGistService.forkGist(TokenManager.getToken(), new BaseRequest(id)), false, new ResponseHandler<ResponseBean>(mView) {
            @Override
            public void onSuccess(ResponseBean responseBean) {
                if (responseBean.success()) {
                    mView.forkSuccess();
                } else {
                    mView.showToast(responseBean.getMessage());
                }
            }

        });
    }

    public void publishGist(String id) {
        logic(mGistService.publishGist(TokenManager.getToken(), new BaseRequest(id)), true, new ResponseHandler<ResponseBean<String>>(mView) {
            @Override
            public void onSuccess(ResponseBean<String> responseBean) {
                if (responseBean.success()) {
                    mView.refresh(responseBean.getData());
                } else {
                    mView.showToast(responseBean.getMessage());
                }
            }

        });
    }

//    public void updateGist(String id, String title, String desc, String source) {
//        logic(mGistService.updateGist(TokenManager.getToken(), new UpdateGistRequest(id, title, desc, source)), true, new ResponseHandler<ResponseBean>(mView) {
//            @Override
//            public void onSuccess(ResponseBean responseBean) {
//                mView.showToast(responseBean.getMessage());
//            }
//        });
//    }

}
