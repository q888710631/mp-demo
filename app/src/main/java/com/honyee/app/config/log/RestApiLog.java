package com.honyee.app.config.log;

import java.io.Serializable;
import java.util.Map;

/**
 * 日志记录
 */
public class RestApiLog implements Serializable {

    /**
     * 类名
     */
    private String className;

    /**
     * 方法名
     */
    private String method;

    /**
     * 请求参数
     */
    private String params;

    /**
     * 请求参数
     */
    private String methodArgs;

    /**
     * 响应参数
     */
    private String result;

    /**
     * 请求方法
     */
    private String requestMethod;

    /**
     * content-type
     */
    private String contentType;

    /**
     * user-agent
     */
    private String userAgent;

    /**
     * 执行时间(ms)
     */
    private long executeMs;

    /**
     * 请求uri
     */
    private String requestURI;

    /**
     * 请求URL
     */
    private String requestURL;

    /**
     * 请求ip,如果
     */
    private String ip;

    /**
     * 用户的真实ip
     */
    private String realIp;

    /**
     * 头部
     */
    private Map<String, String> headers;

    /**
     * 是否抛出异常
     */
    private boolean withThrows;

    /**
     * 抛出的异常
     */
    private Throwable throwable;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public long getExecuteMs() {
        return executeMs;
    }

    public void setExecuteMs(long executeMs) {
        this.executeMs = executeMs;
    }

    public String getRequestURI() {
        return requestURI;
    }

    public void setRequestURI(String requestURI) {
        this.requestURI = requestURI;
    }

    public String getRequestURL() {
        return requestURL;
    }

    public void setRequestURL(String requestURL) {
        this.requestURL = requestURL;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getRealIp() {
        return realIp;
    }

    public void setRealIp(String realIp) {
        this.realIp = realIp;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public boolean isWithThrows() {
        return withThrows;
    }

    public void setWithThrows(boolean withThrows) {
        this.withThrows = withThrows;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    public RestApiLog className(String className) {
        this.className = className;
        return this;
    }

    public RestApiLog method(String method) {
        this.method = method;
        return this;
    }

    public RestApiLog params(String params) {
        this.params = params;
        return this;
    }

    public RestApiLog result(String result) {
        this.result = result;
        return this;
    }

    public RestApiLog requestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
        return this;
    }

    public RestApiLog contentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public RestApiLog userAgent(String userAgent) {
        this.userAgent = userAgent;
        return this;
    }

    public RestApiLog executeMs(long executeMs) {
        this.executeMs = executeMs;
        return this;
    }

    public RestApiLog requestURI(String requestURI) {
        this.requestURI = requestURI;
        return this;
    }

    public RestApiLog requestURL(String requestURL) {
        this.requestURL = requestURL;
        return this;
    }

    public RestApiLog ip(String ip) {
        this.ip = ip;
        return this;
    }

    public RestApiLog realIp(String realIp) {
        this.realIp = realIp;
        return this;
    }

    public RestApiLog headers(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    public RestApiLog withThrows(boolean withThrows) {
        this.withThrows = withThrows;
        return this;
    }

    public RestApiLog throwable(Throwable throwable) {
        this.throwable = throwable;
        return this;
    }

    public String getMethodArgs() {
        return methodArgs;
    }

    public void setMethodArgs(String methodArgs) {
        this.methodArgs = methodArgs;
    }

    @Override
    public String toString() {
        return (
            "RestApiLog{" +
            "className='" +
            className +
            '\'' +
            ", method='" +
            method +
            '\'' +
            ", params='" +
            params +
            '\'' +
            ", methodArgs='" +
            methodArgs +
            '\'' +
            ", result='" +
            result +
            '\'' +
            ", requestMethod='" +
            requestMethod +
            '\'' +
            ", contentType='" +
            contentType +
            '\'' +
            ", userAgent='" +
            userAgent +
            '\'' +
            ", executeMs=" +
            executeMs +
            ", requestURI='" +
            requestURI +
            '\'' +
            ", requestURL='" +
            requestURL +
            '\'' +
            ", ip='" +
            ip +
            '\'' +
            ", realIp='" +
            realIp +
            '\'' +
            ", headers=" +
            headers +
            ", withThrows=" +
            withThrows +
            ", throwable=" +
            throwable +
            '}'
        );
    }
}
