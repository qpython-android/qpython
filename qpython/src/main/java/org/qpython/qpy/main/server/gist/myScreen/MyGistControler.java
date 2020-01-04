package org.qpython.qpy.main.server.gist.myScreen;

import org.greenrobot.eventbus.EventBus;
import org.qpython.qpy.main.server.gist.Controler;
import org.qpython.qpy.main.server.gist.GistEvent;
import org.qpython.qpy.main.server.gist.ResponseHandler;
import org.qpython.qpy.main.server.gist.TokenManager;
import org.qpython.qpy.main.server.gist.request.BaseRequest;
import org.qpython.qpy.main.server.gist.response.GistBean;
import org.qpython.qpy.main.server.gist.response.ResponseBean;

import java.util.List;

/**
 * 文 件 名: MyGistControler
 * 创 建 人: ZhangRonghua
 * 创建日期: 2018/3/9 17:19
 * 修改时间：
 * 修改备注：
 */

public class MyGistControler extends Controler<MyGistView> {
    public MyGistControler(MyGistView view) {
        super(view);
    }

    public void getMyFavorites() {
        logic(mGistService.getMyFavorites(TokenManager.getToken()), true, new ResponseHandler<ResponseBean<List<GistBean>>>() {
            @Override
            public void onSuccess(ResponseBean<List<GistBean>> listResponseBean) {
                if (listResponseBean.success()) {
                    mView.addFavorites(listResponseBean.getData());
                } else {
                    mView.showToast(listResponseBean.getMessage());
                }
            }

            @Override
            public void onError(String msg) {
                mView.showToast(msg);
            }
        });
    }

    public void getMyGists() {
        logic(mGistService.getMyGists(TokenManager.getToken()), true, new ResponseHandler<ResponseBean<List<GistBean>>>() {
            @Override
            public void onSuccess(ResponseBean<List<GistBean>> listResponseBean) {
                if (listResponseBean.success()) {
                    mView.addGists(listResponseBean.getData());
                } else {
                    mView.showToast(listResponseBean.getMessage());
                }
            }

            @Override
            public void onError(String msg) {
                mView.showToast(msg);
            }
        });
    }

    public void deleteGist(String id) {
        logic(mGistService.deleteGist(TokenManager.getToken(), new BaseRequest(id)), true, new
                ResponseHandler<ResponseBean>() {
                    @Override
                    public void onSuccess(ResponseBean responseBean) {
                        if (responseBean.success()) {

                            mView.deleteSuccess((String) responseBean.getData());
                            EventBus.getDefault().post(new GistEvent(GistEvent.DELETE_SUC, (String) responseBean.getData()));
                        } else {
                            mView.showToast(responseBean.getMessage());
                        }
                    }

                    @Override
                    public void onError(String msg) {
                        mView.showToast(msg);
                    }
                });
    }
}
