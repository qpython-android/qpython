package org.qpython.qpy.main.receiver;


public class NotificationBean {

    /**
     * type : <类型  ext/in 分别为外部浏览器打开或者系统内链接>
     * title : <状态栏提示>
     * msg : <详情>
     * link : <打开的链接, http://xxx  或者 appname://somelink 这种>
     * extra : <额外参数, | 分割 如果link为 appname://somelink 这种，这块可能就需要>
     * force : <为true则关闭App notifications也仍然推送>
     */

    private String type;
    private String title;
    private String msg;
    private String link;
    private String extra;
    private boolean force;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public boolean isForce() {
        return force;
    }

    public void setForce(boolean force) {
        this.force = force;
    }
}
