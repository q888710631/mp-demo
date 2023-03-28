package com.honyee.app.config.feign;

import com.honyee.app.utils.LogUtil;
import feign.Logger;
import feign.Request;
import feign.Response;
import feign.Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static feign.Util.*;

public class FeignLogger extends Logger {

    private final org.slf4j.Logger logger = LogUtil.get();

    // 无需日志的Class -> Method，不考虑类名相同且方法完全一致的情况
    private static final MultiValueMap<String, String> DISABLE_FEIGN_LOG_METHOD = new LinkedMultiValueMap<>();

    public FeignLogger() {
        EnableFeignClients ann = FeignConfiguration.class.getAnnotation(EnableFeignClients.class);
        for (String pack : ann.basePackages()) {
            try {
                init(pack);
            } catch (IOException | ClassNotFoundException e) {
                logger.error("初始化DISABLE_FEIGN_LOG_METHOD错误，pack={}，error={}", pack, e.getMessage());
            }
        }
    }

    private void init(String pack) throws IOException, ClassNotFoundException {
        String packFormat = pack.replace(".", "/");
        // 支持查找子包
        String path = packFormat + "/**/*.class";
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resourcePatternResolver.getResources(path);
        for (Resource resource : resources) {
            String filePath = resource.getURL().getPath();
            String classPath = filePath.substring(filePath.indexOf(packFormat));
            String classPackage = classPath.replaceAll("(\\$\\d+)?.class", "").replaceAll("/", ".");
            Class<?> aClass = Class.forName(classPackage);
            String className = aClass.getSimpleName();
            boolean classHasAnn = aClass.getAnnotation(DisableFeignLog.class) != null;
            for (Method method : aClass.getDeclaredMethods()) {
                if (classHasAnn || method.getAnnotation(DisableFeignLog.class) != null) {
                    String params = Arrays.stream(method.getParameterTypes()).map(Class::getSimpleName).collect(Collectors.joining(","));
                    String format = String.format("%s(%s)", method.getName(), params);
                    DISABLE_FEIGN_LOG_METHOD.add(className, format);
                }
            }
        }
    }

    @Override
    protected void logRequest(String configKey, Level logLevel, Request request) {
        if (checkDisableLogV2(configKey)) {
            return;
        }

        StringBuilder logBuilder = new StringBuilder();
        try {
            String protocolVersion = resolveProtocolVersion(request.protocolVersion());
            logBuilder.append(String.format("\nrequest:%s\n\t---> %s %s %s",
                configKey, request.httpMethod().name(), request.url(), protocolVersion));
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
            logger.info(logBuilder.toString());
        }

    }

    @Override
    protected Response logAndRebufferResponse(String configKey, Level logLevel, Response response, long elapsedTime) throws IOException {
        if (checkDisableLogV2(configKey)) {
            return response;
        }
        StringBuilder logBuilder = new StringBuilder();
        try {
            String protocolVersion = resolveProtocolVersion(response.protocolVersion());
            String reason = response.reason() != null && logLevel.compareTo(Level.NONE) > 0 ? " " + response.reason() : "";
            int status = response.status();
            logBuilder.append(String.format("\nresponse: %s\n\t<--- %s %s%s (%sms)", configKey, protocolVersion, status, reason, elapsedTime));
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
            logger.info(logBuilder.toString());
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
    private boolean checkDisableLogV2(String configKey) {
        String[] split = configKey.split("#");
        if (split.length < 2) {
            return false;
        }
        String className = split[0];
        String methodName = split[1];
        List<String> methods = DISABLE_FEIGN_LOG_METHOD.get(className);
        return methods != null && methods.contains(methodName);
    }
}
