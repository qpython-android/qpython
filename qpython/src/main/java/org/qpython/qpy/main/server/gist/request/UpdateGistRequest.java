package org.qpython.qpy.main.server.gist.request;

/**
 * 文 件 名: UpdateGistRequest
 * 创 建 人: ZhangRonghua
 * 创建日期: 2018/3/9 10:14
 * 修改时间：
 * 修改备注：
 */

public class UpdateGistRequest extends BaseRequest {

    private String title;
    private String code;
    private String desc;

    public UpdateGistRequest(String gistId, String title, String desc, String code) {
        super(gistId);
        this.title = title;
        this.code = code;
        this.desc = desc;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
