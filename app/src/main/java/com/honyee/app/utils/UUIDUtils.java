package com.honyee.app.utils;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 生成UUID
 */
@Component
public class UUIDUtils {
    public static String getUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final String SEQUENCE_KEY = "todaySequenceNo_";


    @Resource
    private RedisTemplate<Object, Object> redisTemplate;

    /**
     * 获取序列号
     */
    public Long sequence() {
        return sequence(1);
    }

    /**
     * <pre>
     * 序列号，最多支持每秒10000个
     * 需要更大的支持：
     * 方式1. DATE_TIME_FORMATTER的精度减小，不再到秒级别
     * 方式2. 把Long改诚String
     * </pre>
     * @param step 步进值，也可以当作批量获取来使用
     * @return 例如：init=0，step=10，返回10，则可用的为 [1,10]
     */
    public Long sequence(int step) {
        // 8位
        String date = LocalDateTime.now().format(DATE_TIME_FORMATTER);
        String key = SEQUENCE_KEY + date;
        Long increment = redisTemplate.opsForValue().increment(key, step);
        redisTemplate.expire(key, 2, TimeUnit.SECONDS);
        // Long最多19位
        return Long.parseLong(String.format("%s%05d", date, increment));
    }

}
