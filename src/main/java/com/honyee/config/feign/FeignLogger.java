package com.honyee.config.feign;

import com.honyee.config.Constants;
import com.honyee.utils.LogUtil;
import feign.Logger;
import feign.Request;
import feign.Response;
import feign.Util;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

import static feign.Util.*;

public class FeignLogger extends Logger {

    @Override
    protected void logRequest(String configKey, Level logLevel, Request request) {
        if (checkDisableLog(configKey)) {
            return;
        }

        StringBuilder logBuilder = new StringBuilder();
        try {
            String protocolVersion = resolveProtocolVersion(request.protocolVersion());
            logBuilder.append(String.format("\nrequest:\n\t---> %s %s %s",
                request.httpMethod().name(), request.url(), protocolVersion));
            if (logLevel.ordinal() >= Level.HEADERS.ordinal()) {
                for (String field : request.headers().keySet()) {
                    if (shouldLogRequestHeader(field)) {
                        for (String value : valuesOrEmpty(request.headers(), field)) {
                            logBuilder.append(String.format("\n\t%s: %s", field, value));
                        }
                    }
                }

                int bodyLength = 0;
                if (request.body() != null) {
                    bodyLength = request.length();
                    if (logLevel.ordinal() >= Level.FULL.ordinal()) {
                        String bodyText =
                            request.charset() != null
                                ? new String(request.body(), request.charset())
                                : null;
                        logBuilder.append("\n");
                        logBuilder.append(String.format(bodyText != null ? bodyText : "Binary data"));
                    }
                }
                logBuilder.append(String.format("\n\t---> END HTTP (%s-byte body)", bodyLength));
            }
        } finally {
            LogUtil.get().info(logBuilder.toString());
        }

    }

    @Override
    protected Response logAndRebufferResponse(String configKey, Level logLevel, Response response, long elapsedTime) throws IOException {
        if (checkDisableLog(configKey)) {
            return response;
        }
        StringBuilder logBuilder = new StringBuilder();
        try {
            String protocolVersion = resolveProtocolVersion(response.protocolVersion());
            String reason =
                response.reason() != null && logLevel.compareTo(Level.NONE) > 0 ? " " + response.reason()
                    : "";
            int status = response.status();
            logBuilder.append(String.format("\nresponse:\n\t<--- %s %s%s (%sms)", protocolVersion, status, reason, elapsedTime));
            if (logLevel.ordinal() >= Level.HEADERS.ordinal()) {

                for (String field : response.headers().keySet()) {
                    if (shouldLogResponseHeader(field)) {
                        for (String value : valuesOrEmpty(response.headers(), field)) {
                            logBuilder.append(String.format("\n\t%s: %s", field, value));
                        }
                    }
                }

                int bodyLength = 0;
                if (response.body() != null && !(status == 204 || status == 205)) {
                    // HTTP 204 No Content "...response MUST NOT include a message-body"
                    // HTTP 205 Reset Content "...response MUST NOT include an entity"
                    if (logLevel.ordinal() >= Level.FULL.ordinal()) {
                        logBuilder.append("\n");
                    }
                    byte[] bodyData = Util.toByteArray(response.body().asInputStream());
                    bodyLength = bodyData.length;
                    if (logLevel.ordinal() >= Level.FULL.ordinal() && bodyLength > 0) {
                        logBuilder.append(String.format("\n\t%s", decodeOrDefault(bodyData, UTF_8, "Binary data")));
                    }
                    logBuilder.append(String.format("\n\t<--- END HTTP (%s-byte body)", bodyLength));
                    return response.toBuilder().body(bodyData).build();
                } else {
                    logBuilder.append(String.format("\n\t<--- END HTTP (%s-byte body)", bodyLength));
                }
            }
            return response;
        } finally {
            LogUtil.get().info(logBuilder.toString());
        }
    }

    @Override
    protected void log(String s, String s1, Object... objects) {

    }

    /**
     * 检查是否允许打印日志
     *
     * @return true 禁止打印日志
     */
    private boolean checkDisableLog(String configKey) {
        if (StringUtils.isBlank(configKey)) {
            return false;
        }
        String[] split = configKey.split("#");
        if (split.length < 2) {
            return false;
        }
        String className = split[0];
        String methodName = split[1];

        Class<?> proxy = loadClass(className);
        if (proxy != null) {
            if (proxy.getAnnotation(DisableFeignLog.class) == null) {
                Method method = loadMethod(proxy, methodName);
                return method != null && method.getAnnotation(DisableFeignLog.class) != null;
            } else {
                return true;
            }
        }
        return false;
    }

    /**
     * 查找Proxy
     */
    private static Class<?> loadClass(String className) {
        for (String packageName : Constants.PROXY_PACKAGE) {
            try {
                return Class.forName(packageName + "." + className);
            } catch (ClassNotFoundException e) {
                // ignore
            }
        }
        return null;
    }

    /**
     * 查找method
     *
     * @param proxy      例如 GenericProxy
     * @param methodName 例如test(String)
     */
    private static Method loadMethod(Class<?> proxy, String methodName) {
        String[] split = methodName.split("\\(");
        String methodSimpleName = split[0];
        for (Method method : proxy.getDeclaredMethods()) {
            if (method.getName().equals(methodSimpleName)) {
                String params = Arrays.stream(method.getParameterTypes()).map(Class::getSimpleName).collect(Collectors.joining(","));
                String format = String.format("%s(%s)", methodSimpleName, params);
                if (methodName.equals(format)) {
                    return method;
                }
            }
        }
        return null;
    }
}
