package com.honyee.app.utils;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class SpelUtil {
    /**
     * 创建EvaluationContext，并填充参数
     */
    public static EvaluationContext contextVariable(ProceedingJoinPoint point) {
        MethodSignature signature = (MethodSignature) point.getSignature();
        EvaluationContext context = new StandardEvaluationContext();
        String[] parameterNames = signature.getParameterNames();
        if (parameterNames != null) {
            Object[] args = point.getArgs();
            for (int i = 0; i < parameterNames.length; i++) {
                context.setVariable(parameterNames[i], args[i]);
            }
        }
        return context;
    }
}
