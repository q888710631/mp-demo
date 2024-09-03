package com.honyee.app.dto.file;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 文件上传准备，通过临时凭据是否为空来判断是否获取成功
 */
public class FilePrepareDTO {
    @Schema(name = "文件名")
    private String key;
    @Schema(name = "文件路径")
    private String path;
    @Schema(name = "文件访问链接")
    private String url;

    @Schema(name = "临时凭据AccessKeyId")
    private String accessKeyId;

    @Schema(name = "临时凭据AccessKeySecret")
    private String accessKeySecret;

    @Schema(name = "文件上传临时token")
    private String securityToken;

    @Schema(name = "一般为错误信息")
    private String message;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSecurityToken() {
        return securityToken;
    }

    public void setSecurityToken(String securityToken) {
        this.securityToken = securityToken;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
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
}
