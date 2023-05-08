package com.honyee.app.config.limit;


import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * <pre>
 * 限流模式分为两种：
 * 1. 强力模式：成功锁定后不解锁，等待时间自动解锁
 * 2. 通用限流：使用漏桶模式
 * 3. ip限流：使用漏桶模式
 * </pre>
 */
@Target({METHOD})
@Retention(RUNTIME)
public @interface RateLimit {

    /**
     * 限流模式
     */
    enum LimitMode {
        /**
         * 强力锁定
         */
        LOCK,
        /**
         * 通用限流
         */
        COMMON,
        /**
         * 根据IP限流
         */
        IP
    }

    /**
     * 限流模式
     */
    LimitMode mode() default LimitMode.COMMON;

    /**
     * <pre>
     * 是否使用TemplateParserContext解析，默认false
     * false: #key => honyee
     * true: #{#key} => honyee
     * </pre>
     */
    boolean useTemplate() default false;

    /**
     * 锁定的key，支持SPEL
     */
    String lockKey() default "";

    /**
     * 锁定/等待时长
     */
    long timeLong() default 1000L;

    /**
     * 锁定/等待时间单位
     */
    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;

    /**
     * 漏桶频率
     */
    double rate() default 10.0;

}
