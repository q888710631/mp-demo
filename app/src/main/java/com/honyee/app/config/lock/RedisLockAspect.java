package com.honyee.app.config.lock;

import com.honyee.app.exp.LockOutOfTimeException;
import com.honyee.app.utils.LogUtil;
import com.honyee.app.utils.SpelUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;

@Component
@Aspect
public class RedisLockAspect {

    @Resource
    private RedissonClient redissonClient;

    private final boolean enableLog;

    public RedisLockAspect(@Value("${application.redis-lock.log.enable}") Boolean redisLockLogEnable) {
        this.enableLog = Boolean.TRUE.equals(redisLockLogEnable);
    }

    @Pointcut(value = "@annotation(com.honyee.app.config.lock.RedisLock)")
    public void access() {

    }

    @Around("access()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        RedisLock annotation = method.getAnnotation(RedisLock.class);

        EvaluationContext context = SpelUtil.contextVariable(point);
        String key = parseSpel(context, annotation, annotation.key());
        String value = annotation.valueSpel() ? parseSpel(context, annotation, annotation.value()) : annotation.value();

        LogUtil.info("@RedisLock => value={}, key={}", value, key);
        String lockKey = String.format("lock_%s_%s", value, key);
        RLock lock = redissonClient.getLock(lockKey);
        try {
            if (enableLog) {
                LogUtil.info("锁定：{}", lockKey);
            }
            if (annotation.tryLock()) {
                if (lock.tryLock(annotation.timeLong(), annotation.timeUnit())) {
                    return point.proceed();
                }
                throw new LockOutOfTimeException(lockKey);
            } else {
                lock.lock(annotation.timeLong(), annotation.timeUnit());
                return point.proceed();
            }
        } finally {
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
                if (enableLog) {
                    LogUtil.info("解锁：{}", lockKey);
                }
            }
        }
    }

    /**
     * 解析spel
     */
    private String parseSpel(EvaluationContext context, RedisLock annotation, String el) {
        Expression expressionValue = parseExpression(annotation, el);
        return expressionValue.getValue(context, String.class);
    }

    /**
     * 解析el
     */
    private Expression parseExpression(RedisLock annotation, String el) {
        ExpressionParser parser = new SpelExpressionParser();
        if (annotation.useTemplate()) {
            return parser.parseExpression(el, new TemplateParserContext());
        } else {
            return parser.parseExpression(el);
        }
    }



}
