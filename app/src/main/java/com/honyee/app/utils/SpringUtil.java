package com.honyee.app.utils;

import org.springframework.context.ApplicationContext;

public class SpringUtil {
    public static ApplicationContext CONTEXT;

    /**
     * 获取bean
     */
    public static <T> T getBean(Class<T> var1) {
        return CONTEXT.getBean(var1);
    }

}
