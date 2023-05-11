package com.honyee.app.utils;

import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class ExcelUtil {
    public static final String HEADER_CONTENT_TYPE_DOWNLOAD_EXCEL = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    public static final String HEADER_CONTENT_DISPOSITION_KEY = "Content-disposition";
    public static final String HEADER_CONTENT_DISPOSITION_VALUE = "attachment;filename=%s.xlsx";

    /**
     * 导出excel时初始化response的header
     */
    public static void initResponse(HttpServletResponse response, String fileName) {
        String encodeFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setContentType(HEADER_CONTENT_TYPE_DOWNLOAD_EXCEL);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setHeader(HEADER_CONTENT_DISPOSITION_KEY, String.format(HEADER_CONTENT_DISPOSITION_VALUE, encodeFileName));
    }

}
