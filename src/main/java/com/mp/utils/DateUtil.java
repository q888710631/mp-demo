package com.mp.utils;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

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


}
