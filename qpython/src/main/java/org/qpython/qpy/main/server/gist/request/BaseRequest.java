package org.qpython.qpy.main.server.gist.request;


import java.io.Serializable;

/**
 * 文 件 名: BaseRequest
 * 创 建 人: ZhangRonghua
 * 创建日期: 2018/3/9 10:07
 * 修改时间：
 * 修改备注：
 */

public class BaseRequest extends Request implements Serializable{
    public String gist_id;

    public BaseRequest(String gistId) {
        this.gist_id = gistId;
    }

    public String getGist_id() {
        return gist_id;
    }

    public void setGist_id(String gist_id) {
        this.gist_id = gist_id;
    }

}
