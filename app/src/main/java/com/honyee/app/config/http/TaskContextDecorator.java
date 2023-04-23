package com.honyee.app.config.http;

import org.springframework.core.task.TaskDecorator;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * 线程装饰器
 */
public class TaskContextDecorator implements TaskDecorator {

    @Override
    public Runnable decorate(Runnable runnable) {
        // 请求上下文
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        // 安全上下文
        SecurityContext securityContext = SecurityContextHolder.getContext();

        return () -> {
            try {
                RequestContextHolder.setRequestAttributes(requestAttributes);
                SecurityContextHolder.setContext(securityContext);
                runnable.run();
            } finally {
                RequestContextHolder.resetRequestAttributes();
                SecurityContextHolder.clearContext();
            }
        };
    }
}
