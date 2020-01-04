package org.qpython.qpy.main.server.gist.response;

/**
 * 文 件 名: ADBean
 * 创 建 人: ZhangRonghua
 * 创建日期: 2018/3/12 17:47
 * 修改时间：
 * 修改备注：
 */

public class ADBean {
    private String link;
    private String imgUrl;
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
