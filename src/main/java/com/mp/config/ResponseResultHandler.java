package com.mp.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import org.zalando.problem.Problem;

import java.util.Optional;

@ControllerAdvice
public class ResponseResultHandler implements ResponseBodyAdvice<Object> {
    private final Logger log = LoggerFactory.getLogger(ResponseResultHandler.class);


    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        Class<?> kls = returnType.getDeclaringClass();
        if (null != returnType.getMethodAnnotation(NotBodyAdvice.class)) {
            return false;
        }
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if (body instanceof MyResponse
        || body instanceof String
        || body instanceof byte[]) {

            return body;
        }

        int statusCode = ((ServletServerHttpResponse) response).getServletResponse().getStatus();
        String msg = "成功";
        // 判断code是否大于4xx && 判断是不是problem
        // 错误返回的时候code和message的包装
        if (statusCode >= 400) {
            if ((body instanceof Problem)) {
                Problem problem = (Problem) body;
                msg = problem.getDetail();
            } else {
                msg =
                    String.format(
                        "服务异常 [%s][%s]",
                        "traceId",//Optional.ofNullable(MDC.get(MDCFilter.TRACE_ID_HEADER)).orElse("-"),
                        System.currentTimeMillis()
                    );
            }
        }
        return new MyResponse<>(statusCode, msg, body);
    }
}
