package org.qpython.qpy.main.server.gist.loginScreen;

import android.util.Log;

import com.quseit.util.NUtil;

import org.qpython.qpy.main.app.App;
import org.qpython.qpy.main.app.User;
import org.qpython.qpy.main.server.gist.Controler;
import org.qpython.qpy.main.server.gist.ResponseHandler;
import org.qpython.qpy.main.server.gist.TokenManager;
import org.qpython.qpy.main.server.gist.request.LoginRequest;
import org.qpython.qpy.main.server.gist.response.LoginResponse;
import org.qpython.qpy.main.server.gist.response.ResponseBean;


/**
 * 文 件 名: LoginControler
 * 创 建 人: ZhangRonghua
 * 创建日期: 2018/3/8 16:49
 * 修改时间：
 * 修改备注：
 */

public class LoginControler extends Controler<LoginView> {

    public LoginControler(LoginView view) {
        super(view);
    }

    public void login(User user){

        String userName = user.getUserName();
        String nick = user.getNick();
        String userId = user.getUserId();
        String email = user.getEmail();
        String avatarUrl = user.getAvatarUrl();
        String loginType = (email!=null && email.contains("@")?"google":"wechat");
        //LoginRequest loginRequest = ;
        //LogUtil.d("LoginControler", "login:username:"+userName+"-nick:"+nick+"-userId:"+userId+"-email:"+email+"-avatarUrl:"+avatarUrl+"-logintype:"+loginType);

        LoginRequest lr2 = new LoginRequest(userName,nick, email ,userId,avatarUrl,loginType);
        logic(mGistService.login(lr2),false,new ResponseHandler<ResponseBean<LoginResponse>>() {
            @Override
            public void onSuccess(ResponseBean<LoginResponse> loginResponseResponseBean) {
                mView.hideLoading();

                Log.d("login", "T:"+loginResponseResponseBean.getData().getToken());
                if (loginResponseResponseBean.success()){
                    App.setUser(user);
                    TokenManager.saveToken(loginResponseResponseBean.getData().getToken());
                    //LogUtil.d("login", "T:"+TokenManager.getToken());

                    //mView.showToast("login successfully");
                    //mView.hideLoading();
                    mView.loginSuccess();
                }else {
                    mView.showToast("login fail,please retry later");
                }
            }

            @Override
            public void onError(String msg) {
                mView.hideLoading();
                mView.showToast(msg);
            }
        });
    }
}
