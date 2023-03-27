package com.honyee.app.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogUtil {
    private static final ThreadLocal<Logger> LOGGER_THREAD_LOCAL = new ThreadLocal<>();

    public static Logger get() {
        Logger logger = LOGGER_THREAD_LOCAL.get();
        if (logger == null) {
            logger = LoggerFactory.getLogger(getCallClassName());
            LOGGER_THREAD_LOCAL.set(logger);
        }
        return logger;
    }

    public static void remove() {
        LOGGER_THREAD_LOCAL.remove();
    }

    private static String getCallClassName() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (stackTrace.length > 3) {
            StackTraceElement caller = stackTrace[3];
            return caller.getClassName();
        }
        return LogUtil.get().getName();
    }
}
