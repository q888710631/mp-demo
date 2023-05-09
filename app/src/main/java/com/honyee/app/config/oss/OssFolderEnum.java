package com.honyee.app.config.oss;

/**
 * OSS目录枚举，OSS采用对应存储对象的key中的“/”作为目录分隔符，目前仅考虑单层目录，所以该枚举中仅有第一层目录名称
 */
public enum OssFolderEnum {
    /**
     * 公用图片目录
     */
    COMMON("image/common"),
    ;

    private final String folderName;

    OssFolderEnum(String folderName) {
        this.folderName = folderName;
    }

    public String getFolderName() {
        return folderName;
    }
}
