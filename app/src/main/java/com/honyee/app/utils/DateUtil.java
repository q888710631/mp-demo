package com.honyee.app.utils;

import lombok.extern.slf4j.Slf4j;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class DateUtil {

    private static final String DEFAULT_ZONE_OFFSET = "+8";

    public static final ZoneId ZONE_ID = ZoneId.of("Asia/Shanghai");

    public static final ZoneOffset DEF_ZONE_OFFSET = ZoneOffset.of(DEFAULT_ZONE_OFFSET);

    public static final String ZERO_TIME = "00:00:00";

    public static final String COMMON_TIME_PATTERN = "HH:mm:ss";

    public static final String COMMON_HOUR_COLON_MINUTES_PATTERN = "HH:mm";

    public static final String COMMON_HOUR_MINUTES_PATTERN = "HHmm";

    public static final String COMMON_DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static final String COMMON_DATE_PATTERN = "yyyy-MM-dd";

    public static final String NO_CONNECTOR_DATE_PATTERN = "yyyyMMdd";

    public static final String NO_CONNECTOR_DATE_TIME_PATTERN = "yyyyMMddHHmmss";

    public static final List<String> SIMPLE_WEEK_ARRAY = Arrays.asList("周一", "周二", "周三", "周四", "周五", "周六", "周日");

    public static final DateTimeFormatter COMMON_TIME_FORMATTER = DateTimeFormatter.ofPattern(COMMON_TIME_PATTERN);

    public static final DateTimeFormatter COMMON_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(COMMON_DATE_TIME_PATTERN);

    public static final DateTimeFormatter COMMON_DATE_FORMATTER = DateTimeFormatter.ofPattern(COMMON_DATE_PATTERN);

    public static final DateTimeFormatter NO_CONNECTOR_DATE_FORMATTER = DateTimeFormatter.ofPattern(NO_CONNECTOR_DATE_PATTERN);

    public static final DateTimeFormatter NO_CONNECTOR_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(NO_CONNECTOR_DATE_TIME_PATTERN);

    public static final DateTimeFormatter COMMON_HOUR_MINUTES_FORMATTER = DateTimeFormatter.ofPattern(COMMON_HOUR_MINUTES_PATTERN);

    public static final DateTimeFormatter COMMON_HOUR_COLON_MINUTES_FORMATTER = DateTimeFormatter.ofPattern(COMMON_HOUR_COLON_MINUTES_PATTERN);

    /**
     * 日期字符串 -> LocalDate
     * @param dateStr 2023-08-21
     * @return 2023-08-21 00:00:00
     */
    public static LocalDate strLocalDateToLocalDate(String dateStr) {
        if (dateStr == null) {
            return null;
        }
        if (!dateStr.matches("\\d{4}-\\d{1,2}-\\d{1,2}")) {
            log.error("日期格式不正确");
            return null;
        }

        // 兼容下月、日为单个数字的情况
        dateStr = dateStr.replaceAll("-(\\d)(?=-|$)", "-0$1");
        return LocalDate.parse(dateStr, COMMON_DATE_FORMATTER);
    }

    /**
     * 日期字符串 -> 秒
     * @param dateStr 2023-08-21
     * @return 2023-08-21 00:00:00 的秒时间戳
     */
    public static Long strLocalDateToSecondMin(String dateStr) {
        LocalDate localDate = strLocalDateToLocalDate(dateStr);
        if (localDate == null) {
            return null;
        }
        return localDate.toEpochSecond(LocalTime.MIN, DateUtil.DEF_ZONE_OFFSET);
    }

    /**
     * 日期字符串 -> 秒
     * @param dateStr 2023-08-21
     * @return 2023-08-21 23:59:59.999999999 的秒时间戳
     */
    public static Long strLocalDateToSecondMax(String dateStr) {
        LocalDate localDate = strLocalDateToLocalDate(dateStr);
        if (localDate == null) {
            return null;
        }
        return localDate.toEpochSecond(LocalTime.MAX, DateUtil.DEF_ZONE_OFFSET);
    }

    public static String instantToString(Instant instant) {
        return COMMON_DATE_TIME_FORMATTER.format(instant.atZone(ZoneOffset.of(DEFAULT_ZONE_OFFSET)).toLocalDateTime());
    }

}
