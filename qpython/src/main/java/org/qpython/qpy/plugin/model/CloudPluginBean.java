package org.qpython.qpy.plugin.model;

import com.quseit.common.updater.updatepkg.UpdatePackage;
import org.qpython.qpy.plugin.CloudPluginManager;

import java.io.File;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

public class CloudPluginBean extends RealmObject implements UpdatePackage {
    @PrimaryKey
    private String name;

    private String title;

    private String src;

    private int versionCode;

    private String versionDescription;

    private String description;

    private String path;

    @Ignore
    private String url;

    public CloudPluginBean() {

    }

    public CloudPluginBean(String name, String title, String src, int versionCode,
                           String versionDescription, String description, String path, String url) {
        this.name = name;
        this.title = title;
        this.src = src;
        this.versionCode = versionCode;
        this.versionDescription = versionDescription;
        this.path = path;
        this.url = url;
        this.description = description;
    }

    @Override
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

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public void setVersionDescription(String versionDescription) {
        this.versionDescription = versionDescription;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String getVersion() {
        return String.valueOf(versionCode);
    }

    public String getVersionDescription() {
        return versionDescription;
    }

    @Override
    public String getDownloadUrl() {
        return url;
    }

    @Override
    public boolean checkVersion() {
        return CloudPluginManager.checkUpdate(this);
    }

    @Override
    public void install(File file) {
        CloudPluginManager.install(this, file, false);
    }
}
