package org.qpython.qpy.main.server.gist.request;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

import org.qpython.qpy.console.compont.Base64;

/**
 * 文 件 名: LoginRequest
 * 创 建 人: ZhangRonghua
 * 创建日期: 2018/3/8 14:53
 * 修改时间：
 * 修改备注：
 */

public class LoginRequest {
    @SerializedName(value = "username")
    private String userName;
    @SerializedName(value = "email")
    private String email;
    @SerializedName(value = "profile")
    private Profile profile;

    class Profile {
        @SerializedName(value = "utoken")
        private String utoken;
        @SerializedName(value = "avatar")
        private String avatar;
        @SerializedName(value = "login_type")
        private String loginType;
        @SerializedName(value = "uname")
        private String uname;

        public Profile(String userId, String uname, String avatar, String loginType) {
            Log.d("Profile", "Profile:"+userId);
            this.uname = uname;
            this.utoken = com.quseit.util.Base64.encode(userId).trim();
            this.avatar = avatar;
            this.loginType = loginType;
            Log.d("Profile", "utoken:"+utoken);
        }

        public String getUtoken() {
            return utoken;
        }

        public void setUtoken(String utoken) {
            this.utoken = utoken;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getLoginType() {
            return loginType;
        }

        public void setLoginType(String loginType) {
            this.loginType = loginType;
        }

    }
    public LoginRequest(String userName, String nick, String email, String userId, String avatar, String loginType) {
        this.userName = email;
        this.email = email;
        this.profile = new Profile(userId, nick, avatar, loginType);

    }


    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
