package com.honyee.app.utils;

import com.honyee.app.config.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

    public void error(String msg, Throwable t) {
        get().error(msg, t);
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

}
