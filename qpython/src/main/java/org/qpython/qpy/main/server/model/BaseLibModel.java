package org.qpython.qpy.main.server.model;

import java.io.Serializable;

/**
 * Created by Hmei on 2017-06-23.
 */

public class BaseLibModel implements Serializable{
    protected String src;
    protected String rdate;
    protected String link;
    protected String tmodule;
    protected String smodule;
    protected String title;
    protected String ver;
    protected String cat;
    protected String downloads;
    protected String description;

    boolean installed;

    public BaseLibModel(String title) {
        this.title = title;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getRdate() {
        return rdate;
    }

    public void setRdate(String rdate) {
        this.rdate = rdate;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getTmodule() {
        return tmodule;
    }

    public String getSmodule() {
        return smodule;
    }

    public void setTmodule(String tmodule) {
        this.tmodule = tmodule;
    }

    public void setSmodule(String smodule) {
        this.smodule = smodule;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVer() {
        return ver;
    }

    public void setVer(String ver) {
        this.ver = ver;
    }

    public String getDownloads() {
        return downloads == null ? "-1" : downloads;
    }

    public void setDownloads(String downloads) {
        this.downloads = downloads;
    }

    public String getCat() {
        return cat;
    }

    public void setCat(String cat) {
        this.cat = cat;
    }

    public boolean isInstalled() {
        return installed;
    }

    public void setInstalled(boolean installed) {
        this.installed = installed;
    }
}
