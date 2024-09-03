package com.honyee.app.web;

import com.aliyun.oss.model.ObjectMetadata;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.auth.sts.AssumeRoleRequest;
import com.aliyuncs.auth.sts.AssumeRoleResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.http.ProtocolType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.honyee.app.config.http.MyResponse;
import com.honyee.app.config.oss.*;
import com.honyee.app.dto.file.*;
import com.honyee.app.exp.CommonException;
import com.honyee.app.utils.UUIDUtils;
import com.tencent.cloud.CosStsClient;
import com.tencent.cloud.Response;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 文件上传
 */
@Slf4j
@RestController
@RequestMapping("/api/file")
@Schema(name = "文件上传")
public class FileController implements SmartInitializingSingleton {

    @Resource
    private AlipayOssUtil alipayOssUtils;

    @Resource
    private TencentOssUtil tencentOssUtil;

    @Value("${application.oss.enable}")
    private String ossEnableTarget;

    private DefaultAcsClient alipayAcsClient;

    @Resource
    private ObjectMapper objectMapper;

    @Override
    public void afterSingletonsInstantiated() {
        if (useAlipayOss()) {
            try {
                AlipayOssConfig alipayOssConfig = alipayOssUtils.getAlipayOssConfig();
                DefaultProfile.addEndpoint(alipayOssConfig.getStsRegionId(), "Sts", alipayOssConfig.getStsDomain());
                // STS
                IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", alipayOssConfig.getAccessKeyId(), alipayOssConfig.getAccessKeySecret());
                alipayAcsClient = new DefaultAcsClient(profile);
            } catch (Exception e) {
                log.error("初始化支付宝OSS endpoint错误", e);
            }

        }


    }

    private boolean useAlipayOss() {
        return "alipay".equals(ossEnableTarget);
    }

    public String putObject(OssFolderEnum folder, MultipartFile file) throws IOException {
        if (useAlipayOss()) {
            return alipayOssUtils.putObject(folder, file);
        }
        return tencentOssUtil.putObject(folder, file);
    }

    public String getUrlImage(OssFolderEnum folder, String key) {
        if (useAlipayOss()) {
            return alipayOssUtils.getUrlImage(folder, key);
        }
        return tencentOssUtil.getUrlImage(folder, key);
    }

    @Schema(name = "前端自主上传前获取临时token")
    @GetMapping("/prepare")
    public FilePrepareDTO prepare(OssFolderEnum folder) {
        if (folder == null) {
            folder = OssFolderEnum.IMAGE;
        }


        if (useAlipayOss()) {
            return prepareAlipay(folder);
        }
        return prepareTencent(folder);
    }

    /**
     * 阿里云临时token，指定文件上传路径和文件名
     */
    private FilePrepareDTO prepareAlipay(OssFolderEnum folder) {
        FilePrepareDTO res = new FilePrepareDTO();
        String fileKey = UUIDUtils.getUUID();
        String filePath = alipayOssUtils.getAbsoluteKey(folder, fileKey);
        res.setKey(fileKey);
        res.setPath(filePath);
        AlipayOssConfig alipayOssConfig = alipayOssUtils.getAlipayOssConfig();
        try {
            final AssumeRoleRequest request = new AssumeRoleRequest();
            request.setSysMethod(MethodType.POST);
            request.setSysProtocol(ProtocolType.HTTPS);
            request.setDurationSeconds(1800L); // 最小值900
            request.setRoleArn(alipayOssConfig.getStsRoleArn());  // 要扮演的角色ID
            request.setRoleSessionName("external-username"); // 角色会话名称。通常设置为调用该API的用户身份。

            Map<String, Object> policyMap = new HashMap<>();
            policyMap.put("Version", "1");

            List<Object> statement = new ArrayList<>();
            policyMap.put("Statement", statement);
            // 具体策略
            Map<String, Object> statementMap = new HashMap<>();
            statement.add(statementMap);
            // 允许执行的操作
            statementMap.put("Action", List.of("oss:PutObject"));
            // 指定上传的文件路径：空间/目录/文件名  acs:oss:your_bucket_name:your_object_name
            statementMap.put("Resource", List.of(String.format("acs:oss:%s:%s", alipayOssConfig.getBucketName(), res.getPath())));
            // 该配置的开关
            statementMap.put("Effect", "Allow");

            try {
                request.setPolicy(objectMapper.writeValueAsString(policyMap));
            } catch (JsonProcessingException e) {
                log.error("", e);
                return null;
            }

            // 生成临时授权凭证
            AssumeRoleResponse response = alipayAcsClient.getAcsResponse(request);

            String accessKeyId = response.getCredentials().getAccessKeyId();  // 临时凭据AccessKeyId
            String accessKeySecret = response.getCredentials().getAccessKeySecret();  // 临时凭据AccessKeySecret
            String securityToken = response.getCredentials().getSecurityToken();
            String expiration = response.getCredentials().getExpiration();
            res.setAccessKeyId(accessKeyId);
            res.setAccessKeySecret(accessKeySecret);
            res.setSecurityToken(securityToken);
            return res;
        } catch (ClientException e) {
            log.error("获取阿里云OSS 临时token错误", e);
            res.setMessage(e.getMessage());
            return res;
        }
    }

    /**
     * 腾讯云临时token
     */
    private FilePrepareDTO prepareTencent(OssFolderEnum folder) {
        FilePrepareDTO res = new FilePrepareDTO();
        String fileKey = UUIDUtils.getUUID();
        String filePath = tencentOssUtil.getAbsoluteKey(folder, fileKey);
        res.setKey(fileKey);
        res.setPath(filePath);

        TencentOssConfig tencentOssConfig = tencentOssUtil.getTencentOssConfig();
        TreeMap<String, Object> config = new TreeMap<>();

        try {
            //这里的 SecretId 和 SecretKey 代表了用于申请临时密钥的永久身份（主账号、子账号等），子账号需要具有操作存储桶的权限。
            String secretId = tencentOssConfig.getSecretId();//用户的 SecretId，建议使用子账号密钥，授权遵循最小权限指引，降低使用风险。子账号密钥获取可参见 https://cloud.tencent.com/document/product/598/37140
            String secretKey = tencentOssConfig.getSecretKey();//用户的 SecretKey，建议使用子账号密钥，授权遵循最小权限指引，降低使用风险。子账号密钥获取可参见 https://cloud.tencent.com/document/product/598/37140
            String appid = tencentOssConfig.getAppid();
            String bucket = tencentOssConfig.getBucket() + "-" + appid;
            String region = tencentOssConfig.getRegion();
            // 替换为您的云 api 密钥 SecretId
            config.put("secretId", secretId);
            // 替换为您的云 api 密钥 SecretKey
            config.put("secretKey", secretKey);

            // 设置域名:
            // 如果您使用了腾讯云 cvm，可以设置内部域名
            //config.put("host", "sts.internal.tencentcloudapi.com");

            // 临时密钥有效时长，单位是秒，默认 1800 秒，目前主账号最长 2 小时（即 7200 秒），子账号最长 36 小时（即 129600）秒
            config.put("durationSeconds", 1800);

            // 换成您的 bucket
            config.put("bucket", bucket);
            // 换成 bucket 所在地区
            // 密钥的权限列表。必须在这里指定本次临时密钥所需要的权限。
            // 简单上传、表单上传和分块上传需要以下的权限，其他权限列表请参见 https://cloud.tencent.com/document/product/436/31923
            String[] allowActions = new String[]{
                // 简单上传
                "name/cos:PutObject",
                // 表单上传、小程序上传
                "name/cos:PostObject",
                // 分块上传
                "name/cos:InitiateMultipartUpload",
                "name/cos:ListMultipartUploads",
                "name/cos:ListParts",
                "name/cos:UploadPart",
                "name/cos:CompleteMultipartUpload"
            };
            config.put("allowActions", allowActions);
            //# 临时密钥生效条件，关于condition的详细设置规则和COS支持的condition类型可以参考 https://cloud.tencent.com/document/product/436/71307
            Map<String, Object> policyMap = new HashMap<>();
            policyMap.put("version", "2.0");
            Map<String, Object> statementMap = new HashMap<>();
            policyMap.put("statement", List.of(statementMap));
            statementMap.put("effect", "allow");
            statementMap.put("action", List.of(
                "name/cos:PutObject",
                "name/cos:PostObject",
                "name/cos:InitiateMultipartUpload",
                "name/cos:ListMultipartUploads",
                "name/cos:ListParts",
                "name/cos:UploadPart",
                "name/cos:CompleteMultipartUpload"
            ));
            config.put("region", region);
            // 这里改成允许的路径前缀，可以根据自己网站的用户登录态判断允许上传的具体路径
            // 列举几种典型的前缀授权场景：
            // 1、允许访问所有对象："*"
            // 2、允许访问指定的对象："a/a1.txt", "b/b1.txt"
            // 3、允许访问指定前缀的对象："a*", "a/*", "b/*"
            // 如果填写了“*”，将允许用户访问所有资源；除非业务需要，否则请按照最小权限原则授予用户相应的访问权限范围。
            config.put("allowPrefixes", new String[]{
                res.getPath()
            });
            statementMap.put("resource", List.of(
                String.format("qcs::cos:%s:uid/%s:%s/*",
                    region,
                    appid,
                    bucket
                )
            ));

            config.put("policy", new ObjectMapper().writeValueAsString(policyMap));
            Response response = CosStsClient.getCredential(config);
            res.setAccessKeyId(response.credentials.tmpSecretId);
            res.setAccessKeySecret(response.credentials.tmpSecretKey);
            res.setSecurityToken(response.credentials.sessionToken);
            return res;
        } catch (Exception e) {
            log.error("获取腾讯云COS 临时token错误", e);
            res.setMessage(e.getMessage());
            return res;
        }
    }

    @Schema(name = "图片上传接口")
    @PostMapping(value = "/image")
    public FileResDTO imageUpload(@RequestPart("file") MultipartFile file) {
        try {
            if (MultipartFileUtils.isImageContentType(file)) {
                String key = putObject(OssFolderEnum.IMAGE, file);
                String ossUrl = getUrlImage(OssFolderEnum.IMAGE, key);
                return new FileResDTO(key, ossUrl);
            }
        } catch (IOException e) {
            log.error("上传图片出现异常", e);
            throw new CommonException("上传失败，请稍后重试");
        }
        throw new CommonException("不支持的图片格式");
    }

    @Schema(name = "图片获取接口")
    @GetMapping("/image/url/{key}")
    public FileResDTO imageUrlGet(@PathVariable String key) {
        return new FileResDTO(key, getUrlImage(OssFolderEnum.IMAGE, key));
    }

    @Schema(name = "图片上传接口，支持base64或者 url")
    @PostMapping(value = "/image/base64-or-url")
    public UploadImageResultDTO base64OrUrl(@Valid @RequestBody UploadImageDTO dto) {
        return new UploadImageResultDTO(dto.getValues().stream().map(this::uploadToOss).collect(Collectors.toList()));
    }

    /**
     * 图片上传到oss
     *
     * @param value base64 或者 url
     * @return 成功时返回ossUrl，失败时固定返回error
     */
    private String uploadToOss(String value) {
        String error = "error";
        try {
            if (value.matches("^(?i)data:image/(jp(e)?g|git|png|bmp|);base64,.+")) {
                String base64 = value.split(",")[1];
                if (!Base64.isBase64(base64)) {
                    log.info("上传图片出现异常：base64数据格式不正确");
                    return error;
                }
                // 获取 contentType
                String contentType = value.split(";")[0].split(":")[1];
                // 获取文件扩展名
                String fileExtendName = contentType.split("/")[1];
                // base64 转 bytes
                byte[] file = Base64.decodeBase64(base64);
                ByteArrayInputStream input = new ByteArrayInputStream(file);
                OssFolderEnum folder = OssFolderEnum.IMAGE;
                if (useAlipayOss()) {
                    // 需要这个，不然打开地址时会变成下载
                    ObjectMetadata objectMeta = new ObjectMetadata();
                    objectMeta.setContentLength(file.length);
                    objectMeta.setContentType(contentType);
                    String key = alipayOssUtils.putObject(
                        folder,
                        String.format("%s.%s", UUID.randomUUID(), fileExtendName),
                        input,
                        objectMeta
                    );
                    return alipayOssUtils.getUrlImage(folder, key);
                } else {
                    String key = tencentOssUtil.putObject(folder, input, contentType);
                    return tencentOssUtil.getUrlImage(folder, key);
                }
            }
        } catch (Exception e) {
            log.error("上传图片出现异常：{0}", e);
        }
        return error;
    }

    @Schema(name = "图片上传接口")
    @PostMapping(value = "/image/batch")
    public ResponseEntity<UploadImageResultDTO> imageUploadBatch
        (@RequestPart("files") List<MultipartFile> files) {
        return ResponseEntity.ok(new UploadImageResultDTO(files.stream().map(this::uploadToOss).collect(Collectors.toList())));
    }

    private String uploadToOss(MultipartFile file) {
        try {
            if (MultipartFileUtils.isImageContentType(file)) {
                OssFolderEnum folder = OssFolderEnum.IMAGE;
                String key = putObject(folder, file);
                return getUrlImage(folder, key);
            }
        } catch (IOException e) {
            log.error("上传图片出现异常", e);
        }
        log.info("上传图片出现异常，不支持的图片格式：{}", file.getContentType());
        return "error";
    }

    @PostMapping(value = "/upload-json-or-gif", headers = "content-type=multipart/form-data")
    public MyResponse<UploadJsonOrGifResultDTO> uploadJsonOrGif(MultipartFile[] files) throws IOException {
        UploadJsonOrGifResultDTO result = new UploadJsonOrGifResultDTO();
        List<FileResDTO> errorTips = new ArrayList<>();
        if (files == null || files.length == 0) {
            result.setResult(false);
            result.setList(errorTips);
            FileResDTO fileResDTO = new FileResDTO("文件为空");
            errorTips.add(fileResDTO);
            return MyResponse.ok(result);
        }
        List<MultipartFile> uploadList = new ArrayList<>();
        for (MultipartFile file : files) {
            if (file.getContentType() == null) {
                continue;
            }
            switch (file.getContentType()) {
                case MediaType.APPLICATION_JSON_VALUE:
                case MediaType.IMAGE_GIF_VALUE:
                    long sizeKb = file.getSize() / 1024;
                    // 2MB
                    if (sizeKb > 2 * 1024) {
                        FileResDTO fileResDTO = new FileResDTO(String.format("【文件太大】%s", file.getOriginalFilename()));
                        errorTips.add(fileResDTO);
                    }
                    break;
                default:
                    FileResDTO fileResDTO = new FileResDTO(String.format("【文件格式不支持】%s", file.getOriginalFilename()));
                    errorTips.add(fileResDTO);
            }
            uploadList.add(file);
        }

        if (errorTips.isEmpty()) {
            List<FileResDTO> dataList = new ArrayList<>();
            result.setResult(true);
            result.setList(dataList);

            for (MultipartFile file : uploadList) {
                if (file.getContentType() == null) {
                    continue;
                }
                switch (file.getContentType()) {
                    case MediaType.APPLICATION_JSON_VALUE: {
                        String key = putObject(OssFolderEnum.JSON, file);
                        String ossUrl = getUrlImage(OssFolderEnum.JSON, key);
                        dataList.add(new FileResDTO(key, ossUrl));
                        break;
                    }
                    case MediaType.IMAGE_GIF_VALUE: {
                        String key = putObject(OssFolderEnum.IMAGE, file);
                        String ossUrl = getUrlImage(OssFolderEnum.IMAGE, key);
                        dataList.add(new FileResDTO(key, ossUrl));
                        break;
                    }
                    default:
                }
            }

        } else {
            result.setResult(false);
            result.setList(errorTips);
        }
        return MyResponse.ok(result);
    }


}
