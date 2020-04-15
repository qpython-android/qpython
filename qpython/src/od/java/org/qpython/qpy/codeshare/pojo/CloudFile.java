package org.qpython.qpy.codeshare.pojo;


import org.qpython.qpy.codeshare.CONSTANT;
import org.qpython.qpysdk.QPyConstants;

import java.io.Serializable;

public class CloudFile implements Serializable {
    private String  projectName;
    private String  path;
    private String  uploadTime;
    private String  name;
    private String  content;
    private String  key;
    private boolean uploading;

    public String getPath() {
        if (projectName == null) {
            if (path.contains("scripts3")) {
                return "/" + path.replace(CONSTANT.SLASH_REPLACE, "/").replace(CONSTANT.DOT_REPLACE, ".");
            } else if (path.contains("scripts")) {
                return path.replace(CONSTANT.SLASH_REPLACE, "/") + getName();
            } else {
                return "/" + path.replace(CONSTANT.SLASH_REPLACE, "/").replace(CONSTANT.DOT_REPLACE, ".");
            }
        } else {
            String projNode = path.contains("projects3") ? "projects3/" : "projects/";
            return "/" + projNode + getProjectName() + path
                    .replace(CONSTANT.SLASH_REPLACE, "/")
                    .replace(CONSTANT.DOT_REPLACE, ".")
                    .replace(projNode, "")
                    .replace(getProjectName(), "");
        }
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(String uploadTime) {
        this.uploadTime = uploadTime;
    }

    public String getName() {
        return name.replace(CONSTANT.DOT_REPLACE, ".");
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getContentSize() {
        return content == null ? 0 : content.length();
    }

    public String getProjectName() {
        return projectName == null ? null : projectName.
                replace(CONSTANT.SLASH_REPLACE, "/").replace(CONSTANT.DOT_REPLACE, ".");
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public boolean isUploading() {
        return uploading;
    }

    public void setUploading(boolean uploading) {
        this.uploading = uploading;
    }

    public String getKey() {
        return key == null ? getName().replace(".", CONSTANT.DOT_REPLACE) : key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getAbsolutePath() {
        return QPyConstants.ABSOLUTE_PATH + getPath();
    }
}
