package org.qpython.qpy.plugin.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class LocalPluginBean extends RealmObject {

    @PrimaryKey
    private String name;

    private String title;

    private String src;

    public LocalPluginBean() {
    }

    public LocalPluginBean(String name, String title, String src) {
        this.name = name;
        this.title = title;
        this.src = src;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }
}
