package com.honyee.app.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import lombok.SneakyThrows;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

public class ExcelUtil {
    public static final String HEADER_CONTENT_TYPE_DOWNLOAD_EXCEL = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    public static final String HEADER_CONTENT_DISPOSITION_KEY = "content-disposition";
    public static final String HEADER_CONTENT_DISPOSITION_VALUE = "attachment;filename=%s.xlsx";
    /**
     * 默认分页大小
     */
    public static final int DEFAULT_PAGE_SIZE = 100;

    /**
     * 用于分页数据查询导出
     */
    public interface QueryPageData {
        List<?> invoke(int page, int pageSize);
    }

    /**
     * 实现此接口，输出数据时会自动填充序号
     */
    public interface ColumnIndex{
        void setIndex(Integer index);
    }

    /**
     * 导出excel时初始化response的header
     */
    public static void initResponse(HttpServletResponse response, String fileName) {
        String encodeFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setContentType(HEADER_CONTENT_TYPE_DOWNLOAD_EXCEL);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setHeader(HEADER_CONTENT_DISPOSITION_KEY, String.format(HEADER_CONTENT_DISPOSITION_VALUE, encodeFileName));
        // 允许前端暴露的header：前端可能因为跨域等问题读取不到headers
        response.setHeader("Access-Control-Expose-Headers", HEADER_CONTENT_DISPOSITION_KEY);
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
     * 构建文件名，携带日期
     * @param fileName 文件名
     * @return {fileName}-202409021024
     */
    public static String buildFileName(String fileName) {
        return StrUtil.format("{}-{}",
                fileName,
                DateUtil.formatDateTime(new Date()).replaceAll("[-: ]", ""));
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
        initResponse(response, fileName);
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


    /**
     * 分页列表的导出
     * @param response 用于获取outputStream
     * @param fileName 文件名
     * @param clz 字段配置
     */
    @SneakyThrows
    public static void write(HttpServletResponse response, String fileName, Class<?> clz, QueryPageData QueryPageData) {
        initResponse(response, fileName);
        WriteSheet sheet = buildWriteSheet("Sheet1", clz);
        ExcelWriter writer = null;
        int page = 1;
        int index = 1;
        try {
            writer = EasyExcel.write(response.getOutputStream()).build();
            List<?> list;
            do {
                list = QueryPageData.invoke(page++, DEFAULT_PAGE_SIZE);
                // 设定序号
                for (Object o : list) {
                    if (o instanceof ColumnIndex) {
                        ((ColumnIndex) o).setIndex(index++);
                    }
                }
                writer.write(list, sheet);
            }
            while (CollUtil.isNotEmpty(list));
        } finally {
            if (writer != null) {
                writer.finish();
            }
        }
    }

}
