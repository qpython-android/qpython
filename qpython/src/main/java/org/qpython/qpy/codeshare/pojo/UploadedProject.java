package org.qpython.qpy.codeshare.pojo;


import java.util.List;

public class UploadedProject {
    private String name;
    private int    allFilesCount, uploadFilesCount;
    private List<String> subFilePath;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAllFilesCount() {
        return allFilesCount;
    }

    public void setAllFilesCount(int allFilesCount) {
        this.allFilesCount = allFilesCount;
    }

    public int getUploadFilesCount() {
        return uploadFilesCount;
    }

    public void setUploadFilesCount(int uploadFilesCount) {
        this.uploadFilesCount = uploadFilesCount;
    }

    public List<String> getSubFilePath() {
        return subFilePath;
    }

    public void setSubFilePath(List<String> subFilePath) {
        this.subFilePath = subFilePath;
    }

    public boolean isAllUpload() {
        return allFilesCount == uploadFilesCount;
    }
}
