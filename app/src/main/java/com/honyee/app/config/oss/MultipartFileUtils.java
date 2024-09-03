package com.honyee.app.config.oss;

import org.apache.http.entity.ContentType;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * 文件处理类型，包括文件操作和判断
 */
public class MultipartFileUtils {

    private MultipartFileUtils(){
    }

    private static final List<ContentType> MIME_TYPES = List.of(
        ContentType.IMAGE_JPEG,
        ContentType.IMAGE_PNG,
        ContentType.IMAGE_GIF,
        ContentType.IMAGE_BMP,
        ContentType.IMAGE_SVG,
        ContentType.IMAGE_WEBP,
        ContentType.IMAGE_TIFF
    );

    public static boolean isImageContentType(MultipartFile file) {
        ContentType contentType = ContentType.getByMimeType(
            Optional.ofNullable(file.getContentType()).map(String::toLowerCase).orElse(null)
        );
        return MIME_TYPES.contains(contentType);
    }

    public static boolean isImageContentType(MediaType mediaType) {
        return Arrays.asList(MediaType.IMAGE_JPEG, MediaType.IMAGE_GIF, MediaType.IMAGE_PNG).contains(mediaType);
    }

    /**
     * 获取文件扩展名
     */
    public static String getFileExtensionName(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null) {
            return ".";
        }
        String[] split = contentType.split("/");
        String ext = split[1];
        // 因支付宝只支持jpg不支持jpeg
        if ("jpeg".equals(ext)) {
            ext = "jpg";
        }
        return "." + ext;
    }
}
