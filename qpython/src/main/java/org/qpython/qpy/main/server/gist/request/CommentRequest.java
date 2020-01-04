package org.qpython.qpy.main.server.gist.request;

import com.google.gson.annotations.SerializedName;

/**
 * 文 件 名: CommentRequest
 * 创 建 人: ZhangRonghua
 * 创建日期: 2018/3/9 10:00
 * 修改时间：
 * 修改备注：
 */

public class CommentRequest {

    @SerializedName("from_content")
    private String from_content;
    @SerializedName("to_id")
    private String to_id;
    @SerializedName("gist_id")
    private String gist_id;

    public String getGist_id() {
        return gist_id;
    }

    public void setGist_id(String gist_id) {
        this.gist_id = gist_id;
    }


    public CommentRequest(String from_content, String to_id, String gist_id) {
        this.from_content = from_content;
        this.to_id = to_id;
        this.gist_id = gist_id;
    }

    public String getFrom_content() {
        return from_content;
    }

    public void setFrom_content(String from_content) {
        this.from_content = from_content;
    }

    public String getTo_id() {
        return to_id;
    }

    public void setTo_id(String to_id) {
        this.to_id = to_id;
    }
}
