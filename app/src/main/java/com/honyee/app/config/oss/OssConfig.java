package com.honyee.app.config.oss;

import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.OSSClientBuilder;
import com.honyee.app.utils.LogUtil;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ConfigurationProperties(prefix = "application.oss")
public class OssConfig {

    /**
     * 结尾不要带 /
     */
    private String endpoint;

    private String accessKeyId;

    private String accessKeySecret;

    private String urlProtocol;

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

    /**
     * 访问图片的域名
     * 结尾不要带 /
     */
    private String urlImage;

    private long expireMillis = DateUtils.MILLIS_PER_DAY;

    private ClientBuilderConfiguration client = new ClientBuilderConfiguration();

    @Bean
    public OssUtil ossUtil() {
        try {
            return new OssUtil(new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret, client), this);
        } catch (Exception e) {
            LogUtil.warn("OSS初始化失败，图片上传不可用：{}", e.getMessage());
        }
        return new OssUtil();
    }

    public String getUrlProtocol() {
        return urlProtocol;
    }

    public void setUrlProtocol(String urlProtocol) {
        this.urlProtocol = urlProtocol;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public String getAccessKeySecret() {
        return accessKeySecret;
    }

    public void setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
    }

    public ClientBuilderConfiguration getClient() {
        return client;
    }

    public void setClient(ClientBuilderConfiguration client) {
        this.client = client;
    }

    public long getExpireMillis() {
        return expireMillis;
    }

    public void setExpireMillis(long expireMillis) {
        this.expireMillis = expireMillis;
    }

    public String getFolderEnv() {
        return folderEnv;
    }

    public void setFolderEnv(String folderEnv) {
        this.folderEnv = folderEnv;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public String getFolderRoot() {
        return folderRoot;
    }

    public void setFolderRoot(String folderRoot) {
        this.folderRoot = folderRoot;
    }
}
