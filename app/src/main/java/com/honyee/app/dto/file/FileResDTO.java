package com.honyee.app.dto.file;
/**
 * 文件相关返回类
 */
public class FileResDTO {

    private String key;

    private String url;

    public FileResDTO() {}

    public FileResDTO(String key, String url) {
        this.url = url;
        this.key = key;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "FileResDTO{" + "key='" + key + '\'' + ", url='" + url + '\'' + '}';
    }
}
