package com.honyee.app.config.http;

import com.honyee.app.config.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import org.zalando.problem.Problem;

import javax.validation.ConstraintViolationException;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class ResponseAdviceHandler implements ResponseBodyAdvice<Object> {

    private static final MediaType MEDIA_TYPE = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), StandardCharsets.UTF_8);

    @Autowired
    private Tracer tracer;

    /**
     * http错误码从400开始
     */
    private static final Integer ERROR_CODE_BEGIN = 400;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        Class<?> kls = returnType.getDeclaringClass();
        // 返回值不用MyResponse包装
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
        if (span != null) {
            traceId = span.context().traceId();
        }
        response.getHeaders().add(Constants.TRACE_ID, traceId);
        if (body instanceof MyResponse
            || body instanceof String
            || body instanceof byte[]) {

            return body;
        }

        int statusCode = ((ServletServerHttpResponse) response).getServletResponse().getStatus();
        String message = "成功";
        // 判断code是否大于4xx && 判断是不是problem
        // 错误返回的时候code和message的包装
        if (statusCode >= ERROR_CODE_BEGIN) {
            if ((body instanceof Problem)) {
                Problem problem = (Problem) body;
                message = problem.getDetail();
            } else {
                message = "服务异常";
            }
        }
        return new MyResponse<>(statusCode, message, body);
    }

    @ResponseBody
    @ExceptionHandler({BindException.class, MethodArgumentNotValidException.class, ConstraintViolationException.class})
    public MyResponse<?> argumentValidationHandler(Exception ex) {
        String errorMsg = MyResponseCodeEnums.PARAM_VALID_FAILURE.getMessage();
        BindingResult bindingResult = null;
        if (ex instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException methodArgumentNotValidException = (MethodArgumentNotValidException) ex;
            bindingResult = methodArgumentNotValidException.getBindingResult();
        }
        if (ex instanceof BindException) {
            BindException bindException = (BindException) ex;
            bindingResult = bindException.getBindingResult();
        }
        if (ex instanceof ConstraintViolationException) {
            ConstraintViolationException constraintViolationException = (ConstraintViolationException) ex;
            errorMsg = constraintViolationException.getMessage();
        }
        if (bindingResult != null) {
            errorMsg= bindingResult.getFieldErrors().stream().sorted(Comparator.comparing(FieldError::getField))
                    .map(fieldError -> String.format("%s:%s", fieldError.getField(), fieldError.getDefaultMessage()))
                    .collect(Collectors.joining(";"));
        }
        return new MyResponse<>(MyResponseCodeEnums.PARAM_VALID_FAILURE.getCode(), errorMsg, null);
    }
}
