package org.qpython.qpy.texteditor.ui.adapter.bean;

import org.qpython.qpy.texteditor.common.CommonEnums.FileType;

import java.io.File;

/**
 * To store path list's item data
 * Created by Hmei on 2017-05-11.
 */

public class FolderBean {
    private File     file;
    private FileType type;
    private String   name;
    private String   path;
    private boolean  isUploading;

    public FolderBean(File file) {
        this.file = file;
        type = file.isDirectory() ? FileType.FOLDER : FileType.FILE;
        name = file.getName();
        path = file.getAbsolutePath();
    }

    public FileType getFolder() {
        return path.contains("/projects") ? FileType.PROJECT : FileType.SCRIPT;
    }

    public String getProjectPath() {
        if (getFolder().equals(FileType.PROJECT)) {
            String[] nodes = path.split("/");
            StringBuilder sb = new StringBuilder();
            boolean isProjNode = false;
            for (String node : nodes) {
                if (!isProjNode) {
                    sb.append(node).append("/");
                    isProjNode = node.startsWith("projects");
                } else {
                    sb.append(node);
                    return sb.toString();
                }
            }
            return "";
        } else {
            return "";
        }
    }

    public FileType getType() {
        return type;
    }

    public void setType(FileType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public boolean isUploading() {
        return isUploading;
    }

    public void setUploading(boolean uploading) {
        isUploading = uploading;
    }
}
