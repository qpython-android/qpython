package org.qpython.qpy.main.server.gist.request;

import com.google.gson.annotations.SerializedName;

/**
 * 文 件 名: CreateRequest
 * 创 建 人: ZhangRonghua
 * 创建日期: 2018/3/9 10:18
 * 修改时间：
 * 修改备注：
 */

public class CreateRequest {
    private String title;
    private String code;
    private String desc;
    @SerializedName(value = "source_type")
    private String sourceType;

    @SerializedName(value = "is_share")
    private boolean isSahre;

    public CreateRequest(String title, String source, String desc, String token, String sourceType, boolean isSahre) {
        this.title = title;
        this.code = source;
        this.desc = desc;
        this.sourceType = sourceType;
        this.isSahre = isSahre;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
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
