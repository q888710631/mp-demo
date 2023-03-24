package com.honyee.config.feign;

import feign.Logger;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

public class FeignLogger extends Logger {
    // 日志累计
    private static final ThreadLocal<StringBuilder> LOGGER_THREAD_LOCAL = new ThreadLocal<>();
    // 是否允许打印日志
    private static final ThreadLocal<Boolean> LOGGER_ENABLE_THREAD_LOCAL = new ThreadLocal<>();
    // proxy所在的package
    private static final String[] PROXY_PACKAGE = {"com.honyee.proxy"};

    @Override
    protected void log(String s, String s1, Object... objects) {
        checkEnableLog(s);
        if (!LOGGER_ENABLE_THREAD_LOCAL.get()) {
            return;
        }

        StringBuilder stringBuilder = LOGGER_THREAD_LOCAL.get();
        if (stringBuilder == null) {
            stringBuilder = new StringBuilder();
            LOGGER_THREAD_LOCAL.set(stringBuilder);
        }
        if (s1.startsWith("--->") && !s1.startsWith("---> END HTTP")) {
            stringBuilder.append("\nrequest:");
        }

        if (s1.startsWith("<---") && !s1.startsWith("<--- END HTTP")) {
            stringBuilder.append("\nresponse:");
        }

        stringBuilder.append("\n\t").append(String.format(s1, objects));
    }

    /**
     * 获取日志，并清理
     */
    public static String popLog() {
        try {
            StringBuilder stringBuilder = LOGGER_THREAD_LOCAL.get();
            if (stringBuilder == null) {
                return null;
            }
            return stringBuilder.toString();
        } finally {
            remove();
        }
    }

    public static void remove() {
        LOGGER_THREAD_LOCAL.remove();
        LOGGER_ENABLE_THREAD_LOCAL.remove();
    }

    private static void enableLog() {
        LOGGER_ENABLE_THREAD_LOCAL.set(Boolean.TRUE);
    }

    private static void disableLog() {
        LOGGER_ENABLE_THREAD_LOCAL.set(Boolean.FALSE);
    }

    /**
     * 检查是否允许打印日志
     */
    private static void checkEnableLog(String s) {
        // 已经检查过
        if (LOGGER_ENABLE_THREAD_LOCAL.get() != null) {
            return;
        }
        enableLog();
        if (StringUtils.isBlank(s)) {
            return;
        }
        String[] split = s.split("#");
        if (split.length < 2) {
            return;
        }
        String className = split[0];
        String methodName = split[1];

        Class<?> proxy = loadClass(className);
        if (proxy == null) {
            return;
        }
        if (proxy.getAnnotation(DisableFeignLog.class) == null) {
            Method method = loadMethod(proxy, methodName);
            if (method != null && method.getAnnotation(DisableFeignLog.class) != null) {
                disableLog();
            }
        } else {
            disableLog();
        }
    }

    /**
     * 查找Proxy
     */
    private static Class<?> loadClass(String className) {
        for (String packageName : PROXY_PACKAGE) {
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
