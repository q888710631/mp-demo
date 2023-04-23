package com.honyee.app.utils;

import com.honyee.app.config.Constants;
import com.honyee.app.config.jwt.JwtFilter;
import com.honyee.app.config.lock.RedisLockAspect;
import com.honyee.app.config.log.FeiShuAlertAppender;
import com.honyee.app.config.log.LoggingAspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class LogUtil {
    private static final Logger DEFAULT_LOGGER = LoggerFactory.getLogger(LogUtil.class);

    private static final Map<String, Logger> LOGGER_MAP = new ConcurrentHashMap<>();

    /**
     * 根据className获取Logger
     */
    public static Logger get() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (StackTraceElement e : stackTrace) {
            String className = e.getClassName();
            Logger logger = LOGGER_MAP.get(className);
            if (logger != null) {
                return logger;
            }
            if (className.startsWith(Constants.BASE_PACKAGE) && !className.endsWith(LogUtil.class.getName())) {
                logger = LoggerFactory.getLogger(className);
                LOGGER_MAP.put(className, logger);
                return logger;
            }
        }
        return DEFAULT_LOGGER;
    }

    public static void error(String msg) {
        get().error(msg);
    }

    public static void error(String format, Object arg) {
        get().error(format, arg);
    }

    public static void error(String format, Object arg1, Object arg2) {
        get().error(format, arg1, arg2);
    }

    public static void error(String format, Object... arguments) {
        get().error(format, arguments);
    }

    public static void error(String msg, Throwable t) {
        get().error(msg, t);
    }

    public static void warn(String msg) {
        get().warn(msg);
    }

    public static void warn(String format, Object arg) {
        get().warn(format, arg);
    }

    public static void warn(String format, Object arg1, Object arg2) {
        get().warn(format, arg1, arg2);
    }

    public static void warn(String format, Object... arguments) {
        get().warn(format, arguments);
    }

    public static void info(String msg) {
        get().info(msg);
    }

    public static void info(String format, Object arg) {
        get().info(format, arg);
    }

    public static void info(String format, Object arg1, Object arg2) {
        get().info(format, arg1, arg2);
    }

    public static void info(String format, Object... arguments) {
        get().info(format, arguments);
    }


    public static String filterStackToString(Throwable e) {
        String message = e.getMessage();
        String simpleName = e.getClass().getSimpleName();
        List<String> logList = filterStack(e.getStackTrace());
        logList.add(0, String.format("%s：%s", simpleName, message));
        return String.join("\n\tat ", logList);
    }

    private static final Set<String> excludeClassName = Set.of(
        FeiShuAlertAppender.class.getName(),
        LoggingAspect.class.getName(),
        RedisLockAspect.class.getName(),
        JwtFilter.class.getName()
    );

    public static List<String> filterStack(Throwable e) {
        return filterStack(e.getStackTrace());
    }

    public static List<String> filterStack(StackTraceElement[] stackTraceElements) {
        return Arrays.stream(stackTraceElements).filter(stack -> {
                String className = stack.getClassName();
                return className.startsWith(Constants.BASE_PACKAGE) // 项目内的包名
                    && !excludeClassName.contains(className)
                    && !className.contains("$");
            })
            .map(stack -> {
                String className = stack.getClassName();
                return String.format("%s.%s(%d)",
                    className,
                    stack.getMethodName(),
                    stack.getLineNumber());
            }).collect(Collectors.toList());
    }
}
