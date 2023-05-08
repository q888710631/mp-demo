package com.honyee.app.utils;

import com.honyee.app.config.Constants;
import com.honyee.app.config.filter.FinallyFilter;
import com.honyee.app.config.filter.JwtFilter;
import com.honyee.app.config.filter.RequestWrapperFilter;
import com.honyee.app.config.limit.RateLimitAspect;
import com.honyee.app.config.lock.RedisLockAspect;
import com.honyee.app.config.log.FeiShuAlertAppender;
import com.honyee.app.config.log.LoggingAspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
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

    private static final Set<String> excludeClassName = Set.of(
        LogUtil.class.getName(),
        FeiShuAlertAppender.class.getName(),
        LoggingAspect.class.getName(),
        RedisLockAspect.class.getName(),
        JwtFilter.class.getName(),
        RequestWrapperFilter.class.getName(),
        FinallyFilter.class.getName(),
        RateLimitAspect.class.getName()

    );

    /**
     * 获取当前异常的堆栈
     */
    public static String filterStackToString(Throwable e) {
        String message = e.getMessage();
        String simpleName = e.getClass().getSimpleName();
        List<String> logList = filterStack(e);
        logList.add(0, String.format("%s：%s", simpleName, message));
        return String.join("\n\tat ", logList);
    }

    /**
     * 获取当前异常的堆栈
     */
    public static List<String> filterStack(Throwable e) {
        return filterStack(e.getStackTrace());
    }

    /**
     * 获取当前异常的堆栈
     */
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

    /**
     * 打印当前方法调用堆栈
     *
     * @param mark 标识
     * @param descript 描述
     */
    public static void printCallStackTrace(String mark, String descript) {
        List<String> logList = new ArrayList<>();

        Throwable t = new Throwable();
        logList.add(String.format("\n调用堆栈 [%s][%s]", mark, descript));
        for (StackTraceElement stack : t.getStackTrace()) {
            String className = stack.getClassName();
            if (className.startsWith(Constants.BASE_PACKAGE) // 项目内的包名
                && !excludeClassName.contains(className)
                && !className.contains("$")) {
                logList.add(stack.toString());
            }
        }
        info(String.join("\n", logList));
    }
}
