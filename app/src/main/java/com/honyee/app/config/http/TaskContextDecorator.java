package com.honyee.app.config.http;

import com.honyee.app.config.Constants;
import org.springframework.cloud.sleuth.TraceContext;
import org.springframework.core.task.TaskDecorator;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

public class TaskContextDecorator implements TaskDecorator {

    @Override
    public Runnable decorate(Runnable runnable) {
        // 请求上下文
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        // 安全上下文
        SecurityContext securityContext = SecurityContextHolder.getContext();

        if (null == requestAttributes){
            //
        } else {
            ServletRequestAttributes attributes = (ServletRequestAttributes) requestAttributes;
            HttpServletRequest request = attributes.getRequest();
            TraceContext traceContext = (TraceContext) request.getAttribute(TraceContext.class.getName());
            String traceId = traceContext.traceId();
            request.setAttribute(Constants.TRACE_ID, traceId);
        }

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
