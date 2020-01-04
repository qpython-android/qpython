package org.qpython.qpy.main.server.gist.response;

import com.google.gson.annotations.SerializedName;

/**
 * 文 件 名: ResponseBean
 * 创 建 人: ZhangRonghua
 * 创建日期: 2018/3/8 14:46
 * 修改时间：
 * 修改备注：
 */

public class ResponseBean<T> {
    @SerializedName(value = "msg")
    private String message;
    @SerializedName(value = "errno")
    private int code;
    @SerializedName(value = "data")
    private T data;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean success(){
        return code == 0;
    }
}
