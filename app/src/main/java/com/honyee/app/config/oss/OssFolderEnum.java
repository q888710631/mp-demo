package com.honyee.app.config.oss;

import com.honyee.app.config.Constants;

/**
 * OSS目录枚举，OSS采用对应存储对象的key中的“/”作为目录分隔符，目前仅考虑单层目录，所以该枚举中仅有第一层目录名称
 */
public enum OssFolderEnum {
    /**
     * 公用图片目录
     */
    IMAGE("image/common", 5 * Constants.MB_TO_BYTE),
    /**
     * json文件目录
     */
    JSON("json/common", 5 * Constants.MB_TO_BYTE),
    /**
     * 不限制类型
     */
    OTHER("other/common", 1024 * Constants.MB_TO_BYTE),

    ;


    private final String folderName;

    /**
     * 文件最大限制，字节
     */
    private final long fileMaxSize;

    OssFolderEnum(String folderName, long fileMaxSize) {
        this.folderName = folderName;
        this.fileMaxSize = fileMaxSize;
    }

    public String getFolderName() {
        return folderName;
    }

    public long getFileMaxSize() {
        return fileMaxSize;
    }
}
