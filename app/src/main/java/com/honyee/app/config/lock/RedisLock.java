package com.honyee.app.config.lock;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 加锁，不支持 #result 读取返回值
 * 默认使用方式参考@Cacheable
 */
@Target({METHOD})
@Retention(RUNTIME)
public @interface RedisLock {
    /**
     * 锁前缀
     */
    String value();

    /**
     * 锁key
     */
    String key();

    /**
     * 锁时长
     */
    long timeLong() default 5L;

    /**
     * 锁时长单位
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * 是否使用TemplateParserContext解析，默认false
     * false: #key => honyee
     * true: #{#key} => honyee
     */
    boolean useTemplate() default false;

    /**
     * value是否使用spel
     */
    boolean valueSpel() default false;

    /**
     * 默认使用lock()，为true时使用tryLock()
     */
    boolean tryLock() default false;
}
