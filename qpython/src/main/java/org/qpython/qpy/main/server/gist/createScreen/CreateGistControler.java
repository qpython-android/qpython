package org.qpython.qpy.main.server.gist.createScreen;

import android.util.Log;

import org.qpython.qpy.main.server.gist.Controler;
import org.qpython.qpy.main.server.gist.ResponseHandler;
import org.qpython.qpy.main.server.gist.TokenManager;
import org.qpython.qpy.main.server.gist.request.CreateRequest;
import org.qpython.qpy.main.server.gist.request.UpdateGistRequest;
import org.qpython.qpy.main.server.gist.response.ResponseBean;

/**
 * 文 件 名: PublishControler
 * 创 建 人: ZhangRonghua
 * 创建日期: 2018/3/9 10:32
 * 修改时间：
 * 修改备注：
 */

public class CreateGistControler extends Controler<CreateGistView> {

    public CreateGistControler(CreateGistView view) {
        super(view);
    }

    public void createGist(String title, String desc, String source, String sourceType, boolean isShare) {
        Log.d("createGist", "T:"+TokenManager.getToken());
        logic(mGistService.createGist(TokenManager.getToken(), new CreateRequest(title, source,
                desc, TokenManager.getToken(), sourceType, isShare)), true, new ResponseHandler<ResponseBean<String>>() {
            @Override
            public void onSuccess(ResponseBean<String> responseBean) {
                if (responseBean.success()) {
                    mView.onSuccess(responseBean.getData());
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

    public void updateGist(String id, String title, String desc, String source) {
        logic(mGistService.updateGist(TokenManager.getToken(), new UpdateGistRequest(id, title,
                desc, source)), true, new ResponseHandler<ResponseBean<String>>(mView) {
            @Override
            public void onSuccess(ResponseBean<String> responseBean) {
                mView.showToast(responseBean.getMessage());
                if (responseBean.success()) {
                    mView.onSuccess(responseBean.getData());
                } else {
                    mView.showToast(responseBean.getMessage());
                }
            }
        });
    }
}
