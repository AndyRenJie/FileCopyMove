package com.andy.filecopymove.sample.model;

import java.io.Serializable;

/**
 * 文件操作对象
 */
public class FileModel implements Serializable {
    /**
     * 文件Id
     */
    private String fileId;
    /**
     * 文件名称
     */
    private String fileName;
    /**
     * 文件图标
     */
    private String fileIcon;
    /**
     * 文件路径
     */
    private String filePath;
    /**
     * 文件日期
     */
    private long fileDate;
    /**
     * 文件大小
     */
    private long fileSize;
    /**
     * 文件来源、所在位置
     */
    private String fileFrom;
    /**
     * 是否文件
     */
    private boolean isFile;
    /**
     * 是不是文件夹
     */
    private boolean isDirectory;
    /**
     * 是否跳过
     */
    private boolean isSkip;

    public FileModel() {
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileIcon() {
        return fileIcon;
    }

    public void setFileIcon(String fileIcon) {
        this.fileIcon = fileIcon;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public long getFileDate() {
        return fileDate;
    }

    public void setFileDate(long fileDate) {
        this.fileDate = fileDate;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileFrom() {
        return fileFrom;
    }

    public void setFileFrom(String fileFrom) {
        this.fileFrom = fileFrom;
    }

    public boolean isFile() {
        return isFile;
    }

    public void setFile(boolean file) {
        isFile = file;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public void setDirectory(boolean directory) {
        isDirectory = directory;
    }

    public boolean isSkip() {
        return isSkip;
    }

    public void setSkip(boolean skip) {
        isSkip = skip;
    }

    @Override
    public String toString() {
        return "FileModel{" +
                "fileId='" + fileId + '\'' +
                ", fileName='" + fileName + '\'' +
                ", fileIcon='" + fileIcon + '\'' +
                ", filePath='" + filePath + '\'' +
                ", fileDate=" + fileDate +
                ", fileSize=" + fileSize +
                ", fileFrom='" + fileFrom + '\'' +
                ", isFile=" + isFile +
                ", isDirectory=" + isDirectory +
                ", isSkip=" + isSkip +
                '}';
    }
}
