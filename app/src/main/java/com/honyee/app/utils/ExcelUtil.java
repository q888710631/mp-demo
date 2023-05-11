package com.honyee.app.utils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

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

    /**
     * 构建工作簿
     * @param title 工作簿名称
     * @param clz 输出数据的DTO
     */
    public static WriteSheet buildWriteSheet(String title, Class<?> clz) {
        return EasyExcel.writerSheet(title).head(clz).build();
    }
    /**
     * 输出数据
     */
    public static void write(HttpServletResponse response, String fileName, List<?> list, Class<?> clz) throws IOException {
        write(response, fileName, list, buildWriteSheet("Sheet1", clz));
    }

    /**
     * 输出数据
     */
    public static void write(HttpServletResponse response, String fileName, List<?> list, WriteSheet sheet) throws IOException {
        ExcelUtil.initResponse(response, fileName);
        ExcelWriter writer = null;
        try {

            writer = EasyExcel.write(response.getOutputStream()).build();
            writer.write(list, sheet);
        } finally {
            if (writer != null) {
                writer.finish();
            }
        }
    }

}
