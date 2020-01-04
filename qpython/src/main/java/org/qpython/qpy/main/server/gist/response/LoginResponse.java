package org.qpython.qpy.main.server.gist.response;

import com.google.gson.annotations.SerializedName;

/**
 * 文 件 名: LoginResponse
 * 创 建 人: ZhangRonghua
 * 创建日期: 2018/3/8 16:09
 * 修改时间：
 * 修改备注：
 */

public class LoginResponse {
    @SerializedName(value = "token")
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
