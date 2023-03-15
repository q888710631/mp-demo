package com.mp.config.log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mp.utils.HttpUtils;
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
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.Ordered;
import org.springframework.security.web.header.HeaderWriterFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Aspect
@EnableAspectJAutoProxy
@Component
public class LoggingAspect implements Ordered {

    private static final ObjectMapper objectMapper = new ObjectMapper();

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
            logger(joinPoint)
                .error(
                    "Exception in {}() with cause = {}",
                    joinPoint.getSignature().getName(),
                    e.getCause() != null ? e.getCause() : "NULL"
                );
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
            Optional
                .ofNullable(RequestContextHolder.getRequestAttributes())
                .ifPresent(
                    k -> {
                        ServletRequestAttributes req = (ServletRequestAttributes) k;
                        HttpServletRequest request = req.getRequest();
                        logEntity.setRequestMethod(request.getMethod());
                        logEntity.setContentType((StringUtils.isEmpty(request.getContentType())) ? "empty" : request.getContentType());
                        logEntity.setRequestURI(StringUtils.abbreviate(request.getRequestURI(), 255));
                        logEntity.setRequestURL(StringUtils.abbreviate(request.getRequestURL().toString(), 255));
                        logEntity.setUserAgent(Optional.ofNullable(request.getHeader("user-agent")).orElse(""));
                        logEntity.setIp(request.getRemoteAddr());
                        logEntity.setHeaders(getHeadersInfo(request));
                        logEntity.setRealIp(HttpUtils.getIpAddress(request));
                        logEntity.setParams(getRequestParams(request));
                    }
                );

            //类名
            String className = point.getTarget().getClass().getName();
            logEntity.setClassName(className);
            //请求方法
            String method = point.getSignature().getName() + "()";
            logEntity.setMethod(method);
            //参数
            String methodArgs = wrapArgs(point);
            logEntity.setMethodArgs(methodArgs);
            //调用结果
            result = point.proceed(args);
            logEntity.setWithThrows(false);
        } catch (Throwable throwable) {
            logEntity.setWithThrows(true);
            logEntity.setThrowable(throwable);
            throw throwable;
        } finally {
            stopWatch.split();
            logEntity.setExecuteMs(stopWatch.getSplitTime());
            stopWatch.stop();
            logger(point)
                .info(
                    "\r\n" +
                    "\r\n" +
                    "    Request URL : {}" +
                    "\r\n" +
                    "    Http Method : {}" +
                    "\r\n" +
                    "    Request URI : {}" +
                    "\r\n" +
                    "    Request Params : {}" +
                    "\r\n" +
                    "    Method Args : {}" +
                    "\r\n" +
                    "    Http Headers : {}" +
                    "\r\n" +
                    "    Content-Type : {}" +
                    "\r\n" +
                    "    Class name : {}" +
                    "\r\n" +
                    "    Method Name : {}" +
                    "\r\n" +
                    "    Request IP : {}" +
                    "\r\n" +
                    "    Real IP : {}" +
                    "\r\n" +
                    "    User Agent : {}" +
                    "\r\n" +
                    "    Execution Time : {}" +
                    "ms" +
                    "\r\n" +
                    "    WithThrows : {}" +
                    "\r\n" +
                    "    Result : {}" +
                    "\r\n" +
                    "\r\n",
                    logEntity.getRequestURL(),
                    logEntity.getRequestMethod(),
                    logEntity.getRequestURI(),
                    logEntity.getParams(),
                    logEntity.getMethodArgs(),
                    logEntity.getHeaders(),
                    logEntity.getContentType(),
                    logEntity.getClassName(),
                    logEntity.getMethod(),
                    logEntity.getIp(),
                    logEntity.getRealIp(),
                    logEntity.getUserAgent(),
                    logEntity.getExecuteMs(),
                    wrapThrowMessage(logEntity),
                    wrapResult(result)
                );
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
            .mapToObj(i -> parameterNames[i] + "=" + args[i].toString())
            .collect(Collectors.joining(","));
        return "{" + message + "}";
    }

    private String wrapResult(Object result) {
        return result == null ? "<>" : StringUtils.abbreviate(result.toString(), 2000);
    }

    /**
     * 获取异常信息
     */
    private String wrapThrowMessage(RestApiLog logEntity) {
        return (logEntity.isWithThrows() && logEntity.getThrowable() != null)
            ? (logEntity.getThrowable().getClass().getName() + "[" + logEntity.getThrowable().getMessage() + "]")
            : "false";
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

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }
}
