package com.honyee.app.config.limit;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.util.concurrent.RateLimiter;
import com.honyee.app.exp.RateLimitException;
import com.honyee.app.utils.HttpUtil;
import com.honyee.app.utils.LogUtil;
import com.honyee.app.utils.SpelUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
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

    @Autowired
    private RedissonClient redissonClient;

    @Pointcut(value = "@annotation(com.honyee.app.config.limit.RateLimit)")
    public void access() {

    }

    @Around("access()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes req = (ServletRequestAttributes) requestAttributes;
        // 仅支持request限流
        if (req == null) {
            return point.proceed();
        }

        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        RateLimit annotation = method.getAnnotation(RateLimit.class);
        Object target = point.getTarget();
        RateLimit.LimitMode mode = annotation.mode();
        // 强力限流
        if (mode == RateLimit.LimitMode.LOCK) {
            EvaluationContext context = SpelUtil.contextVariable(point);
            String key = parseSpel(context, annotation, annotation.lockKey());
            RLock lock = redissonClient.getLock("lock_strong_limit_" + key);
            try {
                // 锁定5秒，不解锁
                if (lock.isLocked()) {
                    throwRateLimitException(key);
                }
                if (lock.tryLock(0L, annotation.timeLong(), annotation.timeUnit())) {
                    LogUtil.info("我拿到执行权了");
                    return point.proceed();
                }
            } catch (InterruptedException e) {
                throwRateLimitException(key);
            }
        }

        // 根据IP限流
        if (mode == RateLimit.LimitMode.COMMON) {
            HttpServletRequest request = req.getRequest();
            String ip = request.getRemoteAddr();
            String realIp = HttpUtil.getIpAddress(request);
            String keyIp = String.format("%s=>%s", ip, realIp);
            check(keyIp, annotation.rate(), annotation.timeLong(), annotation.timeUnit(), rateLimiterCacheIp);
        }
        // 通用限流
        if (mode == RateLimit.LimitMode.IP) {
            String key = String.format("%s.%s", target.getClass().getName(), method.getName());
            check(key, annotation.rate(), annotation.timeLong(), annotation.timeUnit(), rateLimiterCacheCommon);
        }

        return point.proceed();
    }

    private void check(String key, double rate, long timeout, TimeUnit timeUnit, Cache<Object, Object> rateLimiterCache) {
        Object ifPresent = rateLimiterCache.getIfPresent(key);
        if (ifPresent == null) {
            ifPresent = RateLimiter.create(rate);
            rateLimiterCache.put(key, ifPresent);
        }
        RateLimiter rateLimiter = (RateLimiter) ifPresent;
        if (!rateLimiter.tryAcquire(timeout, timeUnit)) {
            throwRateLimitException(key);
        }
    }

    private void throwRateLimitException(String key) {
        RateLimitException rateLimitException = new RateLimitException(key);
        LogUtil.info(rateLimitException.getMessage());
        throw rateLimitException;
    }

    /**
     * 解析spel
     */
    private String parseSpel(EvaluationContext context, RateLimit annotation, String el) {
        Expression expressionValue = parseExpression(annotation, el);
        return expressionValue.getValue(context, String.class);
    }

    /**
     * 解析el
     */
    private Expression parseExpression(RateLimit annotation, String el) {
        ExpressionParser parser = new SpelExpressionParser();
        if (annotation.useTemplate()) {
            return parser.parseExpression(el, new TemplateParserContext());
        } else {
            return parser.parseExpression(el);
        }
    }

}
