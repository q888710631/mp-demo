package com.honyee.app.config.oss;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.ObjectMetadata;
import com.honyee.app.utils.UUIDUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 阿里云OSS工具
 */
public class AlipayOssUtil {

    private final OSS oss;

    private final AlipayOssConfig alipayOssConfig;

    private static final String FOLDER_SEPARATOR = "/";

    private static final String URL_HOST_SEPARATOR = ".";

    public AlipayOssUtil() {
        this.oss = null;
        this.alipayOssConfig = new AlipayOssConfig();
    }

    public AlipayOssUtil(OSS oss, AlipayOssConfig alipayOssConfig) {
        this.oss = oss;
        this.alipayOssConfig = alipayOssConfig;
    }

    public AlipayOssConfig getAlipayOssConfig() {
        return alipayOssConfig;
    }

    public String getUrl(String key) {
        return getUrl(OssFolderEnum.IMAGE, key);
    }

    public String getUrl(OssFolderEnum folder, String key) {
        return (
            alipayOssConfig.getUrlProtocol() +
            alipayOssConfig.getBucketName() +
            URL_HOST_SEPARATOR +
            alipayOssConfig.getEndpoint() +
            FOLDER_SEPARATOR +
            getAbsoluteKey(folder, key)
        );
    }

    /**
     * 获取完整的访问URL
     */
    public String getUrlImageByOtherUrl(OssFolderEnum folder, String url, String handleParams) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setReadTimeout(5000);
        connection.setConnectTimeout(5000);
        connection.setRequestMethod("GET");
        try (InputStream inputStream = connection.getInputStream()) {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(connection.getHeaderField("Content-Type"));
            String result =
                alipayOssConfig.getUrlImage() +
                FOLDER_SEPARATOR +
                getAbsoluteKey(folder, putObject(folder, UUIDUtils.getUUID(), inputStream, metadata));
            if (StringUtils.isNotBlank(handleParams)) {
                result = result + "?" + handleParams;
            }
            return result;
        }
    }

    /**
     * 获取完整的访问URL
     */
    public String getUrlImage(OssFolderEnum folder, String key) {
        return (alipayOssConfig.getUrlImage() + FOLDER_SEPARATOR + getAbsoluteKey(folder, key));
    }

    /**
     * 获取完整的文件路径
     */
    public String getAbsoluteKey(OssFolderEnum folder, String key) {
        // 目录环境
        String folderPrefix = alipayOssConfig.getFolderEnv();
        folderPrefix = folderPrefix == null ? "" : folderPrefix;
        // 目录根
        String folderRoot = alipayOssConfig.getFolderRoot();
        folderRoot = folderRoot == null ? "" : folderRoot;
        return folderPrefix + FOLDER_SEPARATOR + folderRoot + FOLDER_SEPARATOR + folder.getFolderName() + FOLDER_SEPARATOR + key;
    }

    public OSSObject getObjectByKey(OssFolderEnum folder, String key) {
        return oss.getObject(alipayOssConfig.getBucketName(), getAbsoluteKey(folder, key));
    }

    public OSSObject getObject(OssFolderEnum folder, String key) {
        return oss.getObject(alipayOssConfig.getBucketName(), folder.getFolderName() + FOLDER_SEPARATOR + key);
    }

    public String putObject(OssFolderEnum folder, File file) {
        return putObject(folder, UUIDUtils.getUUID() + getSuffix(file), file, null);
    }

    public String putObject(OssFolderEnum folder, String key, File file, ObjectMetadata metadata) {
        checkPutObject(folder, key);
        String eTag = oss.putObject(alipayOssConfig.getBucketName(), getAbsoluteKey(folder, key), file, metadata).getETag();
        return StringUtils.isNotBlank(eTag) ? key : "";
    }

    public String putObject(OssFolderEnum folder, MultipartFile file) throws IOException {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        return putObject(folder, UUIDUtils.getUUID(), file.getInputStream(), metadata);
    }

    public String putObject(OssFolderEnum folder, InputStream input) {
        return putObject(folder, UUIDUtils.getUUID(), input, null);
    }

    public String putObject(OssFolderEnum folder, String key, InputStream input, ObjectMetadata metadata) {
        checkPutObject(folder, key);
        String eTag = oss.putObject(alipayOssConfig.getBucketName(), getAbsoluteKey(folder, key), input, metadata).getETag();
        return StringUtils.isNotBlank(eTag) ? key : "";
    }

    private void checkPutObject(OssFolderEnum folder, String key) {
        Assert.notNull(key, "上传文件失败，key不能为空");
        Assert.notNull(folder, "上传文件失败，folder不能为空");
    }

    private static String getSuffix(File file) {
        return file.getName().substring(file.getName().lastIndexOf("."));
    }
}
