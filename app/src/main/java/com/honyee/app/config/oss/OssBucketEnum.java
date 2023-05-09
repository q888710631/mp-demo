package com.honyee.app.config.oss;

/**
 * OSS的bucket枚举
 */
public enum OssBucketEnum {
    /**
     * 默认bucket
     */
    COMMON("common");

    private final String bucketName;

    OssBucketEnum(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getBucketName() {
        return bucketName;
    }
}
