package com.honyee.config.log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.honyee.config.Constants;
import com.honyee.exp.CommonException;
import com.honyee.utils.HttpUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.Ordered;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Aspect
@EnableAspectJAutoProxy
@Component
public class LoggingAspect implements InitializingBean, Ordered {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }

    @Override
    public void afterPropertiesSet() {
//        objectMapper = new ObjectMapper();
    }

    @Pointcut(
        "within(@org.springframework.stereotype.Service *)" +
            " || within(@org.springframework.web.bind.annotation.RestController *)"
    )
    public void springBeanPointcut() {
        // Method is empty as this is just a Pointcut, the implementations are in the advices.
    }

    private Logger logger(JoinPoint joinPoint) {
        return LoggerFactory.getLogger(joinPoint.getSignature().getDeclaringTypeName());
    }

    @AfterThrowing(pointcut = "springBeanPointcut()", throwing = "e")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
        Logger logger = logger(joinPoint);
        if (e instanceof CommonException) {
            for (StackTraceElement stack : e.getStackTrace()) {
                if (stack.getClassName().startsWith(Constants.BASE_PACKAGE)) {
                    logger.warn("自定义异常 {} => {}.{}(), line={}, message={}",
                        e.getClass().getSimpleName(), stack.getClassName(), stack.getMethodName(), stack.getLineNumber(), e.getMessage());
                    break;
                }
            }
        } else {
            logger
                .error(
                    "Exception in {}() with cause = {}",
                    joinPoint.getSignature().getName(),
                    e.getCause() != null ? e.getCause() : "NULL"
                );
        }
    }

    @Around("within(@org.springframework.web.bind.annotation.RestController *) || within(@org.springframework.stereotype.Controller *)")
    public Object log(ProceedingJoinPoint point) throws Throwable {
        Object[] args = point.getArgs();
        RestApiLog logEntity = new RestApiLog();
        Object result = null;
        //起始时间
        StopWatch stopWatch = StopWatch.createStarted();
        try {
            //请求
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            if (requestAttributes != null) {
                ServletRequestAttributes req = (ServletRequestAttributes) requestAttributes;
                HttpServletRequest request = req.getRequest();
                logEntity.requestMethod = request.getMethod();
                String contentType = request.getContentType();
                logEntity.contentType = (StringUtils.isEmpty(contentType)) ? "" : contentType;
                logEntity.requestURI = StringUtils.abbreviate(request.getRequestURI(), 255);
                logEntity.requestURL = StringUtils.abbreviate(request.getRequestURL().toString(), 255);
                logEntity.userAgent = Optional.ofNullable(request.getHeader("user-agent")).orElse("");
                logEntity.ip = request.getRemoteAddr();
                logEntity.headers = getHeadersInfo(request);
                logEntity.realIp = HttpUtil.getIpAddress(request);
                logEntity.params = getRequestParams(request);
                if (logEntity.contentType.startsWith(MediaType.APPLICATION_JSON.toString())) {
                    try {
                        // JwtFilter中对Request通过InputStreamHttpServletRequestWrapper装饰后才能在此处获取body
                        InputStream inputStream = request.getInputStream();
                        logEntity.body = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                            .lines().collect(Collectors.joining(System.lineSeparator()));
                    } catch (IOException e) {
                        // handle exception
                    }
                } else {
                    logEntity.body = "";
                }
            }

            //类名
            logEntity.className = point.getTarget().getClass().getName();
            //请求方法
            logEntity.method = point.getSignature().getName() + "()";
            //参数
            logEntity.methodArgs = wrapArgs(point);
            //调用结果
            result = point.proceed(args);
            logEntity.withThrows = false;
        } catch (Throwable throwable) {
            logEntity.withThrows = true;
            logEntity.throwable = throwable;
            throw throwable;
        } finally {
            stopWatch.split();
            logEntity.executeMs = stopWatch.getSplitTime();
            stopWatch.stop();
            Logger logger = logger(point);
            // 打印请求日志
            logEntity.logPrint(logger);
        }
        return result;
    }

    private String getRequestParams(HttpServletRequest request) {
        try {
            return objectMapper.writeValueAsString(request.getParameterMap());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "JSON parse error";
    }

    /**
     * 生成入参string
     */
    private String wrapArgs(ProceedingJoinPoint joinPoint) {
        String[] parameterNames = ((MethodSignature) joinPoint.getSignature()).getParameterNames();
        Object[] args = joinPoint.getArgs();
        String message = IntStream
            .range(0, args.length)
            .filter(i -> args[i] != null && !(args[i] instanceof HttpServletResponseWrapper))
            .mapToObj(i -> {
                try {
                    return parameterNames[i] + "=" + objectMapper.writeValueAsString(args[i]);
                } catch (JsonProcessingException e) {
                    return parameterNames[i] + "解析异常：" + e.getMessage();
                }
            })
            .collect(Collectors.joining(","));
        if (StringUtils.isBlank(message)) {
            return "";
        }
        return "{" + message + "}";
    }


    /**
     * 获取头部信息
     */
    private Map<String, String> getHeadersInfo(HttpServletRequest request) {
        Map<String, String> map = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = headerNames.nextElement();
            String value = request.getHeader(key);
            map.put(key, value);
        }
        return map;
    }

    /**
     * 日志记录
     */
    public static class RestApiLog {
        // 类名
        private String className;
        // 方法名
        private String method;
        // 请求参数
        private String params;
        // 请求参数
        private String methodArgs;
        // 请求体
        private String body;
        // 响应参数
        private String result;
        // 请求方法
        private String requestMethod;
        // content-type
        private String contentType;
        // user-agent
        private String userAgent;
        // 执行时间(ms)
        private long executeMs;
        // 请求uri
        private String requestURI;
        // 请求URL
        private String requestURL;
        // 请求ip,如果
        private String ip;
        // 用户的真实ip
        private String realIp;
        // 头部
        private Map<String, String> headers;
        // 是否抛出异常
        private boolean withThrows;
        // 抛出的异常
        private Throwable throwable;

        /**
         * 获取异常信息
         */
        private String wrapThrowMessage() {
            return (withThrows && throwable != null) ? (throwable.getClass().getName() + "[" + throwable.getMessage() + "]") : "false";
        }

        private String wrapResult(Object result) {
            return result == null ? "<>" : StringUtils.abbreviate(result.toString(), 2000);
        }

        private void logPrint(Logger logger) {
            logger.info(
                "\r\n" +
                    "\r\n   Request URL : {}" +
                    "\r\n   Http Method : {}" +
                    "\r\n   Request URI : {}" +
                    "\r\n   Request Params : {}" +
                    "\r\n   Request Body : {}" +
                    "\r\n   Method Args : {}" +
                    "\r\n   Http Headers : {}" +
                    "\r\n   Content-Type : {}" +
                    "\r\n   Class name : {}" +
                    "\r\n   Method Name : {}" +
                    "\r\n   Request IP : {}" +
                    "\r\n   Real IP : {}" +
                    "\r\n   User Agent : {}" +
                    "\r\n   Execution Time : {}ms" +
                    "\r\n   WithThrows : {}" +
                    "\r\n   Result : {}" +
                    "\r\n",
                requestURL,
                requestMethod,
                requestURI,
                params,
                body,
                methodArgs,
                headers,
                contentType,
                className,
                method,
                ip,
                realIp,
                userAgent,
                executeMs,
                wrapThrowMessage(),
                wrapResult(result)
            );
        }
    }
}