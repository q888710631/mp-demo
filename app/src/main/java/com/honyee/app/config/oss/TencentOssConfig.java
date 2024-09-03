package com.honyee.app.config.oss;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration(proxyBeanMethods = false)
@ConfigurationProperties(prefix = "application.oss.tencent")
public class TencentOssConfig {

    private String domain;

    /**
     * 目录前缀（环境），一般区分test/prod等
     * 前后不要带 /
     * 例如：
     * 正确 test
     * 错误 /test
     * 错误 test/
     */
    private String folderEnv;

    /**
     * 目录根
     */
    private String folderRoot;

    private String appid;

    private String region;

    private String bucket;

    private String secretId;

    private String secretKey;


    @Bean
    public TencentOssUtil tencentOssUtil() {
        TencentOssUtil tencentOssUtil = new TencentOssUtil(this);

        return tencentOssUtil;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getSecretId() {
        return secretId;
    }

    public void setSecretId(String secretId) {
        this.secretId = secretId;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getFolderEnv() {
        return folderEnv;
    }

    public void setFolderEnv(String folderEnv) {
        this.folderEnv = folderEnv;
    }

    public String getFolderRoot() {
        return folderRoot;
    }

    public void setFolderRoot(String folderRoot) {
        this.folderRoot = folderRoot;
    }
}
