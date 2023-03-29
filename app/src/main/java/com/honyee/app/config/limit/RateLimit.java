package com.honyee.app.config.limit;


import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({METHOD})
@Retention(RUNTIME)
public @interface RateLimit {
    /**
     * 通用限流
     */
    boolean limit() default true;
    double rate() default 10.0;
    long timeout() default 500L;
    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;

    /**
     * 根据ip限流
     */
    boolean limitIp() default false;
    double rateIp() default 1.0;
    long timeoutIp() default 500L;
    TimeUnit timeUnitIp() default TimeUnit.MILLISECONDS;
}
