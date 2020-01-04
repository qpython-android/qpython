package org.qpython.qpy.main.server.gist.response;

import com.google.gson.annotations.SerializedName;

/**
 * 文 件 名: UserBean
 * 创 建 人: ZhangRonghua
 * 创建日期: 2018/3/9 09:40
 * 修改时间：
 * 修改备注：
 */

public class UserBean {
    @SerializedName(value = "uname")
    private String uName;

    @SerializedName(value = "username")
    private String userName;
    @SerializedName(value = "avatar")
    private String avatar;


    public String getUserName() {
        return userName;
    }

    public String getUName() {
        return uName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
