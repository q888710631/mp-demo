package com.honyee.app.config.feign;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 不打印Feign请求日志，注解用有@FeignClient的接口上，或者方法上
 */
@Target({TYPE, FIELD, METHOD})
@Retention(RUNTIME)
public @interface DisableFeignLog {
}
