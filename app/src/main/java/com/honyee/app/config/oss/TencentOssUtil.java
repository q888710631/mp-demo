package com.honyee.app.config.oss;

import com.honyee.app.utils.UUIDUtils;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.BasicSessionCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.region.Region;
import com.tencent.cloud.CosStsClient;
import com.tencent.cloud.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.TreeMap;

@Slf4j
public class TencentOssUtil {

    private static final String FOLDER_SEPARATOR = "/";

    private TencentOssConfig tencentOssConfig;

    /**
     * 永久密钥创建的Client
     */
    private COSClient masterClient;

    public TencentOssUtil() {
    }

    public TencentOssUtil(TencentOssConfig tencentOssConfig) {
        this.tencentOssConfig = tencentOssConfig;
        if (tencentOssConfig.getAppid() == null) {
            log.info("腾讯COS未配置");
        } else {
            masterClient = initMasterClient();
        }
    }

    public TencentOssConfig getTencentOssConfig() {
        return tencentOssConfig;
    }

    public COSClient getMasterClient() {
        return masterClient;
    }

    public String getBuckName() {
        return tencentOssConfig.getBucket() + "-" + tencentOssConfig.getAppid();
    }

    /**
     * 获取完整的访问URL
     */
    public String getUrlImage(OssFolderEnum folder, String key) {
        return (tencentOssConfig.getDomain() + FOLDER_SEPARATOR + getAbsoluteKey(folder, key));
    }

    /**
     * 获取完整的文件路径
     */
    public String getAbsoluteKey(OssFolderEnum folder, String key) {
        // 目录环境
        String folderPrefix = tencentOssConfig.getFolderEnv();
        folderPrefix = folderPrefix == null ? "" : folderPrefix;
        // 目录根
        String folderRoot = tencentOssConfig.getFolderRoot();
        folderRoot = folderRoot == null ? "" : folderRoot;
        return folderPrefix + FOLDER_SEPARATOR + folderRoot + FOLDER_SEPARATOR + folder.getFolderName() + FOLDER_SEPARATOR + key;
    }

    /**
     * 临时密钥配置
     *
     * @param duration 密钥有效期
     */
    private TreeMap<String, Object> initConfig(int duration) {
        TreeMap<String, Object> config = new TreeMap<>();
        // 云 api 密钥 SecretId
        config.put("secretId", tencentOssConfig.getSecretId());
        // 云 api 密钥 SecretKey
        config.put("secretKey", tencentOssConfig.getSecretKey());

        // 设置域名,可通过此方式设置内网域名
        //config.put("host", "sts.internal.tencentcloudapi.com");

        // 临时密钥有效时长，单位是秒
        config.put("durationSeconds", duration);

        // 换成你的 bucket
        config.put("bucket", getBuckName());
        // 换成 bucket 所在地区
        config.put("region", tencentOssConfig.getRegion());

        // 可以通过 allowPrefixes 指定前缀数组, 例子： a.jpg 或者 a/* 或者 * (使用通配符*存在重大安全风险, 请谨慎评估使用)
        config.put("allowPrefixes", new String[]{
                "*"
        });

        // 密钥的权限列表。简单上传和分片需要以下的权限，其他权限列表请看 https://cloud.tencent.com/document/product/436/31923
        String[] allowActions = new String[]{
                // 简单上传
                "name/cos:PutObject",
                "name/cos:PostObject",
                // 分片上传
                "name/cos:InitiateMultipartUpload",
                "name/cos:ListMultipartUploads",
                "name/cos:ListParts",
                "name/cos:UploadPart",
                "name/cos:CompleteMultipartUpload"
        };
        config.put("allowActions", allowActions);
        return config;
    }

    /**
     * 创建一个客户端
     */
    public COSClient initTemplateClient() {
        // 1 传入获取到的临时密钥 (tmpSecretId, tmpSecretKey, sessionToken)
        Response credential = getCredential();
        String tmpSecretId = credential.credentials.tmpSecretId;
        String tmpSecretKey = credential.credentials.tmpSecretKey;
        String sessionToken = credential.credentials.sessionToken;
        BasicSessionCredentials cred = new BasicSessionCredentials(tmpSecretId, tmpSecretKey, sessionToken);
        // 2 设置 bucket 的地域
        // clientConfig 中包含了设置 region, https(默认 http), 超时, 代理等 set 方法, 使用可参见源码或者常见问题 Java SDK 部分
        Region region = new Region(tencentOssConfig.getRegion()); //COS_REGION 参数：配置成存储桶 bucket 的实际地域，例如 ap-beijing，更多 COS 地域的简称请参见 https://cloud.tencent.com/document/product/436/6224
        ClientConfig clientConfig = new ClientConfig(region);
        // 3 生成 cos 客户端
        return new COSClient(cred, clientConfig);
    }

    private COSClient initMasterClient() {
        // 1 初始化用户身份信息（secretId, secretKey）。
        // SECRETID 和 SECRETKEY 请登录访问管理控制台 https://console.cloud.tencent.com/cam/capi 进行查看和管理
        String secretId = tencentOssConfig.getSecretId();
        String secretKey = tencentOssConfig.getSecretKey();
        COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
        // 2 设置 bucket 的地域, COS 地域的简称请参见 https://cloud.tencent.com/document/product/436/6224
        // clientConfig 中包含了设置 region, https(默认 http), 超时, 代理等 set 方法, 使用可参见源码或者常见问题 Java SDK 部分。
        Region region = new Region("COS_REGION");
        ClientConfig clientConfig = new ClientConfig(region);
        // 这里建议设置使用 https 协议
        // 从 5.6.54 版本开始，默认使用了 https
        clientConfig.setHttpProtocol(HttpProtocol.https);
        // 3 生成 cos 客户端。
        return new COSClient(cred, clientConfig);
    }

    /**
     * 基本的临时密钥申请示例，适合对一个桶内的一批对象路径，统一授予一批操作权限
     */
    public Response getCredential() {
        try {
            TreeMap<String, Object> config = initConfig(60);
            return CosStsClient.getCredential(config);
        } catch (Exception e) {
            throw new IllegalArgumentException("no valid secret !");
        }
    }


    /**
     * 上传文件
     *
     * @return absoluteKey
     */
    public String putObject(OssFolderEnum folder, MultipartFile file) throws IOException {
        return putObject(folder, file.getInputStream(), file.getContentType());
    }

    /**
     * 上传文件
     *
     * @return absoluteKey
     */
    public String putObject(OssFolderEnum folder, InputStream inputStream, String contentType) throws IOException {
        // 临时Client
        COSClient cosClient = initTemplateClient();
        // 指定文件将要存放的存储桶
        String bucketName = getBuckName();
        // 指定文件上传到 COS 上的路径，即对象键。例如对象键为 folder/picture.jpg，则表示将文件 picture.jpg 上传到 folder 路径下
        String key = UUIDUtils.getUUID();
        String absoluteKey = getAbsoluteKey(folder, key);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(contentType);
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, absoluteKey, inputStream, metadata);
        PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);
        return absoluteKey;
    }

}
