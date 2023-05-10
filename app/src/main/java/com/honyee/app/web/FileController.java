package com.honyee.app.web;

import com.aliyun.oss.model.ObjectMetadata;
import com.honyee.app.config.oss.MultipartFileUtils;
import com.honyee.app.config.oss.OssFolderEnum;
import com.honyee.app.config.oss.OssUtil;
import com.honyee.app.dto.file.FileResDTO;
import com.honyee.app.dto.file.UploadImageDTO;
import com.honyee.app.dto.file.UploadImageResultDTO;
import com.honyee.app.exp.CommonException;
import com.honyee.app.utils.LogUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 文件操作类
 */
@RestController
@RequestMapping("/api/file")
@Schema(name = "文件相关接口")
public class FileController {

    @Autowired
    private OssUtil ossUtils;

    @Schema(name = "图片上传接口")
    @PostMapping(value = "/image")
    public FileResDTO imageUpload(@RequestPart("file") MultipartFile file) {
        try {
            if (MultipartFileUtils.isImageContentType(file)) {
                String key = ossUtils.putObject(OssFolderEnum.COMMON, file);
                String ossUrl = ossUtils.getUrlImage(OssFolderEnum.COMMON, key);
                return new FileResDTO(key, ossUrl);
            }
        } catch (IOException e) {
            LogUtil.error("上传图片出现异常", e);
            throw new CommonException("上传失败，请稍后重试");
        }
        throw new CommonException("不支持的图片格式");
    }

    @Schema(name = "图片获取接口")
    @GetMapping("/image/url/{key}")
    public FileResDTO imageUrlGet(@PathVariable String key) {
        return new FileResDTO(key, ossUtils.getUrlImage(OssFolderEnum.COMMON, key));
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
                    LogUtil.info("上传图片出现异常：base64数据格式不正确");
                    return error;
                }
                // 获取 contentType
                String contentType = value.split(";")[0].split(":")[1];
                // 获取文件扩展名
                String fileExtendName = contentType.split("/")[1];
                // base64 转 bytes
                byte[] file = Base64.decodeBase64(base64);
                ByteArrayInputStream input = new ByteArrayInputStream(file);
                // 需要这个，不然打开地址时会变成下载
                ObjectMetadata objectMeta = new ObjectMetadata();
                objectMeta.setContentLength(file.length);
                objectMeta.setContentType(contentType);
                String key = ossUtils.putObject(
                    OssFolderEnum.COMMON,
                    String.format("%s.%s", UUID.randomUUID(), fileExtendName),
                    input,
                    objectMeta
                );
                return ossUtils.getUrlImage(OssFolderEnum.COMMON, key);
            }
        } catch (Exception e) {
            LogUtil.error("上传图片出现异常：{0}", e);
        }
        return error;
    }

    @Schema(name = "图片上传接口")
    @PostMapping(value = "/image/batch")
    public ResponseEntity<UploadImageResultDTO> imageUploadBatch(@RequestPart("files") List<MultipartFile> files) {
        return ResponseEntity.ok(new UploadImageResultDTO(files.stream().map(this::uploadToOss).collect(Collectors.toList())));
    }

    private String uploadToOss(MultipartFile file) {
        try {
            if (MultipartFileUtils.isImageContentType(file)) {
                String key = ossUtils.putObject(OssFolderEnum.COMMON, file);
                return ossUtils.getUrlImage(OssFolderEnum.COMMON, key);
            }
        } catch (IOException e) {
            LogUtil.error("上传图片出现异常", e);
        }
        LogUtil.info("上传图片出现异常，不支持的图片格式：{}", file.getContentType());
        return "error";
    }

}
