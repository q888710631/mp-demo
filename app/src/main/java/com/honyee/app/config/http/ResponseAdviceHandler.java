package com.honyee.app.config.http;

import com.honyee.app.exp.CommonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import org.zalando.problem.Problem;

import java.nio.charset.StandardCharsets;

@ControllerAdvice
public class ResponseAdviceHandler implements ResponseBodyAdvice<Object> {

    private static final MediaType MEDIA_TYPE = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), StandardCharsets.UTF_8);

    @Autowired
    private Tracer tracer;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        Class<?> kls = returnType.getDeclaringClass();
        if (null != returnType.getMethodAnnotation(NotBodyAdvice.class)) {
            return false;
        }
        return true;
    }

    /**
     * Response.PrintWriter直接输出结果不会进入该方法
     */
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if (selectedContentType.includes(MEDIA_TYPE)) {
            response.getHeaders().setContentType(MEDIA_TYPE);
        }
        String traceId = "";
        Span span = tracer.currentSpan();
        if(span != null){
            traceId = span.context().traceId();
        }
        response.getHeaders().add("Trace-Id", traceId);
        if (body instanceof MyResponse
            || body instanceof String
            || body instanceof byte[]) {

            return body;
        }

        int statusCode = ((ServletServerHttpResponse) response).getServletResponse().getStatus();
        String message = "成功";
        // 判断code是否大于4xx && 判断是不是problem
        // 错误返回的时候code和message的包装
        if (statusCode >= 400) {
            if ((body instanceof Problem)) {
                Problem problem = (Problem) body;
                message = problem.getDetail();
            } else {
                message = "服务异常";
            }
        }
        return new MyResponse<>(statusCode, message, body);
    }

    @ExceptionHandler(value = CommonException.class)
    @ResponseBody
    public MyResponse<?> handler(CommonException e) {
        return new MyResponse<>(500, e.getCommonMessage());
    }


}
