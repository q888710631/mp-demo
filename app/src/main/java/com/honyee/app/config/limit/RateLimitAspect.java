package com.honyee.app.config.limit;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.util.concurrent.RateLimiter;
import com.honyee.app.exp.RateLimitExistsException;
import com.honyee.app.utils.HttpUtil;
import com.honyee.app.utils.LogUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

@Component
@Aspect
public class RateLimitAspect {
    /**
     * 通用限流
     */
    Cache<Object, Object> rateLimiterCacheCommon = Caffeine.newBuilder()
        .expireAfterAccess(10, TimeUnit.MINUTES)
        .recordStats()
        .build();
    /**
     * 根据IP限流
     */
    Cache<Object, Object> rateLimiterCacheIp = Caffeine.newBuilder()
        .expireAfterAccess(10, TimeUnit.MINUTES)
        .recordStats()
        .build();

    /**
     * 限流策略 ： 1秒钟2个请求
     */
    @Pointcut(value = "@annotation(com.honyee.app.config.limit.RateLimit)")
    public void access() {

    }

    @Around("access()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        RateLimit annotation = method.getAnnotation(RateLimit.class);
        Object target = point.getTarget();

        // 根据IP限流
        if (annotation.limitIp()) {
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            ServletRequestAttributes req = (ServletRequestAttributes) requestAttributes;
            HttpServletRequest request = req.getRequest();
            String ip = request.getRemoteAddr();
            String realIp = HttpUtil.getIpAddress(request);
            String keyIp = String.format("%s=>%s", ip, realIp);
            check(keyIp, annotation.rateIp(), annotation.timeoutIp(), annotation.timeUnitIp(), rateLimiterCacheIp);
        }
        // 通用限流
        if (annotation.limit()) {
            String key = String.format("%s.%s", target.getClass().getName(), method.getName());
            check(key, annotation.rate(), annotation.timeout(), annotation.timeUnit(), rateLimiterCacheCommon);
        }

        return point.proceed();
    }

    private void check(String key, double rate, long timeout, TimeUnit timeUnit, Cache<Object, Object> rateLimiterCache) {
        Object ifPresent = rateLimiterCacheCommon.getIfPresent(key);
        if (ifPresent == null) {
            ifPresent = RateLimiter.create(rate);
            rateLimiterCacheCommon.put(key, ifPresent);
        }
        RateLimiter rateLimiter = (RateLimiter) ifPresent;
        if (!rateLimiter.tryAcquire(timeout, timeUnit)) {
            RateLimitExistsException rateLimitExistsException = new RateLimitExistsException(key);
            LogUtil.warn(rateLimitExistsException.getMessage());
            throw rateLimitExistsException;
        }
    }
}
